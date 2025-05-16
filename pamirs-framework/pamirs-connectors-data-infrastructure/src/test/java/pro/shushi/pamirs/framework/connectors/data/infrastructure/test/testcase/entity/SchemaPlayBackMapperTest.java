package pro.shushi.pamirs.framework.connectors.data.infrastructure.test.testcase.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.AssertionErrors;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.AbstractBaseWithSessionTest;
import pro.shushi.pamirs.framework.connectors.data.infrastructure.test.mock.mapper.SchemaPlayBackMapper;
import pro.shushi.pamirs.framework.connectors.data.infrastructure.test.mock.model.SchemaPlayBack;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.common.util.TimeWatcher;
import pro.shushi.pamirs.meta.enmu.SortDirectionEnum;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * DDL回放mapper测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@DisplayName("DDL回放mapper测试，带乐观锁")
public class SchemaPlayBackMapperTest extends AbstractBaseWithSessionTest {

    @Resource
    private SchemaPlayBackMapper schemaPlayBackMapper;

    @Test
    @Order(0)
    @DisplayName("测试schemaPlayBackMapper插入单条记录功能")
    @Transactional
    public void testInsertOne() {
        // 执行测试用例
        SchemaPlayBack map = onePlayBack();
        map.setOptVersion(0L);
        schemaPlayBackMapper.insert(map);
        AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, map.getId());

