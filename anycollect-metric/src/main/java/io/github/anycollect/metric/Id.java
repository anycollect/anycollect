package io.github.anycollect.metric;

import javax.annotation.Nonnull;
import java.util.Set;

public interface Id {
    Tags getTags();

    boolean hasTagKey(@Nonnull String key);

    String getTagValue(@Nonnull String key);

    Set<String> getTagKeys();

    Tags getMetaTags();

    boolean hasMetaTagKey(@Nonnull String key);

    String getMetaTagValue(@Nonnull String key);

    Set<String> getMetaTagKeys();
}
