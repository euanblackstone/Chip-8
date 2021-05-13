import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

class Screen extends JPanel {
    //original screen of the chip 8
    private final int SCREEN_WIDTH = 64;
    private final int SCREEN_HEIGHT = 32;

    //variables to store the scaled up screen width and height
    private int screenRows;
    private int screenCol;

    //2d array to represent the pixels on the screen
    private boolean[][] pixels;

    private Graphics g;

    private int scale;

    Screen(int scaleFactor) {
        screenRows = SCREEN_HEIGHT * scaleFactor;
        screenCol = SCREEN_WIDTH * scaleFactor;

        pixels = new boolean[SCREEN_WIDTH][SCREEN_HEIGHT];

        this.scale = scaleFactor;
    }

    //toggles the pixel on or off at coordinates x,y
    public boolean togglePixel(int x, int y) {
        if(x > this.SCREEN_WIDTH) {
            x -= this.SCREEN_WIDTH;
        } else if(x < 0) {
            x += this.SCREEN_WIDTH;
        }

        if(y > this.SCREEN_HEIGHT) {
            y -= this.SCREEN_HEIGHT;
        } else if(y < 0) {
            y += this.SCREEN_HEIGHT;
        }

        this.pixels[x][y] ^= true;

        return !this.pixels[x][y];
    }

    //method to repaint the screen, will be run at 60hz
    private void render() {
        for(int y = 0; y < this.SCREEN_HEIGHT; y++) {
            for(int x = 0; x < this.SCREEN_WIDTH; x++) {
                if(this.pixels[x][y]) {
                    g.setColor(Color.WHITE);
                } else {
                    g.setColor(Color.BLACK);
                }
                g.fillRect(x*scale, y*scale, scale, scale);
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.g = g;

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, this.screenCol, this.screenRows);

        render();
    }
}
