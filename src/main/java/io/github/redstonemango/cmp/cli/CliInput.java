package io.github.redstonemango.cmp.cli;

import io.github.redstonemango.cmp.Tuple;

import java.io.IOException;

public record CliInput(IOStream in, IOStream out) implements AutoCloseable {

    public static CliInput parseCli(String[] args) throws InvalidCliFormatException {
        // 0: None, 1: Expecting infile, 2: Expecting outfile
        int state = 0;
        IOStream in = null;
        IOStream out = null;

        for (String arg : args) {
            switch (arg) {
                case "--input", "-i" -> {
                    assertStateIs(state, 0, "Unexpected option at this position: " + arg);
                    state = 1;
                }
                case "--output", "-o" -> {
                    assertStateIs(state, 0, "Unexpected option at this position: " + arg);
                    state = 2;
                }
                default -> {
                    var asFileArg = parseFileArgument(arg);
                    switch (state) {
                        case 1 -> {
                            in = IOStream.createInputStream(asFileArg.a(), asFileArg.b());
                            state = 0;
                        }
                        case 2 -> {
                            out = IOStream.createOutputStream(asFileArg.a(), asFileArg.b());
                            state = 0;
                        }
                        default -> throw new InvalidCliFormatException("Unexpected value: " + arg);
                    }
                }
            }
        }

        if (in == null) throw new InvalidCliFormatException("Input is required");
        if (out == null) throw new InvalidCliFormatException("Output is required");
        if (in.getSyntaxType().getLevel() > out.getSyntaxType().getLevel())
            throw new InvalidCliFormatException("Output type cannot be weaker than input type: " +
                    out.getSyntaxType() + " < " + in.getSyntaxType());
        if (in.getSyntaxType().getLevel() == out.getSyntaxType().getLevel())
            throw new InvalidCliFormatException("Output type cannot the same as input type: " + out.getSyntaxType());

        return new CliInput(in, out);
    }

    /**
     * Convert {@code /path/to/file} to {@code (/path/to/file, null)}.
     * <br>
     * Convert {@code type::/path/to/file} to {@code (/path/to/file, type)}.
     */
    private static Tuple<String, String> parseFileArgument(String arg) {
        int idx = arg.indexOf("::");
        if (idx == -1) {
            return new Tuple<>(arg, null);
        }

        return new Tuple<>(
                arg.substring(idx + 2),
                arg.substring(0, idx)
        );
    }

    private static void assertStateIs(int state, int expected, String message) throws InvalidCliFormatException {
        if (state != expected) {
            throw new InvalidCliFormatException(message);
        }
    }

    @Override
    public void close() throws IOException {
        in.close();
        out.close();
    }
}
