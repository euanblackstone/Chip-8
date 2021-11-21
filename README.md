# Chip-8
A Chip-8 interpreter written in java.

## Goal
This goal of this project was to learn more about low-level programming and emulator development. I wrote this code in java as it is the language I am most comfortable with.

## Usage
If you would like to try this interpreter yourself, first clone the repo into a directory of your choice.

```console
$ git clone https://github.com/euanblackstone/Chip-8.git
```

Next, compile the code and run the main file, making sure to supply the rom you want to run as an argument.

```console
$ javac *.java
$ java Main Ibm
```

## Known Issues
* Flickering
  * With some roms, notably Pong, the screen will flicker. This is natural with the original Chip-8 specifications. To fix this, one could render the desired screen beforehand and render the screen when it finishes.
  * The rom Blitz does not work properly.
