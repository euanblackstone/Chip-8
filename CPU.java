import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayDeque;

//class to represent the cpu of the machine
class CPU {
    //things I will need later
    private Keyboard keyboard;
    private Screen renderer;
    //private Speaker speaker;

    //variable to store the memory
    private short[] memory;

    //8-bit registers referred to as v in technical reference
    private short[] v;

    //chip 8 has one 16-bit register called i
    //should be able to keep this a short since only the last 12 bits are used
    private short i;

    //chip 8 has two timers. Timers decrement towards 0 at a rate of 60 hz
    private int delayTimer;
    private int soundTimer;

    //program counter used to store the currently executing address
    private int PC;

    //int array for stack
    private ArrayDeque<Integer> stack;
    
    private boolean isPaused;

    //variable to control the speed of the emulation cycle
    private int speed;

    CPU(Keyboard keyboard, Screen renderer) {
        this.keyboard = keyboard;
        this.renderer = renderer;
        //this.speaker = speaker;

        //chip 8 has 4096 bytes of memory
        //since java does not have unsigned bytes, a short array is being used
        this.memory = new short[4096];

        //chip 8 has 16 8-bit registers
        this.v = new short[16];

        //0 at initialization
        this.i = 0;

        this.delayTimer = 0;
        this.soundTimer = 0;

        //address that programs are expected to be loaded at
        this.PC = 0x0200;

        //array of 16 16-bit values for stack
        //stack = new short[16];
        this.stack = new ArrayDeque<Integer>();

        //false on initialization
        this.isPaused = false;

        //im leaving this at 10 for now, but will change it once i find a suitable speed
        this.speed = 10;
    }

    /*

        NEEDS FIXING

    */
    //method to load the rom into memory
    public void loadRomIntoMemory(String romName) throws IOException {
        //accesses the rom file and puts all bytes into a byte array
        File rom = new File("roms/" + romName);
        byte[] romBytes = Files.readAllBytes(rom.toPath());

        //for loop to read all bytes of rom and store it in memory starting at address 0x200
        for(int i = 0; i < romBytes.length; i++) {
            this.memory[0x200 + i] = romBytes[i];
        }
        
    }

    //method to load the sprites into memory
    public void loadFontsetInMemory() {
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
        for(int i = 0; i < fontset.length; i++) {
            this.memory[0x80 + i] = fontset[i];
        }
    }

    //method to update the timers
    private void updateTimers() {
        if(this.delayTimer > 0) {
            this.delayTimer -= 1;
        }

        if(this.soundTimer > 0) {
            this.soundTimer -= 1;
        }
    }

    //method for the cpu cycle
    public void emulateCycle() {
        //loop that will control the speed of execution
        for(int i = 0; i < this.speed; i++) {
            //execution will stop if the game is stopped
            if(!this.isPaused) {
                //bit manipulation to get full opcode, since they are 2 bytes each.
                //Retrieves first byte from memory and shifts it 8 bits, then bitwise or with next byte in memory
                //after opcode is retrieved, the pc is incremented by 2 and the instruction is executed
                int opcode = (this.memory[this.PC] << 8 | this.memory[this.PC + 1]);
                this.PC += 2;
                executeOpcode(opcode);
            }
        }

        //timers are updated while the game is not paused
        if(!this.isPaused) {
            updateTimers();
        }

        //methods to play sound and draw graphics will go here
        this.renderer.repaintScreen();
    }

