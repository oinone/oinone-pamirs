package pro.shushi.pamirs.meta.api.prefix;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * 隔离前缀管理器
 * <p>
 * 2021/9/7 2:00 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class KeyPrefixManager {

    public static String generate(String separator) {
        return join(KeyPrefixApi.class, KeyPrefixApi::keyPrefix, null, separator, null);
    }

    public static String generate(String separator, String tail) {
        return join(KeyPrefixApi.class, KeyPrefixApi::keyPrefix, null, separator, tail);
    }

    protected static String generate(Map<String, Object> context, String separator) {
        return join(KeyPrefixApi.class, KeyPrefixApi::keyPrefix, context, separator, null);
    }

    protected static String generate(Map<String, Object> context, String separator, String tail) {
        return join(KeyPrefixApi.class, KeyPrefixApi::keyPrefix, context, separator, tail);
    }

    public static <T> String join(Class<T> type, BiFunction<T, Map<String, Object>, String> func,
                                  Map<String, Object> context, String separator, String tail) {
        List<T> extensions = Spider.getLoader(type).getOrderedExtensions();
        if (CollectionUtils.isEmpty(extensions)) {
            return CharacterConstants.SEPARATOR_EMPTY;
        }
        List<String> strings = new ArrayList<>(extensions.size());
        for (T extension : extensions) {
            String result = func.apply(extension, context);
            if (null != result) {
                strings.add(result);
            }
        }
        String joinString = StringUtils.join(strings, separator);
        if (StringUtils.isNotBlank(tail) && strings.size() >= 1) {
            joinString += tail;
        }
        return joinString;
    }

}
