package io.github.anycollect.extensions.common.expression;

import io.github.anycollect.extensions.common.expression.filters.JoinFilter;
import io.github.anycollect.extensions.common.expression.filters.MatchReplaceFilter;
import io.github.anycollect.extensions.common.expression.filters.TrimFilter;
import io.github.anycollect.extensions.common.expression.parser.TokenType;
import io.github.anycollect.extensions.common.expression.std.StdExpressionFactory;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ExpressionExtensionTest {
    private StdExpressionFactory expressions;

    @BeforeEach
    void setUp() {
        expressions = new StdExpressionFactory(Lists.list(
                new JoinFilter(Collections.singletonList("append")),
                new MatchReplaceFilter(),
                new TrimFilter()
        ));
    }

    @Test
    void componentTest() throws ParseException, EvaluationException {
        String expressionString = "\"site.${site}.host.\" | append((${host} | replace(\"\\.\", _)), \".port.${port}\" | trim)";
        Args args = MapArgs.builder()
                .add("site", "1")
                .add("host", "168.0.0.1")
                .add("port", "80 ")
                .build();
        Expression expression = expressions.create(expressionString);
        String processed = expression.process(args);
        assertThat(processed).isEqualTo("site.1.host.168_0_0_1.port.80");
    }

    @Test
    void failIfTokenIsNotRecognized() {
        String expressionString = "\"site.${site}\" | wrongFilter";
        ParseException ex = Assertions.assertThrows(ParseException.class, () -> expressions.create(expressionString));
        assertThat(ex).hasMessageContaining("wrongFilter");
    }

    @Test
    void failIfUnexpectedEnd() {
        String expressionString = "test | trim(";
        ParseException ex = Assertions.assertThrows(ParseException.class, () -> expressions.create(expressionString));
        assertThat(ex).hasMessageContaining("unexpected end");
    }

    @Test
    void failIfMissedToken() {
        String expressionString = "test | trim(test |";
        ParseException ex = Assertions.assertThrows(ParseException.class, () -> expressions.create(expressionString));
        assertThat(ex).hasMessageContaining(TokenType.FILTER.toString());
    }

    @Test
    void failIfMissedCloseBracketBeforeOpenBracket() {
        String expressionString = "test | trim)";
        ParseException ex = Assertions.assertThrows(ParseException.class, () -> expressions.create(expressionString));
        assertThat(ex).hasMessageContaining(")");
    }

    @Test
    void ifUnexpectedTokenThenFail1() {
        String expressionString = "(test | trim\"";
        ParseException ex = Assertions.assertThrows(ParseException.class, () -> expressions.create(expressionString));
        assertThat(ex).hasMessageContaining(TokenType.CLOSE_BRACKET.toString());
    }

    @Test
    void ifUnexpectedTokenThenFail2() {
        String expressionString = "test | replace(t, l";
        ParseException ex = Assertions.assertThrows(ParseException.class, () -> expressions.create(expressionString));
        assertThat(ex).hasMessageContaining(TokenType.CLOSE_BRACKET.toString());
    }

    @Test
    void ifUnexpectedTokenThenFail3() {
        String expressionString = "test | replace(trim, l)";
        ParseException ex = Assertions.assertThrows(ParseException.class, () -> expressions.create(expressionString));
        assertThat(ex).hasMessageContaining(TokenType.VARIABLE.toString());
    }

    @Test
    void ifUnexpectedSymbolThenFail() {
        String expressionString = "${arg} | @test";
        ParseException ex = Assertions.assertThrows(ParseException.class, () -> expressions.create(expressionString));
        assertThat(ex).hasMessageContaining("@test");
    }
}
