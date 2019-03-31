package io.github.anycollect.metric.frame;

import io.github.anycollect.metric.Tags;
import io.github.anycollect.tags.ConcatTags;
import io.github.anycollect.tags.RemoveTags;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface MetricFrame {
    @Nonnull
    String getKey();

    @Nonnull
    Tags getTags();

    @Nonnull
    Tags getMeta();

    @Nonnull
    default MetricFrame prefix(@Nullable String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return this;
        }
        return new ImmutableMetricFrame(
                prefix + "." + getKey(),
                getTags(),
                getMeta()
        );
    }

    @Nonnull
    default MetricFrame frontTags(@Nonnull Tags tags) {
        return new ImmutableMetricFrame(
                getKey(),
                ConcatTags.of(tags, getTags()),
                getMeta()
        );
    }

    @Nonnull
    default MetricFrame backTags(@Nonnull Tags tags) {
        return new ImmutableMetricFrame(
                getKey(),
                ConcatTags.of(getTags(), tags),
                getMeta()
        );
    }

    @Nonnull
    default MetricFrame frontMeta(@Nonnull Tags meta) {
        return new ImmutableMetricFrame(
                getKey(),
                getTags(),
                ConcatTags.of(meta, getMeta())
        );
    }

    @Nonnull
    default MetricFrame backMeta(@Nonnull Tags meta) {
        return new ImmutableMetricFrame(
                getKey(),
                getTags(),
                ConcatTags.of(getMeta(), meta)
        );
    }

    @Nonnull
    default MetricFrame removeTag(@Nonnull String key) {
        return new ImmutableMetricFrame(
                getKey(),
                RemoveTags.of(getTags(), key),
                getMeta()
        );
    }
}
