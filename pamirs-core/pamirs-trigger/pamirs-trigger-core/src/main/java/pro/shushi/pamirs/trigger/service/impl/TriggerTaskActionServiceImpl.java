package pro.shushi.pamirs.trigger.service.impl;

import com.google.common.base.Joiner;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.core.common.ObjectHelper;
import pro.shushi.pamirs.framework.connectors.data.constant.DbConstants;
import pro.shushi.pamirs.framework.connectors.data.datasource.ddl.DdlManager;
import pro.shushi.pamirs.framework.connectors.data.dialect.Dialects;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.DsDialectComponent;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.event.spi.NotifyTopicEditorApi;
import pro.shushi.pamirs.framework.connectors.event.spi.SystemTopicEditorApi;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.middleware.canal.domain.DBInfo;
import pro.shushi.pamirs.middleware.canal.entity.RefreshFilterEntity;
import pro.shushi.pamirs.middleware.canal.service.CanalService;
import pro.shushi.pamirs.middleware.schedule.api.ScheduleAction;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleQuery;
import pro.shushi.pamirs.trigger.condition.ScheduleSwitchCondition;
import pro.shushi.pamirs.trigger.config.PamirsTriggerConfiguration;
import pro.shushi.pamirs.trigger.constant.NotifyConstant;
import pro.shushi.pamirs.trigger.constant.WorkFlowConstant;
import pro.shushi.pamirs.trigger.model.TriggerTaskAction;
import pro.shushi.pamirs.trigger.service.AbstractTaskActionService;
import pro.shushi.pamirs.trigger.service.TriggerTaskActionExecutor;
import pro.shushi.pamirs.trigger.service.TriggerTaskActionService;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Adamancy Zhang
 * @date 2020-11-02 15:44
 */
@Slf4j
@Service
@Fun(TriggerTaskActionService.FUN_NAMESPACE)
@Conditional(ScheduleSwitchCondition.class)
public class TriggerTaskActionServiceImpl extends AbstractTaskActionService<TriggerTaskAction> implements TriggerTaskActionService {

    @Autowired(required = false)
    private CanalService               canalService;
    @Autowired
    private DdlManager                 ddlManager;
    @Autowired(required = false)
    private PamirsTriggerConfiguration configuration;

    @Function
    @Override
    public List<TriggerTaskAction> selectList(ScheduleQuery wrapper) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long countByEntity(TriggerTaskAction taskAction) {
        return Models.origin().count(taskAction);
    }

    @Function
    @Override
    public List<TriggerTaskAction> selectListByEntity(TriggerTaskAction entity) {
        return Models.origin().queryListByEntity(entity);
    }

    @Function
    @Override
    public Boolean delete(String technicalName) {
        if (StringUtils.isBlank(technicalName)) {
            return false;
        }
        return Models.origin().deleteByWrapper(Pops.<TriggerTaskAction>lambdaQuery()
                .from(TriggerTaskAction.MODEL_MODEL)
                .eq(TriggerTaskAction::getTechnicalName, technicalName)) == 1;
    }

    @Function
    @Override
    public Boolean deleteBatch(Collection<String> technicalNames) {
        if (CollectionUtils.isEmpty(technicalNames)) {
            return false;
        }
        int count = Models.origin().deleteByWrapper(Pops.<TriggerTaskAction>lambdaQuery()
                .from(TriggerTaskAction.MODEL_MODEL)
                .in(TriggerTaskAction::getTechnicalName, technicalNames));
        return count == 1 || count == technicalNames.size();
    }

