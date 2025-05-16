package pro.shushi.pamirs.boot.web.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.model.tree.UiTreeNode;
import pro.shushi.pamirs.boot.base.model.tree.UiTreeNodeMetadata;
import pro.shushi.pamirs.boot.web.manager.tree.UiTreeAllQueryManager;
import pro.shushi.pamirs.boot.web.manager.tree.UiTreeRelationQueryManager;
import pro.shushi.pamirs.boot.web.manager.tree.UiTreeReverselyQueryManager;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.systems.directive.SystemDirectiveEnum;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import java.util.List;

@Base
@Component
@Model.model(UiTreeNode.MODEL_MODEL)
public class UiTreeAction {

    @Autowired
    private UiTreeRelationQueryManager uiTreeRelationQueryManager;
    @Autowired
    private UiTreeReverselyQueryManager uiTreeReverselyQueryManager;
    @Autowired
    private UiTreeAllQueryManager uiTreeAllQueryManager;

    /**
     * 根据全部节点配置,查询所有的节点. 仅适用于小数据量搜索
     * @param metadataList
     * @return
     */
    @Function(openLevel = {FunctionOpenEnum.API})
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public List<UiTreeNode> fetchAll(List<UiTreeNodeMetadata> metadataList) {
        return Models.directive().run(() -> {
            return uiTreeAllQueryManager.fetchAll(metadataList);
        }, SystemDirectiveEnum.BUILT_ACTION, SystemDirectiveEnum.HOOK);
    }

    /**
     * 根据当前节点,查询下级列表
     *
     * @param keywords          查询下级节点时的搜索关键字
     * @param currentNode       当前节点数据
     * @param selfMetadata      当前节点定义
     * @param nextMetadata      下级节点定义
     * @param afterNextMetadata 下下级节点定义, 判断下级是否能展开
     * @param pagination        分页
     * @return
     */
    @Function(openLevel = {FunctionOpenEnum.API})
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public Pagination<UiTreeNode> fetchChildren(String keywords, UiTreeNode currentNode, UiTreeNodeMetadata selfMetadata, UiTreeNodeMetadata nextMetadata, UiTreeNodeMetadata afterNextMetadata, Pagination<UiTreeNode> pagination) {
        return Models.directive().run(() -> {
            return uiTreeRelationQueryManager.fetchChildren(keywords, currentNode, selfMetadata, nextMetadata, afterNextMetadata, pagination.to(new Pagination<>()));
        }, SystemDirectiveEnum.BUILT_ACTION, SystemDirectiveEnum.HOOK);
    }

    /**
     * 通过关键字,从整棵树中查询
     *
     * @param keywords
     * @param metadataList
     * @return
     */
    @Function(openLevel = {FunctionOpenEnum.API})
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public List<UiTreeNode> queryKeywords4Tree(String keywords, List<UiTreeNodeMetadata> metadataList) {
        return Models.directive().run(() -> {
            return uiTreeReverselyQueryManager.queryKeywords4Tree(keywords, metadataList);
        }, SystemDirectiveEnum.BUILT_ACTION, SystemDirectiveEnum.HOOK);
    }

    /**
     * 通过关键字,查询指定父节点下的自关联树
     *
     * @param parentNode          上级节点
     * @param keywords            关键字
     * @param currentNodeMetadata 当前节点,自关联
     * @return
     */
    @Function(openLevel = {FunctionOpenEnum.API})
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public List<UiTreeNode> queryKeywords4InnerSelfTree(UiTreeNode parentNode, String keywords, UiTreeNodeMetadata currentNodeMetadata, UiTreeNodeMetadata nextNodeMetadata) {
        return Models.directive().run(() -> {
            return uiTreeReverselyQueryManager.queryKeywords4InnerSelfTree(parentNode, keywords, currentNodeMetadata, nextNodeMetadata);
        }, SystemDirectiveEnum.BUILT_ACTION, SystemDirectiveEnum.HOOK);
    }

    /**
     * 根据叶子节点数据,反查整棵树
     *
     * @param leafNodes
     * @param metadataList
     * @return
     */
    @Function(openLevel = {FunctionOpenEnum.API})
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public List<UiTreeNode> reverselyQuery(List<UiTreeNode> leafNodes, List<UiTreeNodeMetadata> metadataList) {
        return Models.directive().run(() -> {
            return uiTreeReverselyQueryManager.reverselyQuery(leafNodes, metadataList);
        }, SystemDirectiveEnum.BUILT_ACTION, SystemDirectiveEnum.HOOK);
    }

    /**
     * 根据叶子节点数据,反查整棵树, 并且每层需要补充到指定长度
     *
     * @param leafNodes
     * @param metadataList
     * @param size
     * @return
     */
    @Function(openLevel = {FunctionOpenEnum.API})
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public List<UiTreeNode> reverselyQueryWithSize(List<UiTreeNode> leafNodes, List<UiTreeNodeMetadata> metadataList, Long size) {
        return Models.directive().run(() -> {
            return uiTreeReverselyQueryManager.reverselyQueryWithSize(leafNodes, metadataList, size);
        }, SystemDirectiveEnum.BUILT_ACTION, SystemDirectiveEnum.HOOK);
    }

    /**
     * 根据全部节点配置,查询到指定的节点. 根据expandEndLevel判断展开层级
     *
     * @param metadataList 全部节点配置
     * @return
     */
    @Function(openLevel = {FunctionOpenEnum.API})
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public List<UiTreeNode> fetchExpandEndLevel(List<UiTreeNodeMetadata> metadataList) {
        return Models.directive().run(() -> {
            return uiTreeRelationQueryManager.fetchExpandEndLevel(metadataList);
        }, SystemDirectiveEnum.BUILT_ACTION, SystemDirectiveEnum.HOOK);
    }
}
