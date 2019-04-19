package io.github.anycollect.tags;

import io.github.anycollect.metric.Tag;
import io.github.anycollect.metric.Tags;

import javax.annotation.Nonnull;
import java.util.*;

public final class RemoveTags implements Tags {
    private final Tags base;
    private final Set<String> removed;
    private final int hash;

    public static Tags of(final Tags base, final String removed) {
        return of(base, Collections.singleton(removed));
    }

    public static Tags of(final Tags base, final Set<String> removed) {
        boolean empty = true;
        boolean anyShouldBeRemoved = false;
        for (Tag tag : base) {
            if (!removed.contains(tag.getKey())) {
                empty = false;
            } else {
                anyShouldBeRemoved = true;
            }
        }
        if (empty) {
            return Tags.empty();
        }
        if (!anyShouldBeRemoved) {
            return base;
        }
        return new RemoveTags(base, removed);
    }

    private RemoveTags(final Tags base, final Set<String> removed) {
        this.base = base;
        this.removed = removed;
        this.hash = Objects.hash(base, removed);
    }

    @Override
    public boolean hasTagKey(final String key) {
        return base.hasTagKey(key) && !removed.contains(key);
    }

    @Nonnull
    @Override
    public Tag getTag(final String key) {
        if (removed.contains(key)) {
            throw new IllegalArgumentException("there is no tag value associated with " + key + " key");
        }
        return base.getTag(key);
    }

    @Nonnull
    @Override
    public String getTagValue(final String key) {
        return getTag(key).getValue();
    }

    @Nonnull
    @Override
    public Iterator<Tag> iterator() {
        return new SkipRemovedIterator();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public Tags concat(final Tags tags) {
        return ConcatTags.of(this, tags);
    }

    @Override
    public Tags remove(final String key) {
        return RemoveTags.of(this, key);
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
        if (obj instanceof RemoveTags) {
            RemoveTags that = (RemoveTags) obj;
            return Objects.equals(this.base, that.base)
                    && Objects.equals(this.removed, that.removed);
        }
        return Tags.equals(this, (Tags) obj);
    }

    private class SkipRemovedIterator implements Iterator<Tag> {
        private final Iterator<Tag> baseIterator;
        private Tag next;

        SkipRemovedIterator() {
            this.baseIterator = base.iterator();
            this.next = tryNext();
        }

        @Override
        public boolean hasNext() {
            return this.next != null;
        }

        @Override
        public Tag next() {
            Tag ret = this.next;
            if (ret == null) {
                throw new NoSuchElementException();
            }
            this.next = tryNext();
            return ret;
        }

        private Tag tryNext() {
            if (!baseIterator.hasNext()) {
                return null;
            }
            Tag next = baseIterator.next();
            if (removed.contains(next.getKey())) {
                return tryNext();
            }
            return next;
        }
    }
}