    @Function
    @Override
    public Boolean active(String technicalName) {
        if (configuration == null) {
            throw new UnsupportedOperationException("please enable event.");
        }
        if (null == canalService) {
            throw new UnsupportedOperationException("sql record filter service not found");
        }
        boolean isSuccess = Models.origin().updateByWrapper((TriggerTaskAction) new TriggerTaskAction()
                .setActive(true), Pops.<TriggerTaskAction>lambdaQuery()
                .from(TriggerTaskAction.MODEL_MODEL)
                .eq(TriggerTaskAction::getTechnicalName, technicalName)
                .eq(TriggerTaskAction::getActive, false)) == 1;
        if (isSuccess) {
            TriggerTaskAction origin = Models.origin().queryOneByWrapper(Pops.<TriggerTaskAction>lambdaQuery()
                    .from(TriggerTaskAction.MODEL_MODEL)
                    .select(TriggerTaskAction::getModel)
                    .eq(TriggerTaskAction::getTechnicalName, technicalName));
            ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(origin.getModel());
            String database = Dialects.component(DsDialectComponent.class, modelConfig.getDsKey()).getDatabase(modelConfig.getDsKey());
            String table = modelConfig.getTable();

            Set<String> filters = new HashSet<>();
            filters.add(database + CharacterConstants.SEPARATOR_ESCAPE_DOT + table);
            filters.add(modelConfig.getDsKey() + CharacterConstants.SEPARATOR_ESCAPE_DOT + table);
            canalService.appendFilter(new RefreshFilterEntity()
                    .setDestination(configuration.getCanal().getDestination())
                    .setNewFilter(Joiner.on(CharacterConstants.SEPARATOR_COMMA).skipNulls().join(filters)));
        }
        return isSuccess;
    }

    @Function
    @Override
    public Boolean activeBatch(Collection<String> technicalNames) {
        if (configuration == null) {
            throw new UnsupportedOperationException("please enable event.");
        }
        if (null == canalService) {
            throw new UnsupportedOperationException("sql record filter service not found");
        }
        if (CollectionUtils.isEmpty(technicalNames)) {
            return false;
        }
        int count = Models.origin().updateByWrapper((TriggerTaskAction) new TriggerTaskAction()
                .setActive(true), Pops.<TriggerTaskAction>lambdaQuery()
                .from(TriggerTaskAction.MODEL_MODEL)
                .in(TriggerTaskAction::getTechnicalName, technicalNames)
                .eq(TriggerTaskAction::getActive, false));
        boolean isSuccess = count == 1 || count == technicalNames.size();
        if (isSuccess) {
            List<TriggerTaskAction> origins = Models.origin().queryListByWrapper(Pops.<TriggerTaskAction>lambdaQuery()
                    .from(TriggerTaskAction.MODEL_MODEL)
                    .select(TriggerTaskAction::getModel)
                    .eq(TriggerTaskAction::getTechnicalName, technicalNames));
            Set<String> models = new HashSet<>();
            Set<String> filters = new HashSet<>();
            for (TriggerTaskAction origin : origins) {
                String model = origin.getModel();
                if (ObjectHelper.isRepeat(models, model)) {
                    continue;
                }
                ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(model);
                String database = Dialects.component(DsDialectComponent.class, modelConfig.getDsKey()).getDatabase(modelConfig.getDsKey());
                String table = modelConfig.getTable();
                filters.add(database + CharacterConstants.SEPARATOR_ESCAPE_DOT + table);
                filters.add(modelConfig.getDsKey() + CharacterConstants.SEPARATOR_ESCAPE_DOT + table);
            }
            if (!filters.isEmpty()) {
                canalService.appendFilter(new RefreshFilterEntity()
                        .setDestination(configuration.getCanal().getDestination())
                        .setNewFilter(Joiner.on(CharacterConstants.SEPARATOR_COMMA).skipNulls().join( filters)));
            }
        }
        return isSuccess;
    }

    @Function
    @Override
    public Boolean cancel(String technicalName) {
        // FIXME: 2022/1/13 不能移除canal的filter,移除期间的ddl不会被记录,再次监听后,表结构不一致导致binlog消费失败
        return cancel(technicalName, Boolean.FALSE);
    }

    private Boolean cancel(String technicalName, Boolean removeFilter) {
        if (configuration == null) {
            throw new UnsupportedOperationException("please enable event.");
        }
        if (null == canalService) {
            throw new UnsupportedOperationException("sql record filter service not found");
        }
        if (StringUtils.isBlank(technicalName)) {
            return false;
        }
        boolean isSuccess = Models.origin().updateByWrapper((TriggerTaskAction) new TriggerTaskAction()
                .setActive(false), Pops.<TriggerTaskAction>lambdaQuery()
                .from(TriggerTaskAction.MODEL_MODEL)
                .eq(TriggerTaskAction::getTechnicalName, technicalName)
                .eq(TriggerTaskAction::getActive, true)) == 1;
        if (isSuccess && Boolean.TRUE.equals(removeFilter)) {
            TriggerTaskAction origin = Models.origin().queryOneByWrapper(Pops.<TriggerTaskAction>lambdaQuery()
                    .from(TriggerTaskAction.MODEL_MODEL)
                    .select(TriggerTaskAction::getModel)
                    .eq(TriggerTaskAction::getTechnicalName, technicalName));
            String model = origin.getModel();
            if (Models.origin().count(Pops.<TriggerTaskAction>lambdaQuery()
                    .from(TriggerTaskAction.MODEL_MODEL)
                    .eq(TriggerTaskAction::getModel, model)
                    .eq(TriggerTaskAction::getActive, true)) == 0) {
                ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(model);
                String database = Dialects.component(DsDialectComponent.class, modelConfig.getDsKey()).getDatabase(modelConfig.getDsKey());
                String table = modelConfig.getTable();
                canalService.removeFilter(new RefreshFilterEntity()
                        .setDestination(configuration.getCanal().getDestination())
                        .setNewFilter(database + CharacterConstants.SEPARATOR_ESCAPE_DOT + table));
            }
        }
        return isSuccess;
    }

