import java.io.File;
import java.nio.file.Files;
import java.util.ArrayDeque;

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
    private ArrayDeque<Short> stack;
    
    private boolean isPaused;

    //variable to control the speed of the emulation cycle
    private int speed;

    CPU(Keyboard keyboard, Graphics renderer, Speaker speaker)
    {
        this.keyboard = keyboard;
        this.renderer = renderer;
        this.speaker = speaker;

        //chip 8 has 4096 bytes of memory
        this.memory = new byte[4096];

        //chip 8 has 16 8-bit registers
        this.v = new byte[16];

        //0 at initialization
        this.i = 0;

        this.delayTimer = 0;
        this.soundTimer = 0;

        //address that programs are expected to be loaded at
        this.PC = 0x200;

        //array of 16 16-bit values for stack
        //stack = new short[16];
        this.stack = new ArrayDeque<Short>();

        //false on initialization
        this.isPaused = false;

        //im leaving this at 10 for now, but will change it once i find a suitable speed
        this.speed = 10;
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
            this.memory[0x200 + i] = romBytes[i];
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
            this.memory[i] = fontset[i];
        }
    }

    //method to update the timers
    private void updateTimers()
    {
        if(this.delayTimer > 0)
        {
            this.delayTimer -= 1;
        }

        if(this.soundTimer > 0)
        {
            this.soundTimer -= 1;
        }
    }

    //method for the cpu cycle
    private void emulateCycle()
    {
        //loop that will control the speed of execution
        for(int i = 0; i < this.speed; i++)
        {
            //execution will stop if the game is stopped
            if(!this.isPaused)
            {
                //bit manipulation to get full opcode, since they are 2 bytes each.
                //Retrieves first byte from memory and shifts it 8 bits, then bitwise or with next byte in memory
                //after opcode is retrieved, the pc is incremented by 2 and the instruction is executed
                int opcode = (this.memory[this.PC] << 8 | this.memory[this.PC + 1]);
                this.PC =+ 2;
                executeOpcode(opcode);
            }
        }

        //timers are updated while the game is not paused
        if(!this.isPaused)
        {
            updateTimers();
        }

        //methods to play sound and draw graphics will go here


    }

    //method to execute each opcode
    private void executeOpcode(int opcode)
    {
        byte x = (byte) ((opcode & 0x0F00) >> 8);
        byte y = (byte) ((opcode & 0x00F0) >> 4);
        
        //monolithic switch statement to handle the execution of the instruction
        //"opcode & 0xF000" will give the first 4 bits of the opcode
        switch(opcode & 0xF000)
        {
            //first opcodes to do are 00E0, 1nnn, 6xnn, 7xnn, Annn, Dxyn
            case 0x0000:
                switch(opcode)
                {
                    //opcode to clear the screen
                    case 0x00E0:
                        break;
                    //opcode to return from a subroutine
                    case 0x00EE:
                        this.PC = this.stack.pop();
                        break;
                }
                break;
            //sets the program counter to the last 12 bits of the opcode
            case 0x1000:
                this.PC = (short) (opcode & 0x0FFF);
                break;
            //pushes the current PC on to the stack, and then sets it to the last 12 bits of the opcode
            case 0x2000:
                this.stack.push(this.PC);
                this.PC = (short) (opcode & 0x0FFF);
                break;
            //increments pc by 2 if vx = last byte of opcode
            case 0x3000:
                if(this.v[x] == (opcode & 0x00FF))
                    this.PC +=2;
                break;
            //increments pc by 2 if vx != last byte
            case 0x4000:
                if(this.v[x] != (opcode & 0x00FF))
                    this.PC +=2;
                break;
            //increments pc by 2 if vx = vy
            case 0x5000:
                if(this.v[x] == this.v[y])
                    this.PC += 2;
                break;
            //last byte of opcode is put into vx
            case 0x6000:
                this.v[x] = (byte) (opcode & 0x00FF);
                break;
            //adds last byte of opcode to value in vx
            case 0x7000:
                this.v[x] = (byte) (this.v[x] + (opcode & 0x00FF));
                break;

            case 0x8000:
                switch(opcode & 0x000F)
                {
                    //stores vy into vx
                    case 0x0:
                        this.v[x] = this.v[y];
                        break;
                    
                    case 0x1:
                        this.v[x] = (byte) (this.v[x] | this.v[y]);
                        break;

                    case 0x2:
                        this.v[x] = (byte) (this.v[x] & this.v[y]);
                        break;

                    case 0x3:
                        this.v[x] = (byte) (this.v[x] ^ this.v[y]);
                        break;

                    case 0x4:
                        int sum = this.v[x] + this.v[y];

                        this.v[0xF] = 0;

                        if(sum > 0xFF)
                            this.v[0xF] = 1;

                        this.v[x] = (byte) sum;
                        break;

                    case 0x5:
                        this.v[0xF] = 0;

                        if(this.v[x] > this.v[y])
                            this.v[0xF] = 1;

                        this.v[x] = (byte) (this.v[x] - this.v[y]);
                        break;

                    case 0x6:
                        break;

                    case 0x7:
                        break;

                    case 0xE:
                        break;
                }
            
        }
    }
}