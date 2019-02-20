package io.github.anycollect.assertj;

import io.github.anycollect.metric.Tags;
import org.assertj.core.api.AbstractAssert;

public final class TagsAssert extends AbstractAssert<TagsAssert, Tags> {
    public TagsAssert(final Tags actual) {
        super(actual, TagsAssert.class);
    }

    public static TagsAssert assertThat(final Tags actual) {
        return new TagsAssert(actual);
    }

    public TagsAssert hasTags(final String... tags) {
        if (tags.length % 2 != 0) {
            throw new IllegalArgumentException("tags length must be even");
        }

        int actualNumberOfTags = actual.getTagKeys().size();
        int expectedNumberOfTags = tags.length / 2;
        if (actualNumberOfTags != expectedNumberOfTags) {
            failWithMessage("Expected <%s> tags but was <%s>: %s",
                    expectedNumberOfTags,
                    actualNumberOfTags,
                    actual);
        }
        for (int tagNum = 0; tagNum < expectedNumberOfTags; tagNum++) {
            String key = tags[2 * tagNum];
            String expectedTagValue = tags[2 * tagNum + 1];
            if (!actual.hasTagKey(key)) {
                failWithMessage("Expected tags to have tag with key: <%s>", key);
            }
            String actualTagValue = actual.getTagValue(key);
            if (!actualTagValue.equals(expectedTagValue)) {
                failWithMessage("Expected tag <%s> to have value <%s> but was <%s>",
                        key, expectedTagValue, actualTagValue);
            }
        }
        return this;
    }
}
