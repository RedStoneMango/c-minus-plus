package io.github.redstonemango.cmp.cli;

import org.jspecify.annotations.Nullable;

import java.io.*;

public class IOStream implements AutoCloseable {

    private final @Nullable Reader in;
    private final @Nullable OutputStream out;
    private final SyntaxType syntaxType;

    public IOStream(Reader in, SyntaxType syntaxType) {
        this.in = in;
        this.out = null;
        this.syntaxType = syntaxType;
    }
    public IOStream(OutputStream out, SyntaxType syntaxType) {
        this.in = null;
        this.out = out;
        this.syntaxType = syntaxType;
    }

    public Reader exposeIn() {
        if (in == null) throw new IllegalStateException("IOStream does not represent an input");
        return in;
    }

    public OutputStream exposeOut() {
        if (out == null) throw new IllegalStateException("IOStream does not represent an output");
        return out;
    }

    public SyntaxType getSyntaxType() {
        return syntaxType;
    }

    @Override
    public void close() throws IOException {
        if (in instanceof FileReader) {
            in.close();
        }
        else if (out instanceof FileOutputStream) {
            out.close();
        }
    }

    public static IOStream createInputStream(String file, String type) throws InvalidCliFormatException {
        var typeObj = SyntaxType.ofName(type, SyntaxType.SOURCE);

        Reader reader;
        if (file.equals("-")) {
            try {
                reader = new InputStreamReader(System.in, System.getProperty("stdin.encoding"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("stdin.encoding not supported", e);
            }
        }
        else {
            try {
                reader = new FileReader(file);
            } catch (FileNotFoundException e) {
                throw new InvalidCliFormatException(e.getMessage());
            }
        }

        return new IOStream(reader, typeObj);
    }

    public static IOStream createOutputStream(String file, String type) throws InvalidCliFormatException {
        var typeObj = SyntaxType.ofName(type, SyntaxType.MATH);

        OutputStream stream;
        if (file.equals("-")) {
            stream = System.out;
        }
        else {
            try {
                stream = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                throw new InvalidCliFormatException(e.getMessage());
            }
        }

        return new IOStream(stream, typeObj);
    }

    public enum SyntaxType {
        SOURCE("source", 0),
        LEXER("lexer", 1),
        MATH("math", 2);

        private final String name;
        private final int level;

        SyntaxType(String name, int level) {
            this.name = name;
            this.level = level;
        }

        public String getName() {
            return name;
        }

        public int getLevel() {
            return level;
        }

        /**
         * Return the type known by {@code name} or {@code fallback} if {@code name} is null.
         * Return {@code null} if the name does not exist.
         */
        public static SyntaxType ofName(String name, SyntaxType fallback) throws InvalidCliFormatException {
            if (name == null) return fallback;
            for (SyntaxType syntaxType : values()) {
                if (syntaxType.name.equals(name)) {
                    return syntaxType;
                }
            }
            throw new InvalidCliFormatException("Unknown type of input/output stream: " + name);
        }
    }
}
