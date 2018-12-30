package io.github.anycollect.metric;

public enum Type {
    RATE("rate"), COUNT("count"), GAUGE("gauge"), COUNTER("counter"), TIMESTAMP("timestamp");
    private final String tagValue;

    Type(final String tagValue) {
        this.tagValue = tagValue;
    }

    public String getTagValue() {
        return tagValue;
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
