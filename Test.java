import java.io.IOException;

public class Test {
    
    public static void main(String[] args) throws IOException {
        double cpuhz = 60.0;
        String rom = "BLITZ";
        
        try {
            Emulator chip8 = new Emulator(cpuhz, rom);
            chip8.startTestEmulatorLoop();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
