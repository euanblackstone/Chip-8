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

        //will change later
        PC = 0;

        //array of 16 16-bit values for stack
        stack = new short[16];

        //false on initialization
        isPaused = false;
    }
}