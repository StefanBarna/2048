// UI Class: manages a GUI and interactions with the user
// Author: Stefan Barna
// Version: 2022/01/06

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;
import javax.swing.*;
import java.awt.Graphics;
import javax.swing.JPanel;

public class UI extends JPanel {
    public static final int WIDTH = 520;    // width of the UI
    public static final int HEIGHT = 780;   // height of the UI
    private final int ANIMOD = 3;           // distance moved per timer count

    private final Grid g;       // 2048 grid
    private final Timer time;   // timer object
    private boolean timerOn;    // true of the timer is running
    private int counter;        // timer counter

    // ui buttons
    public JButton reset;       // restart button
    public JButton playAgain;   // restart button (exclusive to the win or loss screen)
    public JButton resume;      // continue button (exclusive to the win screen)

    // direction velocity controls
    int xvel,
        yvel;

    public UI() {
        // set focus
        setFocusable(true);
        requestFocusInWindow();

        // set timer and check for timer related actions performed
        this.time = new Timer(15, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                // check if the action comes from the timer
                counter++;
                repaint();
                // when the timer should close
                if (counter > 5) {
                    time.stop();
                    timerOn = false;
                    counter = 0;
                    xvel = 0;
                    yvel = 0;

                    // cleanup after animation
                    g.resetTileStatus();
                }
            }
        });
        this.time.stop();
        this.timerOn = false;
        this.counter = 0;

        // detect key presses
        addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {}

            public void keyPressed(KeyEvent e) {
                // check if a current move is in play
                if (!g.gameOver() && !g.gameWon()) {
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

                    // check if a viable button was clicked
                    if (!message.equals("")) {
                        g.turn(message);
                        // if the player has made a viable turn
                        if (g.turnMade()) {
                            g.generateTile();
                            repaint();

                            // check for loss
                            if (g.gameOver())
                                playAgain.setEnabled(true);
                            if (g.gameWon()) {
                                playAgain.setEnabled(true);
                                resume.setEnabled(true);
                            }
                        }
                    }
                }
            }

            public void keyReleased(KeyEvent e) {}
        });

        g = new Grid();
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
                // create a new true type font
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

                // display new game button (cover)
                g2D.setColor(Color.decode("#8f7a66"));
                g2D.fillRoundRect(370, 80, 120, 40, 5, 5);

                // print new game text
                f = f.deriveFont(18f);
                g2D.setFont(f);
                g2D.setColor(Color.decode("#ffffff"));
                metrics = graphics.getFontMetrics(f);
                x = 370 + (120 - metrics.stringWidth("New Game")) / 2;
                y = 80 + ((40 - metrics.getHeight()) / 2) + metrics.getAscent();
                graphics.drawString( "New Game", x, y);

                // draw the grid
                this.g.paint(g2D, f, 15 - (this.counter * this.ANIMOD), this.xvel, this.yvel);

                // paint description (header)
                f = f.deriveFont(18f);
                g2D.setFont(f);
                g2D.setColor(Color.decode("#776e65"));
                x = 20;
                y = 635 + metrics.getAscent();
                graphics.drawString("HOW TO PLAY: ", x, y);

                // paint description (body 1)
                x += metrics.stringWidth("HOW TO PLAY: ");
                f = Font.createFont(Font.TRUETYPE_FONT, new File("ClearSans-Regular.ttf")).deriveFont(17f); // register a new font
                ge.registerFont(f);
                g2D.setFont(f);
                metrics = graphics.getFontMetrics(f);
                graphics.drawString("Use your arrow keys to move the tiles. Tiles", x, y);

                // paint description (body 2)
                x = 20;
                y += (metrics.getHeight() / 2) + metrics.getAscent();
                graphics.drawString("with the same number merge into one when they touch. Add", x, y);

                // paint description (body 3)
                y += (metrics.getHeight() / 2) + metrics.getAscent();
                graphics.drawString("them up to reach 2048!", x, y);

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
        frame.setResizable(false);

        // set layout to absolute
        frame.getContentPane().setLayout(null);

        // create a button
        gui.reset = new JButton("New Game");
        gui.reset.setBounds(370, 80, 120, 40);
        gui.reset.setOpaque(false);                                 // set the frame to transparent
        gui.reset.setContentAreaFilled(false);
        gui.reset.setBorderPainted(false);
        gui.reset.setFocusable(false);
        gui.reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gui.g.restart();
                gui.repaint();
                gui.g.setWon(false);
                // set buttons back to disabled
                gui.playAgain.setEnabled(false);
                gui.resume.setEnabled(false);
            }
        });
        frame.add(gui.reset);

        gui.playAgain = new JButton("Play again");
        gui.playAgain.setBounds(190, 400, 120, 40);
        gui.playAgain.setOpaque(false);
        gui.playAgain.setContentAreaFilled(false);
        gui.playAgain.setBorderPainted(false);
        gui.playAgain.setFocusable(false);
        gui.playAgain.setEnabled(true);
        gui.playAgain.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                gui.g.restart();
                gui.repaint();
                gui.g.setWon(false);
                // set buttons back to disabled
                gui.playAgain.setEnabled(false);
                gui.resume.setEnabled(false);
            }
        });
        frame.add(gui.playAgain);

        gui.resume = new JButton("Continue");
        gui.resume.setBounds(190, 445, 120, 40);
        gui.resume.setOpaque(false);
        gui.resume.setContentAreaFilled(false);
        gui.resume.setBorderPainted(false);
        gui.resume.setFocusable(false);
        gui.playAgain.setEnabled(true);
        gui.resume.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                gui.g.setWon(true);
                // set buttons back to disabled
                gui.playAgain.setEnabled(false);
                gui.resume.setEnabled(false);
                gui.repaint();
            }
        });
        frame.add(gui.resume);

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
