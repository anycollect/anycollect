package io.github.anycollect.jmh;

import io.github.anycollect.metric.ImmutableTags;
import io.github.anycollect.metric.Tag;
import io.github.anycollect.metric.Tags;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Iterator;

public class Utils {
    public static Tags generate(final int numberOfTags) {
        ImmutableTags.Builder builder = Tags.builder();
        for (int i = 0; i < numberOfTags; i++) {
            String key = RandomStringUtils.random(5);
            String value = RandomStringUtils.random(10);
            builder.tag(key, value);
        }
        return builder.build();
    }

    public static String stringify(final Tags tags) {
        StringBuilder builder = new StringBuilder();
        Iterator<Tag> iterator = tags.iterator();
        while (iterator.hasNext()) {
            Tag tag = iterator.next();
            builder.append(tag.getKey()).append("=").append("\"").append(tag.getValue()).append("\"");
            if (iterator.hasNext()) {
                builder.append(",");
            }
        }
        return builder.toString();
    }
}
