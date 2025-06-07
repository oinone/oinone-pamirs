package pro.shushi.pamirs.boot.web.converter.action;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.enmu.BaseExpEnumerate;
import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.base.model.ClientAction;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxAction;
import pro.shushi.pamirs.boot.base.ux.annotation.button.UxClientButton;
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
import java.util.*;

/**
 * 客户端动作转换器
 * <p>
 * 2020/11/16 2:48 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SuppressWarnings("rawtypes")
@Component
public class ClientActionConverter implements ModelConverter<Map<String, ClientAction>, Class> {

    @Override
    public int priority() {
        return 99;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Result validate(ExecuteContext context, MetaNames names, Class source) {
        UxClientButton[] uxButtonAnnotations = fetchRepeatableAnnotations(source);
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
                    .error(BaseExpEnumerate.BASE_CLIENT_ACTION_CONFIG_MODEL_ERROR)
                    .append(MessageFormat
                            .format("使用@UxClientButton注解配置动作，请在类上注解@Model.model或者@Fun注明按钮所属模型，class:{0}", source.getName())));
            context.broken().error();
            return result.error();
        }
        return result;
    }

    private UxClientButton[] fetchRepeatableAnnotations(Class<?> clazz) {
        UxClientButton.UxClientButtons annotations = AnnotationUtils.getAnnotation(clazz, UxClientButton.UxClientButtons.class);
        if (null == annotations || 0 == annotations.value().length) {
            UxClientButton annotation = AnnotationUtils.getAnnotation(clazz, UxClientButton.class);
            if (null == annotation) {
                return null;
            }
            return new UxClientButton[]{annotation};
        } else {
            return annotations.value();
        }
    }

    @Override
    public Map<String, ClientAction> convert(MetaNames names, @SuppressWarnings("rawtypes") Class source, Map<String, ClientAction> actionMap) {
        Map<String, ClientAction> result = new HashMap<>();
        UxClientButton[] uxClientButtons = Objects.requireNonNull(fetchRepeatableAnnotations(source));
        for (UxClientButton uxClientButton : uxClientButtons) {
            UxAction uxAction = uxClientButton.action();
            ClientAction action = new ClientAction();
            action.setModel(names.getModel()).setName(uxAction.name());
            @SuppressWarnings("unchecked")
            String sign = Spider.getExtension(ModelSigner.class, ClientAction.MODEL_MODEL).sign(action);
            if (!actionMap.containsKey(sign)) {
                result.put(sign, action);
            } else {
                action = actionMap.get(sign).disableMetaCompleted();
            }
            ActionUtils.configAction(uxAction, action);
            ActionUtils.configClientAction(action, uxClientButton.value());
        }
        return result;
    }

    @Override
    public String group() {
        return ClientAction.MODEL_MODEL;
    }

    @Override
    public ConverterType type() {
        return ConverterType.map;
    }

    @Override
    public List<String> signs(MetaNames names, Class source) {
        List<String> signs = new ArrayList<>();
        UxClientButton[] buttons = fetchRepeatableAnnotations(source);
        if (null == buttons || 0 == buttons.length) {
            return signs;
        }
        for (UxClientButton button : buttons) {
            Action action = new ClientAction().setModel(names.getModel()).setName(button.action().name());
            @SuppressWarnings("unchecked")
            String sign = Spider.getExtension(ModelSigner.class, ClientAction.MODEL_MODEL).sign(action);
            signs.add(sign);
        }
        return signs;
    }

}
