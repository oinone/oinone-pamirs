package pro.shushi.pamirs.framework.connectors.data.infrastructure.mapper.mysql;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.physical.Column;

import java.util.List;

/**
 * 系统字段信息mapper
 * <p>
 * 2020/6/29 2:02 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Mapper
public interface ColumnMapper {

    String FIELDS = "`TABLE_SCHEMA` AS `tableSchema`," +
            "`TABLE_NAME` AS `tableName`," +
            "`COLUMN_NAME` AS `columnName`," +
            "`COLUMN_TYPE` AS `columnType`," +
            "`DATA_TYPE` AS `dataType`," +
            "`CHARACTER_MAXIMUM_LENGTH` AS `characterMaximumLength`," +
            "`CHARACTER_OCTET_LENGTH` AS `characterOctetLength`," +
            "`NUMERIC_PRECISION` AS `numericPrecision`," +
            "`NUMERIC_SCALE` AS `numericScale`," +
            "`DATETIME_PRECISION` AS `datetimePrecision`," +
            "`IS_NULLABLE` AS `nullable`," +
            "`COLUMN_DEFAULT` AS `defaultValue`," +
            "`ORDINAL_POSITION` AS `ordinalPosition`," +
            "`EXTRA` AS `extra`," +
            "`CHARACTER_SET_NAME` AS `characterSetName`," +
            "`COLLATION_NAME` AS `collationName`," +
            "`COLUMN_COMMENT` AS `columnComment`\n";

    @Select("<script>" +
            "SELECT " +
            FIELDS +
            "FROM `INFORMATION_SCHEMA`.`COLUMNS`\n" +
            "WHERE `TABLE_SCHEMA` = #{databaseName}" +
            "</script>")
    List<Column> selectColumnList(@Param("databaseName") String databaseName);

    @Select("<script>" +
            "SELECT " +
            FIELDS +
            "FROM `INFORMATION_SCHEMA`.`COLUMNS` " +
            "WHERE `TABLE_SCHEMA` IN " +
            "<foreach collection=\"databaseNameList\" index=\"index\" item=\"item\" open=\"(\" separator=\",\" close=\")\">" +
            "   #{item}" +
            "</foreach>" +
            "</script>")
    List<Column> selectColumnListInDatabases(@Param("databaseNameList") List<String> databaseNameList);

    @Select("<script>" +
            "SELECT " +
            FIELDS +
            "FROM `INFORMATION_SCHEMA`.`COLUMNS`\n" +
            "WHERE `TABLE_SCHEMA` = #{databaseName} AND `TABLE_NAME` = #{tableName}" +
            "</script>")
    List<Column> selectColumnListByTableName(@Param("databaseName") String databaseName, @Param("tableName") String tableName);

    @Select("<script>" +
            "SELECT " +
            FIELDS +
            "FROM `INFORMATION_SCHEMA`.`COLUMNS`\n" +
            "WHERE `TABLE_SCHEMA` = #{databaseName} AND `TABLE_NAME` = #{tableName} AND `COLUMN_NAME` = #{columnName}" +
            "</script>")
    Column selectColumn(@Param("databaseName") String databaseName, @Param("tableName") String tableName, @Param("columnName") String columnName);

    @Select("<script>" +
            "SELECT COUNT(1) " +
            "FROM `INFORMATION_SCHEMA`.`COLUMNS`\n" +
            "WHERE `TABLE_SCHEMA` = #{databaseName} AND `TABLE_NAME` = #{tableName} AND `COLUMN_NAME` = #{columnName}" +
            "</script>")
    int existColumn(@Param("databaseName") String databaseName, @Param("tableName") String tableName, @Param("columnName") String columnName);

}
