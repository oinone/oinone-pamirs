package pro.shushi.pamirs.boot.web.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.boot.base.enmu.GroupStatisticTypeEnum;
import pro.shushi.pamirs.boot.base.enmu.GroupingExpEnumerate;
import pro.shushi.pamirs.boot.base.tmodel.*;
import pro.shushi.pamirs.boot.base.utils.GroupingUtils;
import pro.shushi.pamirs.boot.web.service.GroupingService;
import pro.shushi.pamirs.boot.web.spi.api.GroupStatisticApi;
import pro.shushi.pamirs.boot.web.utils.GroupStatisticUtils;
import pro.shushi.pamirs.framework.common.utils.DataShardingHelper;
import pro.shushi.pamirs.framework.connectors.data.sql.config.Configs;
import pro.shushi.pamirs.framework.connectors.data.sql.config.ModelFieldConfigWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.framework.gateways.rsql.RsqlParseHelper;
import pro.shushi.pamirs.framework.orm.json.PamirsDataUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.serialize.SerializeProcessor;
import pro.shushi.pamirs.meta.api.core.orm.systems.relation.RelationReadApi;
import pro.shushi.pamirs.meta.api.dto.condition.Order;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.condition.Sort;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.enmu.SortDirectionEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.JsonUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static pro.shushi.pamirs.boot.base.enmu.GroupStatisticTypeEnum.COUNT;
import static pro.shushi.pamirs.boot.base.enmu.GroupStatisticTypeEnum.valueOf;

/**
 * @author Gesi at 17:10 on 2025/9/1
 */
@Service
@Slf4j
public class GroupingServiceImpl implements GroupingService {

    private static final long GROUP_LAZY_LOAD_DATA_LIMIT = 200;

    private static final TypeReference<Map<String, Object>> QUERY_DATA_TYPE_REF = new TypeReference<Map<String, Object>>() {
    };

    @Resource
    private RelationReadApi relationReadApi;

    @Override
    public <T> GroupResult<T> fetchGroupPage(Grouping<T> group, Pagination<T> page) {
        loadGroupBaseInfo(group);
        return queryGroupInfo(group, page);
    }

    @Override
    public <T> GroupResult<T> fetchGroupData(Grouping<T> group) {
        List<GroupPath<T>> expandGroupPaths = group.getExpandGroupPaths();
        if (CollectionUtils.isEmpty(expandGroupPaths)) {
            throw PamirsException.construct(GroupingExpEnumerate.LAZY_LOAD_PATHS_IS_NULL).errThrow();
        }
        loadGroupBaseInfo(group);

        Pagination<T> paginationResult = new Pagination<>(1, -1);
        QueryWrapper<T> queryWrapper = buildPageQueryWrapper(group);
        // 构建查询条件查询数据
        if (hasRelationGroupField(group)) {
            paginationResult = loadDataListByMemory(group, new Pagination<>(1, -1), queryWrapper);
        } else {
            addGroupExpandCondition(group, queryWrapper, expandGroupPaths, true);
            paginationResult = Models.origin().queryPage(paginationResult, parseQueryWrapper(queryWrapper));
        }

        GroupResult<T> groupResult = new GroupResult<>();
        groupResult.setExpandGroupData(new HashMap<>());
        group.setTotalDataCount(null);

        listQueryRelationFields(group, paginationResult.getContent());

        fullGroupInfo(group, groupResult, paginationResult.getContent(), null, true);
        groupResult.setExpandGroupDataStr(new ArrayList<>(expandGroupPaths.size()));
        for (GroupPath<T> expandGroupPath : expandGroupPaths) {
            groupResult.getExpandGroupDataStr().add(groupResult.getExpandGroupData().get(expandGroupPath));
        }
        groupResult.unsetGroups();
        return groupResult;
    }

    @Override
    public <T> GroupResult<T> fetchGroupStatistic(Grouping<T> group) {
        List<GroupPath<T>> expandGroupPaths = group.getExpandGroupPaths();
        if (CollectionUtils.isEmpty(expandGroupPaths)) {
            throw PamirsException.construct(GroupingExpEnumerate.LAZY_LOAD_PATHS_IS_NULL).errThrow();
        }
        loadGroupBaseInfo(group);
        group.setTotalDataCount(null);

        GroupResult<T> groupResult = new GroupResult<>();
        groupResult.setExpandGroupStatistic(new HashMap<>());

        if (hasRelationGroupField(group)) {
            QueryWrapper<T> queryWrapper = buildPageQueryWrapper(group);
            Pagination<T> paginationResult = new Pagination<>(1, -1);
            paginationResult = Models.origin().queryPage(paginationResult, parseQueryWrapper(queryWrapper));
            fullGroupInfo(group, groupResult, paginationResult.getContent(), statisticFunction(), false);
        } else {
            // 先试着不查数据处理统计函数（纯sql进行统计）
            List<GroupPath<T>> queryExpandGroupPaths = new ArrayList<>(group.getExpandGroupPaths());
            queryExpandGroupPaths.removeIf(groupPath -> sqlQueryStatisticDataValues(group, groupResult.getExpandGroupStatistic(), groupPath));
            if (CollectionUtils.isNotEmpty(queryExpandGroupPaths)) {
                QueryWrapper<T> queryWrapper = buildPageQueryWrapper(group);
                addGroupExpandCondition(group, queryWrapper, queryExpandGroupPaths, false);
                Pagination<T> paginationResult = new Pagination<>(1, -1);
                paginationResult.setSortable(false);
                paginationResult = Models.origin().queryPage(paginationResult, parseQueryWrapper(queryWrapper));
                fullGroupInfo(group, groupResult, paginationResult.getContent(), statisticFunction(), false);
            }
        }

        groupResult.setExpandGroupDataStr(new ArrayList<>(expandGroupPaths.size()));
        for (GroupPath<T> expandGroupPath : expandGroupPaths) {
            Map<String, Object> statisticValues = groupResult.getExpandGroupStatistic().get(expandGroupPath);
            groupResult.getExpandGroupDataStr().add(statisticValues != null ? JsonUtils.toJSONString(statisticValues) : null);
        }
        groupResult.unsetGroups();
        return groupResult;
    }

