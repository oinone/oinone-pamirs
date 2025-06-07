package pro.shushi.pamirs.framework.gateways.rsql;

/**
 * RSQL节点类型
 *
 * @author Adamancy Zhang at 14:19 on 2021-10-21
 */
public enum RSQLNodeInfoType {

    AND(" and ", " AND "),

    OR(" or ", " OR "),

    COMPARISON(null, null);

    private final String rsqlConnector;

    private final String sqlConnector;

    RSQLNodeInfoType(String rsqlConnector, String sqlConnector) {
        this.rsqlConnector = rsqlConnector;
        this.sqlConnector = sqlConnector;
    }

    public String getRsqlConnector() {
        return rsqlConnector;
    }

    public String getSqlConnector() {
        return sqlConnector;
    }

    public static boolean isLogic(RSQLNodeInfoType type) {
        return AND.equals(type)
                || OR.equals(type);
    }
}
