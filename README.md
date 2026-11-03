# C-+ — The C is for Calculator

A language for abstracting scripts on a scientific calculator.

Since this project is meant for scientific calculators only, we only support a limited number of operations, no "normal" graphics, etc.

The language was primarily developed for compatibility with the _Casio fx-810DE CW_.

## Compiler

The _C-+_ compiler "`cmpc`" can be used to translate human-readable _C-+_ code into a single-operation mathematical equation for the calculator.

Use usage is straightforward:

```sh
# Compile from stdin to stdout
cmpc --input - --output -

# Compile from file to file
cmpc -i path/to/file.cmp -o path/to/output.math

# Run the lexer only (optionally from stdin)
cmpc -i path/to/file.cmp -o "lexer::path/to/object-model.com"
cmpc -i path/to/file.cmp -o "lexer::-"

# Compile from a lexer input (optionally from stdin)
cmpc -i "lexer::path/to/object-model.com" -o path/to/output.math
cmpc -i "lexer::-" -o path/to/output.math
```

## Language

For an overview of the language's syntax, see [here](./SYNTAX.md).





# Temporary
Idea how things could work:

```
max = 8;
len = getArg()[#];         // Get length of parameter number

nextLen = (len % max) + 1; // Calculate 'len + 1 (mod max)', however, we shift the order a bit so the range is 1..max instead of 0..max-1
res = 1[nextLen.];         // Create a number that's a '1' with 'nextLen' '0's

return res;
```

```
frame = getArg()[:0] % 4; // Extract the integer from the parameter number and take it mod 4 to use it as frame number

// Define a value to be used for each frame
res = {
    frame == 0: 0.1,
    frame == 1: 0.01,
    frame == 2: 0.001,
    frame == 3: 0.01
};

combined = (frame + 1) ++ res;  // Concatenate the 'frame + 1' and the result

return combined[1.]; // Put the decimal point at position 1 of the combined number - since the frame will always be 1 digit, we can hardcode this
```
