package pro.shushi.pamirs.trigger.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.CollectionHelper;
import pro.shushi.pamirs.core.common.StringHelper;
import pro.shushi.pamirs.core.common.cache.MemoryIterableSearchCache;
import pro.shushi.pamirs.core.common.enmu.TimeUnitEnum;
import pro.shushi.pamirs.core.common.function.FunctionHelper;
import pro.shushi.pamirs.core.common.function.context.FunctionContext;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.event.annotation.NotifyListener;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyConsumer;
import pro.shushi.pamirs.framework.connectors.event.condition.NotifySwitchCondition;
import pro.shushi.pamirs.framework.connectors.event.enumeration.ConsumerType;
import pro.shushi.pamirs.framework.gateways.util.BooleanHelper;
import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableConfig;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.fun.Arg;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.VariableNameConstants;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.JsonUtils;
import pro.shushi.pamirs.middleware.canal.EventType;
import pro.shushi.pamirs.middleware.canal.domain.Column;
import pro.shushi.pamirs.middleware.canal.domain.Row;
import pro.shushi.pamirs.middleware.schedule.common.Result;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;
import pro.shushi.pamirs.trigger.config.AutoTriggerSwitchCondition;
import pro.shushi.pamirs.trigger.constant.NotifyConstant;
import pro.shushi.pamirs.trigger.constant.TriggerUserConfiguration;
import pro.shushi.pamirs.trigger.enmu.TriggerConditionEnum;
import pro.shushi.pamirs.trigger.model.TriggerTaskAction;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 触发任务
 *
 * @author Adamancy Zhang
 * @date 2020-11-06 14:23
 */
@Slf4j
@Component
@Conditional({NotifySwitchCondition.class, AutoTriggerSwitchCondition.class})
@Fun(TriggerTaskActionExecutor.FUN_NAMESPACE)
@NotifyListener(consumerType = ConsumerType.ORDERLY, topic = NotifyConstant.AUTO_TRIGGER_TOPIC, tags = "*", bodyClass = Row.class)
public class AutoTriggerTaskActionService implements NotifyConsumer<Row>, TriggerTaskActionExecutor {

    @Autowired
    private TriggerTaskActionService triggerTaskActionService;

