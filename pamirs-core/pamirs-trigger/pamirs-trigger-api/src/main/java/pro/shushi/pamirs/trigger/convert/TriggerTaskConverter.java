package pro.shushi.pamirs.trigger.convert;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.enmu.TimeUnitEnum;
import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.common.constants.AppName;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.util.NamespaceAndFunUtils;
import pro.shushi.pamirs.middleware.schedule.eunmeration.TaskType;
import pro.shushi.pamirs.trigger.annotation.Trigger;
import pro.shushi.pamirs.trigger.model.TriggerTaskAction;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * @author Adamancy Zhang
 * @date 2020-11-03 13:03
 */
@SuppressWarnings("rawtypes")
@Slf4j
@Component
public class TriggerTaskConverter implements ModelConverter<TriggerTaskAction, Method> {

    @Override
    public Result validate(ExecuteContext context, MetaNames names, Method source) {
        Result result = new Result();
        Trigger trigger = source.getAnnotation(Trigger.class);
        if (trigger == null) {
            result.error();
        } else {
            Function function = source.getAnnotation(Function.class);
            Action action = source.getAnnotation(Action.class);
            if (function == null ^ action == null) {
                return result;
            } else {
                return result.error();
            }
        }
        return result;
    }

    @Override
    public TriggerTaskAction convert(MetaNames names, Method source, TriggerTaskAction triggerTaskAction) {
        Trigger trigger = source.getAnnotation(Trigger.class);
        String namespace = NamespaceAndFunUtils.namespace(source);
        String fun = NamespaceAndFunUtils.fun(source);
        triggerTaskAction.setModel(names.getModel())
                .setTechnicalName(trigger.name())
                .setCondition(trigger.condition())
                .setWiredContext(Optional.of(trigger.wiredContext()).filter(StringUtils::isNotBlank).orElse(CharacterConstants.SEPARATOR_EMPTY))
                .setEventParameter(Optional.of(trigger.eventParameter()).filter(StringUtils::isNotBlank).orElse(CharacterConstants.SEPARATOR_EMPTY))
                .setLimitRetryNumber(-1)
                .setNextRetryTimeValue(3)
                .setTaskType(Optional.ofNullable(trigger.taskType()).map(TaskType::getValue).orElse(TaskType.BASE_SCHEDULE_TASK.getValue()))
                .setNextRetryTimeUnit(TimeUnitEnum.SECOND)
                .setDisplayName(I18nUtils.translateTrigger(names.getModule(), namespace, fun, "displayName", trigger.displayName()))
                .setDescription(triggerTaskAction.getDisplayName() + ":" + namespace + "$$" + fun)
                .setTenant(PamirsTenantSession.getTenant())
                .setEnv(PamirsTenantSession.getEnv())
                .setApplication(AppName.get())
                .setExecuteNamespace(namespace)
                .setExecuteFun(fun)
                .setActive(trigger.active());
        return triggerTaskAction;
    }

    @Override
    public String group() {
        return TriggerTaskAction.MODEL_MODEL;
    }

    @Override
    public int priority() {
        return 100;
    }

    @Override
    public Class<?> metaModelClazz() {
        return TriggerTaskAction.class;
    }
}
