package pro.shushi.pamirs.framework.gateways.rsql.connector;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.common.entry.TreeNode;
import pro.shushi.pamirs.framework.connectors.data.sql.config.ModelFieldConfigWrapper;
import pro.shushi.pamirs.framework.gateways.rsql.RSQLNodeInfo;
import pro.shushi.pamirs.framework.gateways.rsql.RSQLNodeInfoType;
import pro.shushi.pamirs.framework.gateways.rsql.RsqlSearchOperation;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.util.PStringUtils;

import java.util.List;

/**
 * SQL节点连接器
 *
 * @author Adamancy Zhang at 14:10 on 2021-10-21
 */
@Slf4j
public class SQLNodeConnector extends AbstractNodeConnector implements NodeConnector {

    public static final NodeConnector INSTANCE = new SQLNodeConnector();

    @Override
    public String logicConnector(TreeNode<RSQLNodeInfo> node, RSQLNodeInfoType type, List<String> values) {
        if (values.size() == 1) {
            return values.get(0);
        } else {
            if (node.getParent() == null) {
                return join(type.getSqlConnector(), values);
            }
            return CharacterConstants.LEFT_BRACKET + join(type.getSqlConnector(), values) + CharacterConstants.RIGHT_BRACKET;
        }
    }

    @Override
    public String comparisonConnector(TreeNode<RSQLNodeInfo> node) {
        RSQLNodeInfo nodeInfo = node.getValue();
        List<String> arguments = nodeInfo.getArguments();
        if (CollectionUtils.isEmpty(arguments)) {
            return null;
        }
        return connect(node, arguments);
    }

    @Deprecated
    @Override
    public String comparisonConnector(RSQLNodeInfo nodeInfo) {
        List<String> arguments = nodeInfo.getArguments();
        if (CollectionUtils.isEmpty(arguments)) {
            return null;
        }
        return connect(nodeInfo, nodeInfo.getOperator(), arguments);
    }

    @Deprecated
    @Override
    public String logicConnector(RSQLNodeInfoType type, List<String> values) {
        if (values.size() == 1) {
            return values.get(0);
        } else {
            return CharacterConstants.LEFT_BRACKET + join(type.getSqlConnector(), values) + CharacterConstants.RIGHT_BRACKET;
        }
    }

    protected String connect(TreeNode<RSQLNodeInfo> node, List<String> arguments) {
        RSQLNodeInfo nodeInfo = node.getValue();
        return connect(nodeInfo, nodeInfo.getOperator(), arguments);
    }

    protected String connect(RSQLNodeInfo nodeInfo, ComparisonOperator operator, List<String> arguments) {
        String column = getColumn(nodeInfo.getModelFieldConfig(), nodeInfo.getField());
        String sqlOperator = getSQLOperator(operator);
        String argument = getArgumentString(operator, arguments);
        return concat(CharacterConstants.SEPARATOR_BLANK,
                column,
                sqlOperator,
                argument);
    }

    protected String getColumn(ModelFieldConfig modelFieldConfig, String field) {
        String column;
        if (modelFieldConfig == null) {
            if (log.isWarnEnabled()) {
                log.warn("model field config is not found, field to column maybe have error. field: {}", field);
            }
            column = PStringUtils.fieldName2Column(field);
        } else {
            column = ModelFieldConfigWrapper.wrap(modelFieldConfig).getSqlSelect(true);
            if (StringUtils.isBlank(column)) {
                if (log.isWarnEnabled()) {
                    log.warn("model field config column is not found, field to column maybe have error. field: {}", field);
                }
                column = PStringUtils.fieldName2Column(field);
            }
        }
        return column;
    }

    protected String getSQLOperator(ComparisonOperator operator) {
        RsqlSearchOperation searchOperation = RsqlSearchOperation.getSimpleOperator(operator);
        if (searchOperation == null) {
            throw new UnsupportedOperationException("Invalid comparison operator. value = " + operator.getSymbol());
        }
        String sqlOperator = searchOperation.getSqlOperator();
        if (sqlOperator == null) {
            throw new UnsupportedOperationException("Invalid sql operator. value = " + operator.getSymbol());
        }
        return sqlOperator;
    }
}
