package io.github.redstonemango.cmp.lexer;

import io.github.redstonemango.cmp.cli.IOStream;

import java.io.IOException;
import java.io.Reader;

public class LexerCharSource implements AutoCloseable {

    private final Reader backing;

    private int curr = -1;

    public LexerCharSource(IOStream backing) {
        if (backing.getSyntaxType() != IOStream.SyntaxType.SOURCE) {
            throw new IllegalArgumentException("LexerCharSource can only process source files");
        }
        this.backing = backing.exposeIn();
    }

    public boolean next() throws IOException {
        curr = backing.read();
        return curr != -1;
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