        // 结果断言
        SchemaPlayBack schemaPlayBack = schemaPlayBackMapper.selectOne(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getDsKey, SCHEMA_PLAY_BACK_DS_KEY));
        AssertionErrors.assertEquals("查询数据库名", SCHEMA_PLAY_BACK_DS_KEY, schemaPlayBack.getDsKey());
        AssertionErrors.assertEquals("查询数据库字符集", SCHEMA_PLAY_BACK_DDL, schemaPlayBack.getDdl());
        AssertionErrors.assertEquals("查询数据库比对字符集", SCHEMA_PLAY_BACK_ERROR, schemaPlayBack.getError());
    }


    @Test
    @Order(0)
    @DisplayName("测试schemaPlayBackMapper批量插入记录功能")
    @Transactional
    public void testInsertBatch() {
        // 执行测试用例
        List<SchemaPlayBack> schemaPlayBacks = playBackBatchList();
        schemaPlayBackMapper.insertBatch(schemaPlayBacks);
        schemaPlayBacks.forEach(v -> AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, v.getId()));

        // 结果断言
        List<SchemaPlayBack> result = schemaPlayBackMapper.selectList(
                Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getDdl, SCHEMA_PLAY_BACK_DDL)
        );
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第一条记录未插入成功", SCHEMA_PLAY_BACK_DS_KEY + "1", result.get(0).getDsKey());
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第二条记录未插入成功", SCHEMA_PLAY_BACK_DS_KEY + "2", result.get(1).getDsKey());
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第三条记录未插入成功", SCHEMA_PLAY_BACK_DS_KEY + "3", result.get(2).getDsKey());
    }

    @Test
    @Order(0)
    @DisplayName("测试schemaPlayBackMapper插入或更新单条记录功能")
    @Transactional
    public void testInsertOrUpdate() {
        // 准备数据
        SchemaPlayBack map = onePlayBack();

        // 执行测试用例
        schemaPlayBackMapper.insertOrUpdate(map);
        AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, map.getId());

        // 结果断言
        SchemaPlayBack schemaPlayBack = schemaPlayBackMapper.selectOne(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getDsKey, SCHEMA_PLAY_BACK_DS_KEY));
        AssertionErrors.assertEquals("插入并查询单条记录失败", SCHEMA_PLAY_BACK_DDL, schemaPlayBack.getDdl());

        // 执行测试用例
        map.setDdl(SCHEMA_PLAY_BACK_DDL + 1);
        schemaPlayBackMapper.insertOrUpdate(map);
        AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, map.getId());

        // 结果断言
        schemaPlayBack = schemaPlayBackMapper.selectOne(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getDsKey, SCHEMA_PLAY_BACK_DS_KEY));
        AssertionErrors.assertEquals("更新并查询单条记录失败", SCHEMA_PLAY_BACK_DDL + 1, schemaPlayBack.getDdl());
    }


    @Test
    @Order(0)
    @DisplayName("测试schemaPlayBackMapper批量插入或更新记录功能")
    @Transactional
    public void testInsertOrUpdateBatch() {
        int size = 5000;
        int batchSize = 5000;
        // 准备数据
        List<SchemaPlayBack> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            SchemaPlayBack map = new SchemaPlayBack();
            map.setDsKey(SCHEMA_PLAY_BACK_DS_KEY + i);
            map.setDdl(SCHEMA_PLAY_BACK_DDL);
            map.setError(SCHEMA_PLAY_BACK_ERROR);
            map.setOptVersion(0L);
            list.add(map);
        }

        // 执行测试用例
        int count = TimeWatcher.watch(() -> schemaPlayBackMapper.insertOrUpdateBatchWithSize(list, batchSize), "首次插入");
        AssertionErrors.assertEquals("根据记录总数错误", size, count);
        list.forEach(v -> AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, v.getId()));

        // 结果断言
        List<SchemaPlayBack> result = schemaPlayBackMapper.selectList(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getError, SCHEMA_PLAY_BACK_ERROR));
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第一条记录未插入成功", SCHEMA_PLAY_BACK_DS_KEY + "0", result.get(0).getDsKey());
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第二条记录未插入成功", SCHEMA_PLAY_BACK_DS_KEY + "1", result.get(1).getDsKey());
        AssertionErrors.assertEquals("根据记录总数错误", size, result.size());

        // 准备数据
        list.get(0).setDdl(SCHEMA_PLAY_BACK_DDL + 1);
        list.get(1).setDdl(SCHEMA_PLAY_BACK_DDL + 1);
        SchemaPlayBack map3 = new SchemaPlayBack();
        map3.setDsKey(SCHEMA_PLAY_BACK_DS_KEY + size);
        map3.setError(SCHEMA_PLAY_BACK_ERROR);
        map3.setDdl(SCHEMA_PLAY_BACK_DDL);
        map3.setOptVersion(0L);
        list.add(map3);

        // 执行测试用例
        TimeWatcher.watch(() -> {
            schemaPlayBackMapper.insertOrUpdateBatchWithSize(list, batchSize);
        }, "更新");
        list.forEach(v -> AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, v.getId()));

        // 结果断言
        result = schemaPlayBackMapper.selectList(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getError, SCHEMA_PLAY_BACK_ERROR));
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第一条记录未更新成功", SCHEMA_PLAY_BACK_DDL + 1, result.get(0).getDdl());
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第二条记录未更新成功", SCHEMA_PLAY_BACK_DDL + 1, result.get(1).getDdl());
        AssertionErrors.assertEquals("根据记录总数错误", size + 1, result.size());
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第三条记录未插入成功", SCHEMA_PLAY_BACK_DDL, result.get(size).getDdl());
    }

    @Test
    @Order(0)
    @DisplayName("测试schemaPlayBackMapper批量插入或更新记录功能")
    @Transactional
    public void testInsertOrUpdateBatch0() {
        int size = 50;
        int batchSize = 1;
        // 准备数据
        List<SchemaPlayBack> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            SchemaPlayBack map = new SchemaPlayBack();
            map.setDsKey(SCHEMA_PLAY_BACK_DS_KEY + i);
            map.setError(SCHEMA_PLAY_BACK_ERROR);
            map.setDdl(SCHEMA_PLAY_BACK_DDL);
            list.add(map);
        }

        // 执行测试用例
        @SuppressWarnings("deprecation") int count = TimeWatcher.watch(() -> schemaPlayBackMapper.insertOrUpdateBatchOnDuplicateKeyWithSize(list, batchSize), "首次插入");
        AssertionErrors.assertEquals("根据记录总数错误", size, count);
        list.forEach(v -> AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, v.getId()));

        // 结果断言
        List<SchemaPlayBack> result = schemaPlayBackMapper.selectList(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getError, SCHEMA_PLAY_BACK_ERROR));
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第一条记录未插入成功", SCHEMA_PLAY_BACK_DS_KEY + "0", result.get(0).getDsKey());
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第二条记录未插入成功", SCHEMA_PLAY_BACK_DS_KEY + "1", result.get(1).getDsKey());
        AssertionErrors.assertEquals("根据记录总数错误", size, result.size());

        // 准备数据
        list.get(0).setDdl(SCHEMA_PLAY_BACK_DDL + 1);
        list.get(1).setDdl(SCHEMA_PLAY_BACK_DDL + 1);
        SchemaPlayBack map3 = new SchemaPlayBack();
        map3.setDsKey(SCHEMA_PLAY_BACK_DS_KEY + size);
        map3.setError(SCHEMA_PLAY_BACK_ERROR);
        map3.setDdl(SCHEMA_PLAY_BACK_DDL);
        list.add(map3);

        // 执行测试用例
        TimeWatcher.watch(() -> {//noinspection deprecation
            schemaPlayBackMapper.insertOrUpdateBatchOnDuplicateKeyWithSize(list, batchSize);
        }, "更新");
        list.forEach(v -> AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, v.getId()));

        // 结果断言
        result = schemaPlayBackMapper.selectList(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getError, SCHEMA_PLAY_BACK_ERROR));
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第一条记录未更新成功", SCHEMA_PLAY_BACK_DDL + 1, result.get(0).getDdl());
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第二条记录未更新成功", SCHEMA_PLAY_BACK_DDL + 1, result.get(1).getDdl());
        AssertionErrors.assertEquals("根据记录总数错误", size + 1, result.size());
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第三条记录未插入成功", SCHEMA_PLAY_BACK_DDL, result.get(size).getDdl());
    }

    @Test
    @Order(0)
    @DisplayName("测试schemaPlayBackMapper根据主键更新单条记录功能")
    @Transactional
    public void testUpdateByPk() {
        // 准备数据
        schemaPlayBackMapper.insert(onePlayBack());
        SchemaPlayBack result = schemaPlayBackMapper.selectOne(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getDsKey, SCHEMA_PLAY_BACK_DS_KEY));

        // 执行测试用例
        result.setDdl(SCHEMA_PLAY_BACK_DDL + 1);
        schemaPlayBackMapper.updateByPk(result);

        // 结果断言
        result = schemaPlayBackMapper.selectOne(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getDsKey, SCHEMA_PLAY_BACK_DS_KEY));
        AssertionErrors.assertEquals("更新单条记录失败", SCHEMA_PLAY_BACK_DDL + 1, result.getDdl());
    }

    @Test
    @Order(0)
    @DisplayName("测试schemaPlayBackMapper根据主键列表批量更新记录功能")
    @Transactional
    public void testUpdateByPks() {
        // 准备数据
        schemaPlayBackMapper.insertBatch(playBackBatchList());
        List<SchemaPlayBack> result = schemaPlayBackMapper.selectList(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getError, SCHEMA_PLAY_BACK_ERROR));
        AssertionErrors.assertEquals("批量更新记录失败", 3, result.size());

        // 执行测试用例
        schemaPlayBackMapper.updateByPks(
                new SchemaPlayBack().setDdl(SCHEMA_PLAY_BACK_DDL + 1).setOptVersion(0L),
                result
        );

        // 结果断言
        Long count = schemaPlayBackMapper.selectCount(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getDdl, SCHEMA_PLAY_BACK_DDL + 1));
        AssertionErrors.assertEquals("批量更新记录失败", 3L, count);
    }

    @Test
    @Order(0)
    @DisplayName("测试schemaPlayBackMapper根据复合主键列表批量更新记录功能")
    @Transactional
    public void testUpdateByMultiPks() {
        // 准备数据
        schemaPlayBackMapper.insertBatch(playBackBatchList());
        List<SchemaPlayBack> result = schemaPlayBackMapper.selectList(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getError, SCHEMA_PLAY_BACK_ERROR));
        AssertionErrors.assertEquals("批量更新记录失败", 3, result.size());

        // 执行测试用例
        schemaPlayBackMapper.updateByPks(
                new SchemaPlayBack().setDdl(SCHEMA_PLAY_BACK_DDL + 1).setOptVersion(0L),
                result
        );

        // 结果断言
        Long count = schemaPlayBackMapper.selectCount(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getDdl, SCHEMA_PLAY_BACK_DDL + 1));
        AssertionErrors.assertEquals("批量更新记录失败", 3L, count);
    }

    @Test
    @Order(0)
    @DisplayName("测试schemaPlayBackMapper根据唯一索引更新单条记录功能")
    @Transactional
    public void testUpdateByUniqueKey() {
        // 准备数据
        schemaPlayBackMapper.insert(onePlayBack().setOptVersion(0L));
        SchemaPlayBack result = schemaPlayBackMapper.selectOne(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getDsKey, SCHEMA_PLAY_BACK_DS_KEY));

        // 执行测试用例
        result.setDdl(SCHEMA_PLAY_BACK_DDL + 1);
        int count = schemaPlayBackMapper.updateByUniqueKey(result);
        AssertionErrors.assertEquals("更新单条记录失败", 1, count);

        // 结果断言
        result = schemaPlayBackMapper.selectOne(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getDsKey, SCHEMA_PLAY_BACK_DS_KEY));
        AssertionErrors.assertEquals("更新单条记录失败", SCHEMA_PLAY_BACK_DDL + 1, result.getDdl());
    }

    @Test
    @Order(0)
    @DisplayName("测试schemaPlayBackMapper根据Wapper批量更新记录功能")
    @Transactional
    public void testUpdateByWrapper() {
        // 准备数据
        schemaPlayBackMapper.insertBatch(playBackBatchList());
        List<SchemaPlayBack> result = schemaPlayBackMapper.selectList(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getError, SCHEMA_PLAY_BACK_ERROR));

        // 执行测试用例
        schemaPlayBackMapper.update(
                new SchemaPlayBack().setDdl(SCHEMA_PLAY_BACK_DDL + 1).setOptVersion(0L),
                Pops.<SchemaPlayBack>lambdaQuery().in(SchemaPlayBack::getId, result.get(0).getId(), result.get(1).getId())
        );

        // 结果断言
        Long count = schemaPlayBackMapper.selectCount(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getDdl, SCHEMA_PLAY_BACK_DDL + 1));
        AssertionErrors.assertEquals("批量更新记录失败", 2L, count);
    }

    @Test
    @Order(0)
    @DisplayName("测试schemaPlayBackMapper根据Wapper批量更新记录功能")
    @Transactional
    public void testUpdateByWrapperAndEntity() {
        // 准备数据
        schemaPlayBackMapper.insertBatch(playBackBatchList());
        List<SchemaPlayBack> result = schemaPlayBackMapper.selectList(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getError, SCHEMA_PLAY_BACK_ERROR));

        // 执行测试用例
        schemaPlayBackMapper.update(
                new SchemaPlayBack().setDdl(SCHEMA_PLAY_BACK_DDL + 1).setOptVersion(0L),
                Pops.query(new SchemaPlayBack().setDdl(SCHEMA_PLAY_BACK_DDL)).in("id", result.get(0).getId(), result.get(1).getId())
        );

        // 结果断言
        Long count = schemaPlayBackMapper.selectCount(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getDdl, SCHEMA_PLAY_BACK_DDL + 1));
        AssertionErrors.assertEquals("批量更新记录失败", 2L, count);
    }

    @Test
    @Order(0)
    @DisplayName("测试schemaPlayBackMapper删除单条记录功能")
    @Transactional
    public void testDeleteByPk() {
        // 准备数据
        schemaPlayBackMapper.insertBatch(playBackBatchList());

        SchemaPlayBack result = schemaPlayBackMapper.selectOne(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getDsKey, SCHEMA_PLAY_BACK_DS_KEY + "1"));
        AssertionErrors.assertNotEquals("删除单条准备数据失败", null, result);

        // 执行测试用例
        schemaPlayBackMapper.deleteByPk(result);

        // 结果断言
        result = schemaPlayBackMapper.selectOne(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getDsKey, SCHEMA_PLAY_BACK_DS_KEY + "1"));
        AssertionErrors.assertEquals("删除单条成功", null, result);
        Long count = schemaPlayBackMapper.selectCount(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getDdl, SCHEMA_PLAY_BACK_DDL));
        AssertionErrors.assertEquals("删除单条失败", 2L, count);
    }

    @Test
    @Order(0)
    @DisplayName("测试schemaPlayBackMapper根据PK列表批量删除记录功能")
    @Transactional
    public void testDeleteByPks() {
        // 准备数据
        schemaPlayBackMapper.insertBatch(playBackBatchList());
        List<SchemaPlayBack> result = schemaPlayBackMapper.selectList(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getError, SCHEMA_PLAY_BACK_ERROR));
        AssertionErrors.assertEquals("批量删除记录失败", 3, result.size());

        // 执行测试用例
        result.remove(0);
        long count = schemaPlayBackMapper.deleteByPks(result);
        AssertionErrors.assertEquals("批量删除记录失败", 2L, count);

        // 结果断言
        count = schemaPlayBackMapper.selectCount(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getError, SCHEMA_PLAY_BACK_ERROR));
        AssertionErrors.assertEquals("批量删除记录失败", 1L, count);
    }

    @Test
    @Order(0)
    @DisplayName("测试schemaPlayBackMapper实体条件删除功能")
    @Transactional
    public void testDeleteByEntity() {
        // 准备数据
        schemaPlayBackMapper.insertBatch(playBackBatchList());
        List<SchemaPlayBack> result = schemaPlayBackMapper.selectList(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getError, SCHEMA_PLAY_BACK_ERROR));
        AssertionErrors.assertEquals("条件删除准备数据失败", 3, result.size());

        // 执行测试用例
        long count = schemaPlayBackMapper.deleteByEntity(result.get(0));
        AssertionErrors.assertEquals("条件删除准备数据失败", 1L, count);

        // 结果断言
        SchemaPlayBack oneResult = schemaPlayBackMapper.selectOne(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getDsKey, SCHEMA_PLAY_BACK_DS_KEY + "1"));
        AssertionErrors.assertEquals("条件删除准备数据失败", null, oneResult);
        count = schemaPlayBackMapper.selectCount(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getError, SCHEMA_PLAY_BACK_ERROR));
        AssertionErrors.assertEquals("条件删除准备数据失败", 2L, count);
    }

    @Test
    @Order(0)
    @DisplayName("测试schemaPlayBackMapper wrapper条件删除功能")
    @Transactional
    public void testDeleteByWrapper() {
        // 准备数据
        schemaPlayBackMapper.insertBatch(playBackBatchList());
        List<SchemaPlayBack> result = schemaPlayBackMapper.selectList(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getError, SCHEMA_PLAY_BACK_ERROR));
        AssertionErrors.assertEquals("条件删除准备数据失败", 3, result.size());

        // 执行测试用例
        schemaPlayBackMapper.delete(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getDsKey, SCHEMA_PLAY_BACK_DS_KEY + "1"));

        // 结果断言
        SchemaPlayBack oneResult = schemaPlayBackMapper.selectOne(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getDsKey, SCHEMA_PLAY_BACK_DS_KEY + "1"));
        AssertionErrors.assertEquals("条件删除准备数据失败", null, oneResult);
        Long count = schemaPlayBackMapper.selectCount(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getError, SCHEMA_PLAY_BACK_ERROR));
        AssertionErrors.assertEquals("条件删除准备数据失败", 2L, count);
    }

    @Test
    @Order(1)
    @DisplayName("测试schemaPlayBackMapper根据pk查询单条记录功能")
    @Transactional
    public void testSelectByPk() {
        // 准备数据
        schemaPlayBackMapper.insert(onePlayBack());
        SchemaPlayBack oneResult = schemaPlayBackMapper.selectOne(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getDsKey, SCHEMA_PLAY_BACK_DS_KEY));

        // 执行测试用例
        SchemaPlayBack result = schemaPlayBackMapper.selectByPk(oneResult);

        // 结果断言
        AssertionErrors.assertEquals("查询单条记录失败", SCHEMA_PLAY_BACK_DS_KEY, result.getDsKey());
    }

    @Test
    @Order(1)
    @DisplayName("测试schemaPlayBackMapper根据pk列表查询记录列表功能")
    @Transactional
    public void selectListByPks() {
        // 准备数据
        schemaPlayBackMapper.insertBatch(playBackBatchList());
        List<SchemaPlayBack> result = schemaPlayBackMapper.selectList(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getError, SCHEMA_PLAY_BACK_ERROR));

        // 执行测试用例
        result = schemaPlayBackMapper.selectListByPks(result);

        // 结果断言
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第一条记录未插入成功", SCHEMA_PLAY_BACK_DS_KEY + "1", result.get(0).getDsKey());
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第二条记录未插入成功", SCHEMA_PLAY_BACK_DS_KEY + "2", result.get(1).getDsKey());
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第三条记录未插入成功", SCHEMA_PLAY_BACK_DS_KEY + "3", result.get(2).getDsKey());
    }

    @Test
    @Order(1)
    @DisplayName("测试schemaPlayBackMapper根据复合pk列表查询记录列表功能")
    @Transactional
    public void selectListByMultiPks() {
        // 准备数据
        schemaPlayBackMapper.insertBatch(playBackBatchList());
        List<SchemaPlayBack> result = schemaPlayBackMapper.selectList(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getError, SCHEMA_PLAY_BACK_ERROR));

        // 执行测试用例
        result = schemaPlayBackMapper.selectListByPks(result);

        // 结果断言
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第一条记录未插入成功", SCHEMA_PLAY_BACK_DS_KEY + "1", result.get(0).getDsKey());
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第二条记录未插入成功", SCHEMA_PLAY_BACK_DS_KEY + "2", result.get(1).getDsKey());
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第三条记录未插入成功", SCHEMA_PLAY_BACK_DS_KEY + "3", result.get(2).getDsKey());
    }

    @Test
    @Order(1)
    @DisplayName("测试schemaPlayBackMapper根据wrapper查询单条记录功能")
    @Transactional
    public void testSelectOne() {
        // 准备数据
        schemaPlayBackMapper.insert(onePlayBack());

        // 执行测试用例
        SchemaPlayBack result = schemaPlayBackMapper.selectOne(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getDsKey, SCHEMA_PLAY_BACK_DS_KEY));

        // 结果断言
        AssertionErrors.assertEquals("查询单条记录失败", SCHEMA_PLAY_BACK_DS_KEY + "", result.getDsKey());
    }

    @Test
    @Order(1)
    @DisplayName("测试schemaPlayBackMapper根据map查询单条记录功能")
    @Transactional
    public void testSelectOneByEntity() {
        // 准备数据
        schemaPlayBackMapper.insert(onePlayBack());

        // 执行测试用例
        SchemaPlayBack result = schemaPlayBackMapper.selectOneByEntity(onePlayBack());

        // 结果断言
        AssertionErrors.assertEquals("查询单条记录失败", SCHEMA_PLAY_BACK_DS_KEY, result.getDsKey());
    }

    @Test
    @Order(1)
    @DisplayName("测试schemaPlayBackMapper根据map查询多条记录功能")
    @Transactional
    public void selectListByEntity() {
        // 准备数据
        schemaPlayBackMapper.insertBatch(playBackBatchList());
        List<SchemaPlayBack> result = schemaPlayBackMapper.selectList(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getError, SCHEMA_PLAY_BACK_ERROR));

        // 执行测试用例
        result = schemaPlayBackMapper.selectListByEntity(result.get(0));

        // 结果断言
        AssertionErrors.assertEquals("根据entity查询记录列表失败", 1, result.size());
        AssertionErrors.assertEquals("根据entity查询记录列表失败", SCHEMA_PLAY_BACK_DS_KEY + "1", result.get(0).getDsKey());
    }

    @Test
    @Order(1)
    @DisplayName("测试schemaPlayBackMapper根据wrapper查询多条记录功能")
    @Transactional
    public void selectList() {
        // 准备数据
        schemaPlayBackMapper.insertBatch(playBackBatchList());

        // 执行测试用例
        List<SchemaPlayBack> result = schemaPlayBackMapper.selectList(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getError, SCHEMA_PLAY_BACK_ERROR));

        // 结果断言
        AssertionErrors.assertEquals("根据wrapper查询记录列表失败", 3, result.size());
        AssertionErrors.assertEquals("根据wrapper查询记录列表失败", SCHEMA_PLAY_BACK_DS_KEY + "1", result.get(0).getDsKey());
    }

    @Test
    @Order(1)
    @DisplayName("测试schemaPlayBackMapper根据wrapper查询多条记录排序功能")
    @Transactional
    public void selectOrderList() {
        // 准备数据
        List<SchemaPlayBack> params = playBackRandomOrderBatchList();
        schemaPlayBackMapper.insertBatch(params);
        SchemaPlayBack one = schemaPlayBackMapper.selectOneByEntity(params.get(0));

        // 执行测试用例
        List<SchemaPlayBack> result = schemaPlayBackMapper.selectList(Pops.<SchemaPlayBack>lambdaQuery()
                .eq(SchemaPlayBack::getError, SCHEMA_PLAY_BACK_ERROR).orderByAsc(SchemaPlayBack::getDsKey));

        // 结果断言
        AssertionErrors.assertEquals("根据wrapper查询记录列表排序失败", 3, result.size());
        AssertionErrors.assertEquals("根据wrapper查询记录列表排序失败，第一条数据错误", SCHEMA_PLAY_BACK_DS_KEY + "1", result.get(0).getDsKey());
        AssertionErrors.assertEquals("根据wrapper查询记录列表排序失败，第二条数据错误", SCHEMA_PLAY_BACK_DS_KEY + "2", result.get(1).getDsKey());
        AssertionErrors.assertEquals("根据wrapper查询记录列表排序失败，第三条数据错误", SCHEMA_PLAY_BACK_DS_KEY + "3", result.get(2).getDsKey());
        AssertionErrors.assertEquals("根据wrapper查询记录列表排序失败，排序错误", result.get(2).getId(), one.getId());
    }

    @Test
    @Order(1)
    @DisplayName("测试schemaPlayBackMapper查询匹配记录数量功能")
    @Transactional
    public void testSelectCount() {
        // 准备数据
        schemaPlayBackMapper.insertBatch(playBackBatchList());

        // 执行测试用例
        Long count = schemaPlayBackMapper.selectCount(Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getError, SCHEMA_PLAY_BACK_ERROR));

        // 结果断言
        AssertionErrors.assertEquals("查询匹配记录数量失败", 3L, count);
    }

    @Test
    @Order(1)
    @DisplayName("测试schemaPlayBackMapper根据wrapper分页查询多条记录功能")
    @Transactional
    public void testSelectPage() {
        // 准备数据
        schemaPlayBackMapper.insertBatch(playBackRandomOrderBatchList());

        // 执行测试用例
        Pagination<SchemaPlayBack> result = schemaPlayBackMapper.selectPage(
                new Pagination<SchemaPlayBack>().setCurrentPage(1).setSize(2L),
                Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getError, SCHEMA_PLAY_BACK_ERROR)
        );
        Pagination<SchemaPlayBack> orderResult = schemaPlayBackMapper.selectPage(
                new Pagination<SchemaPlayBack>().orderBy(SortDirectionEnum.ASC, SchemaPlayBack::getDsKey).setCurrentPage(2).setSize(2L),
                Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getError, SCHEMA_PLAY_BACK_ERROR)
        );

        // 结果断言
        AssertionErrors.assertEquals("根据wrapper分页查询记录列表排序失败，排序错误",
                result.getContent().get(0).getId(), orderResult.getContent().get(0).getId());
    }

    @Test
    @Order(1)
    @DisplayName("测试schemaPlayBackMapper根据wrapper分页查询多条记录功能")
    @Transactional
    public void testSelectPageForWrapOrderBy() {
        // 准备数据
        schemaPlayBackMapper.insertBatch(playBackRandomOrderBatchList());

        // 执行测试用例
        Pagination<SchemaPlayBack> result = schemaPlayBackMapper.selectPage(
                new Pagination<SchemaPlayBack>().setCurrentPage(1).setSize(2L),
                Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getError, SCHEMA_PLAY_BACK_ERROR)
        );
        Pagination<SchemaPlayBack> orderResult = schemaPlayBackMapper.selectPage(
                new Pagination<SchemaPlayBack>().setCurrentPage(2).setSize(2L),
                Pops.<SchemaPlayBack>lambdaQuery().eq(SchemaPlayBack::getError, SCHEMA_PLAY_BACK_ERROR).orderByAsc(SchemaPlayBack::getDsKey)
        );

        // 结果断言
        AssertionErrors.assertEquals("根据wrapper分页查询记录列表排序失败，排序错误",
                result.getContent().get(0).getId(), orderResult.getContent().get(0).getId());
    }

}
