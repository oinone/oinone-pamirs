package pro.shushi.pamirs.framework.connectors.data.test.testcase.meta;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.AbstractBaseTest;
import pro.shushi.pamirs.framework.connectors.data.api.datasource.DataSourceApi;

import jakarta.annotation.Resource;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Optional;

/**
 * 数据库元数据测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@DisplayName("数据库元数据测试")
public class MetaDataTest extends AbstractBaseTest {

    @Resource
    private DataSourceApi dataSourceApi;

    @Test
    @Order(0)
    @DisplayName("测试获取数据库元数据")
    public void testFetchMetaData() throws SQLException {
        DatabaseMetaData metaData = dataSourceApi.get("base").getConnection().getMetaData();
        System.out.println("获取数据库的产品名称: " + metaData.getDatabaseProductName());
        System.out.println("获取数据库的版本号: " + metaData.getDatabaseProductVersion());
        System.out.println("获取数据库的用户名: " + metaData.getUserName());
        System.out.println("获取数据库的URL: " + metaData.getURL());
        System.out.println("获取数据库的驱动名称: " + metaData.getDriverName());
        System.out.println("获取数据库的驱动版本号: " + metaData.getDriverVersion());
        System.out.println("查看数据库是否只允许读操作: " + metaData.isReadOnly());
        System.out.println("查看数据库是否支持事务: " + metaData.supportsTransactions());

        ResultSet rs = metaData.getCatalogs();
        while (rs.next()) {
            for (int i = 1; i <= 1; i++) {
                System.out.println(rs.getObject(i));
            }
            String tableSchema = rs.getString("TABLE_CAT");
            System.out.println(MessageFormat.format("库名: {0}, 字符集: {1}, 比较规则: {2}", tableSchema, null, null));
        }
        rs = metaData.getTables(null, null, "*", new String[]{"TABLE"});
        while (rs.next()) {
            for (int i = 1; i <= 10; i++) {
                System.out.print(rs.getObject(i) + ", ");
            }
            String tableSchema = rs.getString("TABLE_CAT");
            tableSchema = Optional.ofNullable(rs.getString("TABLE_SCHEM")).orElse(tableSchema);
            String tableName = rs.getString("TABLE_NAME");
            String tableComment = rs.getString("REMARKS");
            System.out.println(MessageFormat.format("库名: {0}, 表名: {1}, 备注: {2}", tableSchema, tableName, tableComment));
        }
        rs = metaData.getColumns(null, null, null, null);
        while (rs.next()) {
            for (int i = 1; i <= 24; i++) {
                System.out.print(rs.getObject(i) + ", ");
            }
            String tableSchema = rs.getString("TABLE_CAT");
            tableSchema = Optional.ofNullable(rs.getString("TABLE_SCHEM")).orElse(tableSchema);
            String tableName = rs.getString("TABLE_NAME");
            String columnName = rs.getString("COLUMN_NAME");
            String columnType = rs.getString("TYPE_NAME");
            String columnSize = rs.getString("COLUMN_SIZE");
            String decimalDigits = rs.getString("DECIMAL_DIGITS");
            String isNullable = rs.getString("IS_NULLABLE");
            String columnComment = rs.getString("REMARKS");
            Integer ordinalPosition = rs.getInt("ORDINAL_POSITION");
            System.out.println(MessageFormat.format("库名: {0}, 表名: {1}, 列名: {2}, 类型: {3}, SIZE: {4}, 精度: {5}, 空: {6}, 备注: {7}, 位置: {8}",
                    tableSchema, tableName, columnName, columnType, columnSize, decimalDigits, isNullable, columnComment, ordinalPosition));
        }
        rs = metaData.getIndexInfo(null, null, "PAMIRS_TABLES", false, false);
        while (rs.next()) {
            for (int i = 1; i <= 4; i++) {
                System.out.print(rs.getObject(i) + ", ");
            }
            String tableSchema = rs.getString("TABLE_CAT");
            tableSchema = Optional.ofNullable(rs.getString("TABLE_SCHEM")).orElse(tableSchema);
            String tableName = rs.getString("TABLE_NAME");
            String indexName = rs.getString("INDEX_NAME");
            String indexQualifier = rs.getString("INDEX_QUALIFIER");
            Integer indexType = rs.getInt("TYPE");
            Boolean nonUnique = rs.getBoolean("NON_UNIQUE");
            Integer ordinalPosition = rs.getInt("ORDINAL_POSITION");
            String ascOrDesc = rs.getString("ASC_OR_DESC");
            System.out.println(MessageFormat.format("库名: {0}, 表名: {1}, 索引名: {2}, 类别: {3}, 类型: {4}, 非空: {5}, 位置: {6}, 排序: {7}",
                    tableSchema, tableName, indexName, indexQualifier, indexType, nonUnique, ordinalPosition, ascOrDesc));
        }
    }

}
