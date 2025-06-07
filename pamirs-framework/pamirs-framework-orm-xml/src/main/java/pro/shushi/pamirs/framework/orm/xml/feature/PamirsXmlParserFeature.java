package pro.shushi.pamirs.framework.orm.xml.feature;

public enum PamirsXmlParserFeature {

    ParseEnumCaseSensitive,

    FillTagToObject;;

    PamirsXmlParserFeature() {
        mask = (1 << ordinal());
    }

    public final int mask;

    public final int getMask() {
        return mask;
    }

    public static boolean isEnabled(int features, PamirsXmlParserFeature feature) {
        return (features & feature.mask) != 0;
    }

    public static boolean isEnabled(int features, int fieaturesB, PamirsXmlParserFeature feature) {
        int mask = feature.mask;

        return (features & mask) != 0 || (fieaturesB & mask) != 0;
    }

    public static int config(int features, PamirsXmlParserFeature feature, boolean state) {
        if (state) {
            features |= feature.mask;
        } else {
            features &= ~feature.mask;
        }

        return features;
    }

    public static int of(PamirsXmlParserFeature... features) {
        if (features == null) {
            return 0;
        }

        int value = 0;

        for (PamirsXmlParserFeature feature : features) {
            value |= feature.mask;
        }

        return value;
    }

    public final static PamirsXmlParserFeature[] EMPTY = new PamirsXmlParserFeature[0];

}
