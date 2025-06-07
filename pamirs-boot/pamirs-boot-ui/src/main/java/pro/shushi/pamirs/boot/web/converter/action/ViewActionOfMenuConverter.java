package pro.shushi.pamirs.boot.web.converter.action;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.model.ViewAction;
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
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;

import java.util.List;
import java.util.Map;

/**
 * 菜单窗口动作前端配置注解转化器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings("rawtypes")
@Slf4j
@Component
public class ViewActionOfMenuConverter implements ModelConverter<Map<String, ViewAction>, Class> {

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
    public Map<String, ViewAction> convert(MetaNames names, @SuppressWarnings("rawtypes") Class source, Map<String, ViewAction> metaModelObject) {
        return ActionOfMenuConverterUtils.convert(names.getModule(), ViewAction.MODEL_MODEL, source, metaModelObject,
                (action, menuClazz) -> {
                    MenuUtils.configAction(action, menuClazz).setModule(names.getModule()).setModuleName(names.getModuleName());
                    ActionUtils.configViewAction(action, MenuUtils.fetchViewActionAnnotation(menuClazz));
                    action.setContextType(ActionContextTypeEnum.CONTEXT_FREE);
                });
    }

    @Override
    public String group() {
        return ViewAction.MODEL_MODEL;
    }

    @Override
    public Class<?> metaModelClazz() {
        return ViewAction.class;
    }

    @Override
    public ConverterType type() {
        return ConverterType.map;
    }

    @Override
    public List<String> signs(MetaNames names, Class source) {
        return ActionOfMenuConverterUtils.signs(ViewAction.MODEL_MODEL, source,
                clazz -> MenuUtils.fetchMenuAction(ViewAction.MODEL_MODEL, clazz, names.getModule()));
    }

}
