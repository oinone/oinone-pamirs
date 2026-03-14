package pro.shushi.pamirs.boot.web.converter.action;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.model.UrlAction;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxMenus;
import pro.shushi.pamirs.boot.web.utils.ActionOfMenuConverterUtils;
import pro.shushi.pamirs.boot.web.utils.ActionUtils;
import pro.shushi.pamirs.boot.web.utils.MenuUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ConverterType;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;

import java.util.List;
import java.util.Map;

/**
 * 菜单链接动作前端配置注解转化器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings("rawtypes")
@Slf4j
@Component
public class UrlActionOfMenuConverter implements ModelConverter<Map<String, UrlAction>, Class> {

    @Override
    public int priority() {
        return 201;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Result validate(ExecuteContext context, MetaNames names, Class source) {
        UxMenus menuAnnotation = AnnotationUtils.getAnnotation(source, UxMenus.class);
        Result result = new Result();
        if (null == menuAnnotation) {
            return result.error();
        }
        return result;
    }

    @Override
    public Map<String, UrlAction> convert(MetaNames names, @SuppressWarnings("rawtypes") Class source, Map<String, UrlAction> metaModelObject) {
        return ActionOfMenuConverterUtils.convert(names.getModule(), UrlAction.MODEL_MODEL, source, metaModelObject,
                (action, menuClazz) -> {
                    MenuUtils.configAction(names.getModule(), action, menuClazz);
                    ActionUtils.configUrlAction(action, MenuUtils.fetchUrlActionAnnotation(menuClazz));
                });
    }

    @Override
    public String group() {
        return UrlAction.MODEL_MODEL;
    }

    @Override
    public Class<?> metaModelClazz() {
        return UrlAction.class;
    }

    @Override
    public ConverterType type() {
        return ConverterType.map;
    }

    @Override
    public List<String> signs(MetaNames names, Class source) {
        return ActionOfMenuConverterUtils.signs(UrlAction.MODEL_MODEL, source,
                clazz -> MenuUtils.fetchMenuAction(UrlAction.MODEL_MODEL, clazz, names.getModule()));
    }

}
