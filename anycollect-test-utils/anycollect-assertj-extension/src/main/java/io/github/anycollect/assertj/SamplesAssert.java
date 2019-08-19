package io.github.anycollect.assertj;

import io.github.anycollect.metric.Key;
import io.github.anycollect.metric.Sample;
import io.github.anycollect.metric.Tags;
import org.assertj.core.api.ListAssert;

import java.util.List;

public class SamplesAssert extends ListAssert<Sample> {
    public static SamplesAssert assertThat(final List<Sample> actual) {
        return new SamplesAssert(actual);
    }

    public SamplesAssert(final List<Sample> actual) {
        super(actual);
    }

    public SampleAssert contains(final String key) {
        return contains(key, Tags.empty());
    }

    public SampleAssert contains(final String key, final Tags tags) {
        for (Sample family : actual) {
            if (key.equals(family.getKey()) && tags.equals(family.getTags())) {
                return new SampleAssert(family);
            }
        }
        return new SampleAssert(null);
    }

    public SampleAssert contains(final String key, final Tags tags, final Tags meta) {
        for (Sample sample : actual) {
            if (sample.getKey().equals(Key.of(key)) && tags.equals(sample.getTags()) && meta.equals(sample.getMeta())) {
                return new SampleAssert(sample);
            }
        }
        return new SampleAssert(null);
    }
}
