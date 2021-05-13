import java.io.IOException;

import javax.swing.JFrame;

class Emulator {
    //scale for screen
    private int scale = 10;

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

    Emulator(double cpuhz, String rom) throws IOException {
        this.cpuFrequency = cpuhz;
        this.ns = 1000000000.0 / cpuFrequency;
        
        initializeComponents(rom);
    }

    //method to initialize the emulator components
    private void initializeComponents(String romName) throws IOException {
        screen = new Screen(scale);
        keypad = new Keyboard();
        processingUnit = new CPU(keypad, screen);

        createGUI();

        processingUnit.loadFontsetInMemory();

        processingUnit.loadRomIntoMemory(romName);
    }

    //method to create the window for my screen
    private void createGUI() {
        JFrame window = new JFrame("Chip-8");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        window.add(screen);
        window.pack();
        window.setVisible(true);
    }

    //method to emulate one cpu cycle, could be useless
    private void cycle() {

    }

    //method to control the refresh rate of my emulator
    public void emulatorLoop() {
        this.then = System.nanoTime();

        while(true) {
            this.now = System.nanoTime();
            this.delta += (this.now - this.then) / this.ns;
            this.then = this.now;

            if(this.delta >= 1) {
                processingUnit.emulateCycle();
                delta--;
            }
        }
    }
}
