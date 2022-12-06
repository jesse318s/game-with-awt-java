import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Coin {

    // image that represents the coin's position on the board
    private BufferedImage image;
    // current position of the coin on the board grid
    private Point pos;
    // make a special coin that looks different and is worth more points
    private boolean isSpecial = false;

    public Coin(int x, int y, boolean coinSpecial) {
        // make a special coin that looks different and is worth more points
        isSpecial = coinSpecial;

        // load the assets
        loadImage();

        // initialize the state
        pos = new Point(x, y);
    }

    private void loadImage() {
        try {
            // you can use just the filename if the image file is in your
            // project folder, otherwise you need to provide the file path.
            image = ImageIO.read(new File("images/coin.png"));
            // make a special coin that looks different and is worth more points
            if (isSpecial) {
                image = ImageIO.read(new File("images/specialCoin.png"));
            }
        } catch (IOException exc) {
            System.out.println("Error opening image file: " + exc.getMessage());
        }
    }

    public void draw(Graphics g, ImageObserver observer) {
        // with the Point class, note that pos.getX() returns a double, but
        // pos.x reliably returns an int. https://stackoverflow.com/a/30220114/4655368
        // this is also where we translate board grid position into a canvas pixel
        // position by multiplying by the tile size.
        g.drawImage(
                image,
                pos.x * Board.TILE_SIZE,
                pos.y * Board.TILE_SIZE,
                observer);
    }

    public Point getPos() {
        return pos;
    }

    public boolean getIsSpecial() {
        return isSpecial;
    }

}
