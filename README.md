# C-+ — The C is for Calculator

A language for abstracting scripts on a scientific calculator.

Since this project is meant for scientific calculators only, we only support a limited number of operations, no "normal" graphics, etc.

The language was primarily developed for compatibility with the _Casio fx-810DE CW_.

## Compiler

The _C-+_ compiler "`cmpc`" can be used to translate human-readable _C-+_ code into a single-operation mathematical equation for the calculator.

Use usage is straightforward:

```sh
# Compile from stdin to stdout
cmpc
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

## Syntax

### Indentation, Statements and Comments

Indentation is completely optional and can consist of spaces, tabs, newlines or a mixture of all.

Statements are ended with a `;`.

Comments are begun using two slashes and will end at the end of the line.

### Variables:

_C-+_ differentiates between 2 kinds of variables: _inline variables_ and _expansion variables_:

- **Inline Variables**:
  These are specified inside the _C-+_ source code and inlined during compiletime. Once assigned, an inline variable is immutable.
  ```
  inline someSetting = 12; // Whenever someSetting is referenced, the compiler will insert 12
  ```
- **Expansion Variables**:

  #### Assignment
  On assignment, these are set to an expression to be evaluated on variable access at runtime. This means, the expression will **not** be run when assigned but expanded on-demand
  ```
  sum = 5 + 2; // When sum is referenced, the expression 5+2 will be evaluated and used as the variable value
  ```

  #### Mutating
  An expansion variable can be reassigned to reference a new expression later on overwriting overwrite the old one.
  If the variable itself is part of the newly assigned expression, its current expression will be embedded as part of the new expression. When accessing, the "inner" expression is evaluated before the newly assigned "outer" one.

### Arithmetic:

_C-+_ supports some basic arithmetic operators out-of-the-box. These being:

- `addend + addend` Additon
- `minuend - subtrahend` Substraction
- `factor * factor` Multiplication
- `dividend / divisor` Division
- `frac(numerator, denominator)` Fractions
- `pow(base)` and `pow(base, exponent)` Exponentiation - If no exponent is specified, squaring is assumed
- `root(radicant)` and `root(radicant, index)` Radical - If no index is specified, a square root is assumed
- `(expression1) expression2` Parentheses for specifying the order of operations

#### Native Arithmetic

Additionally, you can specify any mathematical expression supported by your calculator using the unsafe _Native Arithmetic_ operator by wrapping your expression into backticks. _C-+_ will assume the expression to produce some mathematical value and treat it like a regular number. When used as part of a larger computation, native arithmetic will be executed before the other parts of the expression, as if surrounded by parentheses. To use a variable inside a native arithmetic block, surround it with dollar signs.

The example below uses the calculator's native `Int()` function and inserts the variable `myVar` as argument. The result is incremented by 5 and stored in `out`
```
myVar = ...;
out = 5 + `Int($pi$)`;
```

Note, that with native arithmetic, the _C-+_ compiler cannot make any guarantees about the nature and syntax of the expression and blindly trusts it, forwarding it to the compilation result.

### Number Transformation

_C-+_ allows you to not only calculate with numbers but also transform them using their Base10 representation:

Transformations can be applied to any expression that logically represents a groupable number:
- Numbers
- Variables
- Arithmetic expressions inside parentheses
- Native arithmetic blocks

#### Slicing

You can slice a number's Base10 representation, extracting a new number. This operation returns a copy of the original number and does **not** modify the number itself.

To do so, append the `[start:end]` syntax to the number:
```
sliced = myVar[1:3];
```

A `start` or `end` value $`n \ge 0`$ specifies the n'th digit at the left of the decimal point, with the _ones place_ being 0, the _tens place_ being 1, _hundrets place_ being 2, etc.

A `start` or `end` value of $`n \lt 0`$ specifies the n'th decimal place, counting from left to right.

If a slicing process includes the decimal point in the sliced region, it will be **TODO: Make up my mind**.

Examples:
```
num1 = 12345[0:2];   // Will be 345
num2 = 3.141[-1:-3]; // Will be 141
```

#### Combining

You can combine two or more number's Base10 representation, creating a new number. This operation returns a new object and does **not** modify the number itelf.

To do so, use the `a ++ b` operator:
```
num = 12 ++ 83;     // Will be 1283
num = 42 ++ 0 ++ 8; // Will be 4208
```

#### Analyzing

You can analyze the length of a number's Base10 representaion, yielding a resulting integer.

To do so, append a `[#]` _(or `[#.]` to count the decimal digits)_:
```
integers = 12.341[#];  // Will be 2
decimals = 12.341[#.]; // Will be 3
```

### Yielding a Result

To yield a computation result, use a `yield EXPR` statement at the bottom of the code. The statement is needed in order for your code to compile.

The **EXPR** can be anything that logically represents a number:
- Numbers
- Variables
- Arithmetic expressions
- Native arithmetic blocks

```
yield 12 * 7;
yield myVar - 1;
```
