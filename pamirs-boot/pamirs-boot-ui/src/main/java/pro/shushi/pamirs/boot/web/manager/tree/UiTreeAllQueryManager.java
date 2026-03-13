package pro.shushi.pamirs.boot.web.manager.tree;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.model.tree.UiTreeNode;
import pro.shushi.pamirs.boot.base.model.tree.UiTreeNodeMetadata;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 根据树的配置,将全部数据加载到内存
 */
@Slf4j
@Component
public class UiTreeAllQueryManager extends AbstractUiTreeQueryManager {
    /**
     * 每一层数据全表查询,内存组树. 查询次数少,但是可能查到无关的数据,需要配置filter.
     * 实际上大多是单表自循环,用这个实现比一层层查询好
     */
    public List<UiTreeNode> fetchAll(List<UiTreeNodeMetadata> metadataList) {
        Long currentTimeMillis = System.currentTimeMillis();
        if (CollectionUtils.isEmpty(metadataList)) {
            return new ArrayList<>();
        }
        List<List<UiTreeNode>> dataList = new ArrayList<>();
        // 将每个节点配置的全部数据加载到内存中,根据关联关系组树
        for (UiTreeNodeMetadata uiTreeNodeMetadata : metadataList) {
            QueryWrapper<Object> queryWrapper = Pops.query().from(uiTreeNodeMetadata.getModel()).setBatchSize(-1);
            addFilter(uiTreeNodeMetadata.getModel(), uiTreeNodeMetadata.getFilter(), queryWrapper);
            List<Object> queryDataList = Models.data().queryListByWrapper(queryWrapper);
            if (CollectionUtils.isEmpty(queryDataList)) {
                break;
            }
            dataList.add(queryDataList.stream().map(this::convertMap).map(i -> convertTreeNode(i, uiTreeNodeMetadata)).collect(Collectors.toList()));
        }
        log.debug("UiTreeAllQueryManager.fetchAll-Query cost {} ms", System.currentTimeMillis() - currentTimeMillis);
        currentTimeMillis = System.currentTimeMillis();

        if (CollectionUtils.isEmpty(dataList)) {
            return new ArrayList<>();
        }
        List<UiTreeNode> resultList = convertTreeNodeList(dataList, metadataList);
        log.debug("UiTreeAllQueryManager.fetchAll-Build tree cost {} ms", System.currentTimeMillis() - currentTimeMillis);
        currentTimeMillis = System.currentTimeMillis();

        String rootMetaKey = metadataList.get(0).getKey();
        // 除顶级节点,过滤没有父节点的数据
        resultList = resultList.stream().filter(i -> rootMetaKey.equals(i.getMetadataKey()) || CollectionUtils.isNotEmpty(i.getParentKeys())).collect(Collectors.toList());
        // 标记是否叶子节点
        leafTag(resultList);
        // 已经全查了, 不明确全部标记为叶子
        resultList.stream().filter(i -> i.getIsLeaf() == null).forEach(i -> i.setIsLeaf(Boolean.TRUE));
        log.debug("UiTreeAllQueryManager.fetchAll-Mark leaf nodes cost {} ms", System.currentTimeMillis() - currentTimeMillis);
        return resultList;
    }