    /**
     * 加载分组基本信息
     */
    private <T> void loadGroupBaseInfo(Grouping<T> group) {
        String model = group.getModel();
        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(model);
        if (modelConfig == null) {
            throw PamirsException.construct(GroupingExpEnumerate.MODEL_NOT_FIND).appendMsg("模型" + model + "找不到").errThrow();
        }
        group.setModelConfig(modelConfig);
        ConditionQueryWrapper queryWrapper = group.getQueryWrapper();
        if (queryWrapper == null) {
            queryWrapper = new ConditionQueryWrapper();
        }
        queryWrapper.setModel(model);

        if (CollectionUtils.isNotEmpty(group.getExpandGroupPaths())) {
            for (GroupPath<T> expandGroupPath : group.getExpandGroupPaths()) {
                for (GroupPathNode<T> groupPathNode : expandGroupPath.getNodeList()) {
                    groupPathNode.setGroup(group);
                }
            }
        }
    }

    /**
     * 查询分组信息
     */
    private <T> GroupResult<T> queryGroupInfo(final Grouping<T> group, Pagination<?> page) {
        GroupResult<T> groupResult = new GroupResult<>();

        // 构建查询条件
        QueryWrapper<T> queryWrapper = buildPageQueryWrapper(group);

        boolean hasRelationGroupField = hasRelationGroupField(group);
        boolean needPagination = !hasRelationGroupField && page.getSize() != null && page.getSize() >= 0;
        Pagination<T> paginationResult;
        boolean loadData;

        if (hasRelationGroupField) {
            // 有关联字段直接查全量数据走关联字段分组处理
            paginationResult = loadDataListByMemory(group, new Pagination<>(1, -1), queryWrapper);
            loadData = true;
        } else {
            if (needPagination) {
                Pagination<T> pagination = addGroupPaginationCondition(group, queryWrapper, page.getCurrentPage(), page.getSize());
                groupResult.setTotalPages(pagination.getTotalPages());
                groupResult.setTotalElements(pagination.getTotalElements());
            } else {
                groupResult.setTotalPages(1);
            }

            for (GroupField groupField : group.getGroupFields()) {
                ModelFieldConfig modelFieldConfig = group.getModelFieldConfig(groupField.getField());
                SortDirectionEnum orderType = Optional.ofNullable(groupField.getOrderType()).orElse(SortDirectionEnum.ASC);
                queryWrapper.orderBy(true, SortDirectionEnum.ASC.equals(orderType), Configs.wrap(modelFieldConfig).getColumn());
            }
            Sort sort = page.getSort();
            if (!Boolean.FALSE.equals(page.getSortable()) && sort != null && sort.getOrders() != null) {
                for (Order order : sort.getOrders()) {
                    ModelFieldConfig modelFieldConfig = group.getModelFieldConfig(order.getField());
                    SortDirectionEnum orderType = Optional.ofNullable(order.getDirection()).orElse(SortDirectionEnum.ASC);
                    queryWrapper.orderBy(true, SortDirectionEnum.ASC.equals(orderType), Configs.wrap(modelFieldConfig).getColumn());
                }
            }
            // 查询数据
            paginationResult = Models.origin().queryPage(new Pagination<>(1, -1), parseQueryWrapper(queryWrapper));
            loadData = paginationResult.getContent().size() <= GROUP_LAZY_LOAD_DATA_LIMIT;
        }
        if (loadData) {
            listQueryRelationFields(group, paginationResult.getContent());
        }
        group.setTotalDataCount((long) paginationResult.getContent().size());
        groupResult.setTotalDataCount(group.getTotalDataCount());
        fullGroupInfo(group, groupResult, paginationResult.getContent(), null, loadData);
        if (!needPagination) {
            groupResult.setTotalElements(groupResult.getGroups() != null ? groupResult.getGroups().size() : 0L);
        }

        // 处理分组值防止前端报错
        List<GroupInfo<T>> groups = groupResult.getGroups();
        while (CollectionUtils.isNotEmpty(groups)) {
            List<GroupInfo<T>> nextGroups = new ArrayList<>();
            for (GroupInfo<T> groupInfo : groups) {
                Object value = groupInfo.getValue();
                if (value instanceof Collection) {
                    for (Object o : ((Collection<?>) value)) {
                        Map<?, ?> _d = null;
                        if (o instanceof Map) {
                            _d = (Map<?, ?>) o;
                        } else if (o instanceof D) {
                            _d = ((D) o).get_d();
                        }
                        if (_d != null) {
                            _d.entrySet().removeIf(entry -> entry.getValue() instanceof Collection || entry.getValue() instanceof Map || entry.getValue() instanceof D);
                        }
                    }
                } else if (value instanceof Map || value instanceof D) {
                    Map<?, ?> _d;
                    if (value instanceof Map) {
                        _d = (Map<?, ?>) value;
                    } else {
                        _d = ((D) value).get_d();
                    }
                    if (_d != null) {
                        _d.entrySet().removeIf(entry -> entry.getValue() instanceof Collection || entry.getValue() instanceof Map || entry.getValue() instanceof D);
                    }
                }
                if (!Boolean.TRUE.equals(groupInfo.getIsLeaf()) && CollectionUtils.isNotEmpty(groupInfo.getGroups())) {
                    nextGroups.addAll(groupInfo.getGroups());
                }
            }
            groups = nextGroups;
        }

        return groupResult;
    }

