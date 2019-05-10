package io.github.anycollect.extensions.common.expression.std;

import io.github.anycollect.extensions.common.expression.Args;
import io.github.anycollect.extensions.common.expression.EvaluationException;
import io.github.anycollect.extensions.common.expression.MapArgs;
import io.github.anycollect.extensions.common.expression.filters.Filter;
import io.github.anycollect.extensions.common.expression.filters.JoinFilter;
import io.github.anycollect.extensions.common.expression.filters.MatchReplaceFilter;
import io.github.anycollect.extensions.common.expression.ParseException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class StdExpressionTest {
    @Test
    void complexTest() throws ParseException, EvaluationException {
        String expressionString = "\"site.${site}.host.\" | join((${host} | replace(\"\\.\", _)), \".port.${port}\")";
        List<Filter> filters = new ArrayList<>();
        filters.add(new MatchReplaceFilter());
        filters.add(new JoinFilter());
        StdExpressionFactory factory = new StdExpressionFactory(filters);
        StdExpression exp = factory.create(expressionString);
        Args args = MapArgs.builder()
                .add("site", "1")
                .add("host", "168.0.0.1")
                .add("port", "80")
                .build();
        assertThat(exp.process(args)).isEqualTo("site.1.host.168_0_0_1.port.80");
    }
}