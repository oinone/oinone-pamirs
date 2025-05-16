package pro.shushi.pamirs.boot.web.manager.tree;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.base.model.UeModel;
import pro.shushi.pamirs.boot.base.model.tree.UiTreeNode;
import pro.shushi.pamirs.boot.base.model.tree.UiTreeNodeMetadata;
import pro.shushi.pamirs.boot.web.constants.TranslateConstants;
import pro.shushi.pamirs.boot.web.enmu.BootUxdExpEnumerate;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.framework.orm.json.PamirsDataUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Exp;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractUiTreeQueryManager {

    /**
     * 添加自关联树,顶级节点的筛选条件
     *
     * @param model        树的模型
     * @param field        自关联字段
     * @param queryWrapper 查询条件
     */
    protected void addSelfRootFilter(String model, String field, QueryWrapper queryWrapper) {
        ModelFieldConfig fieldConfig = PamirsSession.getContext().getModelField(model, field);
        if (TtypeEnum.M2O.value().equals(fieldConfig.getTtype())) {
            for (String f : fieldConfig.getRelationFields()) {
                queryWrapper.isNull(PStringUtils.fieldName2Column(f));
            }
        } else if (TtypeEnum.O2M.value().equals(fieldConfig.getTtype())) {
            for (String f : fieldConfig.getReferenceFields()) {
                queryWrapper.isNull(PStringUtils.fieldName2Column(f));
            }
        } else {
            log.error("错误的关联字段,model:{},field:{}", fieldConfig.getModel(), fieldConfig.getField());
            throw PamirsException.construct(BootUxdExpEnumerate.SYSTEM_ERROR).errThrow();
        }
    }

    protected void addKeywords(String keywords, UiTreeNodeMetadata metadata, QueryWrapper<?> queryWrapper) {
        if (StringUtils.isEmpty(keywords)) {
            return;
        }
        //每个标题字段,模糊匹配关键字
        final List<String> searchFields = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(metadata.getSearchFields())) {
            searchFields.addAll(metadata.getSearchFields());
        } else if (CollectionUtils.isNotEmpty(metadata.getLabelFields())) {
            searchFields.addAll(metadata.getLabelFields());
        } else {
            UeModel ueModel = (UeModel) new UeModel().setModel(metadata.getModel());
            ueModel = ueModel.queryOne();
            if (ueModel != null && CollectionUtils.isNotEmpty(ueModel.getLabelFields())) {
                searchFields.addAll(ueModel.getLabelFields());
            }
        }
        if (CollectionUtils.isNotEmpty(searchFields)) {
            queryWrapper.and(_w -> {
                for (String searchField : searchFields) {
                    _w.or().like(PStringUtils.fieldName2Column(searchField), keywords);
                }
            });
        }
    }

    protected void addFilter(String model, String filter, QueryWrapper queryWrapper) {
        if (StringUtils.isEmpty(filter)) {
            return;
        }

        //queryWrapper.apply(RsqlParseHelper.parseRsql2Sql(model, filter));
        queryWrapper.setRsql(filter);

    }

    protected Map<String, Object> convertMap(Object obj) {
        if (obj instanceof D) {
            return ((D) obj).get_d();
        }
        try {
            return (Map<String, Object>) obj;
        } catch (Exception e) {
            log.error("强转map异常" + obj.getClass());
            throw e;
        }
    }

    protected UiTreeNode convertTreeNode(Map<String, Object> value, UiTreeNodeMetadata metadata) {
        UiTreeNode result = new UiTreeNode()
                .setMetadataKey(metadata.getKey())
                .setValue(PamirsDataUtils.toJSONString(metadata.getModel(), value))
                .setValueObj(value);


        //执行label表达式
        HashMap<String, Object> map = new HashMap<>();
        map.put("activeRecord", value);

        if (StringUtils.isNotBlank(metadata.getLabel())) {
            try {
                String label = Exp.run(metadata.getLabel(), map);
                Boolean translate = metadata.getTranslate();
                if (Boolean.TRUE.equals(translate)) {
                    if (StringUtils.isNotBlank(label) && !(label.startsWith(TranslateConstants.TRANSLATE_PREFIX))) {
                        label = TranslateConstants.TRANSLATE_PREFIX + label + TranslateConstants.TRANSLATE_SUFFIX;
                    }
                }
                result.setLabel(label);
            } catch (Exception e) {
                log.error("label表达式执行异常,exp:{}, value:{}", metadata.getLabel(), value == null ? null : JSON.toJSONString(value));
            }
        }

        if (StringUtils.isNotBlank(metadata.getIcon())) {
            try {
                result.setIcon(Exp.run(metadata.getIcon(), map));
            } catch (Exception e) {
                log.error("Icon表达式执行异常,exp:{}, value:{}", metadata.getIcon(), value == null ? null : JSON.toJSONString(value));
            }
        }
        return result;
    }

    protected BiFunction<Map<String, Object>, List<String>, String> convertDataKeyFunction = (data, fields) -> {
        return fields.stream().map(data::get).map(String::valueOf).collect(Collectors.joining("-"));
    };

    /**
     * 根据每一层的数据和定义,构造平铺的列表
     *
     * @param dataList
     * @param metadataList
     * @return
     */
    protected List<UiTreeNode> convertTreeNodeList(List<List<UiTreeNode>> dataList, List<UiTreeNodeMetadata> metadataList) {
        //获取每一层的全量数据之后,从上往下组树
        List<List<UiTreeNode>> nodesList = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            UiTreeNodeMetadata metadata = metadataList.get(i);
            ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(metadata.getModel());
            nodesList.add(
                    dataList.get(i)
                            .stream()
                            .peek(n -> n.setPkValue(convertDataKeyFunction.apply(n.getValueObj(), modelConfig.getPk())))
                            .peek(n -> n.setKey(buildKey(n, metadata)))
                            .collect(Collectors.toList())
            );
        }

        // 构造一棵树
        for (int i = 0; i < metadataList.size(); i++) {
            UiTreeNodeMetadata metadata = metadataList.get(i);
            List<UiTreeNode> nodes = null;
            if (i < nodesList.size()) {
                // 正向查询时,数据可能少
                nodes = nodesList.get(i);
            }
            if (CollectionUtils.isEmpty(nodes)) {
                break;
            }
            if (StringUtils.isNotEmpty(metadata.getSelfRelField())) {
                //构造自循环树,处理顶级和最末级
                buildSelfTree(nodes, metadata);
            }
            if (i == 0) {
                continue;
            }
            List<UiTreeNode> parentNodes = nodesList.get(i - 1);
            UiTreeNodeMetadata parentMetadata = metadataList.get(i - 1);
            buildNodeRelKeys(
                    StringUtils.isEmpty(parentMetadata.getSelfRelField()) ? parentNodes : parentNodes.stream().filter(ii -> Boolean.TRUE.equals(ii.getIsSelfTreeLastLeaf())).collect(Collectors.toList()),
                    StringUtils.isEmpty(metadata.getSelfRelField()) ? nodes : nodes.stream().filter(ii -> Boolean.TRUE.equals(ii.getIsSelfTreeRoot())).collect(Collectors.toList()),
                    metadata, parentMetadata.getThroughMapList()
            );
        }

        List<UiTreeNode> resultList = nodesList.stream().flatMap(List::stream).collect(Collectors.toList());
        convertKey(resultList);
        return resultList;
    }

    /**
     * 构造自循环树,标记树的顶级节点和最末级节点,并构造自关联的selfKye和selfParentKey
     *
     * @param nodes               自循环树的所有节点
     * @param currentNodeMetadata 节点配置
     */
    protected void buildSelfTree(List<UiTreeNode> nodes, UiTreeNodeMetadata currentNodeMetadata) {
        ModelFieldConfig relFieldConfig = PamirsSession.getContext().getModelField(currentNodeMetadata.getModel(), currentNodeMetadata.getSelfRelField());

        //构造selfKey,和selfChildrenKey
        //自循环只有o2m和m2o才能确定谁是顶级
        Boolean fieldInParent;
        if (TtypeEnum.M2O.value().equals(relFieldConfig.getTtype())) {
            fieldInParent = Boolean.FALSE;
        } else if (TtypeEnum.O2M.value().equals(relFieldConfig.getTtype())) {
            fieldInParent = Boolean.TRUE;
        } else {
            log.error("错误的关联字段,model:{},field:{}", relFieldConfig.getModel(), relFieldConfig.getField());
            throw PamirsException.construct(BootUxdExpEnumerate.SYSTEM_ERROR).errThrow();
        }

        List<String> parentRelFields;
        List<String> childRelFields;
        if (Boolean.TRUE.equals(fieldInParent)) {
            parentRelFields = relFieldConfig.getRelationFields();
            childRelFields = relFieldConfig.getReferenceFields();
        } else {
            parentRelFields = relFieldConfig.getReferenceFields();
            childRelFields = relFieldConfig.getRelationFields();
        }

        Map<String, UiTreeNode> relKey2ParentNode = nodes.stream().collect(Collectors.toMap(n -> convertDataKeyFunction.apply(n.getValueObj(), parentRelFields), n -> n));
        for (UiTreeNode node : nodes) {
            String relKey = convertDataKeyFunction.apply(node.getValueObj(), childRelFields);
            UiTreeNode parentNode = relKey2ParentNode.get(relKey);
            if (parentNode != null) {
                node.setSelfParentKey(parentNode.getKey());
            }
        }

        Set<String> parentKeys = nodes.stream().map(UiTreeNode::getSelfParentKey).filter(Objects::nonNull).collect(Collectors.toSet());

        for (UiTreeNode node : nodes) {
            if (!parentKeys.contains(node.getKey())) {
                //没有叶子节点
                node.setIsSelfTreeLastLeaf(Boolean.TRUE);
            }
            if (node.getSelfParentKey() == null) {
                //没有父节点
                node.setIsSelfTreeRoot(Boolean.TRUE);
            }
        }
    }

    /**
     * 根据父子节点列表,构造关联字段
     *
     * @param parents          父节点列表,构造这些节点的key
     * @param children         子节点列表,构造这些节点的parentKey
     * @param childrenMetadata 子节点配置.父子关系字段定义在子节点配置上
     */
    protected void buildNodeRelKeys(List<UiTreeNode> parents, List<UiTreeNode> children, UiTreeNodeMetadata childrenMetadata, List<Map<String, Object>> throughMapList) {
        ModelFieldConfig relFieldConfig = PamirsSession.getContext().getModelField(childrenMetadata.getRelModel(), childrenMetadata.getRelField());
        if (!TtypeEnum.isRelationType(relFieldConfig.getTtype())) {
            log.error("错误的关联字段,model:{},field:{}", relFieldConfig.getModel(), relFieldConfig.getField());
            throw PamirsException.construct(BootUxdExpEnumerate.SYSTEM_ERROR).errThrow();
        }
        Boolean fieldInParent;
        if (relFieldConfig.getModel().equals(relFieldConfig.getReferences())) {
            // 父子模型相同
            if (TtypeEnum.M2O.value().equals(relFieldConfig.getTtype())) {
                //自关联字段
                fieldInParent = Boolean.FALSE;
            } else {
                fieldInParent = Boolean.TRUE;
            }
        } else {
            if (childrenMetadata.getModel().equals(relFieldConfig.getModel())) {
                fieldInParent = Boolean.FALSE;
            } else {
                fieldInParent = Boolean.TRUE;
            }
        }

        List<String> parentRelFields;
        List<String> childRelFields;
        if (Boolean.TRUE.equals(fieldInParent)) {
            parentRelFields = relFieldConfig.getRelationFields();
            childRelFields = relFieldConfig.getReferenceFields();
        } else {
            parentRelFields = relFieldConfig.getReferenceFields();
            childRelFields = relFieldConfig.getRelationFields();
        }

        //构造子节点的parentKey, m2m需要借助中间表数据
        Map<String, List<UiTreeNode>> relKey2ParentNodes;
        if (TtypeEnum.M2M.value().equals(relFieldConfig.getTtype())) {
            List<String> throughParentRelFields;
            List<String> throughChildRelFields;
            if (Boolean.TRUE.equals(fieldInParent)) {
                throughParentRelFields = relFieldConfig.getThroughRelationFields();
                throughChildRelFields = relFieldConfig.getThroughReferenceFields();
            } else {
                throughParentRelFields = relFieldConfig.getThroughReferenceFields();
                throughChildRelFields = relFieldConfig.getThroughRelationFields();
            }

            if (CollectionUtils.isEmpty(throughMapList)) {
                QueryWrapper<Object> m2mRelQueryWrapper = Pops.query().from(relFieldConfig.getThrough());
                if (Boolean.TRUE.equals(fieldInParent)) {
                    m2mRelQueryWrapper.in(
                            relFieldConfig.getThroughRelationFields().stream().map(PStringUtils::fieldName2Column).collect(Collectors.toList()),
                            relFieldConfig.getRelationFields().stream().map(i -> parents.stream().map(m -> m.getValueObj().get(i)).collect(Collectors.toList())).toArray(List[]::new)
                    );
                } else {
                    m2mRelQueryWrapper.in(
                            relFieldConfig.getThroughReferenceFields().stream().map(PStringUtils::fieldName2Column).collect(Collectors.toList()),
                            relFieldConfig.getReferenceFields().stream().map(i -> parents.stream().map(m -> m.getValueObj().get(i)).collect(Collectors.toList())).toArray(List[]::new)
                    );
                }
                List<Object> throughList = Models.data().queryListByWrapper(m2mRelQueryWrapper);
                throughMapList = throughList.stream().map(this::convertMap).collect(Collectors.toList());
            }

            Map<String, List<String>> m2mParent2Child = CollectionUtils.isEmpty(throughMapList) ? new HashMap<>() : throughMapList.stream().collect(Collectors.groupingBy(
                    t -> convertDataKeyFunction.apply(t, throughParentRelFields),
                    Collectors.mapping(t -> convertDataKeyFunction.apply(t, throughChildRelFields), Collectors.toList()))
            );

            relKey2ParentNodes = new HashMap<>();
            for (UiTreeNode parent : parents) {
                List<String> childrenRelKeys = m2mParent2Child.get(convertDataKeyFunction.apply(parent.getValueObj(), parentRelFields));
                if (CollectionUtils.isEmpty(childrenRelKeys)) {
                    continue;
                }
                for (String childrenRelKey : childrenRelKeys) {
                    relKey2ParentNodes.computeIfAbsent(childrenRelKey, k -> new ArrayList<>());
                    relKey2ParentNodes.get(childrenRelKey).add(parent);
                }
            }
        } else {
            relKey2ParentNodes = parents.stream().collect(Collectors.groupingBy(n -> convertDataKeyFunction.apply(n.getValueObj(), parentRelFields)));
        }

        for (UiTreeNode child : children) {
            String parentRelKey = convertDataKeyFunction.apply(child.getValueObj(), childRelFields);
            List<UiTreeNode> parentNodes = relKey2ParentNodes.get(parentRelKey);
            if (parentNodes != null) {
                child.setRelParentKeys(parentNodes.stream().map(UiTreeNode::getKey).collect(Collectors.toList()));
            }
        }
    }

    /**
     * 将两组不同的关系字段,合并为一组
     *
     * @param nodes 查询结果列表,通过relKey/relParentKey/selfKey/selfParentKey 变更为key/parentKey
     */
    protected void convertKey(List<UiTreeNode> nodes) {
        for (UiTreeNode node : nodes) {
            node.setParentKeys(node.getSelfParentKey() == null ? node.getRelParentKeys() : Collections.singletonList(node.getSelfParentKey()));
        }
    }

    protected String buildKey(UiTreeNode node, UiTreeNodeMetadata metadata) {
        return metadata.getKey() + ":" + node.getPkValue();
    }

    protected void leafTag(List<UiTreeNode> nodes) {
        if (CollectionUtils.isEmpty(nodes)) {
            return;
        }
        // 内存先组树,确认有子节点就不再做查询
        Set<String> parentKeys = nodes.stream().map(UiTreeNode::getParentKeys).filter(CollectionUtils::isNotEmpty).flatMap(Collection::stream).collect(Collectors.toSet());
        for (UiTreeNode node : nodes) {
            if (node.getIsLeaf() != null) {
                continue;
            }
            if (parentKeys.contains(node.getKey())) {
                node.setIsLeaf(Boolean.FALSE);
            }
        }
    }
}
