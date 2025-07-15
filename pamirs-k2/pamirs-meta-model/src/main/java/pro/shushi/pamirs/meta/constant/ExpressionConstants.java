package pro.shushi.pamirs.meta.constant;

/**
 * 表达式常量
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 17:08
 */
public interface ExpressionConstants {

    /**
     * @deprecated please using {@link ExpressionConstants#S_PLACEHOLDER_TABLE}
     */
    @Deprecated
    String S_PLACEHOLDER = "${moduleAbbr}_%s";

    String S_PLACEHOLDER_TABLE = "${moduleAbbr}_%s";

    String S_PLACEHOLDER_COLUMN = "%s";

}
