package pro.shushi.pamirs.meta.api.core.orm.systems.directive;

import pro.shushi.pamirs.meta.api.session.PamirsKernelThreadLocal;
import pro.shushi.pamirs.meta.api.session.PamirsThreadLocal;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 模型指令抽象方法
 * <p>
 * 2020/7/13 3:17 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class AbstractModelDirectiveApi {

    public static final String META_BIT = "META_BIT";

    protected Long getMetaBit(Object obj) {
        if (validObj(obj)) {
            Object bit = FieldUtils.getFieldValue(obj, META_BIT);
            if (bit == null) {
                return 0L;
            }
            if (bit instanceof Long) {
                return (Long) bit;
            }
            return Long.valueOf(String.valueOf(bit));
        } else {
            return 0L;
        }
    }

    protected <T> T setMetaBit(T listOrObject, long metaBit) {
        if (null == listOrObject) {
            return null;
        }
        if (listOrObject instanceof List) {
            ((List) listOrObject).forEach(v -> setMetaBit(v, metaBit));
        } else {
            if (validObj(listOrObject)) {
                set(listOrObject, metaBit);
            }
        }
        return listOrObject;
    }

    protected boolean validObj(Object obj) {
        return obj instanceof D || obj instanceof Map || obj instanceof PamirsThreadLocal || obj instanceof PamirsKernelThreadLocal;
    }

    protected void setMetaBitValue(Object obj, long disable, long enable) {
        set(obj, (getMetaBit(obj) & ~disable) | enable);
    }

    protected void setMetaBitValues(Object listOrObject, long disable, long enable) {
        if (null == listOrObject) {
            return;
        }
        if (listOrObject instanceof List) {
            ((List) listOrObject).forEach(v -> setMetaBitValue(v, disable, enable));
        } else if (listOrObject.getClass().isArray()) {
            Object[] array = ((Object[]) listOrObject);
            if (0 == array.length) {
                return;
            }
            Arrays.stream(array).forEach(v -> setMetaBitValue(v, disable, enable));
        } else {
            setMetaBitValue(listOrObject, disable, enable);
        }
    }

    protected void enableMetaBitValue(Object obj, long bit) {
        set(obj, getMetaBit(obj) | bit);
    }

    protected void disableMetaBitValue(Object obj, long bit) {
        set(obj, getMetaBit(obj) & ~bit);
    }

    private void set(Object obj, long value) {
        FieldUtils.setFieldValue(obj, META_BIT, value);
    }

    protected boolean hasMetaBit(Object obj, long bit) {
        return (getMetaBit(obj) & bit) == bit;
    }

    protected <T> T enable(T listOrObject, long bit, Function consumer) {
        if (null == listOrObject) {
            return null;
        }
        if (listOrObject instanceof List) {
            ((List) listOrObject).forEach(consumer::apply);
        } else if (listOrObject.getClass().isArray()) {
            Object[] array = ((Object[]) listOrObject);
            if (0 == array.length) {
                return listOrObject;
            }
            Arrays.stream(array).forEach(consumer::apply);
        } else {
            enableMetaBitValue(listOrObject, bit);
        }
        return listOrObject;
    }

    protected <T> T disable(T listOrObject, long bit, Function consumer) {
        if (null == listOrObject) {
            return null;
        }
        if (listOrObject instanceof List) {
            ((List) listOrObject).forEach(consumer::apply);
        } else if (listOrObject.getClass().isArray()) {
            Object[] array = ((Object[]) listOrObject);
            if (0 == array.length) {
                return listOrObject;
            }
            Arrays.stream(array).forEach(consumer::apply);
        } else {
            disableMetaBitValue(listOrObject, bit);
        }
        return listOrObject;
    }

    protected <T> boolean hasBit(T listOrObject, long bit) {
        if (null == listOrObject) {
            return false;
        }
        if (listOrObject instanceof List) {
            List list = (List) listOrObject;
            if (0 == list.size()) {
                return false;
            }
            return hasMetaBit(list.get(0), bit);
        } else if (listOrObject.getClass().isArray()) {
            Object[] array = ((Object[]) listOrObject);
            if (0 == array.length) {
                return false;
            }
            return hasBit(array[0], bit);
        } else {
            return hasMetaBit(listOrObject, bit);
        }
    }

    protected <T> T enable(T listOrObject, SystemDirectiveEnum directive, Function consumer) {
        return enable(listOrObject, directive.getValue(), consumer);
    }

    protected <T> T disable(T listOrObject, SystemDirectiveEnum directive, Function consumer) {
        return disable(listOrObject, directive.getValue(), consumer);
    }

    protected <T> boolean hasBit(T listOrObject, SystemDirectiveEnum directive) {
        return hasBit(listOrObject, directive.getValue());
    }

    protected boolean checkBit(long bit) {
        return bit > 0 && (bit & bit - 1) == 0;
    }

    protected void bitException(long bit) {
//        if (!checkBit(bit)) {
//            throw new RuntimeException("数据异常：" + bit);
//        }
    }

}