    @Override
    public void consume(Message<Row> event) {
        Row changeData = event.getPayload();
        if (StringUtils.isNotBlank(changeData.getTenant())) {
            PamirsTenantSession.setTenant(changeData.getTenant());
        }
        PamirsSession.setEnv(changeData.getEnv());
        //根据表名称触发对应模型指定的触发动作
        String triggerTable = changeData.getTable();
        Map<String, ModelConfig> triggerModelMap = new HashMap<>();
        List<ModelDefinition> modelDefinitions = new ModelDefinition().setTable(triggerTable).queryList();
        if (CollectionUtils.isNotEmpty(modelDefinitions)) {
            for (ModelDefinition model : modelDefinitions) {
                ModelConfig entry = PamirsSession.getContext().getModelCache().get(model.getModel());
                if (triggerTable.equals(entry.getTable())) {
                    triggerModelMap.put(model.getModel(), entry);
                }
            }
        }
        if (triggerModelMap.isEmpty()) {
            return;
        }
        List<TriggerConditionEnum> conditionList = new ArrayList<>();
        EventType eventType = changeData.getEventType();
        switch (eventType) {
            case DELETE:
                conditionList.add(TriggerConditionEnum.ON_DELETE);
                break;
            case UPDATE:
                conditionList.add(TriggerConditionEnum.ON_DELETE);
                conditionList.add(TriggerConditionEnum.ON_UPDATE);
                break;
            case INSERT:
                conditionList.add(TriggerConditionEnum.ON_CREATE);
                break;
            default:
                throw new UnsupportedOperationException("Invalid event type");
        }
        LambdaQueryWrapper<TriggerTaskAction> wrapper = Pops.<TriggerTaskAction>lambdaQuery()
                .from(TriggerTaskAction.class)
                .in(TriggerTaskAction::getModel, triggerModelMap.keySet())
                .in(TriggerTaskAction::getCondition, conditionList)
                .eq(TriggerTaskAction::getActive, true);
        List<TriggerTaskAction> triggerTaskActions = Models.data().queryListByWrapper(wrapper);
        if (triggerTaskActions.isEmpty()) {
            return;
        }
        triggerTaskActions.sort(Comparator.comparingInt(a -> a.getCondition().intValue()));
        Set<String> ignoredTriggerSet = new HashSet<>();
        Set<String> deleteTriggerSet = new HashSet<>();
        switch (eventType) {
            case INSERT:
                addTask(triggerTaskActions, TriggerConditionEnum.ON_CREATE::equals, v -> addTask(v, TriggerConditionEnum.ON_CREATE, triggerModelMap.get(v.getModel()), changeData, event));
                break;
            case DELETE:
                addTask(triggerTaskActions, TriggerConditionEnum.ON_DELETE::equals, v -> addTask(v, TriggerConditionEnum.ON_DELETE, triggerModelMap.get(v.getModel()), changeData, event));
                break;
            case UPDATE:
                addTask(triggerTaskActions, v -> TriggerConditionEnum.ON_UPDATE.equals(v) || TriggerConditionEnum.ON_DELETE.equals(v), v -> {
                    String model = v.getModel();
                    if (ignoredTriggerSet.contains(model)) {
                        return;
                    }
                    ModelConfig modelConfig = triggerModelMap.get(model);
                    PamirsTableConfig tableInfo = PamirsTableInfo.fetchPamirsTableInfo(model);
                    boolean isBeforeDeleted = false, isAfterDeleted = false;
                    UpdateEventSwitch eventSwitch = UpdateEventSwitch.UPDATE;
                    Boolean isLogicDelete = tableInfo.getLogicDelete();
                    if (isLogicDelete != null && isLogicDelete) {
                        String key = tableInfo.getLogicDeleteColumn();
                        if (StringUtils.isBlank(key)) {
                            return;
                        }
                        String value = tableInfo.getLogicNotDeleteValue();
                        if (StringUtils.isBlank(value)) {
                            return;
                        }
                        MemoryIterableSearchCache<String, Column> changeBeforeData = new MemoryIterableSearchCache<>(changeData.getBefore(), Column::getName);
                        MemoryIterableSearchCache<String, Column> changeAfterData = new MemoryIterableSearchCache<>(changeData.getAfter(), Column::getName);
                        Column changeBeforeLogicDeleteData = changeBeforeData.get(key);
                        Column changeAfterLogicDeleteData = changeAfterData.get(key);
                        if (changeBeforeLogicDeleteData != null) {
                            isBeforeDeleted = !value.equals(StringHelper.valueOf(changeBeforeLogicDeleteData.getValue()));
                        }
                        if (changeAfterLogicDeleteData != null) {
                            isAfterDeleted = !value.equals(StringHelper.valueOf(changeAfterLogicDeleteData.getValue()));
                        }
                        eventSwitch = UpdateEventSwitch.eventSwitch(isBeforeDeleted, isAfterDeleted);
                    }
                    switch (eventSwitch) {
                        case UPDATE:
                            if (TriggerConditionEnum.ON_UPDATE.equals(v.getCondition())) {
                                if (!deleteTriggerSet.contains(model)) {
                                    addTask(v, TriggerConditionEnum.ON_UPDATE, modelConfig, changeData, event);
                                }
                            }
                            break;
                        case DELETED:
                            if (TriggerConditionEnum.ON_DELETE.equals(v.getCondition())) {
                                addTask(v, TriggerConditionEnum.ON_DELETE, modelConfig, changeData, event);
                                deleteTriggerSet.add(model);
                            }
                            break;
                        case RECOVERY:
                            log.error("非法的数据库修改操作: 忽略数据行的逻辑删除字段的恢复操作 model: {} id: {}", model, changeData.getId());
                            ignoredTriggerSet.add(model);
                            break;
                        case MODIFY_DELETED_DATA:
                            log.error("非法的数据库修改操作: 忽略对已删除字段的修改操作 model: {} id: {}", model, changeData.getId());
                            ignoredTriggerSet.add(model);
                            break;
                        default:
                            throw new UnsupportedOperationException("Invalid event type");
                    }
                });
                break;
            default:
                throw new UnsupportedOperationException("Invalid event type");
        }
    }

