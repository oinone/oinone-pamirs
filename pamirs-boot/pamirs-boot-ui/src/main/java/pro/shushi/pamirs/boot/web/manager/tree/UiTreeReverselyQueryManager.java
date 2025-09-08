package pro.shushi.pamirs.boot.web.manager.tree;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.model.tree.UiTreeNode;
import pro.shushi.pamirs.boot.base.model.tree.UiTreeNodeMetadata;
import pro.shushi.pamirs.boot.web.enmu.BootUxdExpEnumerate;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.config.Configs;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.framework.orm.json.PamirsDataUtils;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.constant.FieldConstants;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 关键字搜索/选择叶子节点反查等, 从叶子节点查询父节点的实现
 */
@Slf4j
@Component
public class UiTreeReverselyQueryManager extends AbstractUiTreeQueryManager {

    @Autowired
    private UiTreeRelationQueryManager uiTreeRelationQueryManager;

    /**
     * 根据关键字,筛选自关联树, 支持指定树的上级父节点  场景: 级联,选中一个父节点,展开下级自关联树
     *
     * @param parentNode          上级级联的父节点
     * @param keywords            关键字
     * @param currentNodeMetadata 当前节点配置
     */
    public List<UiTreeNode> queryKeywords4InnerSelfTree(UiTreeNode parentNode, String keywords, UiTreeNodeMetadata currentNodeMetadata, UiTreeNodeMetadata nextNodeMetadata) {
        if (StringUtils.isEmpty(keywords)) {
            throw PamirsException.construct(BootUxdExpEnumerate.SYSTEM_ERROR).appendMsg("未指定搜索关键字").errThrow();
        }
        List<UiTreeNode> result = new ArrayList<>();

        QueryWrapper<Object> queryWrapper = Pops.query().from(currentNodeMetadata.getModel());
        buildWrapper4QueryListByKeywords(keywords, currentNodeMetadata, queryWrapper);
        List<Object> dataList = Models.data().queryListByWrapper(queryWrapper);
        if (CollectionUtils.isEmpty(dataList)) {
            return result;
        }

        // 根据pk去重
        List<Map<String, Object>> mapResult = dataList.stream().map(this::convertMap).collect(Collectors.toList());
        List<String> pk = PamirsSession.getContext().getModelConfig(currentNodeMetadata.getModel()).getPk();
        //保证顺序
        LinkedHashMap<String, Map<String, Object>> resultMap = mapResult.stream().collect(Collectors.toMap(i -> convertDataKeyFunction.apply(i, pk), i -> i, (a, b) -> a, LinkedHashMap::new));

        reverselyRecursionSelf(resultMap, new ArrayList<>(resultMap.values()), currentNodeMetadata, pk);

        result = resultMap
                .values()
                .stream()
                .map(m -> convertTreeNode(m, currentNodeMetadata))
                .peek(n -> n.setPkValue(convertDataKeyFunction.apply(n.getValueObj(), pk)))
                .peek(n -> n.setKey(buildKey(n, currentNodeMetadata)))
                .collect(Collectors.toList());
        //构造自循环树,处理顶级和最末级
        buildSelfTree(result, currentNodeMetadata);
        if (parentNode != null) {
            ModelFieldConfig relFieldConfig = PamirsSession.getContext().getModelField(currentNodeMetadata.getRelModel(), currentNodeMetadata.getRelField());
            if (!TtypeEnum.isRelationType(relFieldConfig.getTtype())) {
                log.error("错误的关联字段,model:{},field:{}", relFieldConfig.getModel(), relFieldConfig.getField());
                throw PamirsException.construct(BootUxdExpEnumerate.SYSTEM_ERROR).errThrow();
            }
            parentNode.setValueObj(PamirsDataUtils.parseModelMap(
                    currentNodeMetadata.getModel().equals(relFieldConfig.getModel()) ? relFieldConfig.getReferences() : relFieldConfig.getModel(),
                    parentNode.getValue()));
            //构造两层模型之间的关联字段,用于过滤指定parent下的数据
            buildNodeRelKeys(
                    Collections.singletonList(parentNode),
                    result.stream().filter(i -> Boolean.TRUE.equals(i.getIsSelfTreeRoot())).collect(Collectors.toList()),
                    currentNodeMetadata, null
            );

            String parentKey = parentNode.getKey();
            //过滤parentKye不相同的顶级节点
            result = result.stream().filter(i -> !Boolean.TRUE.equals(i.getIsSelfTreeRoot()) || i.getRelParentKeys().contains(parentKey)).collect(Collectors.toList());
            for (UiTreeNode uiTreeNode : result) {
                //relParentKey使用完毕就置空
                if (Boolean.TRUE.equals(uiTreeNode.getIsSelfTreeRoot())) {
                    uiTreeNode.setRelParentKeys(null);
                }
            }
        }

        convertKey(result);
        List<UiTreeNodeMetadata> metadataList = new ArrayList<>();
        metadataList.add(currentNodeMetadata);
        if (nextNodeMetadata != null) {
            metadataList.add(nextNodeMetadata);
        }
        fetchIsLeaf(result, metadataList);
        return result;
    }

