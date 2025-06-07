package pro.shushi.pamirs.framework.common.utils;

import pro.shushi.pamirs.framework.common.utils.kryo.KryoUtils;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

import java.io.Serializable;
import java.util.List;

public class ObjectUtils {

    /**
     * Deep clone an {@code Object} using serialization.
     *
     * @param <T>    the type of the object involved
     * @param object the {@code Serializable} object to clone
     * @return the cloned object
     */
    public static <T extends Serializable> T clone(final T object) {
        if (object == null) {
            return null;
        }
        return KryoUtils.get().copy(object);
    }

    /**
     * Deep clone an {@code Object} using serialization.
     *
     * @param <T>    the type of the object involved
     * @param object the {@code Serializable} object to clone
     * @return the cloned object
     */
    public static <T extends Serializable> List<T> clone(final List<T> object) {
        if (object == null) {
            return null;
        }
        return KryoUtils.get().copy(object);
    }

    @SuppressWarnings("rawtypes")
    public static Boolean equals(Object a, Object b) {
        if (a instanceof IEnum && b instanceof IEnum) {
            return ((IEnum) a).value().equals(((IEnum) b).value());
        }
        if (null == a && null == b) {
            return true;
        }
        if (null == a) {
            return false;
        } else {
            return a.equals(b);
        }
    }

}
