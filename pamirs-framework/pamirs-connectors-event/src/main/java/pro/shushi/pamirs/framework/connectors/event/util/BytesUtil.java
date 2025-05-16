package pro.shushi.pamirs.framework.connectors.event.util;

import pro.shushi.pamirs.meta.util.JsonUtils;

import java.nio.charset.StandardCharsets;

public class BytesUtil {

    public static byte[] object2Bytes(Object object) {
        return JsonUtils.toJSONString(object).getBytes(StandardCharsets.UTF_8);
    }

    public static <T> T bytes2Object(byte[] bytes, Class<T> cls) {
        return JsonUtils.parseObject(new String(bytes), cls);
    }
}
