package pro.shushi.pamirs.trigger.convert;

import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;
import pro.shushi.pamirs.meta.util.NamespaceAndFunUtils;
import pro.shushi.pamirs.middleware.schedule.eunmeration.TaskType;
import pro.shushi.pamirs.trigger.annotation.XSchedule;
import pro.shushi.pamirs.trigger.enmu.TriggerExpEnumerate;
import pro.shushi.pamirs.trigger.init.ScheduleTaskInit;
import pro.shushi.pamirs.trigger.model.ScheduleTaskAction;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Optional;

/**
 * @author Adamancy Zhang
 * @date 2020-11-03 13:03
 */
@SuppressWarnings("rawtypes")
@Slf4j
@Component
public class ScheduleTaskConverter implements ModelConverter<ScheduleTaskAction, Method> {

    @Override
    public Result validate(ExecuteContext context, MetaNames names, Method source) {
        Result result = new Result();
        XSchedule schedule = source.getAnnotation(XSchedule.class);
        if (schedule == null) {
            return result.error();
        }
        Function function = source.getAnnotation(Function.class);
        if (function == null) {
            return result.error();
        }
        String cron = schedule.cron();
        try {
            if (!CronSequenceGenerator.isValidExpression(cron)) {
                result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                        .error(TriggerExpEnumerate.CRON_EXPRESSION_INVALID)
                        .append(MessageFormat.format("cron: {0}, clazz: {1}, method: {2}", cron, source.getClass(), source.getName())));
                return result.error();
            }
        } catch (Throwable e) {
            log.error("cron expression is invalid", e);
            result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                    .error(TriggerExpEnumerate.CRON_EXPRESSION_INVALID)
                    .append(MessageFormat.format("cron: {0}, clazz: {1}, method: {2}", cron, source.getClass(), source.getName())));
            return result.error();
        }
        return result;
    }

    @Override
    public ScheduleTaskAction convert(MetaNames names, Method source, ScheduleTaskAction metaModelObject) {
        XSchedule schedule = source.getAnnotation(XSchedule.class);
        String namespace = NamespaceAndFunUtils.namespace(source);
        String fun = NamespaceAndFunUtils.fun(source);
        String technicalName = Optional.ofNullable(schedule.name()).filter(StringUtils::isNotBlank).orElse(namespace + CharacterConstants.SEPARATOR_OCTOTHORPE + fun);
        String displayName = Optional.ofNullable(schedule.displayName()).filter(StringUtils::isNotBlank).orElse(technicalName);
        ScheduleTaskInit.addScheduleAction((ScheduleTaskAction) new ScheduleTaskAction()
                .setTechnicalName(technicalName)
                .setLimitExecuteNumber(schedule.limitExecuteNumber())
                .setCron(schedule.cron())
                .setPeriodTimeAnchor(schedule.periodTimeAnchor())
                .setLimitRetryNumber(schedule.limitRetryNumber())
                .setNextRetryTimeValue(Optional.of(schedule.nextRetryTimeValue()).filter(v -> v >= 1).orElse(-1))
                .setNextRetryTimeUnit(schedule.nextRetryTimeUnit())
                .setTaskType(Optional.ofNullable(schedule.taskType()).filter(StringUtils::isNotBlank).orElse(TaskType.REMOTE_SCHEDULE_TASK.getValue()))
                .setDisplayName(displayName)
                .setDescription(displayName + ":" + namespace + "$$" + fun)
                .setExecuteNamespace(namespace)
                .setExecuteFun(fun)
                .setActive(true)
        );
        return null;
    }

    @Override
    public String group() {
        return ScheduleTaskAction.MODEL_MODEL;
    }

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public Class<?> metaModelClazz() {
        return ScheduleTaskAction.class;
    }

    @Override
    public String sign(MetaNames names, Method source) {
        XSchedule schedule = source.getAnnotation(XSchedule.class);
        String technicalName = schedule.name();
        if (StringUtils.isBlank(technicalName)) {
            String namespace = NamespaceAndFunUtils.namespace(source);
            String fun = NamespaceAndFunUtils.fun(source);
            technicalName = namespace + CharacterConstants.SEPARATOR_OCTOTHORPE + fun;
        }
        return technicalName;
    }
}
