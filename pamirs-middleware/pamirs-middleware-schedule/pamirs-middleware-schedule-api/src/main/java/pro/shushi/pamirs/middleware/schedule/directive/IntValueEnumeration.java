package pro.shushi.pamirs.middleware.schedule.directive;

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
    default SELF intValueOf(int value) {
        //noinspection unchecked
        return IntValueEnumerationHelper.intValueOf((Class<SELF>) this.getClass(), value);
    }

    /**
     * get collection SELF by {@link IntValueEnumeration#intValue()}
     *
     * @param value int value
     * @return collection self
     */
    default Collection<SELF> intValuesOf(int value) {
        //noinspection unchecked
        return IntValueEnumerationHelper.intValuesOf((Class<SELF>) this.getClass(), value);
    }
}
