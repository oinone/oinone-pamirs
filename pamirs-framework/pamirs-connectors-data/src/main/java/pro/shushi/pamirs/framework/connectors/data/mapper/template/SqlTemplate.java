package pro.shushi.pamirs.framework.connectors.data.mapper.template;

/**
 * SQL 模板
 * <p>
 * 2020/6/29 12:16 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface SqlTemplate {

    String INSERT = "INSERT INTO %s %s VALUES %s %s";

    String UPDATE = "UPDATE %s %s %s";

    String DELETE = "DELETE FROM %s %s";

    String SELECT = "SELECT %s FROM %s %s";

    String SELECT_COUNT = "SELECT COUNT(%s) FROM %s %s";

    String EQ_CONDITION = " AND %s = #{%s}";

    String UNLOCK_TABLES = "UNLOCK TABLES;";

    String LOCK_TABLES = "LOCK TABLES";

}