    /**
     * 构建一级分组的分页查询条件
     */
    private <T> Pagination<T> addGroupPaginationCondition(Grouping<T> group, QueryWrapper<T> queryWrapper, int pageNo, long pageSize) {
        GroupField firstGroupField = group.getGroupFields().get(0);
        ModelFieldConfig firstModelFieldConfig = group.getModelFieldConfig(firstGroupField.getField());

        Pagination<T> pagination = new Pagination<>(pageNo, pageSize);
        pagination.setSortable(false);
        SortDirectionEnum orderType = Optional.ofNullable(firstGroupField.getOrderType()).orElse(SortDirectionEnum.ASC);
        QueryWrapper<T> groupQueryWrapper = buildPageQueryWrapper(group);
        ModelFieldConfigWrapper firstModelFieldConfigWrapper = Configs.wrap(firstModelFieldConfig);
        groupQueryWrapper.isNotNull(firstModelFieldConfigWrapper.getColumn());
        if (TtypeEnum.isStringType(firstModelFieldConfig.getTtype())) {
            groupQueryWrapper.ne(firstModelFieldConfigWrapper.getColumn(), "");
        }
        groupQueryWrapper.select(firstModelFieldConfigWrapper.getColumn() + " " + firstModelFieldConfig.getField());
        groupQueryWrapper.groupBy(firstModelFieldConfigWrapper.getColumn());
        groupQueryWrapper.orderBy(true, SortDirectionEnum.ASC.equals(orderType), firstModelFieldConfigWrapper.getColumn());
        pagination = Models.origin().queryPage(pagination, parseQueryWrapper(groupQueryWrapper));
        boolean needGroupNullValue;
        if (pagination.getContent() == null) {
            pagination.setContent(new ArrayList<>());
        }

        QueryWrapper<T> groupNullQueryWrapper = buildPageQueryWrapper(group);
        Pagination<T> nullPagination = new Pagination<>(1, 1);
        nullPagination.setSortable(false);
        groupNullQueryWrapper.isNull(firstModelFieldConfigWrapper.getColumn());
        groupNullQueryWrapper.select(firstModelFieldConfigWrapper.getColumn() + " " + firstModelFieldConfig.getField());
        nullPagination = Models.origin().queryPage(nullPagination, parseQueryWrapper(groupNullQueryWrapper));
        if (nullPagination.getTotalElements() > 0) {
            pagination.setTotalElements(pagination.getTotalElements() + 1);
        }

        needGroupNullValue = pagination.getContent().size() < pageSize && CollectionUtils.isNotEmpty(nullPagination.getContent());
        if (pageNo <= pagination.getTotalPages()) {
            Pagination<T> finalPagination = pagination;
            queryWrapper.and(andWrapper -> {
                List<Object> groupValueList =
                        finalPagination.getContent().stream().map(data -> FieldUtils.getFieldValue(data, firstGroupField.getField()))
                                .map(v -> {
                                    if (TtypeEnum.MAP.value().equals(firstModelFieldConfig.getTtype())) {
                                        return JsonUtils.toJSONString(v);
                                    } else if (TtypeEnum.YEAR.value().equals(firstModelFieldConfig.getTtype())) {
                                        if (v instanceof Date) {
                                            Calendar calendar = Calendar.getInstance();
                                            calendar.setTime((Date) v);
                                            return calendar.get(Calendar.YEAR);
                                        }
                                    }
                                    return v;
                                }).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(groupValueList)) {
                    if (Boolean.TRUE.equals(firstModelFieldConfig.getMulti())) {
                        List<Object> inValList = new ArrayList<>(groupValueList.size());
                        for (Object groupValue : groupValueList) {
                            if (groupValue instanceof Collection) {
                                groupValue = Spider.getDefaultExtension(SerializeProcessor.class).serialize(firstModelFieldConfig.getStoreSerialize(), firstModelFieldConfig.getLtype(), groupValue);
                            }
                            inValList.add(groupValue);
                        }
                        groupValueList = inValList;
                    }
                    andWrapper.in(firstModelFieldConfigWrapper.getColumn(), groupValueList);
                }
                if (needGroupNullValue) {
                    andWrapper.or().isNull(firstModelFieldConfigWrapper.getColumn());
                    if (TtypeEnum.isStringType(firstModelFieldConfig.getTtype())) {
                        andWrapper.or().eq(firstModelFieldConfigWrapper.getColumn(), "");
                    } else if (TtypeEnum.OBJ.value().equals(firstModelFieldConfig.getTtype()) || TtypeEnum.MAP.value().equals(firstModelFieldConfig.getTtype())) {
                        andWrapper.or().eq(firstModelFieldConfigWrapper.getColumn(), "").or().eq(firstModelFieldConfigWrapper.getColumn(), JsonUtils.toJSONString(new LinkedHashMap<>()));
                    }
                }
            });
        } else {
            queryWrapper.eq("1", "0");
        }

