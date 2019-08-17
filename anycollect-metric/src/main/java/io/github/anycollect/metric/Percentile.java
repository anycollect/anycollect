package io.github.anycollect.metric;

import lombok.EqualsAndHashCode;
import org.apiguardian.api.API;

import java.util.Objects;

@EqualsAndHashCode
@API(since = "0.1.0", status = API.Status.EXPERIMENTAL)
public final class Percentile implements Stat, Comparable<Percentile> {
    private static final Percentile PERCENTILE_50 = new Percentile(Stat.MAX, 50);
    private static final Percentile PERCENTILE_75 = new Percentile(Stat.MAX, 75);
    private static final Percentile PERCENTILE_90 = new Percentile(Stat.MAX, 90);
    private static final Percentile PERCENTILE_95 = new Percentile(Stat.MAX, 95);
    private static final Percentile PERCENTILE_99 = new Percentile(Stat.MAX, 99);
    private static final Percentile PERCENTILE_999 = new Percentile(Stat.MAX, 999);
    private final Stat stat;
    private final int num;

    static Percentile of(final Stat stat, final double percentile) {
        if (stat.equals(Stat.max())) {
            return of(percentile);
        } else {
            return new Percentile(stat, percentile);
        }
    }

    static Percentile of(final double percentile) {
        if (percentile == 0.5) {
            return PERCENTILE_50;
        } else if (percentile == 0.75) {
            return PERCENTILE_75;
        } else if (percentile == 0.9) {
            return PERCENTILE_90;
        } else if (percentile == 0.95) {
            return PERCENTILE_95;
        } else if (percentile == 0.99) {
            return PERCENTILE_99;
        } else if (percentile == 0.999) {
            return PERCENTILE_999;
        }
        return new Percentile(Stat.max(), percentile);
    }

    static Percentile of(final Stat stat, final int num) {
        if (stat.equals(Stat.max())) {
            return of(num);
        } else {
            return new Percentile(stat, num);
        }
    }

    static Percentile of(final int num) {
        switch (num) {
            case 50:
                return PERCENTILE_50;
            case 75:
                return PERCENTILE_75;
            case 90:
                return PERCENTILE_90;
            case 95:
                return PERCENTILE_95;
            case 99:
                return PERCENTILE_99;
            case 999:
                return PERCENTILE_999;
        }
        return new Percentile(Stat.max(), num);
    }

    private Percentile(final Stat stat, final double percentile) {
        Objects.requireNonNull(stat, "stat must not be null");
        if (percentile <= 0) {
            throw new IllegalArgumentException("percentile must be positive");
        }
        this.stat = stat;
        this.num = (int) ((int) (percentile * 1000) % 10 == 0 ? percentile * 100 : percentile * 1000);
    }

    private Percentile(final Stat stat, final int num) {
        Objects.requireNonNull(stat, "stat must not be null");
        if (num <= 0) {
            throw new IllegalArgumentException("percentile must be positive");
        }
        this.stat = stat;
        this.num = num;
    }

    public Stat getStat() {
        return stat;
    }

    @Override
    public StatType getType() {
        return StatType.PERCENTILE;
    }

    @Override
    public String getTagValue() {
        return stat.getTagValue() + "_" + num;
    }

    @Override
    public String toString() {
        return getTagValue();
    }

    @Override
    public int compareTo(final Percentile that) {
        return Integer.compare(num, that.num);
    }
}
