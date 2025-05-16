package pro.shushi.pamirs.framework.connectors.data.infrastructure.mapper.mysql;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.physical.Database;

import java.util.List;

/**
 * 系统数据库信息mapper
 * <p>
 * 2020/6/29 2:02 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Mapper
public interface DatabaseMapper {

    String FIELDS = "`SCHEMA_NAME` AS `schemaName`," +
            "`DEFAULT_CHARACTER_SET_NAME` AS `characterSetName`," +
            "`DEFAULT_COLLATION_NAME` AS `collationName` ";

    @Select("<script>" +
            "SELECT " +
            FIELDS +
            "FROM `INFORMATION_SCHEMA`.`SCHEMATA`" +
            "</script>")
    List<Database> selectDatabaseList();

    @Select("<script>" +
            "SELECT " +
            FIELDS +
            "FROM `INFORMATION_SCHEMA`.`SCHEMATA` " +
            "WHERE `SCHEMA_NAME` = #{databaseName}" +
            "</script>")
    Database selectDatabase(@Param("databaseName") String databaseName);

}
