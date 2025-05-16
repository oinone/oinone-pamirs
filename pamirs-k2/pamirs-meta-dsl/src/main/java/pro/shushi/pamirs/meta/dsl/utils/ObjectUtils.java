package pro.shushi.pamirs.meta.dsl.utils;

import pro.shushi.pamirs.meta.common.util.UnsafeUtil;

import java.util.List;
import java.util.Map;

/**
 * @Description 描述
 * @Author junwei
 * @Date 2019-12-19
 */
public class ObjectUtils {

    public static Object setValue(Object obj, String key, Object value) {
        if(obj instanceof Map){
            ((Map)obj).put(key, value);
        }else if (obj instanceof Object[]) {
            for (Object o : ((Object[]) obj)) {
                setValue(o, key, value);
            }
        } else if (obj instanceof List) {
            for (Object o : ((List) obj)) {
                setValue(o,key,value);
            }
        } else {
           setValue(UnsafeUtil.getValue(obj,"_d"), key, value);
        }
        return obj;
    }
}
