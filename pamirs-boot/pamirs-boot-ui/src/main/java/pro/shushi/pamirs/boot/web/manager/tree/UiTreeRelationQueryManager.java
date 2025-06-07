package pro.shushi.pamirs.boot.web.manager.tree;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.model.tree.UiTreeNode;
import pro.shushi.pamirs.boot.base.model.tree.UiTreeNodeMetadata;
import pro.shushi.pamirs.boot.web.enmu.BootUxdExpEnumerate;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.framework.orm.json.PamirsDataUtils;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 根据父节点和配置,查询一级关联的叶子节点
 */
@Slf4j
@Component
public class UiTreeRelationQueryManager extends AbstractUiTreeQueryManager {

    public Pagination<UiTreeNode> fetchChildren(String keywords, UiTreeNode currentNode, UiTreeNodeMetadata currentMetadata, UiTreeNodeMetadata nextMetadata, UiTreeNodeMetadata afterNextMetadata, Pagination<Object> pagination) {
        Pagination<UiTreeNode> result = fetchChildrenData(keywords, currentNode, currentMetadata, nextMetadata, pagination);
        // 计算是否还有下级节点
        List<UiTreeNode> dataList = result.getContent();
        if (CollectionUtils.isNotEmpty(dataList)) {
            String metadataKey = dataList.get(0).getMetadataKey();

            UiTreeNodeMetadata current = null;
            UiTreeNodeMetadata next = null;
            if (currentMetadata != null && metadataKey.equals(currentMetadata.getKey())) {
                // 当前层支持自关联,数据来自自关联
                current = currentMetadata;
                next = nextMetadata;
            } else if (nextMetadata != null && metadataKey.equals(nextMetadata.getKey())) {
                // 数据来自下级,需要判断下级的下级是否有数据
                current = nextMetadata;
                next = afterNextMetadata;
            }
            fetchIsLeaf(dataList, current, next);
        }
        return result;
    }

    /**
     * 根据树的父节点,分页查询子节点列表
     *
     * @param currentNode     当前父节点
     * @param currentMetadata 当前父节点配置
     * @param nextMetadata    子节点配置
     * @param pagination      分页器
     * @return
     */
    public Pagination<UiTreeNode> fetchChildrenData(String keywords, UiTreeNode currentNode, UiTreeNodeMetadata currentMetadata, UiTreeNodeMetadata nextMetadata, Pagination<Object> pagination) {
        if (currentNode == null) {
            //首层
            return _queryRoot(keywords, nextMetadata, _qw -> {
                pagination.setModel(nextMetadata.getModel());
                Pagination<Object> result = Models.data().queryPage(pagination, _qw);
                return convertResultPage(result, nextMetadata);
            });
        }
        Map<String, Object> currentValue = PamirsDataUtils.parseModelMap(currentMetadata.getModel(), currentNode.getValue());
        if (StringUtils.isNotEmpty(currentMetadata.getSelfRelField())) {
            //自己可以自关联,先查一下,有数据就结束了
            Pagination<UiTreeNode> result = _queryChildren(keywords, Collections.singletonList(currentValue), currentMetadata, Boolean.TRUE, (_context, _wq) -> {
                if (_wq == null) {
                    // m2m中间表没数据
                    return convertResultPage(new Pagination<>(), currentMetadata);
                }
                pagination.setModel(_context.childrenModel);
                Pagination<Object> dataResult = Models.data().queryPage(pagination, _wq);
                return convertResultPage(dataResult, currentMetadata);
            });
            if (CollectionUtils.isNotEmpty(result.getContent())) {
                return result;
            }
        }
        if (nextMetadata == null) {
            return new Pagination<>();
        }
        return _queryChildren(keywords, Collections.singletonList(currentValue), nextMetadata, Boolean.FALSE, (_context, _wq) -> {
            if (_wq == null) {
                // m2m中间表没数据
                return convertResultPage(new Pagination<>(), nextMetadata);
            }
            pagination.setModel(_context.childrenModel);
            Pagination<Object> dataResult = Models.data().queryPage(pagination, _wq);
            return convertResultPage(dataResult, nextMetadata);
        });
    }

    private <T> T _queryRoot(String keywords, UiTreeNodeMetadata rootMetadata, Function<QueryWrapper<Object>, T> function) {
        QueryWrapper<Object> queryWrapper = Pops.query().from(rootMetadata.getModel());
        addFilter(rootMetadata.getModel(), rootMetadata.getFilter(), queryWrapper);
        addKeywords(keywords, rootMetadata, queryWrapper);
        if (StringUtils.isNotEmpty(rootMetadata.getSelfRelField())) {
            // 顶级是自关联树,增加过滤条件
            addSelfRootFilter(rootMetadata.getModel(), rootMetadata.getSelfRelField(), queryWrapper);
        }
        return function.apply(queryWrapper);
    }

