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

    static Stat percentile(int num) {
        return new Percentile(max(), num);
    }

    static Stat percentile(Stat stat, int num) {
        return new Percentile(stat, num);
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
        return StatHelper.parse(stat);
    }
}