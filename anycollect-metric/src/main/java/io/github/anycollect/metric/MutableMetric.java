package io.github.anycollect.metric;

import javax.annotation.Nullable;

public interface MutableMetric {
    MutableMetric withPrefix(@Nullable String prefix);

    MutableMetric frontTags(Tags prefix);

    MutableMetric backTags(Tags suffix);

    MutableMetric frontMeta(Tags prefix);

    MutableMetric backMeta(Tags suffix);

    MutableMetric removeTag(String key);

    MutableMetric removeMeta(String key);

    Metric commit();
}
