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
        return new Percentile(max(), percentile);
    }

    static Percentile percentile(Stat stat, double percentile) {
        return new Percentile(stat, percentile);
    }

    static Percentile percentile(int num) {
        return new Percentile(max(), num);
    }

    static Percentile percentile(Stat stat, int num) {
        return new Percentile(stat, num);
    }

    static boolean isValid(final Stat stat) {
        return stat == min()
                || stat == max()
                || stat == mean()
                || stat == std()
                || stat == value()
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

    Stat VALUE = new Stat() {
        @Override
        public StatType getType() {
            return StatType.UNKNOWN;
        }

        @Override
        public String getTagValue() {
            return "value";
        }
    };

    StatType getType();

    String getTagValue();

    static Stat parse(final String stat) {
        return StatHelper.parse(stat);
    }
}
