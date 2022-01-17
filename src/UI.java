// UI Class: manages a GUI and interactions with the user
// Author: Stefan Barna
// Version: 2022/01/06

// Title
// Description
// Steps

// TODO: remove option to resize
// TODO: status bar

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;
import javax.swing.*;
import java.awt.Graphics;
import javax.swing.JPanel;

import java.awt.geom.RoundRectangle2D;

public class UI extends JPanel implements ActionListener {
    public static final int WIDTH = 520;    // width of the UI
    public static final int HEIGHT = 800;   // height of the UI
    private final int ANIMOD = 3;              // distance moved per timer count

    private Grid g;         // 2048 grid
    private Timer time;     // timer object
    private boolean timerOn;// true of the timer is running
    private int counter;    // timer counter

    // direction velocity controls
    int xvel,
        yvel;

    public UI() {
        // set focus
        setFocusable(true);
        requestFocusInWindow();

        // set timer
        this.time = new Timer(10, this);
        this.time.stop();
        this.timerOn = false;
        this.counter = 0;

        // detect key presses
        addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {}

            public void keyPressed(KeyEvent e) {
                // check if a current move is in play
                if (!timerOn) {
                    time.start();
                    timerOn = true;

                    // set message appropriate to the arrow clicked
                    String message;
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_UP -> {
                            message = "UP";
                            xvel = 0;
                            yvel = -1;
                        }
                        case KeyEvent.VK_DOWN -> {
                            message = "DOWN";
                            xvel = 0;
                            yvel = 1;
                        }
                        case KeyEvent.VK_LEFT -> {
                            message = "LEFT";
                            xvel = -1;
                            yvel = 0;
                        }
                        case KeyEvent.VK_RIGHT -> {
                            message = "RIGHT";
                            xvel = 1;
                            yvel = 0;
                        }
                        default -> {
                            message = "";
                            xvel = 0;
                            yvel = 0;
                        }
                    }

                    // if the player has made a viable turn
                    if (g.turn(message)) {
                        g.generateTile();
                        repaint();

                        // check game over conditions
                        if (!g.hasEmpty() && !g.hasMerge()) {
                            System.out.println("Game Over");
                            System.out.println("Final Score: " + g.getScore());
                        }
                    }
                }
            }

            public void keyReleased(KeyEvent e) {}
        });

        g = new Grid();
    }

    // checks for performed actions (for animation)
    public void actionPerformed(ActionEvent e) {
        this.counter++;
        repaint();
        // when the timer should close
        if (this.counter > 5) {
            time.stop();
            timerOn = false;
            counter = 0;
            xvel = 0;
            yvel = 0;

            // cleanup after animation
            g.resetTileStatus();
        }
    }

    // saves grid information to a save file
    public void save() {
        // save the high score to a file
        try {
            FileWriter file = new FileWriter("save.txt");
            file.write(String.valueOf(this.g.getHighscore()));
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // load grid information from save file
    public void load() {
        try {
            File file = new File("save.txt");
            Scanner reader = new Scanner(file);
            this.g.setHighscore(Integer.parseInt(reader.nextLine()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void paint(Graphics graphics) {
        super.paint(graphics);

        // check if Graphics is convertible to Graphics2D
        if (graphics instanceof Graphics2D g2D) {
            // enable antialiasing
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // enable custom font
            try {
                Font f = Font.createFont(Font.TRUETYPE_FONT, new File("ClearSans-Bold.ttf")).deriveFont(80f);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                //register the font
                ge.registerFont(f);
                g2D.setFont(f);

                // paint title
                g2D.setColor(Color.decode("#776e65"));
                g2D.drawString("2048", 20, 100);

                // create score and high score boxes
                g2D.setColor(Color.decode("#bbada0"));
                g2D.fillRoundRect(245, 20, 120, 50, 5, 5);
                g2D.fillRoundRect(370, 20, 120, 50, 5, 5);

                // print score and high score headers
                f = f.deriveFont(13f);
                g2D.setFont(f);
                g2D.setColor(Color.decode("#ebded1"));
                FontMetrics metrics = graphics.getFontMetrics(f);

                // center text
                int x = 245 + (120 - metrics.stringWidth("SCORE")) / 2;
                int y = 20 + ((25 - metrics.getHeight()) / 2) + metrics.getAscent();
                graphics.drawString("SCORE", x, y);
                x = 370 + (120 - metrics.stringWidth("BEST")) / 2;
                y = 20 + ((25 - metrics.getHeight()) / 2) + metrics.getAscent();
                graphics.drawString("BEST", x, y);

                // display score
                f = f.deriveFont(25f);
                g2D.setFont(f);
                g2D.setColor(Color.decode("#ffffff"));
                metrics = graphics.getFontMetrics(f);

                x = 245 + (120 - metrics.stringWidth(String.valueOf(this.g.getScore()))) / 2;
                y = 40 + ((25 - metrics.getHeight()) / 2) + metrics.getAscent();
                graphics.drawString(String.valueOf(this.g.getScore()), x, y);

                // display high score
                x = 370 + (120 - metrics.stringWidth(String.valueOf(this.g.getHighscore()))) / 2;
                y = 40 + ((25 - metrics.getHeight()) / 2) + metrics.getAscent();
                graphics.drawString(String.valueOf(this.g.getHighscore()), x, y);

                // display new game button
                g2D.setColor(Color.decode("#8f7a66"));
                g2D.fillRoundRect(370, 80, 120, 40, 5, 5);

                // draw the grid
                this.g.paint(g2D, f, 15 - (this.counter * this.ANIMOD), this.xvel, this.yvel);
            } catch (IOException|FontFormatException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        // GUI setup
        UI gui = new UI();
        JFrame frame = new JFrame("2048");                 // create jframe
        frame.add(gui);                                         // add gui to frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   // set method of exit
        frame.setSize(UI.WIDTH, UI.HEIGHT);                     // set dimensions
        gui.setBackground(Color.decode("#faf8ef"));             // set frame background color
        frame.setVisible(true);                                 // set frame to visible

        // set the window icon
        ImageIcon icon = new ImageIcon("2048.png");
        frame.setIconImage(icon.getImage());

        // receive the high score from the save file
        gui.load();

        // draw the GUI
        gui.repaint();

        // when the gui closes
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent event) {
                gui.save();

                // exit the program
                System.exit(0);
            }
        });
    }
}
