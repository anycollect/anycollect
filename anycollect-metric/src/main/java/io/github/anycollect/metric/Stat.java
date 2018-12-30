package io.github.anycollect.metric;

import java.util.Objects;

public interface Stat {
    static Stat min() {
        return MIN;
    }

    static Stat max() {
        return MAX;
    }

    static Stat mean() {
        return MEAN;
    }

    static Stat std() {
        return STD;
    }

    static Stat percentile(int num) {
        return new Percentile(num);
    }

    static boolean isValid(final Stat stat) {
        return stat == min()
                || stat == max()
                || stat == mean()
                || stat == std()
                || stat.getClass().equals(Percentile.class);
    }

    Stat MIN = new Stat() {
        @Override
        public StatType getType() {
            return StatType.MIN;
        }

        @Override
        public String getTagValue() {
            return "min";
        }
    };

    Stat MAX = new Stat() {
        @Override
        public StatType getType() {
            return StatType.MAX;
        }

        @Override
        public String getTagValue() {
            return "max";
        }
    };

    Stat MEAN = new Stat() {
        @Override
        public StatType getType() {
            return StatType.MEAN;
        }

        @Override
        public String getTagValue() {
            return "mean";
        }
    };

    Stat STD = new Stat() {
        @Override
        public StatType getType() {
            return StatType.STD;
        }

        @Override
        public String getTagValue() {
            return "std";
        }
    };

    StatType getType();

    String getTagValue();

    static Stat parse(final String stat) {
        Objects.requireNonNull(stat, "tag value must not be null");
        if (stat.equals(min().getTagValue())) {
            return min();
        }
        if (stat.equals(max().getTagValue())) {
            return max();
        }
        if (stat.equals(mean().getTagValue())) {
            return mean();
        }
        if (stat.equals(std().getTagValue())) {
            return std();
        }
        if (stat.endsWith("_NUM")) {
            int num = Integer.parseInt(stat.substring(0, stat.length() - 4));
            return percentile(num);
        }
        throw new IllegalArgumentException("unrecognized stat: " + stat);
    }
}
