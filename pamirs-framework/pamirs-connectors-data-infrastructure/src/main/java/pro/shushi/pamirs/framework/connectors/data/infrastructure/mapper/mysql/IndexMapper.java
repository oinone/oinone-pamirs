package pro.shushi.pamirs.framework.connectors.data.infrastructure.mapper.mysql;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.physical.Index;

import java.util.List;

/**
 * 系统索引信息mapper
 * <p>
 * 2020/6/29 2:02 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Mapper
public interface IndexMapper {

    String FIELDS = "`TABLE_SCHEMA` AS `tableSchema`," +
            "`TABLE_NAME` AS `tableName`," +
            "`INDEX_NAME` AS `indexName`," +
            "`COLUMN_NAME` AS `columnName`," +
            "`SEQ_IN_INDEX` AS `seqInIndex`," +
            "`NON_UNIQUE` AS `nonUnique` ";

    @Select("<script>" +
            "SELECT " +
            FIELDS +
            "FROM `INFORMATION_SCHEMA`.`STATISTICS` " +
            "WHERE `TABLE_SCHEMA` = #{databaseName}" +
            "</script>")
    List<Index> selectIndexList(@Param("databaseName") String databaseName);

    @Select("<script>" +
            "SELECT " +
            FIELDS +
            "FROM `INFORMATION_SCHEMA`.`STATISTICS` " +
            "WHERE `TABLE_SCHEMA` IN " +
            "<foreach collection=\"databaseNameList\" index=\"index\" item=\"item\" open=\"(\" separator=\",\" close=\")\">" +
            "   #{item}" +
            "</foreach>" +
            "</script>")
    List<Index> selectIndexListInDatabases(@Param("databaseNameList") List<String> databaseNameList);

    @Select("<script>" +
            "SELECT " +
            FIELDS +
            "FROM `INFORMATION_SCHEMA`.`STATISTICS` " +
            "WHERE `TABLE_SCHEMA` = #{databaseName} AND `TABLE_NAME` = #{tableName}" +
            "</script>")
    List<Index> selectIndexListByTableName(@Param("databaseName") String databaseName, @Param("tableName") String tableName);

    @Select("<script>" +
            "SELECT " +
            FIELDS +
            "FROM `INFORMATION_SCHEMA`.`STATISTICS` " +
            "WHERE `TABLE_SCHEMA` = #{databaseName} AND `TABLE_NAME` = #{tableName} AND `INDEX_NAME` = #{indexName}" +
            "</script>")
    List<Index> selectIndexListByName(@Param("databaseName") String databaseName, @Param("tableName") String tableName, @Param("indexName") String indexName);

    @Select("<script>" +
            "SELECT COUNT(1) " +
            "FROM `INFORMATION_SCHEMA`.`STATISTICS` " +
            "WHERE `TABLE_SCHEMA` = #{databaseName} AND `TABLE_NAME` = #{tableName} AND `INDEX_NAME` = #{indexName}" +
            "</script>")
    int countIndexByName(@Param("databaseName") String databaseName, @Param("tableName") String tableName, @Param("indexName") String indexName);

    @Select("<script>" +
            "SELECT COUNT(1) " +
            "FROM `INFORMATION_SCHEMA`.`STATISTICS` " +
            "WHERE `TABLE_SCHEMA` = #{databaseName} AND `TABLE_NAME` = #{tableName} AND `INDEX_NAME` = #{indexName} AND `NON_UNIQUE` = #{nonUnique}" +
            "</script>")
    int countIndexByNameNonUnique(@Param("databaseName") String databaseName, @Param("tableName") String tableName,
                                  @Param("indexName") String indexName, @Param("nonUnique") boolean nonUnique);

}
