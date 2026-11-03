package io.github.redstonemango.cmp.lexer;

import io.github.redstonemango.cmp.cli.IOStream;

import java.io.IOException;
import java.io.InputStream;

public class LexerCharSink implements AutoCloseable {

    private final InputStream backing;

    public LexerCharSink(IOStream backing) {
        if (backing.getSyntaxType() != IOStream.SyntaxType.SOURCE) {
            throw new IllegalArgumentException("LexerCharSink can only process source files");
        }
        this.backing = backing.exposeIn();
    }

    public char read

    @Override
    public void close() throws IOException {
        backing.close();
    }
}
