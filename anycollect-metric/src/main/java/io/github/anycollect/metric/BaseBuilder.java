package io.github.anycollect.metric;

import javax.annotation.Nonnull;
import java.util.Objects;

public abstract class BaseBuilder<T extends BaseBuilder<T>> {
    private final ImmutableTags.Builder tagsBuilder = new ImmutableTags.Builder();
    private final ImmutableTags.Builder metaBuilder = new ImmutableTags.Builder();

    protected abstract T self();

    protected ImmutableTags.Builder getTagsBuilder() {
        return tagsBuilder;
    }

    protected ImmutableTags.Builder getMetaBuilder() {
        return metaBuilder;
    }

    protected T key(@Nonnull final String value) {
        tagsBuilder.tag(CommonTags.METRIC_KEY.getKey(), value);
        return self();
    }

    protected T type(@Nonnull final Type value) {
        tagsBuilder.tag(CommonTags.METRIC_TYPE.getKey(), value.getTagValue());
        return self();
    }

    protected T unit(@Nonnull final String value) {
        tagsBuilder.tag(CommonTags.UNIT.getKey(), value);
        return self();
    }

    protected T nanos() {
        return unit("ns");
    }

    protected T stat(@Nonnull final Stat stat) {
        if (!Stat.isValid(stat)) {
            throw new IllegalArgumentException("stat " + stat + " is not valid");
        }
        tagsBuilder.tag(CommonTags.STAT.getKey(), stat.getTagValue());
        return self();
    }

    public T tag(@Nonnull final String key, @Nonnull final String value) {
        if (CommonTags.METRIC_KEY.getKey().equals(key)) {
            return key(value);
        }
        if (CommonTags.METRIC_TYPE.getKey().equals(key)) {
            return type(Type.parse(value));
        }
        if (CommonTags.UNIT.getKey().equals(key)) {
            return unit(value);
        }
        if (CommonTags.STAT.getKey().equals(key)) {
            return stat(Stat.parse(value));
        }
        tagsBuilder.tag(key, value);
        return self();
    }

    public T meta(@Nonnull final String key, @Nonnull final String value) {
        metaBuilder.tag(key, value);
        return self();
    }

    public T concatTags(@Nonnull final Tags addition) {
        Objects.requireNonNull(addition, "addition must not be null");
        for (Tag tag : addition) {
            tag(tag.getKey(), tag.getValue());
        }
        return self();
    }

    public T concatMeta(@Nonnull final Tags addition) {
        Objects.requireNonNull(addition, "addition must not be null");
        for (Tag tag : addition) {
            meta(tag.getKey(), tag.getValue());
        }
        return self();
    }
}
