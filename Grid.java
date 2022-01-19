// Grid Class: manages a 4x4 grid of Tile objects, and their functions in the game of 2048
// Author: Stefan Barna
// Version: 2022/01/06

import java.awt.*;
import java.awt.Graphics;

public class Grid {
    public static final int SIZE = 4;   // dimensions of playing grid
    private final Tile[][] grid;        // playing grid consisting of tiles

    private int score;                  // current player score
    private int highscore;              // player highest score

    private boolean won;                // true if the player has won (the game continue past this point)

    // base constructor
    public Grid() {
        // initialize variables
        this.score = 0;
        this.grid = new Tile[SIZE][SIZE];
        this.won = false;

        // initialize all tiles within the grid
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++)
                this.grid[i][j] = new Tile();
        }

        // select two random tiles to give numbers to
        this.generateTile();
        this.generateTile();
    }

    // returns the current score
    public int getScore() {
        return this.score;
    }

    // sets the high score
    public void setHighscore(int score) {
        this.highscore = score;
    }

    // retrieves the current high score
    public int getHighscore() {
        return this.highscore;
    }

    // resets the playing field, as though a new game began
    public void restart() {
        // reset score
        this.score = 0;

        // reset all tiles within the grid
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++)
                this.grid[i][j].setVal(0);
        }

        // select two random tiles to give numbers to
        this.generateTile();
        this.generateTile();
    }

    // generates a random value (2 or 4) on a random tile if a tile is available
    public void generateTile() {
        // randomly generate coordinates and value
        if (!this.hasEmpty())
            return;

        while (true) {
            int x = (int) (Math.floor(Math.random() * SIZE));
            int y = (int) (Math.floor(Math.random() * SIZE));
            if (this.grid[x][y].getVal() == 0) {
                int randval = (int)(Math.pow(2, (int)(Math.random() * 2 + 1))); // can either be 2 or 4
                this.grid[x][y].setVal(randval);
                return;
            }
        }
    }

    // moves all tiles to their rightmost position on the grid
    public void move(Tile[][] grid) {
        // iterate over the grid
        for (int i = 0; i < SIZE; i++) {
            for (int j = SIZE - 2; j >= 0; j--) {
                // check if the element has a value
                if (grid[i][j].getVal() != 0) {
                    // check that next slot is not an edge
                    if (j < SIZE - 1) {
                        for (int k = j; k < SIZE - 1; k++) {
                            // move
                            if (grid[i][k + 1].getVal() == 0) {
                                grid[i][k + 1].setVal(grid[i][k].getVal());
                                grid[i][k].reset();
                                grid[i][k + 1].setAction(true);
                            }
                            // merge
                            else if (grid[i][k + 1].equals(grid[i][k]) && !grid[i][k + 1].getMerged() && !grid[i][k].getMerged()) {
                                // merges and sets merge status of tiles to true
                                // tiles can only merge once per turn
                                grid[i][k + 1].merge(grid[i][k]);

                                // increment score
                                this.score += grid[i][k + 1].getVal();
                                if (this.score > this.highscore)
                                    this.highscore = this.score;
                                grid[i][k].reset();

                                grid[i][k + 1].setAction(true);
                                break;
                            }
                            // cannot perform action
                            else { break; }
                        }
                    }
                }
            }
        }
    }

    // sets up an appropriate grid to move
    public void turn(String key) {
        Tile[][] tileset = new Tile[SIZE][SIZE];

        // copy all values of grid to tileset in an appropriate order to move
        switch (key) {
            case "RIGHT":
                // move right; do nothing
                for (int i = 0; i < SIZE; i++) {
                    for (int j = 0; j < SIZE; j++)
                        tileset[i][j] = this.grid[i][j];
                }
                break;
            case "LEFT":
                // move left; invert y axis
                for (int i = 0; i < SIZE; i++) {
                    for (int j = 0; j < SIZE; j++) {
                        tileset[i][j] = this.grid[i][SIZE - 1 - j];
                    }
                }
                break;
            case "UP":
                // move down; invert x axis
                for (int i = 0; i < SIZE; i++) {
                    for (int j = 0; j < SIZE; j++) {
                        tileset[i][j] = this.grid[SIZE - 1 - j][i];
                    }
                }
                break;
            case "DOWN":
                // move up; reflect across x = y axis
                for (int i = 0; i < SIZE; i++) {
                    for (int j = 0; j < SIZE; j++) {
                        tileset[i][j] = this.grid[j][i];
                    }
                }
                break;
        }

        this.move(tileset);
    }

    // checks if an action was made in the most recent turn
    public boolean turnMade() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (this.grid[i][j].getAction())
                    return true;
            }
        }
        return false;
    }

    // resets the status of the tiles (merged and action are set to false)
    public void resetTileStatus() {
        for (Tile[] row : this.grid) {
            for (Tile tile : row) {
                tile.setMerged(false);
                tile.setAction(false);
            }
        }
    }

    // checks for empty spaces in the grid; returns true if there is an empty space
    public boolean hasEmpty() {
        for (Tile[] row : this.grid) {
            for (Tile tile : row) {
                if (tile.getVal() == 0)
                    return true;
            }
        }
        return false;
    }

    // checks for ability to merge two tiles in the grid; returns true if possible
    public boolean hasMerge() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                // check tile to the right
                if (j < SIZE - 1) {
                    if (this.grid[i][j].equals(this.grid[i][j + 1]))
                        return true;
                }

                // check tile below
                if (i < SIZE - 1) {
                    if (this.grid[i][j].equals(this.grid[i + 1][j]))
                        return true;
                }
            }
        }
        return false;
    }

    // sets the win state of the current game
    public void setWon(boolean win) {
        this.won = win;
    }

    // checks if the player has won
    public boolean gameWon() {
        if (!this.won) {
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (this.grid[i][j].getVal() == 2048) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // checks if the player has lost
    public boolean gameOver() {
        return !this.hasEmpty() && !this.hasMerge();
    }

    // paints the contents of the grid to a graphics object
    public void paint(Graphics graphics, Font f, int offset, int xvel, int yvel) {
        // draw base square, where all tiles are to be drawn
        graphics.setColor(Color.decode("#bbada0"));
        graphics.fillRoundRect(15, 140, 475, 475, 10, 10);

        // set the font for tiles
        f = f.deriveFont(40f);
        graphics.setFont(f);

        // draw each tile
        for (int i = 0; i < Grid.SIZE; i++) {
            for (int j = 0; j < Grid.SIZE; j++) {
                // mid animation paint
                if ((xvel != 0 || yvel != 0) && this.grid[j][i].getAction()) {
                    Tile temp = new Tile();
                    temp.paint(graphics, 30 + (115 * i), 155 + (115 * j), f);
                    if (this.grid[j][i].getVal() != 0)
                        this.grid[j][i].paint(graphics, 30 + (115 * i) + (offset * xvel), 155 + (115 * j) + (offset * yvel), f);
                }
                // default paint
                else
                    this.grid[j][i].paint(graphics, 30 + (115 * i), 155 + (115 * j), f);
            }
        }

        // check for you lose overlay
        if (this.gameOver()) {
            // semi-opaque overlay
            graphics.setColor(new Color(250, 248, 239, 150));
            graphics.fillRoundRect(15, 140, 475, 475, 10, 10);

            // game over display
            graphics.setColor(Color.decode("#776e65"));
            f = f.deriveFont(60f);
            graphics.setFont(f);
            FontMetrics metrics = graphics.getFontMetrics(f);

            int x = 260 - (metrics.stringWidth("Game over!") / 2);
            graphics.drawString("Game over!", x, 360);

            // display try again button
            graphics.setColor(Color.decode("#8f7a66"));
            graphics.fillRoundRect(190, 400, 120, 40, 5, 5);

            f = f.deriveFont(18f);
            graphics.setFont(f);
            graphics.setColor(Color.decode("#ffffff"));
            metrics = graphics.getFontMetrics(f);
            x = 190 + (120 - metrics.stringWidth("Try again")) / 2;
            int y = 400 + ((40 - metrics.getHeight()) / 2) + metrics.getAscent();
            graphics.drawString( "Try again", x, y);
        }
        // check for you win overlay
        else if (this.gameWon()) {
            // semi-opaque overlay
            graphics.setColor(new Color(237, 194, 46, 150));
            graphics.fillRoundRect(15, 140, 475, 475, 10, 10);

            // game over display
            graphics.setColor(Color.decode("#ffffff"));
            f = f.deriveFont(60f);
            graphics.setFont(f);
            FontMetrics metrics = graphics.getFontMetrics(f);

            int x = 260 -(metrics.stringWidth("You win!") / 2);
            graphics.drawString("You win!", x, 360);

            // display try again button
            graphics.setColor(Color.decode("#8f7a66"));
            graphics.fillRoundRect(190, 400, 120, 40, 5, 5);

            f = f.deriveFont(18f);
            graphics.setFont(f);
            graphics.setColor(Color.decode("#ffffff"));
            metrics = graphics.getFontMetrics(f);
            x = 190 + (120 - metrics.stringWidth("Try again")) / 2;
            int y = 400 + ((40 - metrics.getHeight()) / 2) + metrics.getAscent();
            graphics.drawString( "Try again", x, y);

            // display continue button
            graphics.setColor(Color.decode("#8f7a66"));
            graphics.fillRoundRect(190, 455, 120, 40, 5, 5);

            f = f.deriveFont(18f);
            graphics.setFont(f);
            graphics.setColor(Color.decode("#ffffff"));
            metrics = graphics.getFontMetrics(f);
            x = 190 + (120 - metrics.stringWidth("Continue")) / 2;
            y = 455 + ((40 - metrics.getHeight()) / 2) + metrics.getAscent();
            graphics.drawString( "Continue", x, y);
        }
    }
}
