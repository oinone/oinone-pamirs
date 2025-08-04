package pro.shushi.pamirs.framework.connectors.data.infrastructure.test.testcase.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.AssertionErrors;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.AbstractBaseWithSessionTest;
import pro.shushi.pamirs.framework.connectors.data.infrastructure.test.mock.mapper.SchemaMapper;
import pro.shushi.pamirs.framework.connectors.data.infrastructure.test.mock.model.DatabaseSchema;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.common.util.TimeWatcher;
import pro.shushi.pamirs.meta.enmu.SortDirectionEnum;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统mapper测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@DisplayName("系统mapper测试")
public class DatabaseMapperTest extends AbstractBaseWithSessionTest {

    @Resource
    private SchemaMapper schemaMapper;

    @Test
    @Order(0)
    @DisplayName("测试DatabaseMapper插入单条记录功能")
    @Transactional
    public void testInsertOne() {
        // 执行测试用例
        DatabaseSchema map = one();
        schemaMapper.insert(map);
        AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, map.getId());

        // 结果断言
        DatabaseSchema database = schemaMapper.selectOne(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getSchemaName, databaseName));
        AssertionErrors.assertEquals("查询数据库名", databaseName, database.getSchemaName());
        AssertionErrors.assertEquals("查询数据库字符集", DEFAULT_CHARACTER_SET_NAME, database.getDefaultCharacterSetName());
        AssertionErrors.assertEquals("查询数据库比对字符集", DEFAULT_COLLATION_NAME, database.getDefaultCollationName());
    }


    @Test
    @Order(0)
    @DisplayName("测试DatabaseMapper批量插入记录功能")
    @Transactional
    public void testInsertBatch() {
        // 执行测试用例
        List<DatabaseSchema> databases = batchList();
        schemaMapper.insertBatch(databases);
        databases.forEach(v -> AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, v.getId()));

        // 结果断言
        List<DatabaseSchema> result = schemaMapper.selectList(
                Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getDefaultCharacterSetName, DEFAULT_CHARACTER_SET_NAME)
        );
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第一条记录未插入成功", databaseName + "1", result.get(0).getSchemaName());
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第二条记录未插入成功", databaseName + "2", result.get(1).getSchemaName());
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第三条记录未插入成功", databaseName + "3", result.get(2).getSchemaName());
    }

    @Test
    @Order(0)
    @DisplayName("测试DatabaseMapper插入或更新单条记录功能")
    @Transactional
    public void testInsertOrUpdate() {
        // 准备数据
        DatabaseSchema map = one();

        // 执行测试用例
        int i = schemaMapper.insertOrUpdate(map);
        AssertionErrors.assertEquals("插入并查询单条记录失败，影响行数错误", 1, i);
        AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, map.getId());

        // 结果断言
        DatabaseSchema database = schemaMapper.selectOne(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getSchemaName, databaseName));
        AssertionErrors.assertEquals("插入并查询单条记录失败", DEFAULT_CHARACTER_SET_NAME, database.getDefaultCharacterSetName());

        // 执行测试用例
        map.setDefaultCharacterSetName(DEFAULT_CHARACTER_SET_NAME + 1);
        schemaMapper.insertOrUpdate(map);
        AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, map.getId());

        // 结果断言
        database = schemaMapper.selectOne(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getSchemaName, databaseName));
        AssertionErrors.assertEquals("更新并查询单条记录失败", DEFAULT_CHARACTER_SET_NAME + 1, database.getDefaultCharacterSetName());
    }


    @Test
    @Order(0)
    @DisplayName("测试DatabaseMapper批量插入或更新记录功能")
    @Transactional
    public void testInsertOrUpdateBatch() {
        int size = 5000;
        int batchSize = 5000;
        // 准备数据
        List<DatabaseSchema> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            DatabaseSchema map = new DatabaseSchema();
            map.setSchemaName(databaseName + i);
            map.setDefaultCollationName(DEFAULT_COLLATION_NAME);
            map.setDefaultCharacterSetName(DEFAULT_CHARACTER_SET_NAME);
            list.add(map);
        }

        // 执行测试用例
        int count = TimeWatcher.watch(() -> schemaMapper.insertOrUpdateBatchWithSize(list, batchSize), "首次插入");
        AssertionErrors.assertEquals("根据记录总数错误", size, count);
        list.forEach(v -> AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, v.getId()));

        // 结果断言
        List<DatabaseSchema> result = schemaMapper.selectList(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getDefaultCollationName, DEFAULT_COLLATION_NAME));
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第一条记录未插入成功", databaseName + "0", result.get(0).getSchemaName());
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第二条记录未插入成功", databaseName + "1", result.get(1).getSchemaName());
        AssertionErrors.assertEquals("根据记录总数错误", size, result.size());

        // 准备数据
        list.get(0).setDefaultCharacterSetName(DEFAULT_CHARACTER_SET_NAME + 1);
        list.get(1).setDefaultCharacterSetName(DEFAULT_CHARACTER_SET_NAME + 1);
        DatabaseSchema map3 = new DatabaseSchema();
        map3.setSchemaName(databaseName + size);
        map3.setDefaultCollationName(DEFAULT_COLLATION_NAME);
        map3.setDefaultCharacterSetName(DEFAULT_CHARACTER_SET_NAME);
        list.add(map3);

        // 执行测试用例
        TimeWatcher.watch(() -> {
            schemaMapper.insertOrUpdateBatchWithSize(list, batchSize);
        }, "更新");
        list.forEach(v -> AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, v.getId()));

        // 结果断言
        result = schemaMapper.selectList(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getDefaultCollationName, DEFAULT_COLLATION_NAME));
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第一条记录未更新成功", DEFAULT_CHARACTER_SET_NAME + 1, result.get(0).getDefaultCharacterSetName());
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第二条记录未更新成功", DEFAULT_CHARACTER_SET_NAME + 1, result.get(1).getDefaultCharacterSetName());
        AssertionErrors.assertEquals("根据记录总数错误", size + 1, result.size());
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第三条记录未插入成功", DEFAULT_CHARACTER_SET_NAME, result.get(size).getDefaultCharacterSetName());
    }

    @Test
    @Order(0)
    @DisplayName("测试DatabaseMapper批量插入或更新记录功能")
    @Transactional
    public void testInsertOrUpdateBatch0() {
        int size = 50;
        int batchSize = 1;
        // 准备数据
        List<DatabaseSchema> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            DatabaseSchema map = new DatabaseSchema();
            map.setSchemaName(databaseName + i);
            map.setDefaultCollationName(DEFAULT_COLLATION_NAME);
            map.setDefaultCharacterSetName(DEFAULT_CHARACTER_SET_NAME);
            list.add(map);
        }

        // 执行测试用例
        @SuppressWarnings("deprecation") int count = TimeWatcher.watch(() -> schemaMapper.insertOrUpdateBatchOnDuplicateKeyWithSize(list, batchSize), "首次插入");
        AssertionErrors.assertEquals("根据记录总数错误", size, count);
        list.forEach(v -> AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, v.getId()));

        // 结果断言
        List<DatabaseSchema> result = schemaMapper.selectList(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getDefaultCollationName, DEFAULT_COLLATION_NAME));
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第一条记录未插入成功", databaseName + "0", result.get(0).getSchemaName());
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第二条记录未插入成功", databaseName + "1", result.get(1).getSchemaName());
        AssertionErrors.assertEquals("根据记录总数错误", size, result.size());

        // 准备数据
        list.get(0).setDefaultCharacterSetName(DEFAULT_CHARACTER_SET_NAME + 1);
        list.get(1).setDefaultCharacterSetName(DEFAULT_CHARACTER_SET_NAME + 1);
        DatabaseSchema map3 = new DatabaseSchema();
        map3.setSchemaName(databaseName + size);
        map3.setDefaultCollationName(DEFAULT_COLLATION_NAME);
        map3.setDefaultCharacterSetName(DEFAULT_CHARACTER_SET_NAME);
        list.add(map3);

        // 执行测试用例
        TimeWatcher.watch(() -> {//noinspection deprecation
            schemaMapper.insertOrUpdateBatchOnDuplicateKeyWithSize(list, batchSize);
        }, "更新");
        list.forEach(v -> AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, v.getId()));

        // 结果断言
        result = schemaMapper.selectList(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getDefaultCollationName, DEFAULT_COLLATION_NAME));
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第一条记录未更新成功", DEFAULT_CHARACTER_SET_NAME + 1, result.get(0).getDefaultCharacterSetName());
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第二条记录未更新成功", DEFAULT_CHARACTER_SET_NAME + 1, result.get(1).getDefaultCharacterSetName());
        AssertionErrors.assertEquals("根据记录总数错误", size + 1, result.size());
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第三条记录未插入成功", DEFAULT_CHARACTER_SET_NAME, result.get(size).getDefaultCharacterSetName());
    }

    @Test
    @Order(0)
    @DisplayName("测试DatabaseMapper根据主键更新单条记录功能")
    @Transactional
    public void testUpdateByPk() {
        // 准备数据
        schemaMapper.insert(one());
        DatabaseSchema result = schemaMapper.selectOne(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getSchemaName, databaseName));

        // 执行测试用例
        result.setDefaultCharacterSetName(DEFAULT_CHARACTER_SET_NAME + 1);
        schemaMapper.updateByPk(result);

        // 结果断言
        result = schemaMapper.selectOne(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getSchemaName, databaseName));
        AssertionErrors.assertEquals("更新单条记录失败", DEFAULT_CHARACTER_SET_NAME + 1, result.getDefaultCharacterSetName());
    }

    @Test
    @Order(0)
    @DisplayName("测试DatabaseMapper根据主键列表批量更新记录功能")
    @Transactional
    public void testUpdateByPks() {
        // 准备数据
        schemaMapper.insertBatch(batchList());
        List<DatabaseSchema> result = schemaMapper.selectList(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getDefaultCollationName, DEFAULT_COLLATION_NAME));
        AssertionErrors.assertEquals("批量更新记录失败", 3, result.size());

        // 执行测试用例
        schemaMapper.updateByPks(
                new DatabaseSchema().setDefaultCharacterSetName(DEFAULT_CHARACTER_SET_NAME + 1),
                result
        );

        // 结果断言
        Long count = schemaMapper.selectCount(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getDefaultCharacterSetName, DEFAULT_CHARACTER_SET_NAME + 1));
        AssertionErrors.assertEquals("批量更新记录失败", 3L, count);
    }

    @Test
    @Order(0)
    @DisplayName("测试DatabaseMapper根据复合主键列表批量更新记录功能")
    @Transactional
    public void testUpdateByMultiPks() {
        // 准备数据
        schemaMapper.insertBatch(batchList());
        List<DatabaseSchema> result = schemaMapper.selectList(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getDefaultCollationName, DEFAULT_COLLATION_NAME));
        AssertionErrors.assertEquals("批量更新记录失败", 3, result.size());

        // 执行测试用例
        schemaMapper.updateByPks(
                new DatabaseSchema().setDefaultCharacterSetName(DEFAULT_CHARACTER_SET_NAME + 1),
                result
        );

        // 结果断言
        Long count = schemaMapper.selectCount(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getDefaultCharacterSetName, DEFAULT_CHARACTER_SET_NAME + 1));
        AssertionErrors.assertEquals("批量更新记录失败", 3L, count);
    }

    @Test
    @Order(0)
    @DisplayName("测试DatabaseMapper根据唯一索引更新单条记录功能")
    @Transactional
    public void testUpdateByUniqueKey() {
        // 准备数据
        schemaMapper.insert(one());
        DatabaseSchema result = schemaMapper.selectOne(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getSchemaName, databaseName));

        // 执行测试用例
        result.setDefaultCharacterSetName(DEFAULT_CHARACTER_SET_NAME + 1);
        int count = schemaMapper.updateByUniqueKey(result);
        AssertionErrors.assertEquals("更新单条记录失败", 1, count);

        // 结果断言
        result = schemaMapper.selectOne(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getSchemaName, databaseName));
        AssertionErrors.assertEquals("更新单条记录失败", DEFAULT_CHARACTER_SET_NAME + 1, result.getDefaultCharacterSetName());
    }

    @Test
    @Order(0)
    @DisplayName("测试DatabaseMapper根据Wapper批量更新记录功能")
    @Transactional
    public void testUpdateByWrapper() {
        // 准备数据
        schemaMapper.insertBatch(batchList());
        List<DatabaseSchema> result = schemaMapper.selectList(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getDefaultCollationName, DEFAULT_COLLATION_NAME));

        // 执行测试用例
        schemaMapper.update(
                new DatabaseSchema().setDefaultCharacterSetName(DEFAULT_CHARACTER_SET_NAME + 1),
                Pops.<DatabaseSchema>lambdaQuery().in(DatabaseSchema::getId, result.get(0).getId(), result.get(1).getId())
        );

        // 结果断言
        Long count = schemaMapper.selectCount(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getDefaultCharacterSetName, DEFAULT_CHARACTER_SET_NAME + 1));
        AssertionErrors.assertEquals("批量更新记录失败", 2L, count);
    }

    @Test
    @Order(0)
    @DisplayName("测试DatabaseMapper根据Wapper批量更新记录功能")
    @Transactional
    public void testUpdateByWrapperAndEntity() {
        // 准备数据
        schemaMapper.insertBatch(batchList());
        List<DatabaseSchema> result = schemaMapper.selectList(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getDefaultCollationName, DEFAULT_COLLATION_NAME));

        // 执行测试用例
        schemaMapper.update(
                new DatabaseSchema().setDefaultCharacterSetName(DEFAULT_CHARACTER_SET_NAME + 1),
                Pops.query(new DatabaseSchema().setDefaultCharacterSetName(DEFAULT_CHARACTER_SET_NAME)).in("id", result.get(0).getId(), result.get(1).getId())
        );

        // 结果断言
        Long count = schemaMapper.selectCount(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getDefaultCharacterSetName, DEFAULT_CHARACTER_SET_NAME + 1));
        AssertionErrors.assertEquals("批量更新记录失败", 2L, count);
    }

    @Test
    @Order(0)
    @DisplayName("测试DatabaseMapper删除单条记录功能")
    @Transactional
    public void testDeleteByPk() {
        // 准备数据
        schemaMapper.insertBatch(batchList());

        DatabaseSchema result = schemaMapper.selectOne(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getSchemaName, databaseName + "1"));
        AssertionErrors.assertNotEquals("删除单条准备数据失败", null, result);

        // 执行测试用例
        schemaMapper.deleteByPk(result);

        // 结果断言
        result = schemaMapper.selectOne(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getSchemaName, databaseName + "1"));
        AssertionErrors.assertEquals("删除单条成功", null, result);
        Long count = schemaMapper.selectCount(Pops.query());
        AssertionErrors.assertEquals("删除单条失败", 2L, count);
    }

    @Test
    @Order(0)
    @DisplayName("测试DatabaseMapper根据PK列表批量删除记录功能")
    @Transactional
    public void testDeleteByPks() {
        // 准备数据
        schemaMapper.insertBatch(batchList());
        List<DatabaseSchema> result = schemaMapper.selectList(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getDefaultCollationName, DEFAULT_COLLATION_NAME));
        AssertionErrors.assertEquals("批量删除记录失败", 3, result.size());

        // 执行测试用例
        result.remove(0);
        long count = schemaMapper.deleteByPks(result);
        AssertionErrors.assertEquals("批量删除记录失败", 2L, count);

        // 结果断言
        count = schemaMapper.selectCount(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getDefaultCollationName, DEFAULT_COLLATION_NAME));
        AssertionErrors.assertEquals("批量删除记录失败", 1L, count);
    }

    @Test
    @Order(0)
    @DisplayName("测试DatabaseMapper实体条件删除功能")
    @Transactional
    public void testDeleteByEntity() {
        // 准备数据
        schemaMapper.insertBatch(batchList());
        List<DatabaseSchema> result = schemaMapper.selectList(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getDefaultCollationName, DEFAULT_COLLATION_NAME));
        AssertionErrors.assertEquals("条件删除准备数据失败", 3, result.size());

        // 执行测试用例
        long count = schemaMapper.deleteByEntity(result.get(0));
        AssertionErrors.assertEquals("条件删除准备数据失败", 1L, count);

        // 结果断言
        DatabaseSchema oneResult = schemaMapper.selectOne(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getSchemaName, databaseName + "1"));
        AssertionErrors.assertEquals("条件删除准备数据失败", null, oneResult);
        count = schemaMapper.selectCount(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getDefaultCollationName, DEFAULT_COLLATION_NAME));
        AssertionErrors.assertEquals("条件删除准备数据失败", 2L, count);
    }

    @Test
    @Order(0)
    @DisplayName("测试DatabaseMapper wrapper条件删除功能")
    @Transactional
    public void testDeleteByWrapper() {
        // 准备数据
        schemaMapper.insertBatch(batchList());
        List<DatabaseSchema> result = schemaMapper.selectList(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getDefaultCollationName, DEFAULT_COLLATION_NAME));
        AssertionErrors.assertEquals("条件删除准备数据失败", 3, result.size());

        // 执行测试用例
        schemaMapper.delete(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getSchemaName, databaseName + "1"));

        // 结果断言
        DatabaseSchema oneResult = schemaMapper.selectOne(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getSchemaName, databaseName + "1"));
        AssertionErrors.assertEquals("条件删除准备数据失败", null, oneResult);
        Long count = schemaMapper.selectCount(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getDefaultCollationName, DEFAULT_COLLATION_NAME));
        AssertionErrors.assertEquals("条件删除准备数据失败", 2L, count);
    }

    @Test
    @Order(1)
    @DisplayName("测试DatabaseMapper根据pk查询单条记录功能")
    @Transactional
    public void testSelectByPk() {
        // 准备数据
        schemaMapper.insert(one());
        DatabaseSchema oneResult = schemaMapper.selectOne(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getSchemaName, databaseName));

        // 执行测试用例
        DatabaseSchema result = schemaMapper.selectByPk(oneResult);

        // 结果断言
        AssertionErrors.assertEquals("查询单条记录失败", databaseName, result.getSchemaName());
    }

    @Test
    @Order(1)
    @DisplayName("测试DatabaseMapper根据pk列表查询记录列表功能")
    @Transactional
    public void selectListByPks() {
        // 准备数据
        schemaMapper.insertBatch(batchList());
        List<DatabaseSchema> result = schemaMapper.selectList(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getDefaultCollationName, DEFAULT_COLLATION_NAME));

        // 执行测试用例
        result = schemaMapper.selectListByPks(result);

        // 结果断言
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第一条记录未插入成功", databaseName + "1", result.get(0).getSchemaName());
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第二条记录未插入成功", databaseName + "2", result.get(1).getSchemaName());
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第三条记录未插入成功", databaseName + "3", result.get(2).getSchemaName());
    }

    @Test
    @Order(1)
    @DisplayName("测试DatabaseMapper根据复合pk列表查询记录列表功能")
    @Transactional
    public void selectListByMultiPks() {
        // 准备数据
        schemaMapper.insertBatch(batchList());
        List<DatabaseSchema> result = schemaMapper.selectList(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getDefaultCollationName, DEFAULT_COLLATION_NAME));

        // 执行测试用例
        result = schemaMapper.selectListByPks(result);

        // 结果断言
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第一条记录未插入成功", databaseName + "1", result.get(0).getSchemaName());
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第二条记录未插入成功", databaseName + "2", result.get(1).getSchemaName());
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第三条记录未插入成功", databaseName + "3", result.get(2).getSchemaName());
    }

    @Test
    @Order(1)
    @DisplayName("测试DatabaseMapper根据wrapper查询单条记录功能")
    @Transactional
    public void testSelectOne() {
        // 准备数据
        schemaMapper.insert(one());

        // 执行测试用例
        DatabaseSchema result = schemaMapper.selectOne(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getSchemaName, databaseName));

        // 结果断言
        AssertionErrors.assertEquals("查询单条记录失败", databaseName + "", result.getSchemaName());
    }

    @Test
    @Order(1)
    @DisplayName("测试DatabaseMapper根据map查询单条记录功能")
    @Transactional
    public void testSelectOneByEntity() {
        // 准备数据
        schemaMapper.insert(one());

        // 执行测试用例
        DatabaseSchema result = schemaMapper.selectOneByEntity(one());

        // 结果断言
        AssertionErrors.assertEquals("查询单条记录失败", databaseName, result.getSchemaName());
    }

    @Test
    @Order(1)
    @DisplayName("测试DatabaseMapper根据map查询多条记录功能")
    @Transactional
    public void selectListByEntity() {
        // 准备数据
        schemaMapper.insertBatch(batchList());
        List<DatabaseSchema> result = schemaMapper.selectList(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getDefaultCollationName, DEFAULT_COLLATION_NAME));

        // 执行测试用例
        result = schemaMapper.selectListByEntity(result.get(0));

        // 结果断言
        AssertionErrors.assertEquals("根据entity查询记录列表失败", 1, result.size());
        AssertionErrors.assertEquals("根据entity查询记录列表失败", databaseName + "1", result.get(0).getSchemaName());
    }

    @Test
    @Order(1)
    @DisplayName("测试DatabaseMapper根据wrapper查询多条记录功能")
    @Transactional
    public void selectList() {
        // 准备数据
        schemaMapper.insertBatch(batchList());

        // 执行测试用例
        List<DatabaseSchema> result = schemaMapper.selectList(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getDefaultCollationName, DEFAULT_COLLATION_NAME));

        // 结果断言
        AssertionErrors.assertEquals("根据wrapper查询记录列表失败", 3, result.size());
        AssertionErrors.assertEquals("根据wrapper查询记录列表失败", databaseName + "1", result.get(0).getSchemaName());
    }

    @Test
    @Order(1)
    @DisplayName("测试DatabaseMapper根据wrapper查询多条记录排序功能")
    @Transactional
    public void selectOrderList() {
        // 准备数据
        List<DatabaseSchema> params = randomOrderBatchList();
        schemaMapper.insertBatch(params);
        DatabaseSchema one = schemaMapper.selectOneByEntity(params.get(0));

        // 执行测试用例
        List<DatabaseSchema> result = schemaMapper.selectList(Pops.<DatabaseSchema>lambdaQuery()
                .eq(DatabaseSchema::getDefaultCollationName, DEFAULT_COLLATION_NAME).orderByAsc(DatabaseSchema::getSchemaName));

        // 结果断言
        AssertionErrors.assertEquals("根据wrapper查询记录列表排序失败", 3, result.size());
        AssertionErrors.assertEquals("根据wrapper查询记录列表排序失败，第一条数据错误", databaseName + "1", result.get(0).getSchemaName());
        AssertionErrors.assertEquals("根据wrapper查询记录列表排序失败，第二条数据错误", databaseName + "2", result.get(1).getSchemaName());
        AssertionErrors.assertEquals("根据wrapper查询记录列表排序失败，第三条数据错误", databaseName + "3", result.get(2).getSchemaName());
        AssertionErrors.assertEquals("根据wrapper查询记录列表排序失败，排序错误", result.get(2).getId(), one.getId());
    }

    @Test
    @Order(1)
    @DisplayName("测试DatabaseMapper查询匹配记录数量功能")
    @Transactional
    public void testSelectCount() {
        // 准备数据
        schemaMapper.insertBatch(batchList());

        // 执行测试用例
        Long count = schemaMapper.selectCount(Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getDefaultCollationName, DEFAULT_COLLATION_NAME));

        // 结果断言
        AssertionErrors.assertEquals("查询匹配记录数量失败", 3L, count);
    }

    @Test
    @Order(1)
    @DisplayName("测试DatabaseMapper根据wrapper分页查询多条记录功能")
    @Transactional
    public void testSelectPage() {
        // 准备数据
        schemaMapper.insertBatch(randomOrderBatchList());

        // 执行测试用例
        Pagination<DatabaseSchema> result = schemaMapper.selectPage(
                new Pagination<DatabaseSchema>().setCurrentPage(1).setSize(2L),
                Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getDefaultCollationName, DEFAULT_COLLATION_NAME)
        );
        Pagination<DatabaseSchema> orderResult = schemaMapper.selectPage(
                new Pagination<DatabaseSchema>().orderBy(SortDirectionEnum.ASC, DatabaseSchema::getSchemaName).setCurrentPage(2).setSize(2L),
                Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getDefaultCollationName, DEFAULT_COLLATION_NAME)
        );

        // 结果断言
        AssertionErrors.assertEquals("根据wrapper分页查询记录列表排序失败，排序错误",
                result.getContent().get(0).getId(), orderResult.getContent().get(0).getId());
    }

    @Test
    @Order(1)
    @DisplayName("测试DatabaseMapper根据wrapper分页查询多条记录功能")
    @Transactional
    public void testSelectPageForWrapOrderBy() {
        // 准备数据
        schemaMapper.insertBatch(randomOrderBatchList());

        // 执行测试用例
        Pagination<DatabaseSchema> result = schemaMapper.selectPage(
                new Pagination<DatabaseSchema>().setCurrentPage(1).setSize(2L),
                Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getDefaultCollationName, DEFAULT_COLLATION_NAME)
        );
        Pagination<DatabaseSchema> orderResult = schemaMapper.selectPage(
                new Pagination<DatabaseSchema>().setCurrentPage(2).setSize(2L),
                Pops.<DatabaseSchema>lambdaQuery().eq(DatabaseSchema::getDefaultCollationName, DEFAULT_COLLATION_NAME).orderByAsc(DatabaseSchema::getSchemaName)
        );

        // 结果断言
        AssertionErrors.assertEquals("根据wrapper分页查询记录列表排序失败，排序错误",
                result.getContent().get(0).getId(), orderResult.getContent().get(0).getId());
    }

}
