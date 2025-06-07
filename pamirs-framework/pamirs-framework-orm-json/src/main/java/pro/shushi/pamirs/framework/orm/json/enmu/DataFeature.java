package pro.shushi.pamirs.framework.orm.json.enmu;

public enum DataFeature {

    WriteLongUsingToLong,

    WriteDateUsingToTimestamp,

    IgnoreMetaBit;

    DataFeature() {
        mask = (1 << ordinal());
    }

    public final int mask;

    public final int getMask() {
        return mask;
    }

    public static boolean isEnabled(int features, DataFeature feature) {
        return (features & feature.mask) != 0;
    }

    public static boolean isEnabled(int features, int fieaturesB, DataFeature feature) {
        int mask = feature.mask;

        return (features & mask) != 0 || (fieaturesB & mask) != 0;
    }

    public static int config(int features, DataFeature feature, boolean state) {
        if (state) {
            features |= feature.mask;
        } else {
            features &= ~feature.mask;
        }

        return features;
    }

    public static int of(DataFeature... features) {
        if (features == null) {
            return 0;
        }

        int value = 0;

        for (DataFeature feature : features) {
            value |= feature.mask;
        }

        return value;
    }

    public final static DataFeature[] EMPTY = new DataFeature[0];

}
