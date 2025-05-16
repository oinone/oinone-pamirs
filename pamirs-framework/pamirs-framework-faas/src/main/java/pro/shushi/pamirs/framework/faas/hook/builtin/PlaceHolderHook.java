package pro.shushi.pamirs.framework.faas.hook.builtin;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Hook;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.core.faas.HookBefore;
import pro.shushi.pamirs.meta.api.core.faas.hook.PlaceHolderParser;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.util.Map;

/**
 * 占位符转化替换
 *
 * @author shier
 * date  2020/5/7 3:28 下午
 */
@Base
@Component
public class PlaceHolderHook implements HookBefore {

    private static Map<String, PlaceHolderParser> placeHolderParserMap;

    public static Map<String, PlaceHolderParser> getPlaceHolderParserMap() {
        Map<String, PlaceHolderParser> placeHolderParserMap = PlaceHolderHook.placeHolderParserMap;
        if (placeHolderParserMap == null) {
            synchronized (PlaceHolderHook.class) {
                placeHolderParserMap = PlaceHolderHook.placeHolderParserMap;
                if (placeHolderParserMap == null) {
                    placeHolderParserMap = BeanDefinitionUtils.getBeansOfType(PlaceHolderParser.class);
                    PlaceHolderHook.placeHolderParserMap = placeHolderParserMap;
                }
            }
        }
        return placeHolderParserMap;
    }

    @Override
    @Hook(priority = 30) //优先级最小
    public Object run(Function function, Object... args) {
        Map<String, PlaceHolderParser> placeHolderParserMap = getPlaceHolderParserMap();
        for (String placeHolderParser : placeHolderParserMap.keySet()) {
            placeHolderParserMap.get(placeHolderParser).parse(args);
        }
        return function;
    }
}
