package pro.shushi.pamirs.framework.gateways.rsql.connector;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.framework.common.entry.TreeNode;
import pro.shushi.pamirs.framework.gateways.rsql.RSQLNodeInfo;
import pro.shushi.pamirs.framework.gateways.rsql.RSQLNodeInfoType;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.List;

/**
 * RSQL节点连接器
 *
 * @author Adamancy Zhang at 14:10 on 2021-10-21
 */
public class RSQLNodeConnector extends AbstractNodeConnector implements NodeConnector {

    public static final RSQLNodeConnector INSTANCE = new RSQLNodeConnector();

    @Override
    public String logicConnector(TreeNode<RSQLNodeInfo> node, RSQLNodeInfoType type, List<String> values) {
        if (values.size() == 1) {
            return values.get(0);
        } else {
            if (node.getParent() == null) {
                return join(type.getRsqlConnector(), values);
            }
            return CharacterConstants.LEFT_BRACKET + join(type.getRsqlConnector(), values) + CharacterConstants.RIGHT_BRACKET;
        }
    }

    @Override
    public String logicConnector(RSQLNodeInfoType type, List<String> values) {
        if (values.size() == 1) {
            return values.get(0);
        } else {
            return CharacterConstants.LEFT_BRACKET + join(type.getRsqlConnector(), values) + CharacterConstants.RIGHT_BRACKET;
        }
    }

    @Override
    public String comparisonConnector(RSQLNodeInfo nodeInfo) {
        List<String> arguments = nodeInfo.getArguments();
        if (CollectionUtils.isEmpty(arguments)) {
            return null;
        }
        ComparisonOperator operator = nodeInfo.getOperator();
        String argument = getArgumentString(operator, arguments);
        return concat(CharacterConstants.SEPARATOR_BLANK,
                nodeInfo.getField(),
                nodeInfo.getOperator().getSymbol(),
                argument);
    }
}