        return pagination;
    }

    /**
     * 添加指定分组路径的查询条件
     */
    private <T> void addGroupExpandCondition(Grouping<T> group, QueryWrapper<T> queryWrapper, List<GroupPath<T>> expandGroupPaths, boolean needSort) {
        queryWrapper.and(andWrapper -> {
            for (GroupPath<T> expandGroupPath : expandGroupPaths) {
                andWrapper.or().and(pathAndWrapper -> {
                    for (GroupPathNode<T> pathNode : expandGroupPath.getNodeList()) {
                        ModelFieldConfig modelFieldConfig = group.getModelFieldConfig(pathNode.getField());
                        String column = Configs.wrap(modelFieldConfig).getColumn();
                        Object value = pathNode.getRealValue();
                        if (value != null) {
                            if (value instanceof Map) {
                                if (MapUtils.isNotEmpty((Map<?, ?>) value)) {
                                    pathAndWrapper.eq(column, JsonUtils.toJSONString(value));
                                } else {
                                    pathAndWrapper.and(mapAndWrapper -> {
                                        mapAndWrapper.isNull(column).or().eq(column, "").or().eq(column, JsonUtils.toJSONString(new LinkedHashMap<>()));
                                    });
                                }
                            } else if (value instanceof Collection) {
                                pathAndWrapper.eq(column, JsonUtils.toJSONString(value));
                            } else if (TtypeEnum.YEAR.value().equals(modelFieldConfig.getTtype()) && value instanceof Date) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime((Date) value);
                                pathAndWrapper.eq(column, calendar.get(Calendar.YEAR));
                            } else {
                                pathAndWrapper.eq(column, value);
                            }
                        } else {
                            pathAndWrapper.isNull(column);
                        }
                    }
                });
            }
        });

        if (needSort) {
            if (group.getQueryWrapper() != null && group.getQueryWrapper().getSort() != null && CollectionUtils.isNotEmpty(group.getQueryWrapper().getSort().getOrders())) {
                for (Order order : group.getQueryWrapper().getSort().getOrders()) {
                    ModelFieldConfig modelFieldConfig = group.getModelFieldConfig(order.getField());
                    if (SortDirectionEnum.DESC.equals(order.getDirection())) {
                        queryWrapper.orderByDesc(Configs.wrap(modelFieldConfig).getColumn());
                    } else {
                        queryWrapper.orderByAsc(Configs.wrap(modelFieldConfig).getColumn());
                    }
                }
            }
        }
    }

    /**
     * 构建页面传递过来的查询条件（分组拼接条件与该条件做and）
     */
    private <T> QueryWrapper<T> buildPageQueryWrapper(Grouping<T> group) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.from(group.getModel())
                .setRsql(group.getQueryWrapper().getRsql())
                .setQueryData(JsonUtils.parseObject(JsonUtils.toJSONString(group.getQueryWrapper().getQueryData()), QUERY_DATA_TYPE_REF));
        return queryWrapper;
    }

    /**
     * 将queryWrapper里的rsql部分解析
     */
    private <T> QueryWrapper<T> parseQueryWrapper(final QueryWrapper<T> queryWrapper) {
        RsqlParseHelper.parseQueryWrapper(queryWrapper, queryWrapper.getModel());
        return queryWrapper;
    }

    /**
     * 根据分组数据填充分组信息
     */
    private <T> void fullGroupInfo(Grouping<T> group, GroupResult<T> groupResult, List<T> dataList, BiConsumer<Grouping<T>, GroupInfo<T>> statisticConsumer, boolean loadData) {
        List<GroupField> groupFields = group.getGroupFields();

        Map<GroupPath<T>, Map<String, String>> statisticPathMap;
        if (CollectionUtils.isNotEmpty(group.getExpandGroupPaths())) {
            statisticPathMap = group.getExpandGroupPaths().stream().collect(Collectors.toMap(i -> i, i -> Optional.ofNullable(i.getStatisticFieldMap()).orElse(new HashMap<>()), (a, b) -> a));
        } else {
            statisticPathMap = new HashMap<>();
        }

        Map<GroupPath<T>, GroupInfo<T>> groupPathMap = new LinkedHashMap<>();
        Set<GroupPath<T>> firstGroupPathList = new LinkedHashSet<>();
        Set<GroupPath<T>> lastGroupPathList = new HashSet<>();

        // 加载之前已加载过的分组信息
        List<GroupInfo<T>> beforeGroupFields = groupResult.getGroups();
        LinkedList<GroupInfo<T>> beforeGroupFieldStack = new LinkedList<>();
        if (CollectionUtils.isNotEmpty(beforeGroupFields)) {
            beforeGroupFieldStack.addAll(beforeGroupFields);
        }
        while (!beforeGroupFieldStack.isEmpty()) {
            GroupInfo<T> groupInfo = beforeGroupFieldStack.remove(0);
            groupPathMap.put(new GroupPath<>(groupInfo.getGroupPath()), groupInfo);
            if (CollectionUtils.isNotEmpty(groupInfo.getGroups())) {
                beforeGroupFieldStack.addAll(groupInfo.getGroups());
            }
        }

        for (T data : dataList) {
            GroupPath<T> groupPath = null;
            for (int i = 0; i < groupFields.size(); i++) {
                GroupField groupField = groupFields.get(i);
                GroupInfo<T> parentGroupInfo = groupPathMap.get(groupPath);

                Object value = FieldUtils.getFieldValue(data, groupField.getField());
                if (groupPath == null) {
                    groupPath = new GroupPath<>();
                } else {
                    groupPath = new GroupPath<>(groupPath);
                }
                groupPath.addNode(new GroupPathNode<>(group, groupField.getField(), value));

                // 判断当前分组是否已存在
                GroupInfo<T> groupInfo = groupPathMap.get(groupPath);
                boolean isCreateGroup = false;
                if (groupInfo == null) {
                    groupInfo = new GroupInfo<>();
                    groupInfo.setIsLeaf(false);
                    groupInfo.setGroupPath(groupPath);
                    groupInfo.setField(groupField.getField());
                    groupInfo.setRealValue(value);
                    groupInfo.setGroupField(groupField);
                    groupPathMap.put(groupPath, groupInfo);
                    isCreateGroup = true;
                }

                // 将当前分组信息放到父级分组里
                if (parentGroupInfo != null && isCreateGroup) {
                    if (parentGroupInfo.getGroups() == null) {
                        parentGroupInfo.setGroups(new ArrayList<>());
                    }
                    parentGroupInfo.getGroups().add(groupInfo);
                }
                if (i == 0) {
                    firstGroupPathList.add(groupPath);
                }
            }

            // 最终获取到的当前数据所属最后一级分组信息
            GroupInfo<T> lastGroupInfo = groupPathMap.get(groupPath);
            if (lastGroupInfo.getDataList() == null) {
                lastGroupInfo.setDataList(new ArrayList<>());
            }
            lastGroupInfo.getDataList().add(data);
            lastGroupPathList.add(groupPath);
        }

        // 填充分组信息
        // 分组路径长先处理，确保处理父分组时其下面的子分组一定处理过
        Map<Integer, List<GroupPath<T>>> groupPathMapByNodeNum = new HashMap<>();
        for (GroupPath<T> groupPath : groupPathMap.keySet()) {
            groupPathMapByNodeNum.putIfAbsent(groupPath.size(), new ArrayList<>());
            groupPathMapByNodeNum.get(groupPath.size()).add(groupPath);
        }

        List<Integer> groupPathNodeNumList = new ArrayList<>(groupPathMapByNodeNum.keySet());
        groupPathNodeNumList.sort((n1, n2) -> Integer.compare(n2, n1));

        for (Integer nodeNum : groupPathNodeNumList) {
            List<GroupPath<T>> groupPathList = groupPathMapByNodeNum.get(nodeNum);
            for (GroupPath<T> groupPath : groupPathList) {
                // 这里的子级groupInfo一定是都填充完成的
                GroupInfo<T> groupInfo = groupPathMap.get(groupPath);
                groupInfo.storeValue(group);
                List<GroupInfo<T>> childGroups = groupInfo.getGroups();
                moveGroupNullValueToLast(group, childGroups);
                if (CollectionUtils.isNotEmpty(childGroups)) {
                    List<T> groupDataList = new ArrayList<>();
                    for (GroupInfo<T> childGroup : childGroups) {
                        if (CollectionUtils.isNotEmpty(childGroup.getDataList())) {
                            groupDataList.addAll(childGroup.getDataList());
                        }
                    }
                    groupInfo.setDataList(groupDataList);
                }
                if (groupResult.getExpandGroupData() != null) {
                    groupResult.getExpandGroupData().put(groupPath, groupInfo.getDataList() != null ? PamirsDataUtils.toJSONString(group.getModel(), groupInfo.getDataList()) : null);
                }

                // 计算统计函数
                Map<String, String> statisticFieldMap = statisticPathMap.get(groupPath);
                if (MapUtils.isNotEmpty(statisticFieldMap) && groupResult.getExpandGroupStatistic() != null) {
                    groupPath.setStatisticFieldMap(statisticFieldMap);
                    Map<String, Object> beforeStatisticValues = groupResult.getExpandGroupStatistic().computeIfAbsent(groupPath, k -> new HashMap<>());
                    groupInfo.setDataStatistic(beforeStatisticValues);
                    if (statisticConsumer != null) {
                        statisticConsumer.accept(group, groupInfo);
                    }
                }
            }
        }

        // 序列化叶子节点数据List
        for (GroupPath<T> lastGroupPath : lastGroupPathList) {
            GroupInfo<T> lastGroupInfo = groupPathMap.get(lastGroupPath);
            lastGroupInfo.setIsLeaf(true);
            if (lastGroupInfo.getDataList() != null && loadData) {
                lastGroupInfo.setDataListStr(lastGroupInfo.getDataList() != null ? PamirsDataUtils.toJSONString(group.getModel(), lastGroupInfo.getDataList()) : null);
            }
        }

        groupResult.setGroups(firstGroupPathList.stream().map(groupPathMap::get).collect(Collectors.toList()));
        moveGroupNullValueToLast(group, groupResult.getGroups());
    }

    /**
     * 统计函数
     */
    private <T> BiConsumer<Grouping<T>, GroupInfo<T>> statisticFunction() {
        return (group, groupInfo) -> {
            Map<String, Object> dataStatistic = groupInfo.getDataStatistic();
            GroupPath<T> groupPath = groupInfo.getGroupPath();
            Map<String, String> statisticFieldMap = groupPath.getStatisticFieldMap();
            if (MapUtils.isNotEmpty(statisticFieldMap)) {
                statisticFieldMap.forEach((statisticField, statisticType) -> {
                    GroupStatisticTypeEnum statisticTypeEnum = valueOf(statisticType);
                    List<T> dataList = groupInfo.getDataList();
                    ModelFieldConfig modelFieldConfig = group.getModelFieldConfig(statisticField);
                    if (GroupingUtils.isMemoryGroupField(modelFieldConfig) && !Boolean.TRUE.equals(modelFieldConfig.getStore())) {
                        List<T> notNullDataList = dataList.stream().filter(Objects::nonNull).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(notNullDataList)) {
                            Models.origin().listFieldQuery(notNullDataList, modelFieldConfig.getField());
                        }
                    }
                    List<?> fieldDataList;
                    if (dataList != null) {
                        fieldDataList = dataList.stream().map(data -> {
                            if (data == null) {
                                return null;
                            }
                            return FieldUtils.getFieldValue(data, statisticField);
                        }).collect(Collectors.toList());
                    } else {
                        fieldDataList = null;
                    }
                    GroupStatisticApi statisticApi = null;
                    try {
                        statisticApi = Spider.getExtension(GroupStatisticApi.class, statisticTypeEnum.getValue());
                    } catch (Exception e) {
                        log.warn(statisticType + "分组统计函数没有对应的api实现");
                    }
                    if (statisticApi != null) {
                        Object statisticValue = statisticApi
                                .statistic(group, groupInfo, statisticField, fieldDataList);
                        dataStatistic.put(statisticField, statisticValue);
                    }
                });
            }
        };
    }

    /**
     * 将空分组值放在当前分组最后面
     */
    private <T> void moveGroupNullValueToLast(Grouping<T> group, List<GroupInfo<T>> groups) {
        if (CollectionUtils.isNotEmpty(groups)) {
            GroupInfo<T> groupInfo = groups.get(0);
            if (groupInfo.getRealValue() == null) {
                groups.add(groups.remove(0));
                return;
            }
            ModelFieldConfig modelFieldConfig = group.getModelFieldConfig(groupInfo.getField());
            if (TtypeEnum.isStringType(modelFieldConfig.getTtype()) && groupInfo.getRealValue().equals("")) {
                groups.add(groups.remove(0));
            } else if (TtypeEnum.MAP.value().equals(modelFieldConfig.getTtype()) && (groupInfo.getRealValue().equals("") || groupInfo.getRealValue().equals(JsonUtils.toJSONString(new LinkedHashMap<>())))) {
                groups.add(groups.remove(0));
            }
        }
    }

    /**
     * 去数据库查询统计函数
     */
    private <T> boolean sqlQueryStatisticDataValues(Grouping<T> group, Map<GroupPath<T>, Map<String, Object>> resultMap, GroupPath<T> groupPath) {
        Map<String, String> statisticFieldMap = groupPath.getStatisticFieldMap();
        statisticFieldMap.entrySet().removeIf(e -> e.getValue() == null || GroupStatisticTypeEnum.NONE.getValue().equals(e.getValue()));
        if (MapUtils.isEmpty(statisticFieldMap)) {
            return true;
        }
        if (statisticFieldMap.keySet().stream().map(group::getModelFieldConfig).anyMatch(GroupingUtils::isMemoryGroupField)) {
            return false;
        }
        // 以下为sql可以处理的统计函数
        Set<GroupStatisticTypeEnum> totalStatisticTypes = statisticFieldMap.values().stream().map(GroupStatisticTypeEnum::valueOf).collect(Collectors.toSet());
        totalStatisticTypes.remove(COUNT);
        totalStatisticTypes.remove(GroupStatisticTypeEnum.EARLIEST_TIME);
        totalStatisticTypes.remove(GroupStatisticTypeEnum.LATEST_TIME);
        totalStatisticTypes.remove(GroupStatisticTypeEnum.TIME_RANGE_DAY);
        totalStatisticTypes.remove(GroupStatisticTypeEnum.TIME_RANGE_MONTH);
        totalStatisticTypes.remove(GroupStatisticTypeEnum.TIME_RANGE_YEAR);
        totalStatisticTypes.remove(GroupStatisticTypeEnum.SUM);
        totalStatisticTypes.remove(GroupStatisticTypeEnum.AVERAGE);
        totalStatisticTypes.remove(GroupStatisticTypeEnum.MIN);
        totalStatisticTypes.remove(GroupStatisticTypeEnum.MAX);
        if (CollectionUtils.isNotEmpty(totalStatisticTypes)) {
            return false;
        }

        Map<String, Object> statisticValues = new HashMap<>();
        QueryWrapper<T> queryWrapper = buildPageQueryWrapper(group);
        addGroupExpandCondition(group, queryWrapper, Lists.newArrayList(groupPath), false);
        Set<String> currentPathGroupFieldSet = groupPath.getNodeList().stream().map(GroupPathNode::getField).collect(Collectors.toSet());
        for (GroupField groupField : group.getGroupFields()) {
            if (currentPathGroupFieldSet.contains(groupField.getField())) {
                String groupFieldColumn = Configs.wrap(group.getModelFieldConfig(groupField.getField())).getColumn();
                queryWrapper.groupBy(groupFieldColumn);
            }
        }

        List<String> selectList = new ArrayList<>();

        // 构建分组查询函数 select 内容
        statisticFieldMap.forEach((field, statisticType) -> {
            GroupStatisticTypeEnum statisticTypeEnum = valueOf(statisticType);
            ModelFieldConfig modelFieldConfig = group.getModelFieldConfig(field);
            String column = Configs.wrap(modelFieldConfig).getColumn();
            String fieldUpperCase = field.toUpperCase();
            switch (statisticTypeEnum) {
                case COUNT:
                    selectList.add("COUNT(1) AS " + fieldUpperCase + "_COUNT");
                    break;
                case EARLIEST_TIME:
                    selectList.add("MIN(" + column + ") AS " + fieldUpperCase + "_EARLIEST_TIME");
                    break;
                case LATEST_TIME:
                    selectList.add("MAX(" + column + ") AS " + fieldUpperCase + "_LATEST_TIME");
                    break;
                case TIME_RANGE_DAY:
                case TIME_RANGE_MONTH:
                case TIME_RANGE_YEAR:
                    selectList.add("MIN(" + column + ") AS " + fieldUpperCase + "_EARLIEST_TIME");
                    selectList.add("MAX(" + column + ") AS " + fieldUpperCase + "_LATEST_TIME");
                    break;
                case SUM:
                    selectList.add("SUM(" + column + ") AS " + fieldUpperCase + "_SUM");
                    break;
                case AVERAGE:
                    selectList.add("SUM(" + column + ") / COUNT(1) AS " + fieldUpperCase + "_AVERAGE");
                    break;
                case MIN:
                    selectList.add("MIN(" + column + ") AS " + fieldUpperCase + "_MIN");
                    break;
                case MAX:
                    selectList.add("MAX(" + column + ") AS " + fieldUpperCase + "_MAX");
                    break;
            }
        });

        queryWrapper.select(selectList.toArray(new String[0]));
        Pagination<T> pagination = new Pagination<>(1, -1);
        pagination.setSortable(false);
        Pagination<T> paginationResult = Models.origin().queryPage(pagination, parseQueryWrapper(queryWrapper));
        T data = paginationResult.getContent().get(0);

        // 获取查询结果
        statisticFieldMap.forEach((field, statisticType) -> {
            GroupStatisticTypeEnum statisticTypeEnum = valueOf(statisticType);
            String fieldUpperCase = field.toUpperCase();
            Object statisticValue = null;
            switch (statisticTypeEnum) {
                case COUNT:
                    statisticValue = FieldUtils.getFieldValue(data, fieldUpperCase + "_COUNT");
                    break;
                case EARLIEST_TIME:
                    statisticValue = FieldUtils.getFieldValue(data, fieldUpperCase + "_EARLIEST_TIME");
                    break;
                case LATEST_TIME:
                    statisticValue = FieldUtils.getFieldValue(data, fieldUpperCase + "_LATEST_TIME");
                    break;
                case TIME_RANGE_DAY: {
                    Pair<Date, Date> dateRange = GroupStatisticUtils.earliestTimeAndLatestTime(Lists.newArrayList(FieldUtils.getFieldValue(data, fieldUpperCase + "_EARLIEST_TIME"), FieldUtils.getFieldValue(data, fieldUpperCase + "_LATEST_TIME")));
                    statisticValue = GroupStatisticUtils.timeRangeDay(dateRange.getLeft(), dateRange.getRight());
                    break;
                }
                case TIME_RANGE_MONTH: {
                    Pair<Date, Date> dateRange = GroupStatisticUtils.earliestTimeAndLatestTime(Lists.newArrayList(FieldUtils.getFieldValue(data, fieldUpperCase + "_EARLIEST_TIME"), FieldUtils.getFieldValue(data, fieldUpperCase + "_LATEST_TIME")));
                    statisticValue = GroupStatisticUtils.timeRangeMonth(dateRange.getLeft(), dateRange.getRight());
                    break;
                }
                case TIME_RANGE_YEAR: {
                    Pair<Date, Date> dateRange = GroupStatisticUtils.earliestTimeAndLatestTime(Lists.newArrayList(FieldUtils.getFieldValue(data, fieldUpperCase + "_EARLIEST_TIME"), FieldUtils.getFieldValue(data, fieldUpperCase + "_LATEST_TIME")));
                    statisticValue = GroupStatisticUtils.timeRangeYear(dateRange.getLeft(), dateRange.getRight());
                    break;
                }
                case SUM:
                    statisticValue = FieldUtils.getFieldValue(data, fieldUpperCase + "_SUM");
                    statisticValue = GroupStatisticUtils.formatNumber(statisticValue, 2);
                    break;
                case AVERAGE:
                    statisticValue = FieldUtils.getFieldValue(data, fieldUpperCase + "_AVERAGE");
                    statisticValue = GroupStatisticUtils.formatNumber(statisticValue, 2);
                    break;
                case MIN:
                    statisticValue = FieldUtils.getFieldValue(data, fieldUpperCase + "_MIN");
                    break;
                case MAX:
                    statisticValue = FieldUtils.getFieldValue(data, fieldUpperCase + "_MAX");
                    break;
            }

            statisticValues.put(field, statisticValue);
        });

        resultMap.put(groupPath, statisticValues);
        return true;
    }

    /**
     * 内存加载所有数据
     */
    private <T> Pagination<T> loadDataListByMemory(Grouping<T> group, Pagination<T> page, QueryWrapper<T> pageQueryWrapper) {
        for (GroupField groupField : group.getGroupFields()) {
            ModelFieldConfig modelFieldConfig = group.getModelFieldConfig(groupField.getField());
            if (!GroupingUtils.isMemoryGroupField(modelFieldConfig)) {
                SortDirectionEnum orderType = Optional.ofNullable(groupField.getOrderType()).orElse(SortDirectionEnum.ASC);
                pageQueryWrapper.orderBy(true, SortDirectionEnum.ASC.equals(orderType), Configs.wrap(modelFieldConfig).getColumn());
            }
        }
        Sort sort = page.getSort();
        if (!Boolean.FALSE.equals(page.getSortable()) && sort != null && sort.getOrders() != null) {
            for (Order order : sort.getOrders()) {
                ModelFieldConfig modelFieldConfig = group.getModelFieldConfig(order.getField());
                SortDirectionEnum orderType = Optional.ofNullable(order.getDirection()).orElse(SortDirectionEnum.ASC);
                pageQueryWrapper.orderBy(true, SortDirectionEnum.ASC.equals(orderType), Configs.wrap(modelFieldConfig).getColumn());
            }
        }

        Pagination<T> pagination = Models.origin().queryPage(new Pagination<>(1, -1), parseQueryWrapper(pageQueryWrapper));
        if (CollectionUtils.isNotEmpty(pagination.getContent())) {
            for (GroupField groupField : group.getGroupFields()) {
                ModelFieldConfig modelFieldConfig = group.getModelFieldConfig(groupField.getField());
                if (GroupingUtils.isMemoryGroupField(modelFieldConfig) && !Boolean.TRUE.equals(modelFieldConfig.getStore())) {
                    List<T> dataList = Models.origin().listFieldQuery(pagination.getContent(), modelFieldConfig.getField());
                    pagination.setContent(dataList);
                }
            }
        }
        return pagination;
    }

    private boolean hasRelationGroupField(Grouping<?> group) {
        boolean hasRelationGroupField = false;
        for (GroupField groupField : group.getGroupFields()) {
            ModelFieldConfig modelFieldConfig = group.getModelFieldConfig(groupField.getField());
            if (GroupingUtils.isMemoryGroupField(modelFieldConfig)) {
                hasRelationGroupField = true;
                break;
            }
        }
        return hasRelationGroupField;
    }

    /**
     * 填充dataList里的引用字段
     */
    private void listQueryRelationFields(Grouping<?> group, List<?> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        for (ModelFieldConfig modelFieldConfig : group.getModelConfig().getModelFieldConfigList()) {
            String ttype = modelFieldConfig.getTtype();
            if (TtypeEnum.isRelationType(ttype)) {
                List<Object> relationQueryDataList = new ArrayList<>();
                for (Object item : dataList) {
                    if (relationReadApi.isNeedQueryRelation(modelFieldConfig, item)) {
                        relationQueryDataList.add(item);
                    }
                }
                if (!relationQueryDataList.isEmpty()) {
                    String field = modelFieldConfig.getField();
                    DataShardingHelper.build().sharding(relationQueryDataList, (sublist) -> Models.origin().listFieldQuery(sublist, field));
                }
            }
        }
    }

}
