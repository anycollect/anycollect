package io.github.anycollect.metric;

import lombok.EqualsAndHashCode;

import java.util.*;

@EqualsAndHashCode(of = "tags")
public final class Tags implements Iterable<Tag> {
    private final Map<String, String> tags;
    private final List<Tag> tagList;

    public Tags(final List<Tag> tagList) {
        Objects.requireNonNull(tagList, "list of tags must not be null");
        this.tags = new LinkedHashMap<>(tagList.size());
        this.tagList = Collections.unmodifiableList(new ArrayList<>(tagList));
        for (Tag tag : this.tagList) {
            this.tags.put(tag.getKey(), tag.getValue());
        }
    }

    public boolean hasTagKey(final String key) {
        Objects.requireNonNull(key, "tag key must not be null");
        return tags.containsKey(key);
    }

    public String getTagValue(final String key) {
        Objects.requireNonNull(key, "tag key must not be null");
        String value = tags.get(key);
        if (value == null) {
            throw new IllegalArgumentException("there is no tag value associated with " + key + " key");
        }
        return value;
    }

    public Set<String> getTagKeys() {
        return Collections.unmodifiableSet(tags.keySet());
    }

    @Override
    public Iterator<Tag> iterator() {
        return tagList.iterator();
    }
}
