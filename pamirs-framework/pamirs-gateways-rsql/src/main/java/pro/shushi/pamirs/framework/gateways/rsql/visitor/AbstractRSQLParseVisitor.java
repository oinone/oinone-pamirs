package pro.shushi.pamirs.framework.gateways.rsql.visitor;

import cz.jirutka.rsql.parser.ast.*;
import pro.shushi.pamirs.framework.common.entry.TreeNode;
import pro.shushi.pamirs.framework.gateways.rsql.RSQLNodeInfo;
import pro.shushi.pamirs.framework.gateways.rsql.RSQLNodeInfoType;

import java.util.List;

/**
 * 抽象RSQL解析器
 *
 * @author Adamancy Zhang at 15:38 on 2024-08-23
 */
public abstract class AbstractRSQLParseVisitor<T, INFO extends RSQLNodeInfo> implements RSQLVisitor<TreeNode<INFO>, T> {

    protected int counter = 0;

    protected TreeNode<INFO> currentNode;

    @Override
    public TreeNode<INFO> visit(AndNode node, T param) {
        TreeNode<INFO> currentNode = generatorNode(generatorNodeInfo(RSQLNodeInfoType.AND, null, param));
        traversal(currentNode, node.getChildren(), param);
        return currentNode;
    }

    @Override
    public TreeNode<INFO> visit(OrNode node, T param) {
        TreeNode<INFO> currentNode = generatorNode(generatorNodeInfo(RSQLNodeInfoType.OR, null, param));
        traversal(currentNode, node.getChildren(), param);
        return currentNode;
    }

    @Override
    public TreeNode<INFO> visit(ComparisonNode node, T param) {
        return generatorNode(generatorNodeInfo(RSQLNodeInfoType.COMPARISON, node, param));
    }

    protected abstract INFO generatorNodeInfo(RSQLNodeInfoType type, ComparisonNode node, T param);

    protected TreeNode<INFO> generatorNode(INFO newNodeInfo) {
        TreeNode<INFO> currentNode = this.currentNode;
        if (currentNode == null) {
            currentNode = new TreeNode<>(Integer.toString(counter++), newNodeInfo);
            this.currentNode = currentNode;
            return currentNode;
        }
        TreeNode<INFO> newNode = new TreeNode<>(Integer.toString(counter++), newNodeInfo, currentNode);
        if (RSQLNodeInfoType.isLogic(newNodeInfo.getType())) {
            this.currentNode = newNode;
        }
        return this.currentNode;
    }

    protected void traversal(TreeNode<INFO> currentNode, List<Node> nextNodes, T param) {
        if (nextNodes == null) {
            return;
        }
        for (Node nextNode : nextNodes) {
            nextNode.accept(this, param);
            this.currentNode = currentNode;
        }
    }
}
