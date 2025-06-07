package pro.shushi.pamirs.framework.configure.annotation.core.check;

import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import static pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate.BASE_META_CONFIG_CONFLICT_ERROR;

/**
 * 元数据去重校验
 * <p>
 * 2021/3/9 12:48 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class MetaUniqueChecker {

    private static final ThreadLocal<Map<String/*group#sign*/, String/*location*/>> metaLocationMapThreadLocal = new ThreadLocal<>();

    public static void check(ExecuteContext context, Result<?> result, String model, String sign, String... locations) {
        if (!result.isSuccess()) {
            return;
        }
        Map<String/*group#sign*/, String/*location*/> metaLocationMap = metaLocationMapThreadLocal.get();
        if (null == metaLocationMap) {
            metaLocationMap = new HashMap<>();
            metaLocationMapThreadLocal.set(metaLocationMap);
        }
        String key = model + CharacterConstants.SEPARATOR_OCTOTHORPE + sign;
        String location = PStringUtils.join(locations, CharacterConstants.SEPARATOR_DOT);
        if (metaLocationMap.containsKey(key)) {
            String existLocation = metaLocationMap.get(key);
            if (null != location && !location.equals(existLocation)) {
                result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                        .error(BASE_META_CONFIG_CONFLICT_ERROR)
                        .append(MessageFormat.format("model:{0},sign:{1},location:{2},exist:{3}",
                                model, sign, location, existLocation)));
                context.error();
                result.error();
            }
        } else {
            metaLocationMap.put(key, location);
        }
    }

    public static void clear() {
        Map<String/*group#sign*/, String/*location*/> metaLocationMap = metaLocationMapThreadLocal.get();
        if (null != metaLocationMap) {
            metaLocationMap.clear();
            metaLocationMapThreadLocal.set(null);
        }
    }

}
