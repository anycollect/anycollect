package io.github.anycollect.metric;

public enum CommonTags {
    METRIC_KEY("what"),
    METRIC_TYPE("mtype"),
    UNIT("unit"),
    STAT("stat"),
    BUCKET("bin_max");

    private final String key;

    CommonTags(final String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
