import java.io.File;
import java.nio.file.Files;

class CPU
{
    //things I will need later
    private Keyboard keyboard;
    private Graphics renderer;
    private Speaker speaker;

    //variable to store the memory
    private byte[] memory;

    //8-bit registers reffered to as v in technical reference
    private byte[] v;

    //chip 8 has one 16-bit register called i
    private short i;

    //chip 8 has two timers. Timers decrement towards 0 at a rate of 60 hz
    private int delayTimer;
    private int soundTimer;

    //program counter used to store the currently executing address
    private short PC;

    //short array for stack
    private short[] stack;
    
    private boolean isPaused;

    //variable to control the speed of the emulation cycle
    private int speed;

    CPU(Keyboard keyboard, Graphics renderer, Speaker speaker)
    {
        keyboard = keyboard;
        renderer = renderer;
        speaker = speaker;

        //chip 8 has 4096 bytes of memory
        memory = new byte[4096];

        //chip 8 has 16 8-bit registers
        v = new byte[16];

        //0 at initialization
        i = 0;

        delayTimer = 0;
        soundTimer = 0;

        //address that programs are expected to be loaded at
        PC = 0x200;

        //array of 16 16-bit values for stack
        stack = new short[16];

        //false on initialization
        isPaused = false;

        //im leaving this at 10 for now, but will change it once i find a suitable speed
        speed = 10;
    }

    //method to load the rom into memory
    private void loadRomIntoMemory(String romName)
    {
        //accesses the rom file and puts all bytes into a byte array
        File rom = new File("roms/" + romName);
        byte[] romBytes = Files.readAllBytes(rom.toPath());

        //for loop to read all bytes of rom and store it in memory starting at address 0x200
        for(int i = 0; i < romBytes.length; i++)
        {
            memory[0x200 + i] = romBytes[i];
        }
    }

    //method to load the sprites into memory
    private void loadFontsetInMemory()
    {
        //binary representation of hex characters 0-f
        byte[] fontset = {
            (byte) 0xF0, (byte) 0x90, (byte) 0x90, (byte) 0x90, (byte) 0xF0, // 0
            (byte) 0x20, (byte) 0x60, (byte) 0x20, (byte) 0x20, (byte) 0x70, // 1
            (byte) 0xF0, (byte) 0x10, (byte) 0xF0, (byte) 0x80, (byte) 0xF0, // 2
            (byte) 0xF0, (byte) 0x10, (byte) 0xF0, (byte) 0x10, (byte) 0xF0, // 3
            (byte) 0x90, (byte) 0x90, (byte) 0xF0, (byte) 0x10, (byte) 0x10, // 4
            (byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x10, (byte) 0xF0, // 5
            (byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x90, (byte) 0xF0, // 6
            (byte) 0xF0, (byte) 0x10, (byte) 0x20, (byte) 0x40, (byte) 0x40, // 7
            (byte) 0xF0, (byte) 0x90, (byte) 0xF0, (byte) 0x90, (byte) 0xF0, // 8
            (byte) 0xF0, (byte) 0x90, (byte) 0xF0, (byte) 0x10, (byte) 0xF0, // 9
            (byte) 0xF0, (byte) 0x90, (byte) 0xF0, (byte) 0x90, (byte) 0x90, // A
            (byte) 0xE0, (byte) 0x90, (byte) 0xE0, (byte) 0x90, (byte) 0xE0, // B
            (byte) 0xF0, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0xF0, // C
            (byte) 0xE0, (byte) 0x90, (byte) 0x90, (byte) 0x90, (byte) 0xE0, // D
            (byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x80, (byte) 0xF0, // E
            (byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x80, (byte) 0x80  // F
        };

        //loading sprites into memory at hex address 0x000
        for(int i = 0; i < fontset.length; i++)
        {
            memory[i] = fontset[i];
        }
    }

    //method to update the timers
    private void updateTimers()
    {
        if(delayTimer > 0)
        {
            delayTimer -= 1;
        }

        if(soundTimer > 0)
        {
            soundTimer -= 1;
        }
    }

    //method for the cpu cycle
    private void emulateCycle()
    {
        //loop that will control the speed of execution
        for(int i = 0; i < speed; i++)
        {
            //execution will stop if the game is stopped
            if(!isPaused)
            {
                //bit manipulation to get full opcode, since they are 2 bytes each.
                //Retrieves first bit from memory and shifts it 8 bits, then bitwise or with next bit in memory
                //after opcode is retrieved, the instruction is executed
                int opcode = (memory[PC] << 8 | memory[PC + 1]);
                executeOpcode(opcode);
            }
        }

        //timers are updated while the game is not paused
        if(!isPaused)
        {
            updateTimers();
        }

        //methods to play sound and draw graphics will go here


    }

    //method to execute each opcode
    private void executeOpcode(int opcode)
    {

    }
}