    //method to execute each opcode
    private void executeOpcode(int opcode) {
        short x = (short) ((opcode & 0x0F00) >> 8);
        short y = (short) ((opcode & 0x00F0) >> 4);
        
        //monolithic switch statement to handle the execution of the instruction
        //"opcode & 0xF000" will give the first 4 bits of the opcode
        switch(opcode & 0xF000) {
            //first opcodes to do are 00E0, 1nnn, 6xnn, 7xnn, Annn, Dxyn
            case 0x0000:
                switch(opcode) {
                    //opcode to clear the screen
                    case 0x00E0:
                        this.renderer.clear();
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
                    this.PC += 2;
                break;
            //increments pc by 2 if vx != last byte
            case 0x4000:
                if(this.v[x] != (opcode & 0x00FF))
                    this.PC += 2;
                break;
            //increments pc by 2 if vx = vy
            case 0x5000:
                if(this.v[x] == this.v[y])
                    this.PC += 2;
                break;
            //last byte of opcode is put into vx
            case 0x6000:
                this.v[x] = (short) (opcode & 0x00FF);
                break;
            //adds last byte of opcode to value in vx
            case 0x7000:
                this.v[x] = (short) (this.v[x] + (opcode & 0x00FF));
                break;

            case 0x8000:
                switch(opcode & 0x000F) {
                    //stores vy into vx
                    case 0x0:
                        this.v[x] = this.v[y];
                        break;
                    //bitwise ors v[x] and v[y] and stores it in v[x]
                    case 0x1:
                        this.v[x] = (short) (this.v[x] | this.v[y]);
                        break;
                    //bitwise ands v[x] and v[y] and stores it
                    case 0x2:
                        this.v[x] = (short) (this.v[x] & this.v[y]);
                        break;
                    //bitwise xors v[x] and v[y] and stores it
                    case 0x3:
                        this.v[x] = (short) (this.v[x] ^ this.v[y]);
                        break;
                    //stores the sum of v[x] and v[y] in v[x]
                    case 0x4:
                        int sum = this.v[x] + this.v[y];

                        this.v[0xF] = 0;

                        if(sum > 0xFF)
                            this.v[0xF] = 1;

                        //sum & 0xFF prevents against overflow if sum > 255
                        this.v[x] = (short) (sum & 0xFF);
                        break;
                    //v[x] = v[x] - v[y]
                    case 0x5:
                        this.v[0xF] = 0;

                        if(this.v[x] > this.v[y])
                            this.v[0xF] = 1;

                        //& 0xFF prevents negative values from being stored
                        this.v[x] = (short) ((this.v[x] - this.v[y]) & 0xFF);
                        break;
                    //v[f] is set to the least significant bit of v[x], v[x] is then divided by 2 by shifting it one bit to the right
                    case 0x6:
                        this.v[0xF] = (byte) (this.v[x] & 0x1);

                        this.v[x] >>= 1;
                        break;
                    //v[x] = v[y] - v[x]
                    case 0x7:
                        this.v[0xF] = 0;

                        if(this.v[y] > this.v[x])
                            this.v[0xF] = 1;

                        this.v[x] = (short) ((this.v[y] - this.v[x]) & 0xFF);
                        break;
                    //v[f] is set to the most significant bit of v[x], v[x] is multiplied by 2 by shirting one bit to the left
                    case 0xE:
                        this.v[0xF] = (byte) (this.v[x] & 0x80);

                        this.v[x] <<= 1;
                        break;
                }
            //skips next instruction if v[x] != v[y]
            case 0x9000:
                if(this.v[x] != this.v[y]) {
                    this.PC += 2;
                }
                break;
            //register i is set to the last 12 bits of opcode 
            case 0xA000:
                this.i = (short) (opcode & 0x0FFF);
                break;
            //prgram counter is set to the value of last 12 bits of opcode + value from register v[0]
            case 0xB000:
                this.PC = ((opcode & 0x0FFF) + this.v[0]);
                break;
                
            case 0xC000:
                int rand = (int)(Math.random() * 0xFF + 1);

                this.v[x] = (short) ((short) rand & (opcode & 0xFF));
                break;

            case 0xD000:
                int width = 8;
                int height = (opcode & 0xF);

                this.v[0xF] = 0;

                for(int row = 0; row < height; row++) {
                    int sprite = this.memory[this.i + row];

                    for(int col = 0; col < width; col++) {
                        if((sprite & 0x80) > 0) {
                            if(this.renderer.togglePixel(this.v[x] + col, this.v[y] + row)) {
                                this.v[0xF] = 1;
                            }
                        }

                        sprite <<= 1;
                    }
                }
                break;

            case 0xE000:
                switch(opcode & 0x00FF) {
                    case 0x009E:
                        if(this.keyboard.getCurrentKey() == this.v[x]) {
                            this.PC += 2;
                        }
                        break;

                    case 0x00A1:
                        if(this.keyboard.getCurrentKey() != this.v[x]) {
                            this.PC += 2;
                        }
                        break;
                }
            
            case 0xF000:
                switch(opcode & 0x00FF) {
                    case 0x0007:
                        this.v[x] = (short) this.delayTimer;
                        break;

                    case 0x000A:
                        this.isPaused = true;
                        int currentKey = this.keyboard.getCurrentKey();

                        while(currentKey == 0) {
                            currentKey = this.keyboard.getCurrentKey();
                        }

                        this.v[x] = (short) currentKey;
                        this.isPaused = false;
                        break;
                    
                    case 0x0015:
                        this.delayTimer = this.v[x];
                        break;

                    case 0x0018:
                        this.soundTimer = this.v[x];
                        break;

                    case 0x001E:
                        this.i += this.v[x];
                        break;

                    case 0x0029:
                        this.i = (short) (this.v[x] * 5);

                    case 0x0033:
                        this.memory[this.i] = (short) (this.v[x] / 100);

                        this.memory[this.i + 1] = (short) ((this.v[x] % 100) / 10);

                        this.memory[this.i + 2] = (short) (this.v[x] % 10);
                        break;

                    case 0x0055:
                        for(int j = 0; j <= x; j++) {
                            this.memory[this.i + j] = this.v[j];
                        }
                        break;

                    case 0x0065:
                        for(int j = 0; j <= x; j++) {
                            this.v[j] = this.memory[this.i + j];
                        }
                        break;

                    default:
                        System.out.println("unexpected opcode");
                        System.out.println(opcode);
                }
        }
    }
}