    @Override
    @pro.shushi.pamirs.meta.annotation.Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE})
    public Result<Void> execute(ScheduleItem task) {
        Result<Void> result = new Result<>();
        try {
            FunctionHelper.invoke(JsonUtils.parseObjectList(task.getContext()));
        } catch (Throwable e) {
            log.error("trigger task action execute error", e);
            result.setFail(e.getMessage());
        }
        return result;
    }

    private void addTask(List<TriggerTaskAction> triggerTaskActions, Predicate<TriggerConditionEnum> conditionPredicate, Consumer<TriggerTaskAction> consumer) {
        for (TriggerTaskAction triggerTaskAction : triggerTaskActions) {
            if (conditionPredicate.test(triggerTaskAction.getCondition())) {
                consumer.accept(triggerTaskAction);
            }
        }
    }

    private void addTask(TriggerTaskAction triggerTaskAction, TriggerConditionEnum condition, ModelConfig modelConfig, Row changeData, Message<Row> event) {
        String context;
        switch (condition) {
            case ON_CREATE:
                fillSessionUid(changeData.getAfter());
                context = getChangeData(modelConfig, triggerTaskAction, changeData.getId(), Collections.singletonList(changeData.getAfter()), event);
                break;
            case ON_UPDATE:
                fillSessionUid(changeData.getAfter());
                context = getChangeData(modelConfig, triggerTaskAction, changeData.getId(), CollectionHelper.<List<Column>>newInstance().add(changeData.getBefore()).add(changeData.getAfter()).build(), event);
                break;
            case ON_DELETE:
                fillSessionUid(changeData.getAfter());
                context = getChangeData(modelConfig, triggerTaskAction, changeData.getId(), Collections.singletonList(changeData.getBefore()), event);
                break;
            default:
                throw new UnsupportedOperationException("Invalid event type.");
        }
        addTask(triggerTaskAction, context);
    }

    private void addTask(TriggerTaskAction triggerTaskAction, String context) {
        triggerTaskAction.setContext(context);
        triggerTaskAction.setLimitRetryNumber(30);
        triggerTaskAction.setNextRetryTimeValue(1);
        triggerTaskAction.setNextRetryTimeUnit(TimeUnitEnum.MINUTE);
        triggerTaskActionService.submit(triggerTaskAction);
        triggerTaskAction.unsetContext();
    }

    private String getChangeData(ModelConfig modelConfig, TriggerTaskAction triggerTaskAction, Object id, List<List<Column>> changeFieldList, Message<Row> event) {
        List<Object> list = new ArrayList<>();
        Function function = PamirsSession.getContext().getFunction(triggerTaskAction.getExecuteNamespace(), triggerTaskAction.getExecuteFun());
        FunctionContext functionContext = FunctionHelper.getFunctionContext(function);
        list.add(functionContext);

        String contextArgumentName = triggerTaskAction.getWiredContext();
        String eventParameterName = triggerTaskAction.getEventParameter();
        boolean hasContextArgumentName = StringUtils.isNotBlank(contextArgumentName);
        boolean hasEventParameterName = StringUtils.isNotBlank(eventParameterName);
        int index = 0;
        int changeDataSize = changeFieldList.size();
        List<Arg> args = function.getArguments();
        for (Arg arg : args) {
            String argName = arg.getName();
            if (hasContextArgumentName && contextArgumentName.equals(argName)) {
                list.add(triggerTaskAction.getContext());
                continue;
            }
            if (hasEventParameterName && eventParameterName.equals(argName)) {
                String msgId = Optional.ofNullable(event.getHeaders())
                        .map(_headers -> _headers.get("msgId", String.class))
                        .orElse(null);
                list.add(msgId);
                continue;
            }
            if (index < changeDataSize) {
                List<Column> changeFields = changeFieldList.get(index);
                Map<String, Object> modelMap = new HashMap<>();
                modelMap.put(VariableNameConstants.id, id);

                for (Column changeField : changeFields) {
                    for (ModelFieldConfig modelFieldConfig : modelConfig.getModelFieldConfigList()) {
                        String column = modelFieldConfig.getColumn();
                        if (!changeField.isNull() && column != null && (column.equals(changeField.getName())
                                || PStringUtils.column2FieldName(column).equalsIgnoreCase(changeField.getName()))) {
                            Object value = changeField.getValue();
                            if (TtypeEnum.BOOLEAN.value().equals(modelFieldConfig.getTtype())) {
                                value = BooleanHelper.isTrueWithoutException(value);
                            }
                            modelMap.put(modelFieldConfig.getLname(), value);
                            break;
                        }
                    }
                }
                Models.api().setModel(modelMap, modelConfig.getModel());
                list.add(modelMap);
                index++;
            }

        }
        return JsonUtils.toJSONString(list, JSON.DEFAULT_GENERATE_FEATURE & ~SerializerFeature.DisableCircularReferenceDetect.getMask());
    }

    private enum UpdateEventSwitch {
        UPDATE,
        DELETED,
        RECOVERY,
        MODIFY_DELETED_DATA;

        private static UpdateEventSwitch eventSwitch(boolean isBeforeDeleted, boolean isAfterDeleted) {
            if (isBeforeDeleted) {
                if (isAfterDeleted) {
                    return MODIFY_DELETED_DATA;
                } else {
                    return RECOVERY;
                }
            } else {
                if (isAfterDeleted) {
                    return DELETED;
                } else {
                    return UPDATE;
                }
            }
        }
    }

    private void fillSessionUid(List<Column> columns) {
        for (Column column : columns) {
            if ("write_uid".equals(column.getName())) {
                PamirsSession.setUserId(column.getValue() == null ? TriggerUserConfiguration.TRIGGER_SYSTEM_USER_ID : Long.valueOf(column.getValue().toString()));
                break;
            }
        }
    }
}
