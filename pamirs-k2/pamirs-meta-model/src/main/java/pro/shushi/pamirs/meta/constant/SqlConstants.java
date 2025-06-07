package pro.shushi.pamirs.meta.constant;

/**
 * SQL常量
 *
 * @author deng d@shushi.pro
 */
public interface SqlConstants {

    String SELECT = "SELECT";
    String DELETE = "DELETE";
    String INSERT = "INSERT INTO";
    String UPDATE = "UPDATE";

    String SET = "SET";
    String FROM = "FROM";
    String WHERE = "WHERE";
    String VALUES = "VALUES";

    String AND = "AND";
    String OR = "OR";
    String GT = ">";
    String GE = ">=";
    String LT = "<";
    String LE = "<=";
    String NE = "<>";
    String EQ = "=";
    String BITWISE_AND = "&";  //按位与

    String LIKE = "LIKE";
    String IS = "IS";
    String IN = "IN";
    String NOT = "NOT";

    String GROUP_BY = "GROUP BY";

    String MAX = "MAX";
    String MIN = "MIN";
    String AVG = "AVG";
    String SUM = "SUM";
    String COUNT = "COUNT";

    String NULL = "NULL";
    String ALL = "*";

    String AS = "AS";

    String ID = "id";
    String CODE = "code";

    String ON_DUPLICATE_KEY_UPDATE = "ON DUPLICATE KEY UPDATE ";

    String AUTO_INCREMENT = "AUTO_INCREMENT";

    String PRIMARY_KEY = "PRIMARY KEY";

    String ADD_PRIMARY_KEY = "ADD PRIMARY KEY";

}
