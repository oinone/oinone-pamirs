package pro.shushi.pamirs.core.common;

import pro.shushi.pamirs.meta.util.JsonUtils;

/**
 * @author shier
 * date 2020/4/8
 */
public class ObjectMapUtils {
    @Deprecated
    public static <T> String objectToString(T obj) {

        return JsonUtils.toJSONString(obj);
//        String s = SerializeUtils.getObjectMapper().convertValue(objMap, String.class);

    }
}
