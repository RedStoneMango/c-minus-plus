package io.github.redstonemango.cmp.lexer;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class LexerCharSourceTest {

    private String readAll(String input) throws IOException {
        LexerCharSource source = new LexerCharSource(new StringReader(input));

        StringBuilder result = new StringBuilder();

        while (source.next()) {
            result.append(source.getChar());
        }

        return result.toString();
    }

    @Test
    void shouldRemoveSpaces() throws IOException {
        assertEquals("a=12;", readAll("a = 12;"));
    }

    @Test
    void shouldRemoveTabs() throws IOException {
        assertEquals("a=12;b=42;", readAll("\ta = 12;\t b = 42;"));
    }

    @Test
    void shouldRemoveNewlinesAndCarriageReturns() throws IOException {
        assertEquals(
                "a=12;b=42;c=54;",
                readAll("a = 12;\r\nb = 42;\nc = 54;")
        );
    }

    @Test
    void shouldRemoveSingleLineComments() throws IOException {
        assertEquals(
                "a=12;b=42;",
                readAll("a = 12; // comment\nb = 42;")
        );
    }

    @Test
    void shouldRemoveMultilineComments() throws IOException {
        assertEquals(
                "a=12;b=42;",
                readAll("a = 12; /* multiline\ncomment */ b = 42;")
        );
    }

    @Test
    void shouldRemoveMultilineCommentsInOneLine() throws IOException {
        assertEquals(
                "a=12;b=42;",
                readAll("a = 12; /* multiline comment in one line */ b = 42;")
        );
    }

    @Test
    void shouldRemoveCommentsAtEndOfLine() throws IOException {
        assertEquals(
                "a=12;b=42;",
                readAll("a = 12; // comment\nb = 42; // another comment")
        );
    }

    @Test
    void shouldRemoveCommentsBetweenStatements() throws IOException {
        assertEquals(
                "a=12;b=42;",
                readAll("a = 12; /* comment */ b = 42;")
        );
    }

    @Test
    void shouldRemoveMultipleComments() throws IOException {
        String input = """
                /* first comment */
                a = 12;
                // second comment
                b = 42;
                /* third
                   comment */
                """;

        assertEquals("a=12;b=42;", readAll(input));
    }

    @Test
    void shouldHandleEmptyInput() throws IOException {
        assertEquals("", readAll(""));
    }

    @Test
    void shouldHandleInputContainingOnlyWhitespace() throws IOException {
        assertEquals("", readAll(" \t\r\n   "));
    }

    @Test
    void shouldHandleInputContainingOnlyComments() throws IOException {
        assertEquals(
                "",
                readAll("// comment\n/* another comment */")
        );
    }

    @Test
    void shouldPreserveOperatorsAndSpecialCharacters() throws IOException {
        assertEquals(
                "a=(b+c)++b;d=c*1;e=b/c;",
                readAll("a = (b + c) ++ b; d = c * 1; e = b / c;")
        );
    }

    @Test
    void shouldHandleCommentMarkersInsideMultilineComments() throws IOException {
        String input = """
                a = 12;
                /*
                *
                **
                ***
                ///
                //
                */
                b = 42;
                """;

        assertEquals("a=12;b=42;", readAll(input));
    }

    @Test
    void shouldHandleCommentMarkersInsideSingleLineComments() throws IOException {
        String input = """
                a = 12; // / * */ /* */ // *** ///
                b = 42;
                """;
        assertEquals("a=12;b=42;", readAll(input));
    }

    @Test
    void shouldHandleSingleLineCommentsWithThreeSlashes() throws IOException {
        assertEquals("a=12;b=42;", readAll("a=12; /// Hello\nb=42;"));
    }

    @Test
    void shouldHandleMultilineCommentsWith2LeadingAsterixes() throws IOException {
        assertEquals("a=12;b=42;", readAll("a=12; /** Hello\n*/\nb=42;"));
    }

    @Test
    void shouldHandleMultilineCommentsWith2TrailingAsterixes() throws IOException {
        assertEquals("a=12;b=42;", readAll("a=12; /* Hello\n**/\nb=42;"));
    }

    @Test
    void shouldHandleAmbiguousSingleLineComments() throws IOException {
        assertEquals("a=12;b=42;", readAll("a=12; //* Hello\nb=42;"));
    }

    @Test
    void shouldHandleAmbiguousMultilineComments() throws IOException {
        assertEquals("a=12;b=42;", readAll("a=12; /*// Hello*/\nb=42;"));
    }

    @Test
    void shouldHandleEmptySingleLineComments() throws IOException {
        assertEquals("a=12;b=42;", readAll("a=12; //\nb=42;"));
    }

    @Test
    void shouldHandleEmptyMultilineComments() throws IOException {
        assertEquals("a=12;b=42;", readAll("a=12; /**/b=42;"));
    }

    @Test
    void shouldHandleMultilineCommentsInBetweenStatements() throws IOException {
        assertEquals("a=12;", readAll("a = /* Interrupting */ 12;"));
    }

    @Test
    void shouldHandleUnclosedMultilineComment() throws IOException {
        assertEquals("a=12;", readAll("a=12; /* Hello test\nb=42;"));
    }

    @Test
    void shouldHandleSingleCharacterInput() throws IOException {
        assertEquals("a", readAll("a"));
    }

    @Test
    void shouldHandleSingleWhitespaceCharacter() throws IOException {
        assertEquals("", readAll(" "));
    }

    @Test
    void shouldHandleSingleSlash() throws IOException {
        assertEquals("/", readAll("/"));
    }

    @Test
    void shouldHandleTwoSlashes() throws IOException {
        assertEquals("", readAll("//"));
    }

    @Test
    void shouldHandleSingleAsterisk() throws IOException {
        assertEquals("*", readAll("*"));
    }


    @Test
    void shouldHandleWhitespaceAroundComments() throws IOException {
        assertEquals(
                "abc",
                readAll("  a  /* comment */  b  // comment\n c  ")
        );
    }

    @Test
    void shouldHandleCommentsImmediatelyNextToEachOther() throws IOException {
        assertEquals(
                "abcd",
                readAll("a/**//**/b/*x*//*y*/cd")
        );
    }

    @Test
    void shouldHandleCommentsBetweenSpecialCharacters() throws IOException {
        assertEquals(
                "a+b",
                readAll("a/* comment */+/* comment */b")
        );
    }

    @Test
    void shouldHandleCommentAfterWhitespace() throws IOException {
        assertEquals(
                "abc",
                readAll("a   /* comment */   b   c")
        );
    }

    @Test
    void shouldHandleMultilineCommentContainingNewlinesAndTabs() throws IOException {
        assertEquals(
                "ab",
                readAll("a/*\n\t\r\n comment\n\t*/b")
        );
    }

    @Test
    void shouldReturnFalseAfterEndOfInput() throws IOException {
        LexerCharSource source =
                new LexerCharSource(new StringReader("abc"));

        assertTrue(source.next());
        assertEquals('a', source.getChar());

        assertTrue(source.next());
        assertEquals('b', source.getChar());

        assertTrue(source.next());
        assertEquals('c', source.getChar());

        assertFalse(source.next());
    }

    @Test
    void shouldPreserveDivisionOperator() throws IOException {
        assertEquals("a/b", readAll("a / b"));
    }

    @Test
    void shouldPreserveMultipleDivisionOperators() throws IOException {
        assertEquals("a/b/c", readAll("a / b / c"));
    }

    @Test
    void shouldPreserveAsteriskOperators() throws IOException {
        assertEquals("a*b*c", readAll("a * b * c"));
    }

    @Test
    void shouldPreserveRepeatedSlashesThatAreNotComments() throws IOException {
        assertEquals("a/b", readAll("a / b"));
    }
}
