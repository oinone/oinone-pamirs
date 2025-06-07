package pro.shushi.pamirs.framework.gateways.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;

public class RsqlExtendOperator {

    public static final ComparisonOperator ISNULL = new ComparisonOperator("=isnull=");
    public static final ComparisonOperator ISNOTNULL = new ComparisonOperator("=notnull=");
    public static final ComparisonOperator COLUMNEQUAL = new ComparisonOperator("=cole=");
    public static final ComparisonOperator COLUMN_NOT_EQUAL = new ComparisonOperator("=colnot=");
    public static final ComparisonOperator LIKE = new ComparisonOperator("=like=");
    public static final ComparisonOperator LIKE_RIGHT = new ComparisonOperator("=starts=");
    public static final ComparisonOperator LIKE_LEFT = new ComparisonOperator("=ends=");
    public static final ComparisonOperator NOT_LIKE = new ComparisonOperator("=notlike=");
    public static final ComparisonOperator NOT_LIKE_RIGHT = new ComparisonOperator("=notstarts=");
    public static final ComparisonOperator NOT_LIKE_LEFT = new ComparisonOperator("=notends=");

    public static final ComparisonOperator HAS = new ComparisonOperator(new String[]{"=has="}, true);
    public static final ComparisonOperator NOT_HAS = new ComparisonOperator(new String[]{"=hasnt="}, true);
    public static final ComparisonOperator HAS_OR = new ComparisonOperator(new String[]{"=hasor="}, true);
    public static final ComparisonOperator HAS_NOT_OR = new ComparisonOperator(new String[]{"=hasntor="}, true);

    public static final ComparisonOperator BIT = new ComparisonOperator(new String[]{"=bit="}, true);
    public static final ComparisonOperator NOT_BIT = new ComparisonOperator(new String[]{"=notbit="}, true);

}
