package io.github.redstonemango.cmp;

import org.jspecify.annotations.Nullable;

import java.io.File;

public record IOFileData(File file, Type type) {

    public static @Nullable IOFileData createInputFile(File file, String type) {
        var typeObj = Type.ofName(type, Type.SOURCE);
        if (typeObj == null) return null;
        return new IOFileData(file, typeObj);
    }

    public static @Nullable IOFileData createOutputFile(File file, String type) {
        var typeObj = Type.ofName(type, Type.MATH);
        if (typeObj == null) return null;
        return new IOFileData(file, typeObj);
    }

    public enum Type {
        SOURCE("source"),
        MATH("math"),
        LEXER("lexer");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        /**
         * Return the type known by {@code name} or {@code fallback} if {@code name} is null.
         * Return {@code null} if the name does not exist.
         */
        public static @Nullable Type ofName(String name, Type fallback) {
            if (name == null) return fallback;
            for (Type type : values()) {
                if (type.name.equals(name)) {
                    return type;
                }
            }
            return null;
        }
    }
}