    @Function
    @Override
    public Boolean cancelBatch(Collection<String> technicalNames) {
        // FIXME: 2022/1/13 不能移除canal的filter,移除期间的ddl不会被记录,再次监听后,表结构不一致导致binlog消费失败
        return cancelBatch(technicalNames, Boolean.FALSE);
    }

    public Boolean cancelBatch(Collection<String> technicalNames, Boolean removeFilter) {
        if (configuration == null) {
            throw new UnsupportedOperationException("please enable event.");
        }
        if (null == canalService) {
            throw new UnsupportedOperationException("sql record filter service not found");
        }
        if (CollectionUtils.isEmpty(technicalNames)) {
            return false;
        }
        int count = Models.origin().updateByWrapper((TriggerTaskAction) new TriggerTaskAction()
                .setActive(false), Pops.<TriggerTaskAction>lambdaQuery()
                .from(TriggerTaskAction.MODEL_MODEL)
                .in(TriggerTaskAction::getTechnicalName, technicalNames)
                .eq(TriggerTaskAction::getActive, true));
        boolean isSuccess = count == 1 || count == technicalNames.size();
        if (isSuccess && Boolean.TRUE.equals(removeFilter)) {
            List<TriggerTaskAction> origins = Models.origin().queryListByWrapper(Pops.<TriggerTaskAction>lambdaQuery()
                    .from(TriggerTaskAction.MODEL_MODEL)
                    .select(TriggerTaskAction::getModel)
                    .eq(TriggerTaskAction::getTechnicalName, technicalNames));
            Set<String> models = new HashSet<>();
            Set<String> filters = new HashSet<>();
            for (TriggerTaskAction origin : origins) {
                String model = origin.getModel();
                if (ObjectHelper.isRepeat(models, model)) {
                    continue;
                }
                if (Models.origin().count(Pops.<TriggerTaskAction>lambdaQuery()
                        .from(TriggerTaskAction.MODEL_MODEL)
                        .eq(TriggerTaskAction::getModel, model)
                        .eq(TriggerTaskAction::getActive, true)) == 0) {
                    ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(origin.getModel());
                    String database = Dialects.component(DsDialectComponent.class, modelConfig.getDsKey()).getDatabase(modelConfig.getDsKey());
                    String table = modelConfig.getTable();
                    filters.add(database + CharacterConstants.SEPARATOR_ESCAPE_DOT + table);
                }
            }
            if (!filters.isEmpty()) {
                canalService.removeFilter(new RefreshFilterEntity()
                        .setDestination(configuration.getCanal().getDestination())
                        .setNewFilter(String.join(CharacterConstants.SEPARATOR_COMMA, filters)));
            }
        }
        return isSuccess;
    }

    @Function
    @Override
    public TriggerTaskAction createOrUpdate(TriggerTaskAction data) {
        if (configuration == null) {
            throw new UnsupportedOperationException("please enable event.");
        }
        if (null == canalService) {
            throw new UnsupportedOperationException("sql record filter service not found");
        }
        String model = data.getModel();
        if (StringUtils.isBlank(model)) {
            throw new IllegalArgumentException("Invalid model value is null.");
        }
        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(model);
        if (modelConfig == null) {
            throw new IllegalArgumentException("Not exist model config.");
        }
        TriggerTaskAction origin = FetchUtil.fetchOne(data);
        if (origin == null) {
            data = data.create();
        } else {
            TriggerTaskAction finalData = data;
            FetchUtil.<TriggerTaskAction, Integer>consumerQueryWrapper(data, () -> null, wrapper -> Models.origin().updateByWrapper(finalData, wrapper));
        }
        //构建RefreshFilterEntity
        RefreshFilterEntity filterEntity = buildFilterEntity(modelConfig);
        canalService.appendFilter(filterEntity);

        return data;
    }

