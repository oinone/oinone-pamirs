package pro.shushi.pamirs.meta.common.enmu;

import pro.shushi.pamirs.meta.common.util.UnsafeUtil;

import java.io.Serializable;
import java.util.Map;

/**
 * 枚举接口
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/17 1:53 上午
 */
public interface IEnum<T extends Serializable> {

    default String displayName() {
        return (String) UnsafeUtil.getValue(this, "displayName");
    }

    default String name() {
        return (String) UnsafeUtil.getValue(this, "name");
    }

    @SuppressWarnings("unchecked")
    default T value() {
        return (T) UnsafeUtil.getValue(this, "value");
    }

    default String help() {
        return (String) UnsafeUtil.getValue(this, "help");
    }

    default Map<String, Object> attributes() {
        return null;
    }

    default int ordinal() {
        return 10;
    }

}
