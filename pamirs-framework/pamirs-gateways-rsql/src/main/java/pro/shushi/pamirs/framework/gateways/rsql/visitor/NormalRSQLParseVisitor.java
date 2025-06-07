package pro.shushi.pamirs.framework.gateways.rsql.visitor;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import pro.shushi.pamirs.framework.common.entry.TreeNode;
import pro.shushi.pamirs.framework.gateways.rsql.RSQLNodeInfo;
import pro.shushi.pamirs.framework.gateways.rsql.RSQLNodeInfoType;

/**
 * 普通RSQL解析
 *
 * @author Adamancy Zhang at 15:30 on 2024-08-23
 */
public class NormalRSQLParseVisitor<T> extends AbstractRSQLParseVisitor<T, RSQLNodeInfo> implements RSQLVisitor<TreeNode<RSQLNodeInfo>, T> {

    @Override
    protected RSQLNodeInfo generatorNodeInfo(RSQLNodeInfoType type, ComparisonNode node, T param) {
        if (node == null) {
            return new RSQLNodeInfo(type);
        }
        return new RSQLNodeInfo(type, null, null, node);
    }
}
