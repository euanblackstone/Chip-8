import java.io.IOException;

public class Main {
    
    public static void main(String[] args) throws IOException {
        double cpuhz = 60.0;
        String rom = "INVADERS";
        
        try {
            Emulator chip8 = new Emulator(cpuhz, rom);
            chip8.startEmulatorLoop();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
