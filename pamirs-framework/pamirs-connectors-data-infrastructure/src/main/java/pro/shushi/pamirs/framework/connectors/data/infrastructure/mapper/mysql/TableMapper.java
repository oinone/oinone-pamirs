package pro.shushi.pamirs.framework.connectors.data.infrastructure.mapper.mysql;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.physical.Table;

import java.util.List;

/**
 * 系统表信息mapper
 * <p>
 * 2020/6/29 2:02 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Mapper
public interface TableMapper {

    String TABLE_FIELDS = "`TABLE_SCHEMA` AS `tableSchema`," +
            "`TABLE_NAME` AS `tableName`," +
            "`TABLE_COMMENT` AS `tableComment`," +
            "`TABLE_COLLATION` AS `tableCollation`";

    @Select("<script>" +
            "SELECT " +
            TABLE_FIELDS +
            "FROM `INFORMATION_SCHEMA`.`TABLES` " +
            "WHERE `TABLE_SCHEMA` = #{databaseName}" +
            "</script>")
    List<Table> selectTableList(@Param("databaseName") String databaseName);

    @Select("<script>" +
            "SELECT " +
            TABLE_FIELDS +
            "FROM `INFORMATION_SCHEMA`.`TABLES` " +
            "WHERE `TABLE_SCHEMA` IN " +
            "<foreach collection=\"databaseNameList\" index=\"index\" item=\"item\" open=\"(\" separator=\",\" close=\")\">" +
            "   #{item}" +
            "</foreach>" +
            "</script>")
    List<Table> selectTableListInDatabases(@Param("databaseNameList") List<String> databaseNameList);

    @Select("<script>" +
            "SELECT " +
            TABLE_FIELDS +
            "FROM `INFORMATION_SCHEMA`.`TABLES` " +
            "WHERE `TABLE_SCHEMA` = #{databaseName} AND `TABLE_NAME` = #{tableName}" +
            "</script>")
    Table selectTable(@Param("databaseName") String databaseName, @Param("tableName") String tableName);

}
