package io.github.redstonemango.cmp.lexer;

import io.github.redstonemango.cmp.cli.IOStream;

import java.io.IOException;
import java.io.Reader;

public class LexerCharSource implements AutoCloseable {

    private final Reader backing;

    private int curr = -1;
    private int buffer = -1;
    private boolean prevWasSlash;
    private boolean prevWasAsterix;

    public LexerCharSource(IOStream backing) {
        if (backing.getSyntaxType() != IOStream.SyntaxType.SOURCE) {
            throw new IllegalArgumentException("LexerCharSource can only process source files");
        }
        this.backing = backing.exposeIn();
    }

    LexerCharSource(Reader reader) {
        backing = reader;
    }

    public boolean next() throws IOException {
        if (buffer != -1) {
            curr = buffer;
            buffer = -1;
            return true;
        }

        // 0 = No comment, 1 = Single-line commend, 2 = Multiline comment
        int comment = 0;
        prevWasSlash = false;
        prevWasAsterix = false;

        while ((curr = backing.read()) != -1) {
            switch (curr) {
                case ' ', '\t', '\r' -> {
                    if (checkDrainControlCharBuffer(false)) return true;
                }
                case '\n' -> {
                    if (comment == 1) comment = 0; // Consume newline and end single-line comments here
                    else if (checkDrainControlCharBuffer(false)) return true;
                }
                case '/' -> {
                    if (comment == 1) continue;
                    if (comment == 2) {
                        if (prevWasAsterix) {
                            prevWasAsterix = false;
                            comment = 0;
                        }
                        continue;
                    }
                    if (prevWasSlash) {
                        comment = 1;
                        prevWasSlash = false;
                    }
                    else {
                        prevWasSlash = true;
                    }
                }
                case '*' -> {
                    if (comment == 0 && prevWasSlash) {
                        comment = 2;
                        prevWasSlash = false;
                    }
                    else if (comment == 2) {
                        prevWasAsterix = true;
                    }
                    else if (comment == 0) {
                        return true;
                    }
                }
                default -> {
                    if (checkDrainControlCharBuffer(true)) return true;
                    if (comment != 0) continue; // Don't account for comments
                    return true;
                }
            }
        }
        return checkDrainControlCharBuffer(true);
    }

    private boolean checkDrainControlCharBuffer(boolean keepCurrent) {
        // Previous was a slash but this isn't - we've incorrectly consumed a '/'
        // Restore the '/' and add the current one as buffer to be drained on consecutive next() call
        if (prevWasSlash) {
            prevWasSlash = false;
            if (keepCurrent) {
                buffer = curr;
            }
            curr = '/';
            return true;
        }
        if (prevWasAsterix) {
            // We'll never want to keep an asterix since we'll only store it inside a multiline comment
            //  in which nothing shall be output
            prevWasAsterix = false;
        }
        return false;
    }

    public char getChar() {
        if (curr == -1) {
            throw new IllegalStateException("No current character");
        }
        return (char) curr;
    }

    @Override
    public void close() throws IOException {
        backing.close();
    }
}
