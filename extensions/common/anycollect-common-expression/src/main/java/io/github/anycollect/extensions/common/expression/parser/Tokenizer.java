package io.github.anycollect.extensions.common.expression.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Tokenizer {
    private final List<TokenDefinition> tokenDefinitions;

    public static Builder builder() {
        return new Builder();
    }

    private Tokenizer(final Builder builder) {
        this.tokenDefinitions = new ArrayList<>(builder.tokenDefinitions);
    }

    public List<Token> tokenize(final String input) throws ParseException {
        String current = input;
        List<Token> tokens = new ArrayList<>();
        while (!current.isEmpty()) {
            boolean match = false;
            for (TokenDefinition tokenDefinition : tokenDefinitions) {
                Matcher matcher = tokenDefinition.pattern.matcher(current);
                if (matcher.find()) {
                    match = true;
                    tokens.add(Token.of(tokenDefinition.type, matcher.group().trim()));
                    current = matcher.replaceFirst("");
                    current = current.trim();
                    break;
                }
            }
            if (!match) {
                throw new ParseException("unexpected symbol found in: " + current);
            }
        }
        return tokens;
    }

    private static class TokenDefinition {
        private final TokenType type;
        private final Pattern pattern;

        TokenDefinition(final TokenType type, final Pattern pattern) {
            this.type = type;
            this.pattern = pattern;
        }
    }

    public static final class Builder {
        private final List<TokenDefinition> tokenDefinitions = new ArrayList<>();

        public Builder add(final TokenType type, final String regex) {
            this.tokenDefinitions.add(new TokenDefinition(type, Pattern.compile("^(" + regex + ")")));
            return this;
        }

        public Tokenizer build() {
            return new Tokenizer(this);
        }
    }
}
