import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Board extends JPanel implements ActionListener, KeyListener {

    // controls the delay between each tick in ms
    private final int DELAY = 25;
    // controls the size of the board
    public static final int TILE_SIZE = 50;
    public static final int ROWS = 15; // change the dimensions of the game board
    public static final int COLUMNS = 20; // change the dimensions of the game board
    // controls how many coins appear on the board
    public static final int NUM_COINS = 5;
    // suppress serialization warning
    private static final long serialVersionUID = 490905409104883233L;

    // keep a reference to the timer object that triggers actionPerformed() in
    // case we need access to it in another method
    private Timer timer;
    // objects that appear on the game board
    private Player player;
    private static ArrayList<Coin> coins; // think about other keyboard keys that might perform some other action
    private static Mine mine; // add an object type that reduces your score when touched. Or maybe it ends the
                              // game
    // add a game clock
    private static int delaysTicked = 0;

    public Board() {
        // set the game board size
        setPreferredSize(new Dimension(TILE_SIZE * COLUMNS, TILE_SIZE * ROWS));
        // set the game board background color
        setBackground(new Color(66, 135, 245)); // change the colors that are being used

        // initialize the game state
        player = new Player();
        coins = populateCoins();
        mine = createMine(); // add an object type that reduces your score when touched. Or maybe it ends the
        // game

        // this timer will call the actionPerformed() method every DELAY ms
        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // this method is called by the timer every DELAY ms.
        // use this space to update the state of your game or animation
        // before the graphics are redrawn.

        // prevent the player from disappearing off the board
        player.tick();

        // give the player points for collecting coins
        collectCoins();
        // add an object type that reduces your score when touched. Or maybe it ends the
        // game
        triggerMine();

        // calling repaint() will trigger paintComponent() to run again,
        // which will refresh/redraw the graphics.
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // when calling g.drawImage() we can use "this" for the ImageObserver
        // because Component implements the ImageObserver interface, and JPanel
        // extends from Component. So "this" Board instance, as a Component, can
        // react to imageUpdate() events triggered by g.drawImage()

        // draw our graphics.
        drawBackground(g);
        drawScore(g);
        // add a game clock
        drawClock(g);
        // add more scoreboard/HUD elements
        drawCoinCounter(g);
        drawSpecialCounter(g);
        mine.draw(g, this); // add an object type that reduces your score when touched. Or maybe it ends the
        // game
        for (Coin coin : coins) {
            coin.draw(g, this);
        }
        player.draw(g, this);

        // this smooths out animations on some systems
        Toolkit.getDefaultToolkit().sync();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // this is not used but must be defined as part of the KeyListener interface
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // react to key down events
        player.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // react to key up events
    }

    private void drawBackground(Graphics g) {
        // draw a checkered background
        g.setColor(new Color(178, 116, 194)); // change the colors that are being used
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                // only color every other tile
                if ((row + col) % 2 == 1) {
                    // draw a square tile at the current row/column position
                    g.fillRect(
                            col * TILE_SIZE,
                            row * TILE_SIZE,
                            TILE_SIZE,
                            TILE_SIZE);
                }
            }
        }
    }

    private void drawScore(Graphics g) {
        // set the text to be displayed
        String text = "$" + player.getScore();
        // we need to cast the Graphics to Graphics2D to draw nicer text
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(
                RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        // set the text color and font
        g2d.setColor(new Color(4, 0, 255)); // change the colors that are being used
        g2d.setFont(new Font("Lato", Font.BOLD, 25));
        // draw the score in the bottom center of the screen
        // https://stackoverflow.com/a/27740330/4655368
        FontMetrics metrics = g2d.getFontMetrics(g2d.getFont());
        // the text will be contained within this rectangle.
        // here I've sized it to be the entire bottom row of board tiles
        Rectangle rect = new Rectangle(0, TILE_SIZE * (ROWS - 1), TILE_SIZE * COLUMNS, TILE_SIZE);
        // determine the x coordinate for the text
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        // determine the y coordinate for the text
        // (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        // draw the string
        g2d.drawString(text, x, y);
    }

    // add a game clock
    private void drawClock(Graphics g) {
        delaysTicked += timer.getDelay();
        // set the text to be displayed
        String text = "Game Clock | " + (delaysTicked / 1000);
        // we need to cast the Graphics to Graphics2D to draw nicer text
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(
                RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        // set the text color and font
        g2d.setColor(new Color(4, 0, 255)); // change the colors that are being used
        g2d.setFont(new Font("Lato", Font.BOLD, 25));
        FontMetrics metrics = g2d.getFontMetrics(g2d.getFont());
        // determine the x coordinate for the text
        int x = TILE_SIZE + 5;
        // determine the y coordinate for the text
        int y = metrics.getHeight();
        // draw the string
        g2d.drawString(text, x, y);
    }

    // add more scoreboard/HUD elements
    private void drawCoinCounter(Graphics g) {
        // set the text to be displayed
        String text = "Coins uncollected: " + coins.size();
        // we need to cast the Graphics to Graphics2D to draw nicer text
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(
                RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        // set the text color and font
        g2d.setColor(new Color(4, 0, 255)); // change the colors that are being used
        g2d.setFont(new Font("Lato", Font.BOLD, 25));
        FontMetrics metrics = g2d.getFontMetrics(g2d.getFont());
        // the text will be contained within this rectangle.
        // here I've sized it to be the entire bottom row of board tiles
        Rectangle rect = new Rectangle(0, TILE_SIZE * (ROWS - 1), TILE_SIZE * COLUMNS, TILE_SIZE);
        // determine the x coordinate for the text
        int x = 5;
        // determine the y coordinate for the text
        // (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        // draw the string
        g2d.drawString(text, x, y);
    }

    // add more scoreboard/HUD elements
    private void drawSpecialCounter(Graphics g) {
        boolean isSpecialCollected = true;

        for (Coin coin : coins) {
            if (coin.getIsSpecial() == true) {
                isSpecialCollected = false;
            }
        }
        // set the text to be displayed
        String text = "Special collected: " + isSpecialCollected;
        // we need to cast the Graphics to Graphics2D to draw nicer text
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(
                RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        // set the text color and font
        g2d.setColor(new Color(4, 0, 255)); // change the colors that are being used
        g2d.setFont(new Font("Lato", Font.BOLD, 25));
        FontMetrics metrics = g2d.getFontMetrics(g2d.getFont());
        // the text will be contained within this rectangle.
        // here I've sized it to be the entire bottom row of board tiles
        Rectangle rect = new Rectangle(0, TILE_SIZE * (ROWS - 1), TILE_SIZE * COLUMNS, TILE_SIZE);
        // determine the x coordinate for the text
        int x = rect.x + (rect.width - metrics.stringWidth(text)) - 5;
        // determine the y coordinate for the text
        // (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        // draw the string
        g2d.drawString(text, x, y);
    }

    private static ArrayList<Coin> populateCoins() { // think about other keyboard keys that might perform some other
                                                     // action
        ArrayList<Coin> coinList = new ArrayList<>();
        Random rand = new Random();

        // create the given number of coins in random positions on the board.
        // note that there is not check here to prevent two coins from occupying the
        // same
        // spot, nor to prevent coins from spawning in the same spot as the player
        for (int i = 0; i < NUM_COINS; i++) {
            int coinX = rand.nextInt(COLUMNS);
            int coinY = rand.nextInt(ROWS);
            // make a special coin that looks different and is worth more points
            boolean coinSpecial = false;
            if (i == 0) {
                coinSpecial = true;
            }
            coinList.add(new Coin(coinX, coinY, coinSpecial));
        }

        return coinList;
    }

    private void collectCoins() {
        // end or restart the game when all coins are collected, or when a certain score
        // is reached
        if (coins.size() == 0) {
            JOptionPane.showMessageDialog(null,
                    "Congrats! You've collected all of the Bitcoins.");
            System.exit(0);
        }
        // allow player to pickup coins
        ArrayList<Coin> collectedCoins = new ArrayList<>();
        for (Coin coin : coins) {
            // if the player is on the same tile as a coin, collect it
            if (player.getPos().equals(coin.getPos())) {
                // give the player some points for picking this up
                // make a special coin that looks different and is worth more points
                if (coin.getIsSpecial()) {
                    player.addScore(200);
                } else {
                    player.addScore(150); // change how many points you get per coin
                }
                collectedCoins.add(coin);
            }
        }
        // remove collected coins from the board
        coins.removeAll(collectedCoins);
    }

    // add an object type that reduces your score when touched. Or maybe it ends the
    // game
    private static Mine createMine() {
        Mine newMine;
        Random rand = new Random();
        int mineX = rand.nextInt(COLUMNS);
        int mineY = rand.nextInt(ROWS);

        newMine = new Mine(mineX, mineY);

        return newMine;
    }

    // add an object type that reduces your score when touched. Or maybe it ends the
    // game
    private void triggerMine() {
        if (player.getPos().equals(mine.getPos())) {
            JOptionPane.showMessageDialog(null,
                    "You triggered the mine. Game Over!");
            System.exit(0);
        }
    }

    // think about other keyboard keys that might perform some other action
    public static void reset() {
        coins = populateCoins();
        mine = createMine();
        delaysTicked = 0;
    }

}
