package io.github.redstonemango.cmp;

public record CliInput(IOFileData in, IOFileData out) {

    public static CliInput parseCli(String[] args) {
        // 0: None, 1: Expecting infile, 2: Expecting outfile
        int state = 0;
        for (String arg : args) {
            switch (arg) {
                case ""
            }
        }
    }

}
