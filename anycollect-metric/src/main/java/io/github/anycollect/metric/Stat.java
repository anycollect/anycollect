package io.github.anycollect.metric;

public interface Stat {
    Percentile PERCENTILE_50 = new Percentile(Stat.MAX, 50);
    Percentile PERCENTILE_75 = new Percentile(Stat.MAX, 75);
    Percentile PERCENTILE_90 = new Percentile(Stat.MAX, 90);
    Percentile PERCENTILE_95 = new Percentile(Stat.MAX, 95);
    Percentile PERCENTILE_99 = new Percentile(Stat.MAX, 99);
    Percentile PERCENTILE_999 = new Percentile(Stat.MAX, 999);

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
        return new Percentile(max(), percentile);
    }

    static Percentile percentile(Stat stat, double percentile) {
        return new Percentile(stat, percentile);
    }

    static Percentile percentile(int num) {
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
        return new Percentile(max(), num);
    }

    static Percentile percentile(Stat stat, int num) {
        return new Percentile(stat, num);
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
