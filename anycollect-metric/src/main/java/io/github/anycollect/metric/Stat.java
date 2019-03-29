package io.github.anycollect.metric;

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

    static Stat value() {
        return VALUE;
    }

    static Percentile percentile(double percentile) {
        return Percentile.of(percentile);
    }

    static Percentile percentile(Stat stat, double percentile) {
        return Percentile.of(stat, percentile);
    }

    static Percentile percentile(int num) {
        return Percentile.of(num);
    }

    static Percentile percentile(Stat stat, int num) {
        return Percentile.of(stat, num);
    }

    static LeBucket le(double max) {
        return LeBucket.of(max);
    }

    static LeBucket leInf() {
        return LeBucket.inf();
    }

    static boolean isValid(final Stat stat) {
        return stat == min()
                || stat == max()
                || stat == mean()
                || stat == std()
                || stat == value()
                || stat.getClass().equals(Percentile.class)
                || stat.getClass().equals(LeBucket.class);
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

        @Override
        public String toString() {
            return getTagValue();
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

        @Override
        public String toString() {
            return getTagValue();
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

        @Override
        public String toString() {
            return getTagValue();
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

        @Override
        public String toString() {
            return getTagValue();
        }
    };

    Stat VALUE = new Stat() {
        @Override
        public StatType getType() {
            return StatType.UNKNOWN;
        }

        @Override
        public String getTagValue() {
            return "value";
        }

        @Override
        public String toString() {
            return getTagValue();
        }
    };

    StatType getType();

    String getTagValue();

    static Stat parse(final String stat) {
        return StatHelper.parse(stat);
    }
}
