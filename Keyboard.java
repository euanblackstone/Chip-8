import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.util.Map;
import static java.util.Map.entry;

class Keyboard extends KeyAdapter
{
    //variable to store the current key being pressed
    private int currentKeyPressed;

    //key map to map qwerty keyboard keys to chip8's hexadecimal keyboard
    Map<Integer, Integer> keyMap = Map.ofEntries(
        entry(0x1, KeyEvent.VK_1),
        entry(0x2, KeyEvent.VK_2),
        entry(0x3, KeyEvent.VK_3),
        entry(0xC, KeyEvent.VK_4),
        entry(0x4, KeyEvent.VK_Q),
        entry(0x5, KeyEvent.VK_W),
        entry(0x6, KeyEvent.VK_E),
        entry(0xD, KeyEvent.VK_R),
        entry(0x7, KeyEvent.VK_A),
        entry(0x8, KeyEvent.VK_S),
        entry(0x9, KeyEvent.VK_D),
        entry(0xE, KeyEvent.VK_F),
        entry(0xA, KeyEvent.VK_Z),
        entry(0x0, KeyEvent.VK_X),
        entry(0xB, KeyEvent.VK_C),
        entry(0xF, KeyEvent.VK_V)
    );

    //key to escape out of the emulator
    private static final int QUIT_KEY = KeyEvent.VK_ESCAPE;

    public Keyboard()
    {
        //initializes currentKeyPressed to 0
        currentKeyPressed = 0;
    }

    //method that writes the key being pressed to the current key pressed variable
    @Override
    public void keyPressed(KeyEvent e)
    {
        currentKeyPressed = keyMap.get(e.getKeyCode());
    }

    //method that clears the current key being pressed if released
    @Override
    public void keyReleased(KeyEvent e)
    {
        currentKeyPressed = 0;
    }

    //returns current key being pressed
    public int getCurrentKey()
    {
        return currentKeyPressed;
    }
}
