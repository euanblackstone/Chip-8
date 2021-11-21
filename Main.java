import java.io.IOException;

public class Main {
    
    public static void main(String[] args) throws IOException {
        double cpuhz = 60.0;
        String rom = args[0].toUpperCase();
        
        try {
            Emulator chip8 = new Emulator(cpuhz, rom);
            chip8.startEmulatorLoop();
        } catch(IOException ex) {
            System.out.println("Rom unknown. Please use a rom from rom folder.");
            System.exit(0);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
