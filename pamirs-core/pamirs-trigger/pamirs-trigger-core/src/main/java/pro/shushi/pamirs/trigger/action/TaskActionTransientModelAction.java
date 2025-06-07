package pro.shushi.pamirs.trigger.action;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.core.common.cache.MemoryIterableSearchCache;
import pro.shushi.pamirs.framework.connectors.data.sql.config.Configs;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.dto.condition.Order;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.condition.Sort;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.SortDirectionEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;
import pro.shushi.pamirs.middleware.schedule.common.Page;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;
import pro.shushi.pamirs.trigger.service.ScheduleOperateService;
import pro.shushi.pamirs.trigger.tmodel.TaskActionTransientModel;

import java.util.ArrayList;
import java.util.List;

import static pro.shushi.pamirs.meta.enmu.FunctionOpenEnum.*;

/**
 * @author Adamancy Zhang on 2021-02-04 16:42
 */
@Base
@Component
@Model.model(TaskActionTransientModel.MODEL_MODEL)
public class TaskActionTransientModelAction {

    @Autowired
    private ScheduleOperateService scheduleOperateService;

    @Function.Advanced(type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(FunctionConstants.queryPage)
    @Function(openLevel = {LOCAL, REMOTE, API})
    public Pagination<TaskActionTransientModel> queryPage(Pagination<TaskActionTransientModel> page, IWrapper<TaskActionTransientModel> queryWrapper) {
        LambdaQueryWrapper<TaskActionTransientModel> wrapper = ((QueryWrapper<TaskActionTransientModel>) queryWrapper).lambda();
        Long size = page.getSize();
        if (size == null) {
            size = 15L;
        }
        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(TaskActionTransientModel.MODEL_MODEL);
        MemoryIterableSearchCache<String, ModelFieldConfig> cache = new MemoryIterableSearchCache<>(modelConfig.getModelFieldConfigList(), ModelFieldConfig::getField);
        StringBuilder orderBuilder = new StringBuilder();
        Sort sort = page.getSort();
        if (sort != null) {
            List<Order> orders = sort.getOrders();
            if (CollectionUtils.isNotEmpty(orders)) {
                for (Order order : orders) {
                    if (order != null) {
                        String field = order.getField();
                        if (StringUtils.isBlank(field)) {
                            continue;
                        }
                        ModelFieldConfig modelFieldConfig = cache.get(field);
                        if (modelFieldConfig == null) {
                            continue;
                        }
                        String column = Configs.wrap(modelFieldConfig).getSqlSelect(true);
                        if (StringUtils.isBlank(column)) {
                            continue;
                        }
                        SortDirectionEnum sortDirection = order.getDirection();
                        if (sortDirection == null) {
                            sortDirection = SortDirectionEnum.ASC;
                        }
                        orderBuilder.append(column).append(CharacterConstants.SEPARATOR_BLANK).append(sortDirection.value());
                    }
                }
            }
        }
        String scene = FetchUtil.fetchScene();
        boolean isNeedQuery;
        // FIXME: zbh 20220331 scene规则发生变更 待测试
        if ("TriggerMenus_PamirsDepartmentMenu".equals(scene)) {
            wrapper.isNotNull(TaskActionTransientModel::getTechnicalName);
            isNeedQuery = true;
        } else if ("TriggerMenus_PamirsOrganizationMenu".equals(scene)) {
            wrapper.isNull(TaskActionTransientModel::getTechnicalName);
            isNeedQuery = true;
        } else {
            isNeedQuery = false;
        }
        if (isNeedQuery) {
            Page<ScheduleItem> result = scheduleOperateService.selectListByWhere(wrapper.getSqlSegment(), orderBuilder.toString(), page.getCurrentPage(), size.intValue());
            page.setContent(TaskActionTransientModel.transfers(result.getList()));
            page.setTotalElements(result.getTotal().longValue());
        }
        return page;
    }

    @Function.Advanced(type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(FunctionConstants.queryByEntity)
    @Function(openLevel = {LOCAL, REMOTE, API})
    public TaskActionTransientModel queryOne(TaskActionTransientModel query) {
        ScheduleItem item = scheduleOperateService.selectById(query.getId());
        return TaskActionTransientModel.transfer(item);
    }

    @Action.Advanced(name = FunctionConstants.create, managed = true)
    @Action(displayName = "确定", summary = "添加", bindingType = ViewTypeEnum.FORM)
    @Function(name = FunctionConstants.create)
    @Function.fun(FunctionConstants.create)
    public TaskActionTransientModel create(TaskActionTransientModel data) {
        return data;
    }

    @Action.Advanced(name = FunctionConstants.update, managed = true)
    @Action(displayName = "确定", summary = "修改", bindingType = ViewTypeEnum.FORM)
    @Function(name = FunctionConstants.update)
    @Function.fun(FunctionConstants.update)
    public TaskActionTransientModel update(TaskActionTransientModel data) {
        scheduleOperateService.updateById(TaskActionTransientModel.reverse(data));
        return data;
    }

    @Action.Advanced(name = FunctionConstants.delete, managed = true)
    @Action(displayName = "删除", contextType = ActionContextTypeEnum.SINGLE_AND_BATCH)
    @Function(name = FunctionConstants.delete)
    @Function.fun(FunctionConstants.deleteWithFieldBatch)
    public List<TaskActionTransientModel> deleteWithFieldBatch(List<TaskActionTransientModel> dataList) {
        List<Long> ids = new ArrayList<>();
        for (TaskActionTransientModel item : dataList) {
            if (item != null) {
                Long id = item.getId();
                if (id != null) {
                    ids.add(id);
                }
            }
        }
        scheduleOperateService.deleteByIds(ids);
        return dataList;
    }

    @Action(displayName = "取消", contextType = ActionContextTypeEnum.SINGLE_AND_BATCH)
    public List<TaskActionTransientModel> cancel(List<TaskActionTransientModel> dataList) {
        List<Long> ids = new ArrayList<>();
        for (TaskActionTransientModel item : dataList) {
            if (item != null) {
                Long id = item.getId();
                if (id != null) {
                    ids.add(id);
                }
            }
        }
        scheduleOperateService.cancelByIds(ids);
        return dataList;
    }

    @Action(displayName = "恢复", contextType = ActionContextTypeEnum.SINGLE_AND_BATCH)
    public List<TaskActionTransientModel> recovery(List<TaskActionTransientModel> dataList) {
        List<Long> ids = new ArrayList<>();
        for (TaskActionTransientModel item : dataList) {
            if (item != null) {
                Long id = item.getId();
                if (id != null) {
                    ids.add(id);
                }
            }
        }
        scheduleOperateService.recoveryByIds(ids);
        return dataList;
    }
}
