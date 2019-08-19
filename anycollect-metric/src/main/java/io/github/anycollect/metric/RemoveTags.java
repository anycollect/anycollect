package io.github.anycollect.metric;

import javax.annotation.Nonnull;
import java.util.*;

final class RemoveTags implements Tags {
    private final Tags base;
    private final Set<Key> removed;
    private final int hash;

    static Tags of(final Tags base, final Key removed) {
        return of(base, Collections.singleton(removed));
    }

    static Tags of(final Tags base, final Set<Key> removed) {
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

    private static boolean isRemoved(final Key key, final Set<Key> removed) {
        for (final Key test : removed) {
            if (test.equals(key)) {
                return true;
            }
        }
        return false;
    }

    private RemoveTags(final Tags base, final Set<Key> removed) {
        this.base = base;
        this.removed = removed;
        this.hash = Objects.hash(base, removed);
    }

    @Override
    public boolean hasTagKey(final Key key) {
        return base.hasTagKey(key) && !isRemoved(key, removed);
    }

    @Nonnull
    @Override
    public Tag getTag(final Key key) {
        if (isRemoved(key, removed)) {
            throw new IllegalArgumentException("there is no tag value associated with " + key + " key");
        }
        return base.getTag(key);
    }

    @Nonnull
    @Override
    public String getTagValue(final Key key) {
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

    @Override
    public String toString() {
        return Tags.toString(this);
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