    private <T> T _queryChildren(String keywords, List<Map<String, Object>> currentNodes, UiTreeNodeMetadata treeNodeMetadata, Boolean isSelf, BiFunction<RelationQueryContext, QueryWrapper<Object>, T> function) {
        RelationQueryContext relationQueryContext = new RelationQueryContext(treeNodeMetadata, isSelf);
        // 处理查询条件
        QueryWrapper<Object> queryWrapper = buildRelationQueryWrapper(relationQueryContext, currentNodes, keywords);
        return function.apply(relationQueryContext, queryWrapper);
    }

    private Pagination<UiTreeNode> convertResultPage(Pagination<Object> result, UiTreeNodeMetadata metadata) {
        Pagination<UiTreeNode> treeResult = result.to(new Pagination<UiTreeNode>());
        List<Object> resultList = result.getContent();
        treeResult.setContent(convertResultList(resultList, metadata));
        return treeResult;
    }

    private List<UiTreeNode> convertResultList(List<Object> resultList, UiTreeNodeMetadata metadata) {
        if (CollectionUtils.isEmpty(resultList)) {
            return new ArrayList<>();
        }

        return resultList.stream()
                .map(this::convertMap)
                .map(i -> convertTreeNode(i, metadata))
                .collect(Collectors.toList());
    }