    /**
     * 一层一层关联查询的,只会查到需要的关联数据,但是查询db的次数多,并且in的条件可能特别长
     * 缺少的方法在pro.shushi.pamirs.boot.web.manager.tree.UiTreeRelationQueryManager
     */
//    public List<UiTreeNode> fetchAll(List<UiTreeNodeMetadata> metadataList) {
//        if (CollectionUtils.isEmpty(metadataList)) {
//            return new ArrayList<>();
//        }
//
//        List<List<UiTreeNode>> dataList = new ArrayList<>();
//
//        for (int i = 0; i < metadataList.size(); i++) {
//            UiTreeNodeMetadata currentMetadata = metadataList.get(i);
//            List<Object> currentList;
//            if (i == 0) {
//                // 顶级数据
//                currentList = _queryRoot(null, currentMetadata, _qw -> {
//                    return Models.data().queryListByWrapper(_qw);
//                });
//            } else {
//                List<UiTreeNode> parentNodes = dataList.get(i - 1);
//                if (CollectionUtils.isEmpty(parentNodes)) {
//                    break;
//                }
//
//                UiTreeNodeMetadata parentMetadata = metadataList.get(i - 1);
//                if (StringUtils.isNotEmpty(parentMetadata.getSelfRelField())) {
//                    parentNodes = parentNodes.stream().filter(ii -> Boolean.TRUE.equals(ii.getIsSelfTreeLastLeaf())).collect(Collectors.toList());
//                }
//
//                currentList = _queryChildren(null, parentNodes.stream().map(UiTreeNode::getValueObj).collect(Collectors.toList()),
//                        currentMetadata, Boolean.FALSE, (_context, _wq) -> {
//                            if (_wq == null) {
//                                // m2m中间表没数据
//                                return new ArrayList<>();
//                            }
//                            return Models.data().queryListByWrapper(_wq);
//                        });
//            }
//            if (CollectionUtils.isEmpty(currentList)) {
//                break;
//            }
//
//            List<UiTreeNode> currentResult;
//            if (StringUtils.isNotEmpty(currentMetadata.getSelfRelField())) {
//                // 根据pk去重
//                List<Map<String, Object>> mapResult = currentList.stream().map(this::convertMap).collect(Collectors.toList());
//                List<String> pk = PamirsSession.getContext().getModelConfig(currentMetadata.getModel()).getPk();
//                //保证顺序
//                LinkedHashMap<String, Map<String, Object>> resultMap = mapResult.stream().collect(Collectors.toMap(ii -> convertDataKeyFunction.apply(ii, pk), ii -> ii, (a, b) -> a, LinkedHashMap::new));
//
//                recursionSelf(resultMap, new ArrayList<>(resultMap.values()), currentMetadata, pk);
//
//                currentResult = resultMap
//                        .values()
//                        .stream()
//                        .map(m -> convertTreeNode(m, currentMetadata))
//                        .peek(n -> n.setPkValue(convertDataKeyFunction.apply(n.getValueObj(), pk)))
//                        .peek(n -> n.setKey(buildKey(n, currentMetadata)))
//                        .collect(Collectors.toList());
//                //构造自循环树,处理顶级和最末级
//                buildSelfTree(currentResult, currentMetadata);
//            } else {
//                currentResult = currentList.stream()
//                        .map(this::convertMap)
//                        .map(m -> convertTreeNode(m, currentMetadata))
//                        .collect(Collectors.toList());
//            }
//            dataList.add(currentResult);
//        }
//        List<UiTreeNode> result = convertTreeNodeList(dataList, metadataList);
//        leafTag(result);
//        result.stream().filter(i -> i.getIsLeaf() == null).forEach(i -> i.setIsLeaf(Boolean.TRUE));
//        return result;
//    }
//
//    /**
//     * 从父节点递归查询叶子节点
//     *
//     * @param allNodes
//     * @param parents
//     * @param metadata
//     * @param pk
//     */
//    private void recursionSelf(Map<String, Map<String, Object>> allNodes, List<Map<String, Object>> parents, UiTreeNodeMetadata metadata, List<String> pk) {
//        if (CollectionUtils.isEmpty(parents)) {
//            return;
//        }
//        List<Object> children = _queryChildren(null, parents, metadata, Boolean.TRUE, (_context, _wq) -> {
//            if (_wq == null) {
//                // m2m中间表没数据
//                return new ArrayList<>();
//            }
//            return Models.data().queryListByWrapper(_wq);
//        });
//        if (CollectionUtils.isEmpty(children)) {
//            return;
//        }
//
//        List<Map<String, Object>> filterParents = new ArrayList<>();
//        //从所有节点中,过滤已经存在的
//        children.stream().map(this::convertMap).forEach(parent -> {
//            String pkv = convertDataKeyFunction.apply(parent, pk);
//            if (!allNodes.containsKey(pkv)) {
//                allNodes.put(pkv, parent);
//                filterParents.add(parent);
//            }
//        });
//        recursionSelf(allNodes, filterParents, metadata, pk);
//    }

}
