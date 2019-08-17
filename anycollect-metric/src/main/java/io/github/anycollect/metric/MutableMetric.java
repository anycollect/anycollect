package io.github.anycollect.metric;

import org.apiguardian.api.API;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * Mutable view of metric.
 * Contains modify methods to efficiently change internal state of {@link Metric}.
 * The common way to create an instance is {@link Metric#modify()}.
 * To perform changes and create immutable {@link Metric} method {@link MutableMetric#commit()} is used.
 */
@API(since = "0.1.0", status = API.Status.EXPERIMENTAL)
@NotThreadSafe
public interface MutableMetric {
    /**
     * Appends prefix to the key.
     *
     * @param prefix - string to be added to key {@link Metric#getKey()}
     * @return {@link this}
     * @see Key#withPrefix(String)
     */
    @Nonnull
    MutableMetric withPrefix(@Nullable String prefix);

    /**
     * Append tags in front of existing ones.
     *
     * @param prefix - tags to be added
     * @return {@link this}
     * @see Tags#concat(Tags)
     */
    @Nonnull
    MutableMetric frontTags(@Nullable Tags prefix);

    /**
     * Append tags in back of existing ones.
     *
     * @param suffix - tags to be added
     * @return {@link this}
     * @see Tags#concat(Tags)
     */
    @Nonnull
    MutableMetric backTags(@Nullable Tags suffix);

    /**
     * Append meta in front of existing ones.
     *
     * @param prefix - meta to be added
     * @return {@link this}
     * @see Tags#concat(Tags)
     */
    @Nonnull
    MutableMetric frontMeta(@Nullable Tags prefix);

    /**
     * Append meta in back of existing ones.
     *
     * @param suffix - meta to be added
     * @return {@link this}
     * @see Tags#concat(Tags)
     */
    @Nonnull
    MutableMetric backMeta(@Nullable Tags suffix);

    /**
     * Removes tag with key from tags.
     *
     * @param key - the key of tag to be deleted
     * @return {@link this}
     */
    @Nonnull
    MutableMetric removeTag(@Nullable Key key);

    /**
     * Removes tag with key from meta.
     *
     * @param key - the key of meta to be deleted
     * @return {@link this}
     */
    @Nonnull
    MutableMetric removeMeta(@Nullable Key key);

    /**
     * Returns immutable metric with all modifications applied.
     *
     * @return immutable metric with all modifications applied
     */
    @Nonnull
    Metric commit();
}
