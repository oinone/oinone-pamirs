package pro.shushi.pamirs.boot.web.converter.action;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.enmu.BaseExpEnumerate;
import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.base.model.UrlAction;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxAction;
import pro.shushi.pamirs.boot.base.ux.annotation.button.UxLinkButton;
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
 * 链接动作转换器
 * <p>
 * 2020/11/16 2:48 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SuppressWarnings("rawtypes")
@Component
public class UrlActionConverter implements ModelConverter<Map<String, UrlAction>, Class> {

    @Override
    public int priority() {
        return 99;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Result validate(ExecuteContext context, MetaNames names, Class source) {
        UxLinkButton[] uxButtonAnnotations = fetchRepeatableAnnotations(source);
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
                    .error(BaseExpEnumerate.BASE_URL_ACTION_CONFIG_MODEL_ERROR)
                    .append(MessageFormat
                            .format("使用@UxClientButton注解配置动作，请在类上注解@Model.model或者@Fun注明按钮所属模型，class:{0}", source.getName())));
            context.broken().error();
            return result.error();
        }
        return result;
    }

    private UxLinkButton[] fetchRepeatableAnnotations(Class<?> clazz) {
        UxLinkButton.UxLinkButtons annotations = AnnotationUtils.getAnnotation(clazz, UxLinkButton.UxLinkButtons.class);
        if (null == annotations || 0 == annotations.value().length) {
            UxLinkButton annotation = AnnotationUtils.getAnnotation(clazz, UxLinkButton.class);
            if (null == annotation) {
                return null;
            }
            return new UxLinkButton[]{annotation};
        } else {
            return annotations.value();
        }
    }

    @Override
    public Map<String, UrlAction> convert(MetaNames names, @SuppressWarnings("rawtypes") Class source, Map<String, UrlAction> actionMap) {
        Map<String, UrlAction> result = new HashMap<>();
        UxLinkButton[] uxLinkButtons = fetchRepeatableAnnotations(source);
        if (null == uxLinkButtons) {
            return result;
        }
        for (UxLinkButton uxLinkButton : uxLinkButtons) {
            UxAction uxAction = uxLinkButton.action();
            UrlAction action = new UrlAction();
            action.setModel(names.getModel()).setName(uxAction.name());
            @SuppressWarnings("unchecked")
            String sign = Spider.getExtension(ModelSigner.class, UrlAction.MODEL_MODEL).sign(action);
            if (!actionMap.containsKey(sign)) {
                result.put(sign, action);
            } else {
                action = actionMap.get(sign).disableMetaCompleted();
            }
            ActionUtils.configAction(names.getModule(), uxAction, action);
            ActionUtils.configUrlAction(action, uxLinkButton.value());
        }
        return result;
    }

    @Override
    public String group() {
        return UrlAction.MODEL_MODEL;
    }

    @Override
    public ConverterType type() {
        return ConverterType.map;
    }

    @Override
    public List<String> signs(MetaNames names, Class source) {
        List<String> signs = new ArrayList<>();
        UxLinkButton[] actions = fetchRepeatableAnnotations(source);
        if (null == actions || 0 == actions.length) {
            return signs;
        }
        for (UxLinkButton button : actions) {
            Action action = new UrlAction().setModel(names.getModel()).setName(button.action().name());
            @SuppressWarnings("unchecked")
            String sign = Spider.getExtension(ModelSigner.class, UrlAction.MODEL_MODEL).sign(action);
            signs.add(sign);
        }
        return signs;
    }

}
