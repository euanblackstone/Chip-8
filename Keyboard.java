import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Map;
import static java.util.Map.entry;

class Keyboard extends KeyAdapter
{
    //variable to store the current key being pressed
    private int currentKeyPressed;

    //key map to map qwerty keyboard keys to chip8's hexadecimal keyboard
    private Map<Integer, Integer> keyMap = Map.ofEntries(
        entry(KeyEvent.VK_1, 0x1),
        entry(KeyEvent.VK_2, 0x2),
        entry(KeyEvent.VK_3, 0x3),
        entry(KeyEvent.VK_4, 0xc),
        entry(KeyEvent.VK_Q, 0x4),
        entry(KeyEvent.VK_W, 0x5),
        entry(KeyEvent.VK_E, 0x6),
        entry(KeyEvent.VK_R, 0xD),
        entry(KeyEvent.VK_A, 0x7),
        entry(KeyEvent.VK_S, 0x8),
        entry(KeyEvent.VK_D, 0x9),
        entry(KeyEvent.VK_F, 0xE),
        entry(KeyEvent.VK_Z, 0xA),
        entry(KeyEvent.VK_X, 0x0),
        entry(KeyEvent.VK_C, 0xB),
        entry(KeyEvent.VK_V, 0xF)
    ); 

    //key to escape out of the emulator
    private static final int QUIT_KEY = KeyEvent.VK_ESCAPE;

    public Keyboard() {
        //initializes currentKeyPressed to 0
        currentKeyPressed = 0;
    }

    //method that writes the key being pressed to the current key pressed variable
    @Override
    public void keyPressed(KeyEvent e) {
        try{
            currentKeyPressed = keyMap.get(e.getKeyCode());
        } catch( Exception ex) {
            System.out.println("Key unknown");
        }
    }

    //method that clears the current key being pressed if released
    @Override
    public void keyReleased(KeyEvent e) {
        currentKeyPressed = 0;
    }

    //returns current key being pressed
    public int getCurrentKey() {
        return currentKeyPressed;
    }

}
