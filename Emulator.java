import java.io.IOException;

import javax.swing.JFrame;

class Emulator {
    //scale for screen
    private int scale = 20;

    //emulator componenets
    private Screen screen;
    private Keyboard keypad;
    private CPU processingUnit;

    //refresh rate components
    private double cpuFrequency;
    private double ns;
    
    private long then;
    private long now;
    private double delta;


    private String rom;

    Emulator(double cpuhz, String rom) throws IOException {
        this.cpuFrequency = cpuhz;
        this.ns = 1000000000.0 / cpuFrequency;

        this.rom = rom;
        
        initializeComponents();
    }

    //method to initialize the emulator components
    private void initializeComponents() throws IOException {
        this.screen = new Screen(scale);
        this.keypad = new Keyboard();
        this.processingUnit = new CPU(keypad, screen);

        createGUI();

        this.processingUnit.loadFontsetInMemory();

        this.processingUnit.loadRomIntoMemory(this.rom);
    }

    //method to create the window for my screen
    private void createGUI() {
        JFrame window = new JFrame("Chip-8");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(this.screen.getScreenWidth(), this.screen.getScreenHeight());
        window.setResizable(false);

        window.add(this.screen);
        window.addKeyListener(this.keypad);
        window.pack();
        window.setVisible(true);
    }

    //method to control the refresh rate of my emulator
    public void startEmulatorLoop() {
        this.then = System.nanoTime();

        while(true) {
            this.now = System.nanoTime();
            this.delta += (this.now - this.then) / this.ns;
            this.then = this.now;

            if(this.delta >= 1) {
                this.processingUnit.emulateCycle();
                delta--;
            }
        }
    }

}
