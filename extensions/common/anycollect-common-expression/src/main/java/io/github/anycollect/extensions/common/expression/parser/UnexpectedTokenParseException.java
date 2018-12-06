package io.github.anycollect.extensions.common.expression.parser;

import java.util.Arrays;

public class UnexpectedTokenParseException extends ParseException {
    public UnexpectedTokenParseException(final Token found, final TokenType expected) {
        super(String.format("expecting: %s but found: %s: \"%s\"",
                expected, found.getType(), found.getSequence()));
    }

    public UnexpectedTokenParseException(final Token found, final TokenType... expected) {
        super(String.format("expecting: %s but found: %s: \"%s\"",
                Arrays.asList(expected), found.getType(), found.getSequence()));
    }
}
