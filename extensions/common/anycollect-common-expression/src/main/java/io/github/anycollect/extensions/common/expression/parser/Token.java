package io.github.anycollect.extensions.common.expression.parser;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public final class Token {
    private final TokenType type;
    private final String sequence;

    public static Token of(final TokenType type, final String sequence) {
        return new Token(type, sequence);
    }

    private Token(final TokenType type, final String sequence) {
        this.type = type;
        this.sequence = sequence;
    }
}
