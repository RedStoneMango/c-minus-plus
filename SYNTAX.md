# Syntax

## Indentation, Statements and Comments

Indentation is completely optional and can consist of spaces, tabs, newlines or a mixture of all.

Statements are ended with a `;`.

Comments are begun using two slashes and will end at the end of the line.

## Variables

### Naming

Variables may be named using uppercase and lowercase letters, underscores and arabic numerals as long as the name does not start with a numeral.

### Assignment
On assignment, variables are set to an expression to be evaluated on runtime access. This means, the expression will **not** be run when assigned but expanded on-demand.
```
sum = 5 + 2; // When sum is referenced, the expression 5+2 will be evaluated and used as the variable value
```

### Mutating
An variable can be reassigned to reference a new expression later on, overwriting the old expression for subsequent accesses of the variables.
If the variable itself is part of the newly assigned expression, its current expression will be embedded as part of the new expression. When accessing, the "inner" expression is evaluated before the newly assigned "outer" one.

## Arithmetic

_C-+_ supports some basic arithmetic operators out-of-the-box. These being:

- `addend + addend` Additon
- `minuend - subtrahend` Substraction
- `factor * factor` Multiplication
- `dividend / divisor` Division
- `dividend % modulus` Modulo _(this operation is not native to the fx-810DE CW, so the compilert implements it manually)_
- `frac(numerator, denominator)` Fractions
- `pow(base)` and `pow(base, exponent)` Exponentiation - If no exponent is specified, squaring is assumed
- `root(radicant)` and `root(radicant, index)` Radical - If no index is specified, a square root is assumed
- `(expression1) expression2` Parentheses for specifying the order of operations

### Native Arithmetic

Additionally, you can specify any mathematical expression supported by your calculator using the unsafe _Native Arithmetic_ operator by wrapping your expression into backticks. _C-+_ will assume the expression to produce some mathematical value and treat it like a regular number. When used as part of a larger computation, native arithmetic will be executed before the other parts of the expression, as if surrounded by parentheses. To use a variable inside a native arithmetic block, surround it with dollar signs.

The example below uses the calculator's native `Int()` function and inserts the variable `myVar` as argument. The result is incremented by 5 and stored in `out`
```
myVar = ...;
out = 5 + `Int($myVar$)`;
```

Note, that with native arithmetic, the _C-+_ compiler cannot make any guarantees about the nature and syntax of the expression and blindly trusts it, forwarding it to the compilation result.

## Number Transformation

_C-+_ allows you to not only calculate with numbers but also transform them using their Base10 representation:

Transformations can be applied to any expression that logically represents a groupable number:
- Numbers
- Conditional Numbers
- Variables
- Arithmetic expressions inside parentheses
- Native arithmetic blocks
- `getArg()` or `getEnv(name)`

### Slicing

You can slice a number's Base10 representation, extracting a new number. This operation returns a copy of the original number and does **not** modify the number itself.

To do so, append the `[start:end]` syntax to the number:
```
sliced = myVar[1:3];
```

A `start` or `end` value $`n \ge 0`$ specifies the n'th digit at the left of the decimal point, with the _ones place_ being 0, the _tens place_ being 1, _hundrets place_ being 2, etc.

A `start` or `end` value of $`n \lt 0`$ specifies the n'th decimal place, counting from left to right.

If a slicing process includes the decimal point in the sliced region, it will be **TODO: Make up my mind**.

In case of only one value being supplied, the other one will be implicitely set to the available extreme, i.e. `[-1:]` references everything from the first decimal digit to the end of the number, whereas `[:0]` will reference everything from the beginning until the ones place. `[:]` has to effect.

Slicing is evaluated left-to-right.

Examples:
```
num1 = 12345[2:0];    // Will be 345
num2 = 3.1419[-2:-3]; // Will be 41
num3 = 5.1419[-2:];   // Will be 419
```

### Combining

You can combine two or more number's Base10 representation, creating a new number. This operation returns a new object and does **not** modify the number itelf.

To do so, use the `a ++ b` operator:
```
num = 12 ++ 83;     // Will be 1283
num = 42 ++ 0 ++ 8; // Will be 4208
```

Combining is evaluated left-to-right, taking precedence over arithmetic expressions unless grouped by parentheses.

### Decimal Insertion

You can move / insert the decimal point to any position $`n`$ (from left to right) inside a number using the `[n.]` syntax.

If $`n = 0`$, the entire number is treated as decimal digits for the integer 0. $`n \lt 0`$ or $`n \gt maxDigits`$ will space out the number respectively:
```
num = 12345[2.]  // Will be 12.345
num = 12345[0.]  // Will be 0.12345
num = 12345[7.]  // Will be 1234500.0
num = 12345[-2.] // Will be 0.0012345
num = 123.4[1.]  // Will be 1.234
```

Decimal insertion is evaluated left-to-right.

### Analyzing

You can analyze the length of a number's Base10 representaion, yielding a resulting integer.

To do so, append a `[#]`. By putting a period in front of / behind the pound sign, you can count the integer/decimal places respectively:
```
integers = 12.341[#];  // Will be 5
decimals = 12.341[.#]; // Will be 2
decimals = 12.341[#.]; // Will be 3
```

Analyzing is evaluated left-to-right.

## Conditional Numbers

A number's value can be dynamically assigned based on a condition, evaluated once the conditional number is accessed. The conditional number itself can be treated like any regular number, being subject to number transformations or arithmetic. Note, that a conditional number cannot be embedded into a _Native Arithmetic_ block unless stored inside a variable beforehand.

To specify a conditional number, use the `{COND1: NUM1, COND2: NUM2}` syntax, specifying a condition followed by a colon and the value to be used in case the condition is fulfilled. The first branch to be fulfilled will be used.

Exmaple:
```
num = {
    getEnv(z) == 1: 42,    // If getEnv(z) is 1, use 42
    getEnv(z) > 140: 36,   // If it is > 140, use 36
    getEnv(z) != 3: 103,   // If it is not 3 (and <= 140 since that's been ruled out by the earlier condition), use 103
    *: 18                  // If no condition is true, use 18
} + 12;                    // Whatever the value is, add 12
```

### Conditional Operators

The following conditions are supported:

- `a == b` Is equal
- `a != b` Is not equal
- `a < b` Less than
- `a > b` Greater than
- `*` Default value in case no other condition is fulfilled. If no default is provided, it will be $`0`$.

## Producing a Result

To produce a computation result, use a `return EXPR` statement at the bottom of the code. The statement is needed in order for your code to compile.

The **EXPR** can be anything that logically represents a number:
- Numbers
- Conditional Numbers
- Variables
- Arithmetic expressions
- Native arithmetic blocks
- `getArg()` or `getEnv(name)`

```
return 12 * 7;
return myVar - 1;
```

If the `return` is followed by and other statement, the code will not compile.


## Retrieving the Argument

The resulting math statement is going to be called with a parameter $`x`$. You can obtain it using the `getArg()` method.

The parameter is a simple number and behaves just like a normal number does:

```
argument = getArg();
argumentLen = getArg()[#];
...
```


## Retrieving Environment Variables

You can also retrieve your calculator's environment variables which can be edited in the calculator's "Variable" menu _(or the repective menu name for the calculator you are using)_.

The environment variable is identified by a name _(usually a single-letter identifier like 'z')_ and can be accessed at runtime using the `getEnv(name)` method.

The name parameter is expected to be the name of the environment variable on your calculator and will not undergo any expansion or modification at compile- or runtime.

The returned value is a simple number and behaves just like a normal number does:

```
envVar1 = getEnv(z);
envVar2 = getEnv(A);
...
```
