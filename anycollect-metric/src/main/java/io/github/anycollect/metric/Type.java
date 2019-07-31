package io.github.anycollect.metric;

public enum Type {
    RATE("rate", "r"),
    GAUGE("gauge", "g"),
    COUNTER("counter", "c"),
    TIMESTAMP("timestamp", "t"),
    AGGREGATE("aggregate", "a");

    private final String tagValue;
    private final String shortString;

    Type(final String tagValue, final String shortString) {
        this.tagValue = tagValue;
        this.shortString = shortString;
    }

    public String getTagValue() {
        return tagValue;
    }

    @Override
    public String toString() {
        return shortString;
    }

    public static Type parse(final String value) {
        for (Type type : values()) {
            if (type.getTagValue().equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("string " + value + " is not represent any of metric types");
    }
}
