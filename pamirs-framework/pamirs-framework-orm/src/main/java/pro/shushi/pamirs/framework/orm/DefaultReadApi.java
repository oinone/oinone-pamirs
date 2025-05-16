package pro.shushi.pamirs.framework.orm;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.api.utils.SortUtils;
import pro.shushi.pamirs.framework.connectors.data.mapper.GenericMapper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.config.ModelFieldConfigWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.framework.orm.helper.QueryFieldColumnsHelper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.PamirsMapperConfigurationProxy;
import pro.shushi.pamirs.meta.api.core.orm.ReadApi;
import pro.shushi.pamirs.meta.api.core.orm.convert.DataConverter;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.condition.Sort;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.api.dto.entity.MapWrapper;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.BaseModel;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.FunctionCategoryEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static pro.shushi.pamirs.meta.enmu.FunctionOpenEnum.*;

/**
 * 函数API实现
 *
 * @author d@shushi.pro
 * @author wx@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
@Slf4j
@Fun(BaseModel.MODEL_MODEL)
@Component
public class DefaultReadApi extends AbstractReadWriteApi implements ReadApi, FunctionConstants {

    @Resource
    private GenericMapper genericMapper;

    @Resource
    private DataConverter persistenceDataConverter;

    @Resource
    private PamirsMapperConfigurationProxy pamirsMapperConfigurationProxy;

    @Function.Advanced(displayName = "根据主键查询记录", type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(queryByPk)
    @Function(openLevel = {LOCAL, REMOTE, API})
    @Override
    public <T> T queryByPk(T query) {
        if (null == query) {
            return null;
        }
        String model = getModel(query);
        DataMap result = genericMapper.selectByPk(persistenceDataConverter.in(model, query));
        persistenceDataConverter.out(model, query);
        return persistenceDataConverter.out(model, result);
    }

    @Function.Advanced(displayName = "查询单条记录", type = FunctionTypeEnum.QUERY, category = FunctionCategoryEnum.QUERY_ONE, managed = true)
    @Function.fun(queryByEntity)
    @Function(openLevel = {LOCAL, REMOTE, API})
    @Override
    public <T> T queryOne(T query) {
        if (null == query) {
            return null;
        }
        String model = getModel(query);
        DataMap result = genericMapper.selectOneByEntity(persistenceDataConverter.in(model, query));
        persistenceDataConverter.out(model, query);
        return persistenceDataConverter.out(model, result);
    }

    @Function.Advanced(displayName = "根据条件查询单条记录", type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(queryByWrapper)
    @Function(openLevel = {LOCAL, REMOTE, API})
    @Override
    public <T> T queryOneByWrapper(IWrapper<T> queryWrapper) {
        if (null == queryWrapper) {
            return null;
        }
        String model = queryWrapper.getModel();
        DataMap queryEntity = MapWrapper.wrap(persistenceDataConverter.in(model, queryWrapper.getEntity())).getDataMap();
        DataMap result = genericMapper.selectOne(queryWrapper.generic(model, queryEntity));
        persistenceDataConverter.out(model, queryWrapper.getEntity());
        return persistenceDataConverter.out(model, result);
    }

    @Function.Advanced(displayName = "查询记录列表", type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(queryListByEntity)
    @Function(openLevel = {LOCAL, REMOTE, API})
    @Override
    public <T> List<T> queryListByEntity(T query) {
        return queryListByEntityWithBatchSize(query, null);
    }

    @Function.Advanced(displayName = "查询记录列表(分批查询)", type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(queryListByEntityWithBatchSize)
    @Function(openLevel = {LOCAL, REMOTE})
    @Override
    public <T> List<T> queryListByEntityWithBatchSize(T query, Integer batchSize) {
        if (null == query) {
            return null;
        }
        String model = getModel(query);
        if (batchSize == null || batchSize == 0) {
            batchSize = fetchReadBatchSize(model);
        }
        if (batchSize < 0) {
            List<DataMap> result = genericMapper.selectListByEntity(persistenceDataConverter.in(model, query));
            persistenceDataConverter.out(model, query);
            return persistenceDataConverter.out(model, result);
        } else {
            Pagination<T> pagination = toPage(new Pagination<>(), model);
            pagination.setSize((long) batchSize);
            QueryWrapper<T> queryWrapper = new QueryWrapper<>();
            queryWrapper.setModel(model);
            queryWrapper.setEntity(query);
            Pagination<T> contentPagination = queryPage(pagination, queryWrapper);
            List<T> allList = contentPagination.getContent();
            if (allList.size() < batchSize) {
                return allList;
            }
            int totalPage = contentPagination.getTotalPages();
            for (int currentPage = 2; currentPage <= totalPage; currentPage++) {
                pagination.setCurrentPage(currentPage);
                List<T> list = queryListByWrapper(pagination, queryWrapper);
                allList.addAll(list);
                if (list.size() < batchSize) {
                    break;
                }
            }
            return allList;
        }
    }

    @Function.Advanced(displayName = "根据条件查询记录列表", type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(queryListByWrapper)
    @Function(openLevel = {LOCAL, REMOTE, API})
    @Override
    public <T> List<T> queryListByWrapper(IWrapper<T> queryWrapper) {
        if (null == queryWrapper) {
            return null;
        }
        String model = queryWrapper.getModel();
        int batchSize = queryWrapper.getBatchSize();
        if (batchSize == 0) {
            batchSize = fetchReadBatchSize(model);
        }
        if (batchSize < 0) {
            DataMap queryEntity = MapWrapper.wrap(persistenceDataConverter.in(model, queryWrapper.getEntity())).getDataMap();
            List<DataMap> result = genericMapper.selectList(queryWrapper.generic(model, queryEntity));
            persistenceDataConverter.out(model, queryWrapper.getEntity());
            return persistenceDataConverter.out(model, result);
        } else {
            Pagination<T> pagination = toPage(new Pagination<>(queryWrapper.getSortable()), model);
            pagination.setSize((long) batchSize);
            Pagination<T> contentPagination = queryPage(pagination, queryWrapper);
            List<T> allList = contentPagination.getContent();
            if (allList.size() < batchSize) {
                return allList;
            }
            int totalPage = contentPagination.getTotalPages();
            for (int currentPage = 2; currentPage <= totalPage; currentPage++) {
                pagination.setCurrentPage(currentPage);
                List<T> list = queryListByWrapper(pagination, queryWrapper);
                allList.addAll(list);
                if (list.size() < batchSize) {
                    break;
                }
            }
            return allList;
        }
    }

    @Function.Advanced(displayName = "分页查询记录列表", type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(queryListByPage)
    @Function(name = queryListByPage, openLevel = {LOCAL, REMOTE})
    @Override
    public <T> List<T> queryListByEntity(Pagination<T> page, T query) {
        if (null == page && null == query) {
            return null;
        }
        String model = getModel(query);
        Pagination<DataMap> request = toPage(page, model);
        List<DataMap> result = genericMapper.selectListByPage(request,
                Pops.query(persistenceDataConverter.in(model, query)));
        persistenceDataConverter.out(model, query);
        return persistenceDataConverter.out(model, result);
    }

    @Function.Advanced(displayName = "根据条件分页查询记录列表", type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(queryListByPageAndWrapper)
    @Function(name = queryListByPageAndWrapper, openLevel = {LOCAL, REMOTE})
    @Override
    public <T> List<T> queryListByWrapper(Pagination<T> page, IWrapper<T> queryWrapper) {
        if (null == page) {
            return null;
        }
        String model = queryWrapper.getModel();
        Pagination<DataMap> request = toPage(page, model);
        DataMap queryEntity = MapWrapper.wrap(persistenceDataConverter.in(model, queryWrapper.getEntity())).getDataMap();
        List<DataMap> result = genericMapper.selectListByPage(request, queryWrapper.generic(model, queryEntity));
        persistenceDataConverter.out(model, queryWrapper.getEntity());
        return persistenceDataConverter.out(model, result);
    }

    @Function.Advanced(displayName = "根据条件分页查询记录列表和总数", type = FunctionTypeEnum.QUERY, category = FunctionCategoryEnum.QUERY_PAGE, managed = true)
    @Function.fun(queryPage)
    @Function(openLevel = {LOCAL, REMOTE, API})
    @Override
    public <T> Pagination<T> queryPage(Pagination<T> page, IWrapper<T> queryWrapper) {
        if (null == page) {
            return null;
        }
        String model = queryWrapper.getModel();
        Pagination<DataMap> result = toPage(page, model);
        DataMap queryEntity = MapWrapper.wrap(persistenceDataConverter.in(model, queryWrapper.getEntity())).getDataMap();
        result = genericMapper.selectPage(result, queryWrapper.generic(model, queryEntity));
        persistenceDataConverter.out(model, queryWrapper.getEntity());
        page.setContent(persistenceDataConverter.out(model, result.getContent()));
        result.to(page);
        return page;
    }

    @Function.Advanced(displayName = "查询记录总数", type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(count)
    @Function(openLevel = {LOCAL, REMOTE, API})
    @Override
    public <T> Long count(T query) {
        if (null == query) {
            return null;
        }
        String model = getModel(query);
        Long result = genericMapper.selectCountByEntity(persistenceDataConverter.in(model, query));
        persistenceDataConverter.out(model, query);
        return result;
    }

    @Function.Advanced(displayName = "根据条件查询记录总数", type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(countByWrapper)
    @Function(name = countByWrapper, openLevel = {LOCAL, REMOTE, API})
    @Override
    public <T> Long count(IWrapper<T> queryWrapper) {
        if (null == queryWrapper) {
            return null;
        }
        String model = queryWrapper.getModel();
        DataMap queryEntity = MapWrapper.wrap(persistenceDataConverter.in(model, queryWrapper.getEntity())).getDataMap();
        Long result = genericMapper.selectCount(queryWrapper.generic(model, queryEntity));
        persistenceDataConverter.out(model, queryWrapper.getEntity());
        return result;
    }

    @Function.Advanced(displayName = "根据关联关系,分页查询记录列表和总数", type = FunctionTypeEnum.QUERY, managed = true)
    @Function(openLevel = {LOCAL, REMOTE, API})
    public <T> Pagination<T> relationQueryPage(Pagination<T> page, IWrapper<T> queryWrapper,
                                               String relationModel, String relationField, Map<String, Object> relationData) {
        ModelFieldConfig modelFieldConfig = PamirsSession.getContext().getModelField(relationModel, relationField);
        if (modelFieldConfig == null) {
            throw new RuntimeException("字段不存在");
        }
        if (!TtypeEnum.M2M.value().equals(modelFieldConfig.getTtype())) {
            throw new RuntimeException("字段类型错误");
        }
        // 支持 @Field.Relation(relationFields = {"id", "#USER#"}, referenceFields = {"id"})
        if (modelFieldConfig.getRelationFields().stream().filter(a -> !a.startsWith(CharacterConstants.SEPARATOR_OCTOTHORPE))
                .filter(_f -> !relationData.containsKey(_f)).findAny().orElse(null) != null) {
            throw new RuntimeException("缺少关系字段");
        }

        // 查中间表
        QueryWrapper<Object> throughWrapper = Pops.query().from(modelFieldConfig.getThrough());
        for (int i = 0; i < modelFieldConfig.getThroughRelationFields().size(); i++) {
            ModelFieldConfig relationFieldConfig = QueryFieldColumnsHelper.fetchQueryFieldConfig(modelFieldConfig, modelFieldConfig.getThrough(),
                    modelFieldConfig.getThroughRelationFields().get(i));
            throughWrapper.eq(
                    ModelFieldConfigWrapper.wrap(relationFieldConfig).getSqlSelect(),
                    relationFieldValue(relationData, modelFieldConfig.getRelationFields().get(i))
            );
        }
        List<Object> throughDataList = Models.origin().queryListByWrapper(throughWrapper);
        if (CollectionUtils.isEmpty(throughDataList)) {
            return page;
        }

        List<String> queryColumns = QueryFieldColumnsHelper.fetchQueryFieldColumns(modelFieldConfig, queryWrapper.getModel(), modelFieldConfig.getReferenceFields());
        if (queryWrapper instanceof QueryWrapper) {
            ((QueryWrapper<T>) queryWrapper).in(queryColumns,
                    modelFieldConfig.getThroughReferenceFields().stream()
                            .map(_f -> throughDataList.stream()
                                    .map(_td -> FieldUtils.getFieldValue(_td, _f))
                                    .collect(Collectors.toList()))
                            .toArray(List[]::new));
        } else {
            // TODO: 2023/3/3 lambda? 内部调用?
            throw new RuntimeException("不支持");
        }
        Map<String, Object> queryData = queryWrapper.getQueryData();
        if (null == queryData) {
            queryData = new HashMap<>();
        }
        queryData.put(relationField, throughDataList);
        queryWrapper.setQueryData(queryData);
        page.setModel(queryWrapper.getModel());
        return Models.data().queryPage(page, queryWrapper);
    }

    @SuppressWarnings("unchecked")
    private <T, S> Pagination<T> toPage(Pagination<S> source, String model) {
        Pagination<T> target = source.to(new Pagination<>());
        if (target.getSortable() != null && !target.getSortable()) {
            // 显示设置为不需要排序的场景
            target.setSort(new Sort());
        } else {
            if (null == target.getSort()) {
                String ordering = PamirsSession.getContext().getModelConfig(model).getOrdering();
                target.setSort(SortUtils.sort(ordering));
            }

        }
        return target;
    }

    private Integer fetchReadBatchSize(String model) {
        // @see pro.shushi.pamirs.framework.connectors.data.api.orm.BatchSizeHintApi
        Integer batchSize = PamirsSession.getBatchSize();
        if (batchSize == null) {
            batchSize = pamirsMapperConfigurationProxy.batchOperationForModel(model).getRead();
        }
        return batchSize;
    }

    private Object relationFieldValue(Map<String, Object> relationData, String relationField) {
        if (relationData.containsKey(relationField)) {
            return relationData.get(relationField);
        }
        if (relationField.startsWith(CharacterConstants.SEPARATOR_OCTOTHORPE)
                && relationField.endsWith(CharacterConstants.SEPARATOR_OCTOTHORPE)) {
            // @Field.Relation(relationFields = {"id", "#USER#"}, referenceFields = {"id"})
            //  #USER# 的情况，直接返回USER
            return relationField.substring(0, relationField.length() - 1).substring(1);
        }

        return null;
    }

}
