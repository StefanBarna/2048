// class managing individual tiles in the 2048 grid
// value 0 represents empty tile
import java.awt.*;

public class Tile {
    private static final int WIDTH = 100; // width of a tile
    private static final int HEIGHT = 100;// height of a tile

    private int val;            // value of the tile
    private boolean merged;     // representing if the tile has merged this turn
    private boolean actionMade; // representing if the tile has moved or merged this turn (for animation)
    Color color;                // background color of tile
    Color pen;

    // base constructor
    public Tile() {
        this.val = 0;
        this.setColor();
        this.setPen();
    }

    // value setter
    public void setVal(int val) {
        this.val = val;
        this.setColor();
        this.setPen();
    }

    // value getter
    public int getVal() {
        return this.val;
    }

    // merged setter
    public void setMerged(boolean merged) {
        this.merged = merged;
    }

    // merged getter
    public boolean getMerged() {
        return this.merged;
    }

    // sets if an action was made
    public void setAction(boolean actionMade) {
        this.actionMade = actionMade;
    }

    // returns whether an action was made on this tile
    public boolean getAction() {
        return this.actionMade;
    }

    // checks if this Tile holds the same value as the parameter
    public boolean equals(Tile t) {
        return (this.val == t.val);
    }

    // adds this Tile value to the parameter Tile value and stores in current instance
    public void merge(Tile t) {
        this.val += t.val;
        this.merged = true;
        this.setColor();
        this.setPen();
    }

    // resets Tile information
    public void reset() {
        this.val = 0;
        this.merged = false;
        this.setColor();
        this.setPen();
    }

    // sets the colour of the tile appropriate to the tile value
    private void setColor() {
        switch (this.val) { // select a color based on the tile value
            case 0 -> this.color = Color.decode("#cdc1b4");
            case 2 -> this.color = Color.decode("#eee4da");
            case 4 -> this.color = Color.decode("#ede0c8");
            case 8 -> this.color = Color.decode("#f2b179");
            case 16 -> this.color = Color.decode("#f59563");
            case 32 -> this.color = Color.decode("#f67c5f");
            case 64 -> this.color = Color.decode("#f65e3b");
            case 128 -> this.color = Color.decode("#edcf72");
            case 256 -> this.color = Color.decode("#edcc61");
            case 512 -> this.color = Color.decode("#edc850");
            case 1024 -> this.color = Color.decode("#edc53f");
            case 2048 -> this.color = Color.decode("#edc22e");
            default ->    // tile is black past the 2048 tile
                    this.color = Color.decode("#3d3a33");
        }
    }

    // sets the colour of the pen displaying the tile value
    private void setPen() {
        switch (this.val) {
            case 0 -> // if there's no value on the tile hide the text with the same color
                    this.pen = Color.decode("#cdc1b4");
            // if the value is 2 or 4, use a darker color
            case 2, 4 -> this.pen = Color.decode("#776e65");
            default ->// otherwise use a lighter color
                    this.pen = Color.decode("#ffffff");
        }
    }

    // paints the tile to a certain graphics with the provided x and y position
    public void paint(Graphics graphics, int x, int y, Font font) {
        // retrieve font metrics
        FontMetrics metrics = graphics.getFontMetrics(font);

        // paint background
        graphics.setColor(this.color);
        graphics.fillRoundRect(x, y, Tile.WIDTH, Tile.HEIGHT, 10, 10);

        // add text
        graphics.setColor(this.pen);
        String text = String.valueOf(this.val);
        int rectx = x + (Tile.WIDTH - metrics.stringWidth(text)) / 2;
        int recty = y + ((Tile.HEIGHT - metrics.getHeight()) / 2) + metrics.getAscent();
        graphics.drawString(text, rectx, recty);
    }
}
