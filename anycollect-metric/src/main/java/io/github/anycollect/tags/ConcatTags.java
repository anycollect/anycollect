package io.github.anycollect.tags;

import io.github.anycollect.metric.Tag;
import io.github.anycollect.metric.Tags;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public final class ConcatTags implements Tags {
    private final Tags base;
    private final Tags delta;
    private final int hash;

    public static Tags of(final Tags base, final Tags delta) {
        if (base.isEmpty()) {
            return delta;
        } else if (delta.isEmpty()) {
            return base;
        }
        return new ConcatTags(base, delta);
    }

    private ConcatTags(final Tags base, final Tags delta) {
        this.base = base;
        this.delta = delta;
        this.hash = Objects.hash(base, delta);
    }

    @Override
    public boolean hasTagKey(final String key) {
        return base.hasTagKey(key) || delta.hasTagKey(key);
    }

    @Nonnull
    @Override
    public Tag getTag(final String key) {
        return delta.hasTagKey(key) ? delta.getTag(key) : base.getTag(key);
    }

    @Nonnull
    @Override
    public Iterator<Tag> iterator() {
        return new ConcatIterator();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Tags)) {
            return false;
        }
        if (obj instanceof ConcatTags) {
            ConcatTags that = (ConcatTags) obj;
            return Objects.equals(this.base, that.base)
                    && Objects.equals(this.delta, that.delta);
        }
        return Tags.equals(this, (Tags) obj);
    }

    private class ConcatIterator implements Iterator<Tag> {
        private final Iterator<Tag> baseIterator;
        private final Iterator<Tag> deltaIterator;
        private boolean baseTurn = true;
        private Tag next;

        ConcatIterator() {
            this.baseIterator = base.iterator();
            this.deltaIterator = delta.iterator();
            this.next = tryNext();
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public Tag next() {
            Tag ret = this.next;
            if (ret == null) {
                throw new NoSuchElementException("no tag");
            }
            this.next = tryNext();
            return ret;
        }

        private Tag tryNext() {
            if (baseTurn) {
                if (!baseIterator.hasNext()) {
                    baseTurn = false;
                    return tryNext();
                }
                Tag tag = baseIterator.next();
                if (delta.hasTagKey(tag.getKey())) {
                    return delta.getTag(tag.getKey());
                } else {
                    return tag;
                }
            } else {
                if (!deltaIterator.hasNext()) {
                    return null;
                }
                Tag tag = deltaIterator.next();
                if (base.hasTagKey(tag.getKey())) {
                    // already used this tag when override base tags
                    return tryNext();
                } else {
                    return tag;
                }
            }
        }
    }
}
