package pro.shushi.pamirs.core.common.directive;

import java.util.Collection;

/**
 * @author Adamancy Zhang
 * @date 2020-10-22 12:28
 */
public interface IntValueEnumeration<SELF extends Enum<SELF> & IntValueEnumeration<SELF>> extends IntValue {

    /**
     * get SELF by {@link IntValueEnumeration#intValue()}
     *
     * @param value int value
     * @return self
     */
    @SuppressWarnings("unchecked")
    default SELF intValueOf(int value) {
        return IntValueEnumerationHelper.intValueOf((Class<SELF>) this.getClass(), value);
    }

    /**
     * get collection SELF by {@link IntValueEnumeration#intValue()}
     *
     * @param value int value
     * @return collection self
     */
    @SuppressWarnings("unchecked")
    default Collection<SELF> intValuesOf(int value) {
        return IntValueEnumerationHelper.intValuesOf((Class<SELF>) this.getClass(), value);
    }
}
