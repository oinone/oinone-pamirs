package pro.shushi.pamirs.boot.web.converter.action;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.boot.base.model.ServerAction;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.domain.model.Prop;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;
import pro.shushi.pamirs.meta.util.NamespaceAndFunUtils;
import pro.shushi.pamirs.meta.util.PropUtils;
import pro.shushi.pamirs.meta.util.SystemSourceUtils;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.*;

/**
 * 动作注解转化器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings("unused")
@Slf4j
@Component
public class ServerActionConverter implements ModelConverter<ServerAction, Method> {

    @Override
    public int priority() {
        return 1;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Result validate(ExecuteContext context, MetaNames names, Method source) {
        // 可以在这里进行注解配置建议
        Result result = new Result();
        Fun funAnnotation = AnnotationUtils.getAnnotation(source.getDeclaringClass(), Fun.class);
        Action actionAnnotation = AnnotationUtils.getAnnotation(source, Action.class);
        if (null == actionAnnotation) {
            return result.error();
        }
        Model modelAnnotation = AnnotationUtils.getAnnotation(source.getDeclaringClass(), Model.class);
        if (StringUtils.isNotBlank(names.getModel()) && null != modelAnnotation && null != funAnnotation) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                    .append(MessageFormat
                            .format("模型内部动作不需要配置@Fun注解，系统会自动给model属性填充模型编码，class:{0}, method:{1}",
                                    source.getDeclaringClass().getName(), source.getName())));
            result.error();
        }

        if (StringUtils.isBlank(names.getModel()) && null == funAnnotation) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                    .append(MessageFormat
                            .format("请为动作类配置@Fun注解，value值为动作所属模型编码，系统不会自动给model属性填充模型编码，class:{0}, method:{1}",
                                    source.getDeclaringClass().getName(), source.getName())));
            result.error();
        }
        return result;
    }

    @Override
    public ServerAction convert(MetaNames names, Method source, ServerAction action) {
        Fun funAnnotation = AnnotationUtils.getAnnotation(source.getDeclaringClass(), Fun.class);
        String model = Optional.ofNullable(names.getModel()).filter(StringUtils::isNotBlank).orElse(Optional.ofNullable(funAnnotation).map(Fun::value).orElse(null));
        Action actionAnnotation = AnnotationUtils.getAnnotation(source, Action.class);
        assert actionAnnotation != null;
        Action.Advanced actionAdvanced = AnnotationUtils.getAnnotation(source, Action.Advanced.class);
        SystemSourceEnum systemSource = SystemSourceUtils.fetch(source);
        action.setFun(NamespaceAndFunUtils.fun(source))
                .setRule(Optional.ofNullable(actionAdvanced).map(Action.Advanced::rule).orElse(StringUtils.EMPTY))
                .setInvisible(Optional.ofNullable(actionAdvanced).map(Action.Advanced::invisible).orElse(StringUtils.EMPTY))
                .setName(Optional.ofNullable(actionAdvanced).map(Action.Advanced::name).filter(StringUtils::isNotBlank).orElse(source.getName()))
                .setDisplayName(I18nUtils.translateServerAction(names.getModule(), model, action.getName(), "displayName", StringUtils.defaultIfBlank(actionAnnotation.displayName(), source.getName())))
                .setLabel(I18nUtils.translateServerAction(names.getModule(), model, action.getName(), "label", StringUtils.defaultIfBlank(actionAnnotation.label(), action.getDisplayName())))
                .setModel(model)
                .setDescription(I18nUtils.translateServerAction(names.getModule(), model, action.getName(), "description", actionAnnotation.summary()))
                .setActionType(ActionTypeEnum.SERVER)
                .setContextType(actionAnnotation.contextType())
                .setBindingType(convertEnumClassFromEnumName(actionAnnotation.bindingType()))
                .setMapping(PropUtils.convertPropMapFromAnnotation(actionAnnotation.mapping()))
                .setContext(PropUtils.convertPropMapFromAnnotation(actionAnnotation.context()))
                .setDisable(Optional.ofNullable(actionAdvanced).map(Action.Advanced::disable).filter(StringUtils::isNotBlank).orElse(null))
                .setInvisible(Optional.ofNullable(actionAdvanced).map(Action.Advanced::invisible).filter(StringUtils::isNotBlank).orElse(null))
                .setRule(Optional.ofNullable(actionAdvanced).map(Action.Advanced::rule).filter(StringUtils::isNotBlank).orElse(null))
                .setBindingViewName(Optional.ofNullable(actionAdvanced).map(Action.Advanced::bindingView).filter(StringUtils::isNotBlank).orElse(null))
                .setPriority(Optional.ofNullable(actionAdvanced).map(Action.Advanced::priority).orElse(99))
                .setSystemSource(systemSource)
                .setAttributes(Optional.ofNullable(actionAnnotation.attributes()).map(PropUtils::convertPropListFromAnnotation).map(props -> {
                    Map<String, Object> attribute = new HashMap<>();
                    for (Prop prop : props) {
                        attribute.put(prop.getName(), prop.getValue());
                    }
                    return attribute;
                }).orElse(null));
        return action;
    }

    @Override
    public String group() {
        return ServerAction.MODEL_MODEL;
    }

    @Override
    public Class<?> metaModelClazz() {
        return ServerAction.class;
    }

    private List<ViewTypeEnum> convertEnumClassFromEnumName(ViewTypeEnum[] names) {
        if (null == names || 0 == names.length) {
            return null;
        }
        return new ArrayList<>(Arrays.asList(names));
    }

}
