import java.awt.*;
import java.awt.Graphics;

public class Grid {
    public static final int SIZE = 4;            // dimensions of playing grid
    private Tile[][] grid;  // playing grid consisting of tiles
    private int score;      // current player score
    private int highscore;    // player highest score

    // base constructor
    public Grid() {
        // initialize variables
        this.score = 0;
        this.grid = new Tile[SIZE][SIZE];

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

    public int getHighscore() {
        return this.highscore;
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
    public boolean move(Tile[][] grid) {
        boolean action = false;

        // iterate over the grid
        for (int i = 0; i < SIZE; i++) {
            for (int j = SIZE - 2; j >= 0; j--) {
                // check if the element has a value
                // TODO: grid[i][j] is null
                if (grid[i][j].getVal() != 0) {
                    // check that next slot is not an edge
                    if (j < SIZE - 1) {
                        for (int k = j; k < SIZE - 1; k++) {
                            // move
                            if (grid[i][k + 1].getVal() == 0) {
                                grid[i][k + 1].setVal(grid[i][k].getVal());
                                grid[i][k].reset();
                                grid[i][k + 1].setAction(true);
                                action = true;
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
                                action = true;
                                break;
                            }
                            // cannot perform action
                            else { break; }
                        }
                    }
                }
            }
        }

        // return whether something happened
        return action;
    }

    // sets up an appropriate grid to move
    public boolean turn(String key) {
        boolean action = false;
        Tile[][] tileset = new Tile[SIZE][SIZE];

        // copy all values of grid to tileset in an appropriate order to move
        switch (key) {
            case "RIGHT":
                // move right; do nothing
                for (int i = 0; i < SIZE; i++) {
                    for (int j = 0; j < SIZE; j++) {
                        tileset[i][j] = this.grid[i][j];
                    }
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

        return this.move(tileset);
    }

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

    public void paint(Graphics graphics, Font f, int offset, int xvel, int yvel) {
        // draw base square, where all tiles are to be drawn
        graphics.setColor(Color.decode("#bbada0"));
        graphics.fillRoundRect(15, 130, 475, 475, 10, 10);

        // set the font for tiles
        f = f.deriveFont(40f);
        graphics.setFont(f);

        // draw each tile
        for (int i = 0; i < Grid.SIZE; i++) {
            for (int j = 0; j < Grid.SIZE; j++) {
                // mid animation paint
                if ((xvel != 0 || yvel != 0) && this.grid[j][i].getAction()) {
                    Tile temp = new Tile();
                    temp.paint(graphics, 30 + (115 * i), 145 + (115 * j), f);
                    if (this.grid[j][i].getVal() != 0)
                        this.grid[j][i].paint(graphics, 30 + (115 * i) + (offset * xvel), 145 + (115 * j) + (offset * yvel), f);
                }
                // default paint
                else
                    this.grid[j][i].paint(graphics, 30 + (115 * i), 145 + (115 * j), f);
            }
        }
    }
}
