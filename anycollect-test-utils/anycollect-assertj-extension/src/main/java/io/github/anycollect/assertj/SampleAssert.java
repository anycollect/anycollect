package io.github.anycollect.assertj;

import io.github.anycollect.metric.Key;
import io.github.anycollect.metric.Sample;
import io.github.anycollect.metric.Stat;
import io.github.anycollect.metric.Type;
import org.assertj.core.api.AbstractAssert;

public final class SampleAssert extends AbstractAssert<SampleAssert, Sample> {
    public SampleAssert(final Sample actual) {
        super(actual, SampleAssert.class);
    }

    public static SampleAssert assertThat(final Sample actual) {
        return new SampleAssert(actual);
    }

    public SampleAssert hasKey(final Key key) {
        if (!key.equals(actual.getKey())) {
            failWithMessage("expected <%s> to have key %s but was <%s>", actual, key, actual.getKey());
        }
        return this;
    }

    public SampleAssert hasKey(final String key) {
        if (!key.equals(actual.getKey().normalize())) {
            failWithMessage("expected <%s> to have key %s but was <%s>", actual, key, actual.getKey());
        }
        return this;
    }

    public SampleAssert hasTags(final String... tags) {
        TagsAssert.assertThat(actual.getTags()).hasTags(tags);
        return this;
    }

    public SampleAssert hasMeta(final String... tags) {
        TagsAssert.assertThat(actual.getMeta()).hasTags(tags);
        return this;
    }

    public SampleAssert hasValue(final double value) {
        return hasMetric(Stat.VALUE, Type.GAUGE, "", value);
    }

    public SampleAssert hasMetric(final Stat stat, final Type type, final String unit, final double value) {
        if (!stat.equals(actual.getStat())
                || !type.equals(actual.getType())
                || !unit.equals(actual.getUnit())
                || value != actual.getValue()) {
            failWithMessage("Expected <%s> to have measurement of stat <%s>, type <%s>, unit <%s> and value <%s>",
                    actual, stat, type, unit, value);
        }
        return this;
    }
}