    /**
     * 根据关键字,查询整棵树中所有匹配的节点
     *
     * @param keywords     关键字
     * @param metadataList 树每一级的配置
     */
    public List<UiTreeNode> queryKeywords4Tree(String keywords, List<UiTreeNodeMetadata> metadataList) {
        if (StringUtils.isEmpty(keywords)) {
            throw PamirsException.construct(BootUxdExpEnumerate.SYSTEM_ERROR).appendMsg("未指定搜索关键字").errThrow();
        }
        List<UiTreeNode> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(metadataList)) {
            return result;
        }
        List<List<UiTreeNode>> dataList = new ArrayList<>();
        // 逆向从叶子倒查
        for (int i = metadataList.size() - 1; i >= 0; i--) {
            List<UiTreeNode> currentDataList;
            //最末层
            if (i == metadataList.size() - 1) {
                currentDataList = _fetchParentNodes(keywords, metadataList.get(i), null, null);
            } else {
                //从叶子节点查,每次取子节点数据都取列表第一个,查询结束后插入第一个
                currentDataList = _fetchParentNodes(keywords, metadataList.get(i), metadataList.get(i + 1), dataList.get(0));
            }
            dataList.add(0, currentDataList);

        }
        if (CollectionUtils.isEmpty(dataList)) {
            return result;
        }
        result = convertTreeNodeList(dataList, metadataList);
        fetchIsLeaf(result, metadataList);
        return result;
    }

    /**
     * 根据叶子节点倒查整棵树
     *
     * @param metadataList
     * @return
     */
    public List<UiTreeNode> reverselyQuery(List<UiTreeNode> leafNodes, List<UiTreeNodeMetadata> metadataList) {
        if (CollectionUtils.isEmpty(leafNodes)) {
            throw PamirsException.construct(BootUxdExpEnumerate.SYSTEM_ERROR).appendMsg("未指定叶子节点").errThrow();
        }
        List<UiTreeNode> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(metadataList)) {
            return result;
        }
        List<List<UiTreeNode>> dataList = new ArrayList<>();
        // 逆向从叶子倒查
        for (int i = metadataList.size() - 1; i >= 0; i--) {
            //最末层
            if (i == metadataList.size() - 1) {
                UiTreeNodeMetadata currentMetadata = metadataList.get(i);
                String model = currentMetadata.getModel();
                ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(model);
                List<Map<String, Object>> leafDatas = leafNodes.stream()
                        .map(UiTreeNode::getValue)
                        .<Map<String, Object>>map(_v -> PamirsDataUtils.parseModelMap(currentMetadata.getModel(), _v))
                        .collect(Collectors.toList());
                QueryWrapper<Object> queryWrapper = Pops.query().from(currentMetadata.getModel());
                addFilter(currentMetadata.getModel(), currentMetadata.getFilter(), queryWrapper);

                List<String> uniques = matchUniques(modelConfig, leafDatas.get(0));
                if (CollectionUtils.isEmpty(uniques)) {
                    throw PamirsException.construct(BootUxdExpEnumerate.SYSTEM_ERROR).appendMsg("叶子结点数据缺少唯一键").errThrow();
                }
                queryWrapper.in(
                        uniques.stream().map(field -> Configs.wrap(PamirsSession.getContext().getModelField(model, field)).getColumn()).collect(Collectors.toList()),
                        uniques.stream().map(field -> leafDatas.stream().map(m -> m.get(field)).collect(Collectors.toList())).toArray(List[]::new)
                );
                List<Map<String, Object>> localDataList = Models.data().queryListByWrapper(queryWrapper).stream().map(this::convertMap).collect(Collectors.toList());
                List<Map<String, Object>> localDataMapList = localDataList.stream().map(this::convertMap).collect(Collectors.toList());

                if (StringUtils.isNotEmpty(currentMetadata.getSelfRelField())) {
                    List<String> pks = modelConfig.getPk();
                    //保证顺序
                    LinkedHashMap<String, Map<String, Object>> dataMap = localDataMapList.stream().collect(Collectors.toMap(_m -> convertDataKeyFunction.apply(_m, pks), _m -> _m, (a, b) -> a, LinkedHashMap::new));
                    reverselyRecursionSelf(dataMap, new ArrayList<>(dataMap.values()), currentMetadata, pks);
                    dataList.add(0, dataMap.values().stream().map(ii -> convertTreeNode(ii, currentMetadata)).collect(Collectors.toList()));
                } else {
                    dataList.add(0, localDataMapList.stream().map(ii -> convertTreeNode(ii, currentMetadata)).collect(Collectors.toList()));
                }
            } else {
                //从叶子节点查,每次取子节点数据都取列表第一个,查询结束后插入第一个
                dataList.add(0, _fetchParentNodes(null, metadataList.get(i), metadataList.get(i + 1), dataList.get(0)));
            }
        }
        if (CollectionUtils.isEmpty(dataList)) {
            return result;
        }
        result = convertTreeNodeList(dataList, metadataList);
        fetchIsLeaf(result, metadataList);
        return result;
    }

    /**
     * 获取数据的唯一键配置
     */
    private List<String> matchUniques(ModelConfig modelConfig, Map<String, Object> queryData) {
        List<String> result = null;
        List<String> pks = modelConfig.getPk();

        result = pks;
        for (String pk : pks) {
            if (!queryData.containsKey(pk)) {
                result = null;
                break;
            }
        }
        if (result != null) {
            return result;
        }

        List<String> uniques = modelConfig.getUniques();
        if (CollectionUtils.isEmpty(uniques)) {
            return result;
        }
        for (String unique : uniques) {
            List<String> uniqueFields = Arrays.stream(unique.split(CharacterConstants.SEPARATOR_COMMA)).collect(Collectors.toList());
            result = uniqueFields;

            for (String uniqueField : uniqueFields) {
                if (!queryData.containsKey(uniqueField)) {
                    result = null;
                    break;
                }
            }

            if (result != null) {
                return result;
            }
        }
        return result;
    }

    public List<UiTreeNode> reverselyQueryWithSize(List<UiTreeNode> leafNodes, List<UiTreeNodeMetadata> metadataList, Long size) {
        List<UiTreeNode> result = reverselyQuery(leafNodes, metadataList);

        List<UiTreeNode> fullNodes = new ArrayList<>();
        size = Optional.ofNullable(size).orElse(20L);

        Map<String, UiTreeNodeMetadata> metadataMap = metadataList.stream().collect(Collectors.toMap(UiTreeNodeMetadata::getKey, i -> i));
        Map<String, List<UiTreeNode>> metadataKey2Nodes = result.stream().collect(Collectors.groupingBy(UiTreeNode::getMetadataKey));
        Map<String, List<UiTreeNode>> parent2Children = new HashMap<>();
        result.stream().filter(i -> CollectionUtils.isNotEmpty(i.getParentKeys())).forEach(uiTreeNode -> {
            for (String parentKey : uiTreeNode.getParentKeys()) {
                parent2Children.computeIfAbsent(parentKey, _k -> parent2Children.put(_k, new ArrayList<>()));
                parent2Children.get(parentKey).add(uiTreeNode);
            }
        });

        List<TreeLevel> treeLevels = new ArrayList<>();
        UiTreeNodeMetadata parentMetadata = null;
        UiTreeNode parentNode = null;
        TreeLevel parentLevel = null;
        while (true) {
            List<UiTreeNode> currentNodes;
            if (parentNode == null) {
                UiTreeNodeMetadata firstMetadata = metadataList.get(0);
                // 根据配置的key,获取第一级的所有数据, 可能第一级就是自关联,需要过滤数据
                currentNodes = Optional.ofNullable(metadataKey2Nodes.get(firstMetadata.getKey()))
                        .map(_nodes -> _nodes.stream().filter(_n -> CollectionUtils.isEmpty(_n.getParentKeys())).collect(Collectors.toList()))
                        .orElse(null);
            } else {
                // 根据parent的key,获取它的所有children
                currentNodes = parent2Children.get(parentNode.getKey());
            }
            if (CollectionUtils.isEmpty(currentNodes)) {
                // 没有下级节点了,结束
                break;
            }

            TreeLevel level = new TreeLevel();
            treeLevels.add(level);

            level.setParentMetadata(parentMetadata);
            level.setParentNode(parentNode);
            level.setCurrentNodes(currentNodes);

            // 取第一个作为下级的父节点
            UiTreeNode firstChild = currentNodes.get(0);

            UiTreeNodeMetadata childMetadata = metadataMap.get(firstChild.getMetadataKey());
            level.setCurrentMetadata(childMetadata);
            if (parentLevel != null) {
                parentLevel.setNextMetadata(childMetadata);
            }

            parentNode = firstChild;
            parentMetadata = childMetadata;
            parentLevel = level;
        }

        for (TreeLevel treeLevel : treeLevels) {
            List<UiTreeNode> childrenNodes = treeLevel.getCurrentNodes();
            long shortSize = size - childrenNodes.size();
            if (shortSize <= 0) {
                continue;
            }

            UiTreeNodeMetadata parentMeta = Optional.ofNullable(treeLevel.getParentMetadata()).map(UiTreeNodeMetadata::copy).orElse(null);
            UiTreeNodeMetadata currentMeta = treeLevel.getCurrentMetadata().copy();

            // 拷贝对象,设置过滤条件
            UiTreeNodeMetadata fillerMetadata;
            if (parentMeta == null) {
                fillerMetadata = currentMeta;
            } else {
                if (currentMeta.getKey().equals(parentMeta.getKey())) {
                    // 置空, 只用父的自关联配置做查询
                    currentMeta = null;

                    fillerMetadata = parentMeta;
                } else {
                    // 置空自关联,直接用子节点的配置关联查询
                    parentMeta.unsetSelfRelField();

                    fillerMetadata = currentMeta;
                }
            }

            String filter = fillerMetadata.getFilter();
            if (StringUtils.isNotBlank(filter)) {
                filter = "(" + filter + ") and ";
            } else {
                filter = "";
            }
            // FIXME: 2023/2/14 这里rsql写死了id, 前端目前也是这么处理的
            filter = filter + "id =out= ( " +
                    childrenNodes.stream().map(i -> i.getValueObj().get(FieldConstants.ID)).filter(Objects::nonNull).map(String::valueOf).collect(Collectors.joining(","))
                    + " )";
            fillerMetadata.setFilter(filter);

            List<UiTreeNode> localFullNodes = uiTreeRelationQueryManager.fetchChildrenData(
                    null, treeLevel.getParentNode(), parentMeta, currentMeta, new Pagination<>().setSize(shortSize)
            ).getContent();

            // 判断是否还有下级
            UiTreeNodeMetadata nextMeta = treeLevel.getNextMetadata();
            currentMeta = treeLevel.getCurrentMetadata().copy();
            if (nextMeta != null) {
                if (currentMeta.getKey().equals(nextMeta.getKey())) {
                    // 置空, 只用父的自关联配置做查询
                    nextMeta = null;
                } else {
                    // 置空自关联, 直接用子节点的配置关联查询
                    currentMeta.unsetSelfRelField();
                }
            }
            uiTreeRelationQueryManager.fetchIsLeaf(localFullNodes, currentMeta, nextMeta);

            ModelConfig childModel = PamirsSession.getContext().getModelConfig(treeLevel.getCurrentMetadata().getModel());
            for (UiTreeNode n : localFullNodes) {
                n.setPkValue(convertDataKeyFunction.apply(n.getValueObj(), childModel.getPk()));
                n.setKey(buildKey(n, treeLevel.getCurrentMetadata()));
                if (treeLevel.getParentNode() != null) {
                    n.setParentKeys(Collections.singletonList(treeLevel.getParentNode().getKey()));
                }
                n.setFiller(Boolean.TRUE);
            }
            fullNodes.addAll(localFullNodes);
        }

        result.addAll(fullNodes);
        return result;
    }

    @Data
    private static class TreeLevel {
        private UiTreeNodeMetadata parentMetadata;
        private UiTreeNodeMetadata currentMetadata;
        private UiTreeNodeMetadata nextMetadata;

        private UiTreeNode parentNode;
        private List<UiTreeNode> currentNodes;
    }


    /**
     * 根据关键字 和 子节点列表,倒查父节点列表.
     *
     * @param keywords             关键字
     * @param parentNodeMetadata   当前父节点配置
     * @param childrenNodeMetadata 子节点配置
     * @param childrenNodes        子节点数据集
     */
    private List<UiTreeNode> _fetchParentNodes(String keywords, UiTreeNodeMetadata parentNodeMetadata, UiTreeNodeMetadata childrenNodeMetadata, List<UiTreeNode> childrenNodes) {
        QueryWrapper<Object> queryWrapper = Pops.query().from(parentNodeMetadata.getModel());
        queryWrapper.and(_w1 -> {
            //根据关键字筛选当前
            buildWrapper4QueryListByKeywords(keywords, parentNodeMetadata, _w1);
            if (childrenNodeMetadata != null) {
                //根据子节点查询父节点
                _w1.or(_w2 -> {
                    ModelFieldConfig relFieldConfig = PamirsSession.getContext().getModelField(childrenNodeMetadata.getRelModel(), childrenNodeMetadata.getRelField());
                    _buildWrapper4FetchParentByChildren(childrenNodes.stream().map(UiTreeNode::getValueObj).collect(Collectors.toList()), parentNodeMetadata, relFieldConfig, _w2);
                });
            }
        });

        List<Object> result = Models.data().queryListByWrapper(queryWrapper);

        // 根据pk去重
        List<Map<String, Object>> mapResult = result.stream().map(this::convertMap).collect(Collectors.toList());
        List<String> pk = PamirsSession.getContext().getModelConfig(parentNodeMetadata.getModel()).getPk();
        //保证顺序
        LinkedHashMap<String, Map<String, Object>> resultMap = mapResult.stream().collect(Collectors.toMap(i -> convertDataKeyFunction.apply(i, pk), i -> i, (a, b) -> a, LinkedHashMap::new));

        if (StringUtils.isNotEmpty(parentNodeMetadata.getSelfRelField())) {
            reverselyRecursionSelf(resultMap, new ArrayList<>(resultMap.values()), parentNodeMetadata, pk);
        }
        return resultMap.values().stream().map(i -> convertTreeNode(i, parentNodeMetadata)).collect(Collectors.toList());
    }

    /**
     * 根据子节点数据集,查询父节点
     *
     * @param childrenNodes       子节点数据集
     * @param currentNodeMetadata 父节点配置
     * @param relFieldConfig      子节点和父节点的关联字段
     */
    private List<Object> _fetchParentByChildren(List<Map<String, Object>> childrenNodes, UiTreeNodeMetadata currentNodeMetadata, ModelFieldConfig relFieldConfig) {
        QueryWrapper<Object> queryWrapper = Pops.query().from(currentNodeMetadata.getModel());
        queryWrapper = _buildWrapper4FetchParentByChildren(childrenNodes, currentNodeMetadata, relFieldConfig, queryWrapper);
        return Models.data().queryListByWrapper(queryWrapper);
    }

    private QueryWrapper<Object> _buildWrapper4FetchParentByChildren(List<Map<String, Object>> childrenNodes, UiTreeNodeMetadata currentNodeMetadata, ModelFieldConfig relFieldConfig, QueryWrapper<Object> queryWrapper) {
        if (CollectionUtils.isEmpty(childrenNodes)) {
            //一定查不到
            return null;
        }
        if (!TtypeEnum.isRelationType(relFieldConfig.getTtype())) {
            log.error("错误的关联字段,model:{},field:{}", relFieldConfig.getModel(), relFieldConfig.getField());
            throw PamirsException.construct(BootUxdExpEnumerate.SYSTEM_ERROR).errThrow();
        }

        String parentModel = currentNodeMetadata.getModel();
        String childrenModel = relFieldConfig.getModel().equals(parentModel) ? relFieldConfig.getReferences() : relFieldConfig.getModel();

        Boolean fieldInChild;
        if (parentModel.equals(childrenModel)) {
            // 父子模型相同
            if (!TtypeEnum.M2O.value().equals(relFieldConfig.getTtype())) {
                //自关联字段
                fieldInChild = Boolean.FALSE;
            } else {
                fieldInChild = Boolean.TRUE;
            }
        } else {
            if (!parentModel.equals(relFieldConfig.getModel())) {
                fieldInChild = Boolean.TRUE;
            } else {
                fieldInChild = Boolean.FALSE;
            }
        }

        // 处理查询条件
        if (TtypeEnum.M2M.value().equals(relFieldConfig.getTtype())) {
            QueryWrapper<Object> m2mRelQueryWrapper = Pops.query().from(relFieldConfig.getThrough());
            if (Boolean.TRUE.equals(fieldInChild)) {
                String through = relFieldConfig.getThrough();
                m2mRelQueryWrapper.in(
                        relFieldConfig.getThroughRelationFields().stream().map(field -> Configs.wrap(PamirsSession.getContext().getModelField(through, field)).getColumn()).collect(Collectors.toList()),
                        relFieldConfig.getRelationFields().stream().map(i -> childrenNodes.stream().map(m -> m.get(i)).collect(Collectors.toList())).toArray(List[]::new)
                );
            } else {
                String through = relFieldConfig.getThrough();
                m2mRelQueryWrapper.in(
                        relFieldConfig.getThroughReferenceFields().stream().map(field -> Configs.wrap(PamirsSession.getContext().getModelField(through, field)).getColumn()).collect(Collectors.toList()),
                        relFieldConfig.getReferenceFields().stream().map(i -> childrenNodes.stream().map(m -> m.get(i)).collect(Collectors.toList())).toArray(List[]::new)
                );
            }
            List<Object> throughList = Models.data().queryListByWrapper(m2mRelQueryWrapper);
            List<Map<String, Object>> throughMapList = throughList.stream().map(this::convertMap).collect(Collectors.toList());
            currentNodeMetadata.setThroughMapList(throughMapList);
            if (CollectionUtils.isEmpty(throughMapList)) {
                // 没有数据
                //一定查不到
                return null;
            } else {
                if (Boolean.TRUE.equals(fieldInChild)) {
                    String references = relFieldConfig.getReferences();
                    queryWrapper.in(
                            relFieldConfig.getReferenceFields().stream().map(field -> Configs.wrap(PamirsSession.getContext().getModelField(references, field)).getColumn()).collect(Collectors.toList()),
                            relFieldConfig.getThroughReferenceFields().stream().map(i -> throughMapList.stream().map(through -> through.get(i)).collect(Collectors.toList())).toArray(List[]::new)
                    );
                } else {
                    String model = relFieldConfig.getModel();
                    queryWrapper.in(
                            relFieldConfig.getRelationFields().stream().map(field -> Configs.wrap(PamirsSession.getContext().getModelField(model, field)).getColumn()).collect(Collectors.toList()),
                            relFieldConfig.getThroughRelationFields().stream().map(i -> throughMapList.stream().map(through -> through.get(i)).collect(Collectors.toList())).toArray(List[]::new)
                    );
                }
                addFilter(parentModel, currentNodeMetadata.getFilter(), queryWrapper);
            }
        } else {
            if (Boolean.TRUE.equals(fieldInChild)) {
                String references = relFieldConfig.getReferences();
                queryWrapper.in(
                        relFieldConfig.getReferenceFields().stream().map(field -> Configs.wrap(PamirsSession.getContext().getModelField(references, field)).getColumn()).collect(Collectors.toList()),
                        relFieldConfig.getRelationFields().stream().map(i -> childrenNodes.stream().map(m -> m.get(i)).collect(Collectors.toList())).toArray(List[]::new)
                );
            } else {
                String model = relFieldConfig.getModel();
                queryWrapper.in(
                        relFieldConfig.getRelationFields().stream().map(field -> Configs.wrap(PamirsSession.getContext().getModelField(model, field)).getColumn()).collect(Collectors.toList()),
                        relFieldConfig.getReferenceFields().stream().map(i -> childrenNodes.stream().map(m -> m.get(i)).collect(Collectors.toList())).toArray(List[]::new)
                );
            }
            addFilter(parentModel, currentNodeMetadata.getFilter(), queryWrapper);
        }
        return queryWrapper;
    }

    /**
     * 根据关键字查询列表
     *
     * @param keywords 关键字
     * @param metadata 关键字查询的节点配置
     */
    public void buildWrapper4QueryListByKeywords(String keywords, UiTreeNodeMetadata metadata, QueryWrapper<Object> queryWrapper) {
        addFilter(metadata.getModel(), metadata.getFilter(), queryWrapper);
        addKeywords(keywords, metadata, queryWrapper);
    }

    /**
     * 根据叶子节点列表,递归查询所有父节点
     *
     * @param allNodes 自关联树的全部节点
     * @param leafs    当前查询的叶子节点
     * @param metadata 节点配置
     * @param pk       用于确定唯一性的字段. 保证递归不死循环
     */
    private void reverselyRecursionSelf(Map<String, Map<String, Object>> allNodes, List<Map<String, Object>> leafs, UiTreeNodeMetadata metadata, List<String> pk) {
        if (CollectionUtils.isEmpty(leafs)) {
            return;
        }
        ModelFieldConfig relFieldConfig = PamirsSession.getContext().getModelField(metadata.getModel(), metadata.getSelfRelField());
        List<Object> parents = _fetchParentByChildren(leafs, metadata, relFieldConfig);
        if (CollectionUtils.isEmpty(parents)) {
            return;
        }

        List<Map<String, Object>> filterParents = new ArrayList<>();
        //从所有节点中,过滤已经存在的
        parents.stream().map(this::convertMap).forEach(parent -> {
            String pkv = convertDataKeyFunction.apply(parent, pk);
            if (!allNodes.containsKey(pkv)) {
                allNodes.put(pkv, parent);
                filterParents.add(parent);
            }
        });
        reverselyRecursionSelf(allNodes, filterParents, metadata, pk);
    }

    private void fetchIsLeaf(List<UiTreeNode> nodes, List<UiTreeNodeMetadata> metadataList) {
        if (CollectionUtils.isEmpty(nodes)) {
            return;
        }
        // 内存先组树,确认有子节点就不再做查询
        leafTag(nodes);

        nodes = nodes.stream().filter(i -> i.getIsLeaf() == null).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(nodes)) {
            return;
        }

        Map<String, UiTreeNodeMetadata> metaMap = new HashMap<>();
        UiTreeNodeMetadata last = null;
        for (UiTreeNodeMetadata uiTreeNodeMetadata : metadataList) {
            if (last != null) {
                last.setNext(uiTreeNodeMetadata);
            }
            metaMap.put(uiTreeNodeMetadata.getKey(), uiTreeNodeMetadata);
            last = uiTreeNodeMetadata;
        }

        Map<String, List<UiTreeNode>> nodeMetaKeyMap = nodes.stream().collect(Collectors.groupingBy(UiTreeNode::getMetadataKey));
        nodeMetaKeyMap.forEach((_metaKey, _nodes) -> {
            UiTreeNodeMetadata uiTreeNodeMetadata = metaMap.get(_metaKey);
            uiTreeRelationQueryManager.fetchIsLeaf(_nodes, uiTreeNodeMetadata, uiTreeNodeMetadata.getNext());
        });
    }
}
