package pro.shushi.pamirs.boot.web.utils;

import org.apache.commons.lang3.ArrayUtils;
import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelSigner;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 菜单动作转换器帮助类
 * 2021/12/3 12:44 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class ActionOfMenuConverterUtils {

    public static <T extends Action> Map<String, T> convert(String module,
                                                            String model,
                                                            Class<?> source,
                                                            Map<String, T> actionMap,
                                                            BiConsumer<T, Class<?>> consumer) {
        Map<String, T> resultMap = new HashMap<>();
        Class<?>[] declaredClasses = source.getDeclaredClasses();
        actionConvert(module, model, actionMap, resultMap, declaredClasses, consumer);
        return resultMap;
    }

    private static <T extends Action> void actionConvert(String module,
                                                         String model,
                                                         Map<String, T> context,
                                                         Map<String, T> result,
                                                         Class<?>[] declaredClasses,
                                                         BiConsumer<T, Class<?>> consumer) {
        if (ArrayUtils.isEmpty(declaredClasses)) {
            return;
        }
        for (Class<?> clazz : declaredClasses) {
            if (null == clazz) {
                continue;
            }
            T action = MenuUtils.fetchMenuAction(model, clazz, module);
            if (null != action) {
                @SuppressWarnings("unchecked")
                String sign = Spider.getExtension(ModelSigner.class, model).sign(action);
                if (!context.containsKey(sign)) {
                    result.put(sign, action);
                } else {
                    action = context.get(sign).disableMetaCompleted();
                }
                action.setSign(sign);
                consumer.accept(action, clazz);
            }
            Class<?>[] childDeclaredClasses = clazz.getDeclaredClasses();
            actionConvert(module, model, context, result, childDeclaredClasses, consumer);
        }
    }

    public static <T extends Action> List<String> signs(String model,
                                                        Class<?> source,
                                                        Function<Class<?>, T> fetcher) {
        List<String> signs = new ArrayList<>();
        Class<?>[] declaredClasses = source.getDeclaredClasses();
        actionSigns(model, signs, declaredClasses, fetcher);
        return signs;
    }

    private static <T extends Action> void actionSigns(String model,
                                                       List<String> signs,
                                                       Class<?>[] declaredClasses,
                                                       Function<Class<?>, T> fetcher) {
        if (ArrayUtils.isEmpty(declaredClasses)) {
            return;
        }
        for (Class<?> clazz : declaredClasses) {
            if (null == clazz) {
                continue;
            }
            T action = fetcher.apply(clazz);
            if (null != action) {
                @SuppressWarnings("unchecked")
                String sign = Spider.getExtension(ModelSigner.class, model).sign(action);
                signs.add(sign);
            }
            Class<?>[] childDeclaredClasses = clazz.getDeclaredClasses();
            actionSigns(model, signs, childDeclaredClasses, fetcher);
        }
    }

}
