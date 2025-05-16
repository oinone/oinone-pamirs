package pro.shushi.pamirs.framework.gateways.rsql.computer;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import pro.shushi.pamirs.framework.gateways.rsql.RSQLNodeInfo;
import pro.shushi.pamirs.framework.gateways.rsql.RsqlSearchOperation;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.List;
import java.util.Map;

/**
 * RSQL节点计算器
 *
 * @author Adamancy Zhang at 12:44 on 2024-08-01
 */
public class RSQLNodeComputer<T extends D> implements NodeComputer<T> {

    private static final RSQLNodeComputer<? extends D> INSTANCE = new RSQLNodeComputer<>();

    @SuppressWarnings("unchecked")
    public static <T extends D> NodeComputer<T> getInstance() {
        return (NodeComputer<T>) INSTANCE;
    }

    @Override
    public boolean comparisonCompute(RSQLNodeInfo nodeInfo, T data) {
        String field = nodeInfo.getField();
        ComparisonOperator operator = nodeInfo.getOperator();
        RsqlSearchOperation operation = RsqlSearchOperation.getSimpleOperator(operator);
        assert operation != null : "Invalid search operation. symbol: " + operator.getSymbol();
        Object value = getValue(data, field);
        if (operator.isMultiValue()) {
            return comparisonComputeMulti(value, operation, nodeInfo.getArguments());
        }
        return comparisonComputeSingle(value, operation, nodeInfo.getArguments().get(0));
    }

    private Object getValue(D data, String fieldExpr) {
        String[] ss = fieldExpr.split(CharacterConstants.SEPARATOR_ESCAPE_DOT);
        Map<String, Object> target = data.get_d();
        int l = ss.length, ed = l - 1;
        for (int i = 0; i < l; i++) {
            String s = ss[i];
            Object value = target.get(s);
            if (i == ed) {
                return value;
            }
            if (value instanceof D) {
                target = ((D) value).get_d();
            } else {
                throw new IllegalArgumentException("Invalid rsql selector. field: " + fieldExpr);
            }
        }
        throw new IllegalArgumentException("Invalid rsql selector. field: " + fieldExpr);
    }

    private boolean comparisonComputeSingle(Object value, RsqlSearchOperation operation, String argument) {
        switch (operation) {
            case EQUAL:
                if (value == null) {
                    return false;
                }
                break;
            case NOT_EQUAL:
                break;
            case GREATER_THAN:
                break;
            case GREATER_THAN_OR_EQUAL:
                break;
            case LESS_THAN:
                break;
            case LESS_THAN_OR_EQUAL:
                break;
            case IS_NULL:
                break;
            case IS_NOT_NULL:
                break;
            case COLUMN_EQUAL:
                break;
            case COLUMN_NOT_EQUAL:
                break;
            case LIKE:
                break;
            case STARTS:
                break;
            case ENDS:
                break;
            case NOT_LIKE:
                break;
            case NOT_STARTS:
                break;
            case NOT_ENDS:
                break;
            case HAS:
                break;
            case NOT_HAS:
                break;
            case BIT:
                break;
            case NOT_BIT:
                break;
        }
        return false;
    }

    private boolean comparisonComputeMulti(Object value, RsqlSearchOperation operation, List<String> arguments) {
        switch (operation) {
            case IN:
                break;
            case NOT_IN:
                break;
        }
        return false;
    }
}
