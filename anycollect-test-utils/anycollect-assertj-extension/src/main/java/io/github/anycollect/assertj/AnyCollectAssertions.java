package io.github.anycollect.assertj;

import io.github.anycollect.metric.Sample;
import io.github.anycollect.metric.Tags;
import org.assertj.core.api.Assertions;

import java.util.List;

public final class AnyCollectAssertions extends Assertions {
    private AnyCollectAssertions() {
    }

    public static TagsAssert assertThat(final Tags actual) {
        return TagsAssert.assertThat(actual);
    }

    public static SampleAssert assertThat(final Sample actual) {
        return SampleAssert.assertThat(actual);
    }

    public static SamplesAssert assertThatSamples(final List<Sample> actual) {
        return new SamplesAssert(actual);
    }
}
