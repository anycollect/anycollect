package io.github.anycollect.extensions.common.expression.ast;

import io.github.anycollect.extensions.common.expression.EvaluationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VariableExpressionNodeTest {
    @Test
    void gettingUnresolvedVariableMustFail() {
        VariableExpressionNode node = new VariableExpressionNode("${arg}");
        assertThat(node.isResolved()).isFalse();
        EvaluationException ex = Assertions.assertThrows(EvaluationException.class, node::getValue);
        assertThat(ex).hasMessageContaining("arg");
    }

    @Test
    void variablePassedToConstructorMustHaveCorrectSyntax() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new VariableExpressionNode("arg"));
    }
}