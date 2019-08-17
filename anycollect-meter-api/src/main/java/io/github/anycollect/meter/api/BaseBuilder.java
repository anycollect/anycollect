package io.github.anycollect.meter.api;

import io.github.anycollect.metric.Key;
import io.github.anycollect.metric.Stat;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.metric.Type;

import javax.annotation.Nonnull;
import java.util.Objects;

abstract class BaseBuilder<T extends BaseBuilder<T>> {
    private Key key;
    private String unit = "";
    private Stat stat;
    private Type type;
    private final Tags.Builder tagsBuilder = new Tags.Builder();
    private final Tags.Builder metaBuilder = new Tags.Builder();

    protected abstract T self();

    protected Key getKey() {
        return key;
    }

    protected String getUnit() {
        return unit;
    }

    protected Stat getStat() {
        return stat;
    }

    protected Type getType() {
        return type;
    }

    protected Tags.Builder getTagsBuilder() {
        return tagsBuilder;
    }

    protected Tags.Builder getMetaBuilder() {
        return metaBuilder;
    }

    protected T key(@Nonnull final String key) {
        return key(Key.of(key));
    }

    protected T key(@Nonnull final Key key) {
        Objects.requireNonNull(key, "key must not be null");
        this.key = key;
        return self();
    }

    protected T type(@Nonnull final Type type) {
        Objects.requireNonNull(type, "type must not be null");
        this.type = type;
        return self();
    }

    protected T unit(@Nonnull final String unit) {
        Objects.requireNonNull(unit, "unit must not be null");
        this.unit = unit;
        return self();
    }

    protected T nanos() {
        return unit("ns");
    }

    protected T stat(@Nonnull final Stat stat) {
        Objects.requireNonNull(stat, "stat must not be null");
        if (!Stat.isValid(stat)) {
            throw new IllegalArgumentException("stat " + stat + " is not valid");
        }
        this.stat = stat;
        return self();
    }

    public T tag(@Nonnull final String key, @Nonnull final String value) {
        tagsBuilder.tag(key, value);
        return self();
    }

    public T meta(@Nonnull final String key, @Nonnull final String value) {
        metaBuilder.tag(key, value);
        return self();
    }

    public T concatTags(@Nonnull final Tags addition) {
        Objects.requireNonNull(addition, "addition must not be null");
        tagsBuilder.concat(addition);
        return self();
    }

    public T concatMeta(@Nonnull final Tags addition) {
        Objects.requireNonNull(addition, "addition must not be null");
        metaBuilder.concat(addition);
        return self();
    }
}