    public void fetchIsLeaf(List<UiTreeNode> dataList, UiTreeNodeMetadata currentMetadata, UiTreeNodeMetadata nextMetadata) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }

        dataList = dataList.stream().filter(i -> i.getIsLeaf() == null).collect(Collectors.toList());
        if (currentMetadata != null && currentMetadata.getSelfRelField() != null) {
            // 判断有没有自关联下级
            _fetchIsLeaf(dataList, currentMetadata, Boolean.TRUE);
        }

        dataList = dataList.stream().filter(i -> i.getIsLeaf() == null).collect(Collectors.toList());
        if (nextMetadata != null) {
            // 判断有没有下级
            _fetchIsLeaf(dataList, nextMetadata, Boolean.FALSE);
        }

        // 剩下未标记的数据,全部标记为末级节点
        dataList.stream().filter(i -> i.getIsLeaf() == null).forEach(i -> i.setIsLeaf(Boolean.TRUE));
    }

    private void _fetchIsLeaf(List<UiTreeNode> currentNodes, UiTreeNodeMetadata treeNodeMetadata, Boolean isSelf) {
        if (CollectionUtils.isEmpty(currentNodes)) {
            return;
        }

        RelationQueryContext relationQueryContext = new RelationQueryContext(treeNodeMetadata, isSelf);
        // 处理查询条件
        QueryWrapper<Object> queryWrapper = buildRelationQueryWrapper(relationQueryContext, currentNodes.stream().map(UiTreeNode::getValueObj).collect(Collectors.toList()), null);
        if (queryWrapper == null) {
            return;
        }
        // 增加分组条件
        queryWrapper.select(relationQueryContext.childFields.stream().map(i -> PStringUtils.fieldName2Column(i) + " as " + i).toArray(String[]::new));
        queryWrapper.groupBy(relationQueryContext.childFields.stream().map(PStringUtils::fieldName2Column).toArray(String[]::new));
        queryWrapper.setBatchSize(-1);

        List<Object> children = Models.data().queryListByWrapper(queryWrapper);
        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        List<Map<String, Object>> childMapList = children.stream().map(this::convertMap).collect(Collectors.toList());
        // 数据做关联,标记isLeaf
        Map<String, List<UiTreeNode>> currentNodeMap = currentNodes.stream()
                .collect(Collectors.groupingBy(i -> convertDataKeyFunction.apply(i.getValueObj(), relationQueryContext.parentFields)));
        Map<String, List<String>> child2Parents = null;
        if (TtypeEnum.M2M.value().equals(relationQueryContext.relFieldConfig.getTtype())) {
            // 中间表映射
            List<Map<String, Object>> throughMapList = relationQueryContext.getThroughMapList();
            child2Parents = throughMapList.stream().collect(Collectors.groupingBy(
                    t -> convertDataKeyFunction.apply(t, relationQueryContext.throughChildFields),
                    Collectors.mapping(t -> convertDataKeyFunction.apply(t, relationQueryContext.throughParentFields), Collectors.toList()))
            );
        }
        for (Map<String, Object> childMap : childMapList) {
            String relKey = convertDataKeyFunction.apply(childMap, relationQueryContext.childFields);
            List<UiTreeNode> parents;
            if (TtypeEnum.M2M.value().equals(relationQueryContext.relFieldConfig.getTtype())) {
                List<String> parentKeys = child2Parents.get(relKey);
                parents = Optional.ofNullable(parentKeys)
                        .map(keys -> keys.stream()
                                .map(currentNodeMap::get)
                                .filter(Objects::nonNull)
                                .flatMap(Collection::stream)
                                .collect(Collectors.toList()))
                        .orElse(null);
            } else {
                parents = currentNodeMap.get(relKey);
            }
            if (parents != null) {
                for (UiTreeNode parent : parents) {
                    parent.setIsLeaf(Boolean.FALSE);
                }
            }
        }
    }

    private <T> QueryWrapper<T> buildRelationQueryWrapper(RelationQueryContext relationQueryContext, List<Map<String, Object>> currentNodes, String keywords) {
        QueryWrapper<T> queryWrapper = Pops.query();
        queryWrapper.from(relationQueryContext.childrenModel);
        if (TtypeEnum.M2M.value().equals(relationQueryContext.relFieldConfig.getTtype())) {
            QueryWrapper<Object> m2mRelQueryWrapper = Pops.query().from(relationQueryContext.relFieldConfig.getThrough());
            m2mRelQueryWrapper.in(
                    relationQueryContext.throughParentFields.stream().map(PStringUtils::fieldName2Column).collect(Collectors.toList()),
                    relationQueryContext.parentFields.stream().map(i -> currentNodes.stream().map(m -> m.get(i)).collect(Collectors.toList())).toArray(List[]::new)
            );
            List<Object> throughList = Models.data().queryListByWrapper(m2mRelQueryWrapper);
            if (CollectionUtils.isEmpty(throughList)) {
                // 空数据,返回空查询对象
                return null;
            } else {
                List<Map<String, Object>> throughMapList = throughList.stream().map(this::convertMap).collect(Collectors.toList());
                // 记录中间表数据
                relationQueryContext.setThroughMapList(throughMapList);
                queryWrapper.in(
                        relationQueryContext.childFields.stream().map(PStringUtils::fieldName2Column).collect(Collectors.toList()),
                        relationQueryContext.throughChildFields.stream().map(i -> throughMapList.stream().map(through -> through.get(i)).collect(Collectors.toList())).toArray(List[]::new)
                );
            }
        } else {
            queryWrapper.in(
                    relationQueryContext.childFields.stream().map(PStringUtils::fieldName2Column).collect(Collectors.toList()),
                    relationQueryContext.parentFields.stream().map(i -> currentNodes.stream().map(m -> m.get(i)).collect(Collectors.toList())).toArray(List[]::new)
            );
        }

        addFilter(relationQueryContext.childrenModel, relationQueryContext.treeNodeMetadata.getFilter(), queryWrapper);
        addKeywords(keywords, relationQueryContext.treeNodeMetadata, queryWrapper);
        if (!Boolean.TRUE.equals(relationQueryContext.isSelf) && StringUtils.isNotEmpty(relationQueryContext.treeNodeMetadata.getSelfRelField())) {
            //不是自关联查自己,且下层存在自关联
            addSelfRootFilter(relationQueryContext.childrenModel, relationQueryContext.treeNodeMetadata.getSelfRelField(), queryWrapper);
        }
        return queryWrapper;
    }

    public List<UiTreeNode> fetchExpandEndLevel(List<UiTreeNodeMetadata> metadataList) {
        long currentTimeMillis = System.currentTimeMillis();
        if (CollectionUtils.isEmpty(metadataList)) {
            return new ArrayList<>();
        }

        List<List<UiTreeNode>> dataList = new ArrayList<>();

        UiTreeNodeMetadata currentMetadata = null;
        UiTreeNodeMetadata nextMetadata = null;
        for (int i = 0; i < metadataList.size(); i++) {
            UiTreeNodeMetadata uiTreeNodeMetadata = metadataList.get(i);
            QueryWrapper<Object> queryWrapper = Pops.query().from(uiTreeNodeMetadata.getModel()).setBatchSize(-1);
            addFilter(uiTreeNodeMetadata.getModel(), uiTreeNodeMetadata.getFilter(), queryWrapper);
            List<Object> queryDataList = Models.data().queryListByWrapper(queryWrapper);
            if (CollectionUtils.isEmpty(queryDataList)) {
                break;
            }
            dataList.add(queryDataList.stream().map(this::convertMap).map(v -> convertTreeNode(v, uiTreeNodeMetadata)).collect(Collectors.toList()));
            if (uiTreeNodeMetadata.getExpandEndLevel() != null && Boolean.TRUE.equals(uiTreeNodeMetadata.getExpandEndLevel())) {
                currentMetadata = uiTreeNodeMetadata;
                if (i + 1 < metadataList.size()) {
                    nextMetadata = metadataList.get(i + 1);
                }
                break;
            }
        }

        log.debug("uiTreeReverselyQueryManager.fetchExpandEndLevel-查询耗时 {} ms", System.currentTimeMillis() - currentTimeMillis);
        currentTimeMillis = System.currentTimeMillis();

        if (CollectionUtils.isEmpty(dataList)) {
            return new ArrayList<>();
        }

        List<UiTreeNode> resultList = convertTreeNodeList(dataList, metadataList);
        log.debug("uiTreeReverselyQueryManager.fetchExpandEndLevel-构建树耗时 {} ms", System.currentTimeMillis() - currentTimeMillis);
        currentTimeMillis = System.currentTimeMillis();

        String rootMetaKey = metadataList.get(0).getKey();
        // 除顶级节点,过滤没有父节点的数据
        resultList = resultList.stream().filter(i -> rootMetaKey.equals(i.getMetadataKey()) || CollectionUtils.isNotEmpty(i.getParentKeys())).collect(Collectors.toList());

        //标记顶级节点是否叶子节点
        leafTag(resultList);

        // 标记是否叶子节点
        fetchIsLeaf(resultList, currentMetadata, nextMetadata);

        log.debug("uiTreeReverselyQueryManager.fetchExpandEndLevel-标记叶节点耗时 {} ms", System.currentTimeMillis() - currentTimeMillis);
        return resultList;
    }


    @Data
    private class RelationQueryContext {
        UiTreeNodeMetadata treeNodeMetadata;
        Boolean isSelf;

        ModelFieldConfig relFieldConfig;
        String parentModel;
        String childrenModel;
        Boolean fieldInParent;

        List<String> childFields;
        List<String> parentFields;

        List<String> throughChildFields;
        List<String> throughParentFields;

        private List<Map<String, Object>> throughMapList;

        public RelationQueryContext(UiTreeNodeMetadata treeNodeMetadata, Boolean isSelf) {
            this.treeNodeMetadata = treeNodeMetadata;
            this.isSelf = isSelf;

            String relField;
            String relModel;
            if (Boolean.TRUE.equals(isSelf)) {
                relModel = treeNodeMetadata.getModel();
                relField = treeNodeMetadata.getSelfRelField();
            } else {
                relModel = treeNodeMetadata.getRelModel();
                relField = treeNodeMetadata.getRelField();
            }
            relFieldConfig = PamirsSession.getContext().getModelField(relModel, relField);
            if (!TtypeEnum.isRelationType(relFieldConfig.getTtype())) {
                log.error("错误的关联字段,model:{},field:{}", relFieldConfig.getModel(), relFieldConfig.getField());
                throw PamirsException.construct(BootUxdExpEnumerate.SYSTEM_ERROR).errThrow();
            }
            if (relModel.equals(treeNodeMetadata.getModel())) {
                parentModel = relFieldConfig.getReferences();
                childrenModel = relFieldConfig.getModel();
            } else {
                parentModel = relFieldConfig.getModel();
                childrenModel = relFieldConfig.getReferences();
            }

            //确定字段归属
            if (parentModel.equals(childrenModel)) {
                // 父子模型相同
                if (TtypeEnum.M2O.value().equals(relFieldConfig.getTtype())) {
                    //自关联字段
                    fieldInParent = Boolean.FALSE;
                } else {
                    fieldInParent = Boolean.TRUE;
                }
            } else {
                if (parentModel.equals(relFieldConfig.getModel())) {
                    fieldInParent = Boolean.TRUE;
                } else {
                    fieldInParent = Boolean.FALSE;
                }
            }

            if (Boolean.TRUE.equals(fieldInParent)) {
                childFields = relFieldConfig.getReferenceFields();
                parentFields = relFieldConfig.getRelationFields();
                if (TtypeEnum.M2M.value().equals(relFieldConfig.getTtype())) {
                    throughChildFields = relFieldConfig.getThroughReferenceFields();
                    throughParentFields = relFieldConfig.getThroughRelationFields();
                }
            } else {
                childFields = relFieldConfig.getRelationFields();
                parentFields = relFieldConfig.getReferenceFields();
                if (TtypeEnum.M2M.value().equals(relFieldConfig.getTtype())) {
                    throughChildFields = relFieldConfig.getThroughRelationFields();
                    throughParentFields = relFieldConfig.getThroughReferenceFields();
                }
            }
        }
    }
}
