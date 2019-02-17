package io.github.anycollect.metric;

/**
 * Less than or Equal to [max] bucket of events
 */
public final class LeBucket implements Stat, Comparable<LeBucket> {
    private static final LeBucket INF = new LeBucket(Double.POSITIVE_INFINITY);
    private final double max;

    public static LeBucket inf() {
        return INF;
    }

    public static LeBucket of(final double max) {
        if (max == Double.POSITIVE_INFINITY) {
            return inf();
        }
        return new LeBucket(max);
    }

    private LeBucket(final double max) {
        this.max = max;
    }

    @Override
    public StatType getType() {
        return StatType.LE_BUCKET;
    }

    @Override
    public String getTagValue() {
        return "le_" + Double.toString(max);
    }

    public double getMax() {
        return max;
    }

    @Override
    public String toString() {
        return getTagValue();
    }

    @Override
    public int compareTo(final LeBucket that) {
        return Double.compare(max, that.max);
    }
}
