package pro.shushi.pamirs.core.common;

import com.google.common.collect.Lists;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author shier
 * date 2020/4/8
 */
public class ParamUtils {

    private ParamUtils() {
        //reject create object
    }

    public static boolean isComplexObject(Object o) {
        switch (o.getClass().getName()) {
            case "java.lang.String":
            case "java.lang.Integer":
            case "java.lang.Long":
            case "java.lang.Character":
            case "java.lang.Float":
            case "java.lang.Short":
            case "java.lang.Byte":
            case "java.lang.Boolean":
            case "boolean":
            case "java.util.Date":
            case "java.sql.Timestamp":
            case "java.math.BigInteger":
            case "java.math.BigDecimal":
            case "pro.shushi.pamirs.base.event.ActionEvent":
            case "pro.shushi.pamirs.base.data.domain.Sort"://不拦截该选项
                return Boolean.FALSE;
            default:
                return Boolean.TRUE;
        }
    }

    public static Long createLong(Object o) {
        if (ObjectUtils.isEmpty(o)) {
            return null;
        }
        if (o instanceof String) {
            if (o.toString().trim().length() == 0) {
                return null;
            }
            try {
                return Long.valueOf((String) o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (o instanceof Long) {
            return (Long) o;
        } else {
            return Long.valueOf(o + "");
        }
        return null;
    }

    public static Integer createInteger(Object o) {
        if (ObjectUtils.isEmpty(o)) {
            return null;
        }
        if (o instanceof String) {
            if (o.toString().trim().length() == 0) {
                return null;
            }
            return Integer.valueOf((String) o);
        } else if (o instanceof Long) {
            return (Integer) o;
        } else {
            return Integer.valueOf(o + "");
        }
    }

    public static List<Long> createLongList(Object o) {
        if (ObjectUtils.isEmpty(o)) {
            return null;
        }
        if (o instanceof List) {
            return ((List<Long>) o).stream().map(v -> createLong(v)).collect(Collectors.toList());
        } else {
            return Lists.newArrayList(createLong(o));
        }
    }

}
