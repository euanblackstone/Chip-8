import java.awt.Color;
import java.awt.Graphics;

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
        // if(x >= this.SCREEN_WIDTH) {
        //     x = x % this.SCREEN_WIDTH;
        // } else if(x < 0) {
        //     x = x % this.SCREEN_WIDTH;
        // }

        // if(y >= this.SCREEN_HEIGHT) {
        //     y = y % this.SCREEN_HEIGHT;
        // } else if(y < 0) {
        //     y = y % this.SCREEN_HEIGHT;
        // }

        x = x & 0xFF;
        y = y & 0xFF;

        if(x >= this.SCREEN_WIDTH) {
            x = x % this.SCREEN_WIDTH;
        }

        if(y >= this.SCREEN_HEIGHT) {
            y = y % this.SCREEN_HEIGHT;
        }

        this.pixels[x][y] ^= true;

        return !this.pixels[x][y];
    }

    public void clear() {
        this.pixels = new boolean[SCREEN_WIDTH][SCREEN_HEIGHT];
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

    public void repaintScreen() {
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.g = g;

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, this.screenCol, this.screenRows);

        render();
    }

    public int getScreenWidth() {
        return this.screenCol;
    }

    public int getScreenHeight() {
        return this.screenRows;
    }

    public static void main(String[] args) {
        Screen screen = new Screen(20);
        JFrame window = new JFrame("Chip-8");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        window.add(screen);
        window.pack();
        window.setVisible(true);

        screen.togglePixel(5, 3);
        screen.togglePixel(8, 12);

        screen.repaintScreen();

        screen.togglePixel(1, 3);
        screen.togglePixel(5, 3);
    }
}