    @Function
    @Override
    public Boolean submit(TriggerTaskAction taskItem) {
        return super.submit(taskItem);
    }

    @Override
    protected ScheduleItem generatorScheduleItem(ScheduleItem scheduleItem, TriggerTaskAction taskItem) {
        initExecuteTaskAction(scheduleItem, taskItem);
        scheduleItem.setInterfaceName(TriggerTaskActionExecutor.FUN_NAMESPACE);
        scheduleItem.setMethodName(ScheduleAction.DEFAULT_METHOD);
        return scheduleItem;
    }

    @Override
    protected TriggerTaskAction generatorScheduleTaskAction(ScheduleItem scheduleItem) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected ScheduleItem generatorScheduleItemQueryEntity(TriggerTaskAction entity) {
        throw new UnsupportedOperationException();
    }

    private RefreshFilterEntity buildFilterEntity(ModelConfig modelConfig) {
        String database = Dialects.component(DsDialectComponent.class, modelConfig.getDsKey()).getDatabase(modelConfig.getDsKey());
        String table = modelConfig.getTable();

        //FILTERS
        List<String> allFilters = new ArrayList<>();
        allFilters.add(database + CharacterConstants.SEPARATOR_ESCAPE_DOT + table);
        allFilters.add(modelConfig.getDsKey() + CharacterConstants.SEPARATOR_ESCAPE_DOT + table);
        for (String defaultTable : WorkFlowConstant.DEFAULT_WORKFLOW_FILTER_TABLES) {
            String currentDatabase = database;
            String currentDsKey = null;
            if ("workflow_workflow_timer_instance".equals(defaultTable)) {
                ModelConfig currentModelConfig = PamirsSession.getContext().getModelConfig("workflow.WorkflowTimerInstance");
                currentDatabase = Dialects.component(DsDialectComponent.class, currentModelConfig.getDsKey()).getDatabase(currentModelConfig.getDsKey());
                currentDsKey = currentModelConfig.getDsKey();
            } else if ("workflow_workflow_user_task".equals(defaultTable)) {
                ModelConfig currentModelConfig = PamirsSession.getContext().getModelConfig("workflow.WorkflowUserTask");
                currentDatabase = Dialects.component(DsDialectComponent.class, currentModelConfig.getDsKey()).getDatabase(currentModelConfig.getDsKey());
                currentDsKey = currentModelConfig.getDsKey();
            }
            if (!defaultTable.equalsIgnoreCase(table)) {
                allFilters.add(currentDatabase + CharacterConstants.SEPARATOR_ESCAPE_DOT + defaultTable);
                allFilters.add(currentDsKey + CharacterConstants.SEPARATOR_ESCAPE_DOT + defaultTable);
            }
        }
        String filters = Joiner.on(CharacterConstants.SEPARATOR_COMMA).skipNulls().join(allFilters);
        RefreshFilterEntity filterEntity = new RefreshFilterEntity().setDestination(configuration.getCanal().getDestination()).setNewFilter(filters);
        log.info("===buildFilterEntity====destination:{}, filters:{}", configuration.getCanal().getDestination(), filters);

        //TOPIC
        String topic = Spider.getDefaultExtension(NotifyTopicEditorApi.class).handlerTopic(NotifyConstant.AUTO_TRIGGER_TOPIC);
        topic = Spider.getDefaultExtension(SystemTopicEditorApi.class).handlerTopic(topic);
        filterEntity.setMqTopic(topic);
        log.info("===buildFilterEntity====topic:{}", topic);

        //DBInfo
        Map<String, String> dsConfig = ddlManager.getDsConfig(modelConfig.getDsKey());
        URI url = ddlManager.getUri(modelConfig.getDsKey());
        DBInfo dbInfo = new DBInfo();
        dbInfo.setAddress(url.getHost());
        dbInfo.setPort(url.getPort());
        dbInfo.setUserName(dsConfig.get(DbConstants.FIELD_USERNAME));
        dbInfo.setPassword(dsConfig.get(DbConstants.FIELD_PASSWORD));
//        filterEntity.setDbInfo(dbInfo);
        return filterEntity;
    }

}
