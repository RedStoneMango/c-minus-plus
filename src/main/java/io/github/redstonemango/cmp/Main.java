package io.github.redstonemango.cmp;

import io.github.redstonemango.cmp.cli.CliInput;
import io.github.redstonemango.cmp.cli.InvalidCliFormatException;

import java.io.IOException;

public class Main {
    static void main(String[] args) {
        try (CliInput cliInput = CliInput.parseCli(args)) {
            handleCliInput(cliInput);
        }
        catch (InvalidCliFormatException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        catch (IOException e) {
            System.err.print("Error cleaning up the IO streams: ");
            e.printStackTrace(System.err);
        }
    }

    private static void handleCliInput(CliInput cliInput) throws IOException {
    }
}
