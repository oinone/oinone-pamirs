package pro.shushi.pamirs.core.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ArrayUtils;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;

public class SerializeUtils {

    private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        objectMapper.setDateFormat(formatter);
    }

    public static String[] serialize(Object[] args) throws IOException {
        if (ArrayUtils.isEmpty(args)) {
            return new String[0];
        }
        String[] argSerials = new String[args.length];
        int cursor = 0;
        for (Object o : args) {
            argSerials[cursor] = String.class.equals(o.getClass()) ? (String) o : ObjectMapUtils.objectToString(o);
            cursor++;
        }
        return argSerials;
    }

    public static Object[] serializeToMap(Object[] args) {
        if (ArrayUtils.isEmpty(args)) {
            return new Map[0];
        }
        Map<String, Object>[] argSerials = new Map[args.length];
        int cursor = 0;
        for (Object o : args) {
            argSerials[cursor] = JsonUtils.parseObject2Map(o);
            cursor++;
        }
        return argSerials;
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
