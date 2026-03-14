package pro.shushi.pamirs.boot.web.converter.action;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.enmu.BaseExpEnumerate;
import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxAction;
import pro.shushi.pamirs.boot.base.ux.annotation.button.UxRouteButton;
import pro.shushi.pamirs.boot.web.utils.ActionUtils;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ConverterType;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelReflectSigner;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelSigner;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 窗口动作转换器
 * <p>
 * 2020/11/16 2:48 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SuppressWarnings("rawtypes")
@Component
public class ViewActionConverter implements ModelConverter<Map<String, ViewAction>, Class> {

    @Override
    public int priority() {
        return 99;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Result validate(ExecuteContext context, MetaNames names, Class source) {
        UxRouteButton[] uxButtonAnnotations = fetchRepeatableAnnotations(source);
        Result<?> result = new Result<>();
        if (null == uxButtonAnnotations || 0 == uxButtonAnnotations.length) {
            return result.error();
        }
        String model = names.getModel();
        if (StringUtils.isBlank(model)) {
            model = Spider.getExtension(ModelReflectSigner.class, ModelDefinition.MODEL_MODEL).sign(names, source);
            names.setModel(model);
        }
        if (StringUtils.isBlank(model)) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                    .error(BaseExpEnumerate.BASE_VIEW_ACTION_CONFIG_MODEL_ERROR)
                    .append(MessageFormat
                            .format("使用@UxClientButton注解配置动作，请在类上注解@Model.model或者@Fun注明按钮所属模型，class:{0}", source.getName())));
            context.broken().error();
            return result.error();
        }
        return result;
    }

    private UxRouteButton[] fetchRepeatableAnnotations(Class<?> clazz) {
        UxRouteButton.UxRouteButtons annotations = AnnotationUtils.getAnnotation(clazz, UxRouteButton.UxRouteButtons.class);
        if (null == annotations || 0 == annotations.value().length) {
            UxRouteButton annotation = AnnotationUtils.getAnnotation(clazz, UxRouteButton.class);
            if (null == annotation) {
                return null;
            }
            return new UxRouteButton[]{annotation};
        } else {
            return annotations.value();
        }
    }

    @Override
    public Map<String, ViewAction> convert(MetaNames names, @SuppressWarnings("rawtypes") Class source, Map<String, ViewAction> actionMap) {
        Map<String, ViewAction> result = new HashMap<>();
        UxRouteButton[] routes = fetchRepeatableAnnotations(source);
        if (null == routes) {
            return result;
        }
        for (UxRouteButton uxRouteButton : routes) {
            UxAction uxAction = uxRouteButton.action();
            ViewAction viewAction = new ViewAction();
            viewAction.setModel(names.getModel()).setName(uxAction.name());
            @SuppressWarnings("unchecked")
            String sign = Spider.getExtension(ModelSigner.class, ViewAction.MODEL_MODEL).sign(viewAction);
            if (!actionMap.containsKey(sign)) {
                result.put(sign, viewAction);
            } else {
                viewAction = actionMap.get(sign).disableMetaCompleted();
            }
            ActionUtils.configAction(names.getModule(), uxAction, viewAction);
            ActionUtils.configViewAction(names.getModule(), viewAction, uxRouteButton.value());
        }
        return result;
    }

    @Override
    public String group() {
        return ViewAction.MODEL_MODEL;
    }

    @Override
    public ConverterType type() {
        return ConverterType.map;
    }

    @Override
    public List<String> signs(MetaNames names, Class source) {
        List<String> signs = new ArrayList<>();
        UxRouteButton[] uxRouteButtons = fetchRepeatableAnnotations(source);
        if (null == uxRouteButtons || 0 == uxRouteButtons.length) {
            return signs;
        }
        for (UxRouteButton uxRouteButton : uxRouteButtons) {
            Action action = new ViewAction().setModel(names.getModel()).setName(uxRouteButton.action().name());
            @SuppressWarnings("unchecked")
            String sign = Spider.getExtension(ModelSigner.class, ViewAction.MODEL_MODEL).sign(action);
            signs.add(sign);
        }
        return signs;
    }

}
