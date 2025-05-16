package pro.shushi.pamirs.framework.gateways.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.RSQLOperators;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public enum RsqlSearchOperation {
    EQUAL(RSQLOperators.EQUAL, "=", false),
    NOT_EQUAL(RSQLOperators.NOT_EQUAL, "<>", true),
    GREATER_THAN(RSQLOperators.GREATER_THAN, ">", false),
    GREATER_THAN_OR_EQUAL(RSQLOperators.GREATER_THAN_OR_EQUAL, ">=", false),
    LESS_THAN(RSQLOperators.LESS_THAN, "<", false),
    LESS_THAN_OR_EQUAL(RSQLOperators.LESS_THAN_OR_EQUAL, "<=", false),
    IN(RSQLOperators.IN, "IN", false),
    NOT_IN(RSQLOperators.NOT_IN, "NOT IN", true),
    IS_NULL(RsqlExtendOperator.ISNULL, "IS NULL", false),
    IS_NOT_NULL(RsqlExtendOperator.ISNOTNULL, "IS NOT NULL", true),
    COLUMN_EQUAL(RsqlExtendOperator.COLUMNEQUAL, EQUAL.sqlOperator, false),
    COLUMN_NOT_EQUAL(RsqlExtendOperator.COLUMN_NOT_EQUAL, NOT_EQUAL.sqlOperator, true),
    LIKE(RsqlExtendOperator.LIKE, "LIKE", false),
    STARTS(RsqlExtendOperator.LIKE_RIGHT, "LIKE", false),
    ENDS(RsqlExtendOperator.LIKE_LEFT, "LIKE", false),
    NOT_LIKE(RsqlExtendOperator.NOT_LIKE, "NOT LIKE", true),
    NOT_STARTS(RsqlExtendOperator.NOT_LIKE_RIGHT, "NOT LIKE", true),
    NOT_ENDS(RsqlExtendOperator.NOT_LIKE_LEFT, "NOT LIKE", true),
    BIT(RsqlExtendOperator.BIT, EQUAL.sqlOperator, false),
    NOT_BIT(RsqlExtendOperator.NOT_BIT, NOT_EQUAL.sqlOperator, true),
    HAS(RsqlExtendOperator.HAS, null, false),
    NOT_HAS(RsqlExtendOperator.NOT_HAS, null, true),
    HAS_OR(RsqlExtendOperator.HAS_OR, null, false),
    HAS_NOT_OR(RsqlExtendOperator.HAS_NOT_OR, null, true),
    ;

    private final ComparisonOperator operator;

    private final String sqlOperator;

    private final boolean not;

    private static final Set<ComparisonOperator> operators = Collections.unmodifiableSet(Arrays.stream(RsqlSearchOperation.values()).map(RsqlSearchOperation::getOperator).collect(Collectors.toSet()));

    RsqlSearchOperation(ComparisonOperator operator, String sqlOperator, boolean not) {
        this.operator = operator;
        this.sqlOperator = sqlOperator;
        this.not = not;
    }

    public static RsqlSearchOperation getSimpleOperator(ComparisonOperator operator) {
        for (RsqlSearchOperation operation : values()) {
            if (operation.getOperator() == operator) {
                return operation;
            }
        }
        return null;
    }

    public static Set<ComparisonOperator> getOperators() {
        return operators;
    }

    public ComparisonOperator getOperator() {
        return operator;
    }

    public String getSqlOperator() {
        return sqlOperator;
    }

    public boolean isNot() {
        return not;
    }
}