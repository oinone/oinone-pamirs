package pro.shushi.pamirs.framework.connectors.data.test.testcase.generic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.AssertionErrors;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.AbstractBaseTest;
import pro.shushi.pamirs.framework.connectors.data.mapper.GenericMapper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.test.mock.model.TestModel;
import pro.shushi.pamirs.framework.connectors.data.test.mock.model.TestMultiPkModel;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.common.util.TimeWatcher;
import pro.shushi.pamirs.meta.enmu.SortDirectionEnum;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 通用mapper测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@DisplayName("通用mapper测试")
public class GenericMapperTest extends AbstractBaseTest {

    private final static String modelModel = TestModel.modelModel;

    @Resource
    private GenericMapper genericMapper;

    @Test
    @Order(0)
    @DisplayName("测试通用mapper无事务插入单条记录功能")
    public void testInsertOneWithoutTx() {
        // 执行测试用例
        DataMap one = one(modelModel);
        genericMapper.insert(one);
        AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, one.get("id"));

        // 结果断言
        Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(modelModel).eq("`relation`", "test"));
        AssertionErrors.assertEquals("插入并查询单条记录失败", "yihui", result.get("module"));
    }

    @Test
    @Order(0)
    @DisplayName("测试通用mapper插入单条记录功能")
    @Transactional
    public void testInsertOne() {
        // 执行测试用例
        DataMap one = one(modelModel);
        genericMapper.insert(one);
        AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, one.get("id"));

        // 结果断言
        Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(modelModel).eq("`relation`", "test"));
        AssertionErrors.assertEquals("插入并查询单条记录失败", "yihui", result.get("module"));
    }

    @Test
    @Order(0)
    @DisplayName("测试通用mapper批量插入记录功能")
    @Transactional
    public void testInsertBatch() {
        // 执行测试用例
        List<DataMap> dataList = batchList(modelModel);
        genericMapper.insertBatch(dataList);
        dataList.forEach(v -> AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, v.get("id")));

        // 结果断言
        List<DataMap> result = genericMapper.selectList(Pops.<DataMap>query().from(modelModel).eq("`relation`", "test"));
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第一条记录未插入成功", "yihui1", result.get(0).get("module"));
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第二条记录未插入成功", "yihui2", result.get(1).get("module"));
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第三条记录未插入成功", "yihui3", result.get(2).get("module"));
    }

    @Test
    @Order(0)
    @DisplayName("测试通用mapper插入或更新单条记录功能")
    @Transactional
    public void testInsertOrUpdate() {
        // 准备数据
        DataMap map = one(modelModel);

        // 执行测试用例
        genericMapper.insertOrUpdate(map);
        AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, map.get("id"));

        // 结果断言
        Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(modelModel).eq("`relation`", "test"));
        AssertionErrors.assertEquals("插入并查询单条记录失败", "1.0.0", result.get("version"));

        // 执行测试用例
        map.put("version", "2.0.0");
        genericMapper.insertOrUpdate(map);
        AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, map.get("id"));

        // 结果断言
        result = genericMapper.selectOne(Pops.<DataMap>query().from(modelModel).eq("`relation`", "test"));
        AssertionErrors.assertEquals("更新并查询单条记录失败", "2.0.0", result.get("version"));
    }

    @Test
    @Order(0)
    @DisplayName("测试通用mapper批量插入或更新记录功能")
    @Transactional
    public void testInsertOrUpdateBatch() {
        int size = 20000;
        int batchSize = 10000;
        // 准备数据
        List<DataMap> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            DataMap map = new DataMap(modelModel);
            map.put("module", "yihui" + i);
            map.put("relation", "test");
            map.put("version", "1.0.0");
            list.add(map);
        }

        // 执行测试用例
        int count = TimeWatcher.watch(() -> genericMapper.insertOrUpdateBatchWithSize(list, batchSize), "首次插入");
        AssertionErrors.assertEquals("根据记录总数错误", size, count);
        list.forEach(v -> AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, v.get("id")));

        // 结果断言
        List<DataMap> result = genericMapper.selectList(Pops.<DataMap>query().from(modelModel).eq("`relation`", "test"));
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第一条记录未插入成功", "yihui0", result.get(0).get("module"));
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第二条记录未插入成功", "yihui1", result.get(1).get("module"));
        AssertionErrors.assertEquals("根据记录总数错误", size, result.size());

        // 准备数据
        list.get(0).put("version", "2.0.0");
        list.get(1).put("version", "2.0.0");
        DataMap map3 = new DataMap(modelModel);
        map3.setValue(TestModel::getModule, "yihui" + size);
        map3.setValue(TestModel::getRelation, "test");
        map3.setValue(TestModel::getVersion, "1.0.0");
        list.add(map3);

        // 执行测试用例
        TimeWatcher.watch(() -> {genericMapper.insertOrUpdateBatchWithSize(list, batchSize);}, "更新");
        list.forEach(v -> AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, v.get("id")));

        // 结果断言
        result = genericMapper.selectList(Pops.<DataMap>query().from(modelModel).eq("`relation`", "test"));
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第一条记录未更新成功", "2.0.0", result.get(0).get("version"));
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第二条记录未更新成功", "2.0.0", result.get(1).get("version"));
        AssertionErrors.assertEquals("根据记录总数错误", size + 1, result.size());
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第三条记录未插入成功", "1.0.0", result.get(size).get("version"));
    }

    @Test
    @Order(0)
    @DisplayName("测试通用mapper批量插入或更新记录功能")
    @Transactional
    public void testInsertOrUpdateBatch0() {
        int size = 50;
        int batchSize = 1;
        // 准备数据
        List<DataMap> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            DataMap map = new DataMap(modelModel);
            map.put("module", "yihui" + i);
            map.put("relation", "test");
            map.put("version", "1.0.0");
            list.add(map);
        }

        // 执行测试用例
        @SuppressWarnings("deprecation")
        int count = TimeWatcher.watch(() -> genericMapper.insertOrUpdateBatchOnDuplicateKeyWithSize(list, batchSize), "首次插入");
        AssertionErrors.assertEquals("根据记录总数错误", size, count);
        list.forEach(v -> AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, v.get("id")));

        // 结果断言
        List<DataMap> result = genericMapper.selectList(Pops.<DataMap>query().from(modelModel).eq("`relation`", "test"));
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第一条记录未插入成功", "yihui0", result.get(0).get("module"));
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第二条记录未插入成功", "yihui1", result.get(1).get("module"));
        AssertionErrors.assertEquals("根据记录总数错误", size, result.size());

        // 准备数据
        list.get(0).put("version", "2.0.0");
        list.get(1).put("version", "2.0.0");
        DataMap map3 = new DataMap(modelModel);
        map3.setValue(TestModel::getModule, "yihui" + size);
        map3.setValue(TestModel::getRelation, "test");
        map3.setValue(TestModel::getVersion, "1.0.0");
        list.add(map3);

        // 执行测试用例

        TimeWatcher.watch(() -> {//noinspection deprecation
            genericMapper.insertOrUpdateBatchOnDuplicateKeyWithSize(list, batchSize);
        }, "更新");
        list.forEach(v -> AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, v.get("id")));

        // 结果断言
        result = genericMapper.selectList(Pops.<DataMap>query().from(modelModel).eq("`relation`", "test"));
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第一条记录未更新成功", "2.0.0", result.get(0).get("version"));
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第二条记录未更新成功", "2.0.0", result.get(1).get("version"));
        AssertionErrors.assertEquals("根据记录总数错误", size + 1, result.size());
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第三条记录未插入成功", "1.0.0", result.get(size).get("version"));
    }

    @Test
    @Order(0)
    @DisplayName("测试通用mapper根据主键更新单条记录功能")
    @Transactional
    public void testUpdateByPk() {
        // 准备数据
        genericMapper.insert(one(modelModel));
        Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(modelModel).eq("module", "yihui"));

        // 执行测试用例
        DataMap map = new DataMap(modelModel);
        map.put("id", result.get("id"));
        map.put("version", "2.0.0");
        genericMapper.updateByPk(map);

        // 结果断言
        result = genericMapper.selectOne(Pops.<DataMap>query().from(modelModel).eq("module", "yihui"));
        AssertionErrors.assertEquals("更新单条记录失败", "2.0.0", result.get("version"));
    }

    @Test
    @Order(0)
    @DisplayName("测试通用mapper根据主键列表批量更新记录功能")
    @Transactional
    public void testUpdateByPks() {
        // 准备数据
        genericMapper.insertBatch(batchList(modelModel));
        List<DataMap> result = genericMapper.selectList(Pops.<DataMap>query().from(modelModel).eq("`relation`", "test"));
        AssertionErrors.assertEquals("批量更新记录失败", 3, result.size());

        // 执行测试用例
        genericMapper.updateByPks(
                new DataMap(modelModel).setValue("version", "2.0.0"),
                result
        );

        // 结果断言
        Long count = genericMapper.selectCount(Pops.<DataMap>query().from(modelModel).eq("`version`", "2.0.0"));
        AssertionErrors.assertEquals("批量更新记录失败", 3L, count);
    }

    @Test
    @Order(0)
    @DisplayName("测试通用mapper根据复合主键列表批量更新记录功能")
    @Transactional
    public void testUpdateByMultiPks() {
        // 准备数据
        genericMapper.insertBatch(batchList(TestMultiPkModel.modelModel));
        List<DataMap> result = genericMapper.selectList(Pops.<DataMap>query().from(TestMultiPkModel.modelModel).eq("`relation`", "test"));
        AssertionErrors.assertEquals("批量更新记录失败", 3, result.size());

        // 执行测试用例
        genericMapper.updateByPks(
                new DataMap(TestMultiPkModel.modelModel).setValue("version", "2.0.0"),
                result
        );

        // 结果断言
        Long count = genericMapper.selectCount(Pops.<DataMap>query().from(TestMultiPkModel.modelModel).eq("`version`", "2.0.0"));
        AssertionErrors.assertEquals("批量更新记录失败", 3L, count);
    }

    @Test
    @Order(0)
    @DisplayName("测试通用mapper根据唯一索引更新单条记录功能")
    @Transactional
    public void testUpdateByUniqueKey() {
        // 准备数据
        genericMapper.insert(one(modelModel));
        Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(modelModel).eq("module", "yihui"));

        // 执行测试用例
        DataMap map = new DataMap(modelModel);
        map.put("id", result.get("id"));
        map.put("version", "2.0.0");
        genericMapper.updateByUniqueKey(map);

        // 结果断言
        result = genericMapper.selectOne(Pops.<DataMap>query().from(modelModel).eq("module", "yihui"));
        AssertionErrors.assertEquals("更新单条记录失败", "2.0.0", result.get("version"));
    }

    @Test
    @Order(0)
    @DisplayName("测试通用mapper根据Wapper批量更新记录功能")
    @Transactional
    public void testUpdateByWrapper() {
        // 准备数据
        genericMapper.insertBatch(batchList(modelModel));
        List<DataMap> result = genericMapper.selectList(Pops.<DataMap>query().from(modelModel).eq("`relation`", "test"));

        // 执行测试用例
        genericMapper.update(
                new DataMap(modelModel).setValue("version", "2.0.0"),
                Pops.<DataMap>query().from(modelModel).in("id", result.get(0).get("id"), result.get(1).get("id"))
        );

        // 结果断言
        Long count = genericMapper.selectCount(Pops.<DataMap>query().from(modelModel).eq("`version`", "2.0.0"));
        AssertionErrors.assertEquals("批量更新记录失败", 2L, count);
    }

    @Test
    @Order(0)
    @DisplayName("测试通用mapper根据Wapper批量更新记录功能")
    @Transactional
    public void testUpdateByWrapperAndEntity() {
        // 准备数据
        genericMapper.insertBatch(batchList(modelModel));
        List<DataMap> result = genericMapper.selectList(Pops.<DataMap>query().from(TestModel.class).eq("`relation`", "test"));

        // 执行测试用例
        genericMapper.update(
                new DataMap(modelModel).setValue("version", "2.0.0"),
                Pops.query(new DataMap(modelModel).setValue("version", "1.0.0")).in("id", result.get(0).get("id"), result.get(1).get("id"))
        );

        // 结果断言
        Long count = genericMapper.selectCount(Pops.<DataMap>query().from(TestModel.class).eq("`version`", "2.0.0"));
        AssertionErrors.assertEquals("批量更新记录失败", 2L, count);
    }

    @Test
    @Order(0)
    @DisplayName("测试通用mapper删除单条记录功能")
    @Transactional
    public void testDeleteByPk() {
        // 准备数据
        genericMapper.insertBatch(batchList(modelModel));

        DataMap result = genericMapper.selectOne(Pops.<DataMap>query().from(TestModel.class).eq("`module`", "yihui1"));
        AssertionErrors.assertNotEquals("删除单条准备数据失败", null, result);

        // 执行测试用例
        genericMapper.deleteByPk(result);

        // 结果断言
        result = genericMapper.selectOne(Pops.<DataMap>query().from(TestModel.class).eq("`module`", "yihui1"));
        AssertionErrors.assertEquals("删除单条成功", null, result);
        Long count = genericMapper.selectCount(Pops.<DataMap>query().from(TestModel.class).apply("1=1"));
        AssertionErrors.assertEquals("删除单条失败", 2L, count);
    }

    @Test
    @Order(0)
    @DisplayName("测试通用mapper根据PK列表批量删除记录功能")
    @Transactional
    public void testDeleteByPks() {
        // 准备数据
        genericMapper.insertBatch(batchList(TestMultiPkModel.modelModel));
        List<DataMap> result = genericMapper.selectList(Pops.<DataMap>query().from(TestMultiPkModel.modelModel).eq("`relation`", "test"));
        AssertionErrors.assertEquals("批量删除记录失败", 3, result.size());

        // 执行测试用例
        result.remove(0);
//        result.forEach(v->v.setModel(TestMultiPkModel.modelModel));
        long count = genericMapper.deleteByPks(result);
        AssertionErrors.assertEquals("批量删除记录失败", 2L, count);

        // 结果断言
        count = genericMapper.selectCount(Pops.<DataMap>query().from(TestMultiPkModel.modelModel).eq("`version`", "1.0.0"));
        AssertionErrors.assertEquals("批量删除记录失败", 1L, count);
    }

    @Test
    @Order(0)
    @DisplayName("测试通用mapper实体条件删除功能")
    @Transactional
    public void testDeleteByEntity() {
        // 准备数据
        genericMapper.insertBatch(batchList(modelModel));
        List<DataMap> result = genericMapper.selectList(Pops.<DataMap>query().from(modelModel).eq("`relation`", "test"));
        AssertionErrors.assertEquals("条件删除准备数据失败", 3, result.size());

        // 执行测试用例
        long count = genericMapper.deleteByEntity(result.get(0));
        AssertionErrors.assertEquals("条件删除准备数据失败", 1L, count);

        // 结果断言
        DataMap oneResult = genericMapper.selectOne(Pops.<DataMap>query().from(modelModel).eq("`module`", "yihui1"));
        AssertionErrors.assertEquals("条件删除准备数据失败", null, oneResult);
        count = genericMapper.selectCount(Pops.<DataMap>query().from(modelModel).eq("`relation`", "test"));
        AssertionErrors.assertEquals("条件删除准备数据失败", 2L, count);
    }

    @Test
    @Order(0)
    @DisplayName("测试通用mapper wrapper条件删除功能")
    @Transactional
    public void testDeleteByWrapper() {
        // 准备数据
        genericMapper.insertBatch(batchList(TestMultiPkModel.modelModel));
        List<DataMap> result = genericMapper.selectList(Pops.<DataMap>query().from(TestMultiPkModel.modelModel).eq("`relation`", "test"));
        AssertionErrors.assertEquals("条件删除准备数据失败", 3, result.size());

        // 执行测试用例
        genericMapper.delete(Pops.<DataMap>query().from(TestMultiPkModel.modelModel).eq("`module`", "yihui1"));

        // 结果断言
        DataMap oneResult = genericMapper.selectOne(Pops.<DataMap>query().from(TestMultiPkModel.modelModel).eq("`module`", "yihui1"));
        AssertionErrors.assertEquals("条件删除准备数据失败", null, oneResult);
        Long count = genericMapper.selectCount(Pops.<DataMap>query().from(TestMultiPkModel.modelModel).eq("`relation`", "test"));
        AssertionErrors.assertEquals("条件删除准备数据失败", 2L, count);
    }

    @Test
    @Order(1)
    @DisplayName("测试通用mapper根据pk查询单条记录功能")
    @Transactional
    public void testSelectByPk() {
        // 准备数据
        genericMapper.insert(one(modelModel));
        DataMap oneResult = genericMapper.selectOne(Pops.<DataMap>query().from(modelModel).eq("`module`", "yihui"));

        // 执行测试用例
        DataMap result = genericMapper.selectByPk(oneResult);

        // 结果断言
        AssertionErrors.assertEquals("查询单条记录失败", "yihui", result.get("module"));
    }

    @Test
    @Order(1)
    @DisplayName("测试通用mapper根据pk列表查询记录列表功能")
    @Transactional
    public void selectListByPks() {
        // 准备数据
        genericMapper.insertBatch(batchList(modelModel));
        List<DataMap> result = genericMapper.selectList(Pops.<DataMap>query().from(modelModel).eq("`relation`", "test"));

        // 执行测试用例
//        result.forEach(v->v.setModel(modelModel));
        result = genericMapper.selectListByPks(result);

        // 结果断言
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第一条记录未插入成功", "yihui1", result.get(0).get("module"));
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第二条记录未插入成功", "yihui2", result.get(1).get("module"));
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第三条记录未插入成功", "yihui3", result.get(2).get("module"));
    }

    @Test
    @Order(1)
    @DisplayName("测试通用mapper根据复合pk列表查询记录列表功能")
    @Transactional
    public void selectListByMultiPks() {
        // 准备数据
        genericMapper.insertBatch(batchList(TestMultiPkModel.modelModel));
        List<DataMap> result = genericMapper.selectList(Pops.<DataMap>query().from(TestMultiPkModel.modelModel).eq("`relation`", "test"));

        // 执行测试用例
//        result.forEach(v->v.setModel(TestMultiPkModel.modelModel));
        result = genericMapper.selectListByPks(result);

        // 结果断言
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第一条记录未插入成功", "yihui1", result.get(0).get("module"));
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第二条记录未插入成功", "yihui2", result.get(1).get("module"));
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第三条记录未插入成功", "yihui3", result.get(2).get("module"));
    }

    @Test
    @Order(1)
    @DisplayName("测试通用mapper根据wrapper查询单条记录功能")
    @Transactional
    public void testSelectOne() {
        // 准备数据
        genericMapper.insert(one(modelModel));

        // 执行测试用例
        DataMap result = genericMapper.selectOne(Pops.<DataMap>query().from(modelModel).eq("`module`", "yihui"));

        // 结果断言
        AssertionErrors.assertEquals("查询单条记录失败", "yihui", result.get("module"));
    }

    @Test
    @Order(1)
    @DisplayName("测试通用mapper根据map查询单条记录功能")
    @Transactional
    public void testSelectOneByEntity() {
        // 准备数据
        genericMapper.insert(one(TestMultiPkModel.modelModel));

        // 执行测试用例
        DataMap result = genericMapper.selectOneByEntity(one(TestMultiPkModel.modelModel));

        // 结果断言
        AssertionErrors.assertEquals("查询单条记录失败", "yihui", result.get("module"));
    }

    @Test
    @Order(1)
    @DisplayName("测试通用mapper根据map查询多条记录功能")
    @Transactional
    public void selectListByEntity() {
        // 准备数据
        genericMapper.insertBatch(batchList(TestMultiPkModel.modelModel));
        List<DataMap> result = genericMapper.selectList(Pops.<DataMap>query().from(TestMultiPkModel.modelModel).eq("`relation`", "test"));

        // 执行测试用例
        result = genericMapper.selectListByEntity(result.get(0));

        // 结果断言
        AssertionErrors.assertEquals("根据entity查询记录列表失败", 1, result.size());
        AssertionErrors.assertEquals("根据entity查询记录列表失败", "yihui1", result.get(0).get("module"));
    }

    @Test
    @Order(1)
    @DisplayName("测试通用mapper根据wrapper查询多条记录功能")
    @Transactional
    public void selectList() {
        // 准备数据
        genericMapper.insertBatch(batchList(modelModel));

        // 执行测试用例
        List<DataMap> result = genericMapper.selectList(Pops.<DataMap>query().from(modelModel).eq("`relation`", "test"));

        // 结果断言
        AssertionErrors.assertEquals("根据wrapper查询记录列表失败", 3, result.size());
        AssertionErrors.assertEquals("根据wrapper查询记录列表失败", "yihui1", result.get(0).get("module"));
    }

    @Test
    @Order(1)
    @DisplayName("测试通用mapper根据wrapper查询多条记录排序功能")
    @Transactional
    public void selectOrderList() {
        // 准备数据
        List<DataMap> params = randomOrderBatchList(TestMultiPkModel.modelModel);
        genericMapper.insertBatch(params);
        DataMap one = genericMapper.selectOneByEntity(params.get(0));

        // 执行测试用例
        List<DataMap> result = genericMapper.selectList(Pops.<DataMap>query().from(TestMultiPkModel.modelModel).eq("`relation`", "test").orderByAsc("module"));

        // 结果断言
        AssertionErrors.assertEquals("根据wrapper查询记录列表排序失败", 3, result.size());
        AssertionErrors.assertEquals("根据wrapper查询记录列表排序失败，第一条数据错误", "yihui1", result.get(0).get("module"));
        AssertionErrors.assertEquals("根据wrapper查询记录列表排序失败，第二条数据错误", "yihui2", result.get(1).get("module"));
        AssertionErrors.assertEquals("根据wrapper查询记录列表排序失败，第三条数据错误", "yihui3", result.get(2).get("module"));
        AssertionErrors.assertEquals("根据wrapper查询记录列表排序失败，排序错误", result.get(2).get("id"), one.get("id"));
    }

    @Test
    @Order(1)
    @DisplayName("测试通用mapper查询匹配记录数量功能")
    @Transactional
    public void testSelectCount() {
        // 准备数据
        genericMapper.insertBatch(batchList(modelModel));

        // 执行测试用例
        Long count = genericMapper.selectCount(Pops.<DataMap>query().from(modelModel).eq("`relation`", "test"));

        // 结果断言
        AssertionErrors.assertEquals("查询匹配记录数量失败", 3L, count);
    }

    @Test
    @Order(1)
    @DisplayName("测试通用mapper根据wrapper分页查询多条记录功能")
    @Transactional
    public void testSelectPage() {
        // 准备数据
        genericMapper.insertBatch(randomOrderBatchList(modelModel));

        // 执行测试用例
        Pagination<DataMap> result = genericMapper.selectPage(
                new Pagination<DataMap>().setCurrentPage(1).setSize(2L),
                Pops.<DataMap>query().from(modelModel).eq("`relation`", "test")
        );
        Pagination<DataMap> orderResult = genericMapper.selectPage(
                new Pagination<DataMap>().orderBy(SortDirectionEnum.ASC, TestModel::getModule).setCurrentPage(2).setSize(2L),
                Pops.<DataMap>query().from(modelModel).eq("`relation`", "test")
        );

        // 结果断言
        AssertionErrors.assertEquals("根据wrapper分页查询记录列表排序失败，排序错误",
                result.getContent().get(0).get("id"), orderResult.getContent().get(0).get("id"));
    }

    @Test
    @Order(1)
    @DisplayName("测试通用mapper根据wrapper分页查询多条记录功能")
    @Transactional
    public void testSelectPageForWrapOrderBy() {
        // 准备数据
        genericMapper.insertBatch(randomOrderBatchList(modelModel));

        // 执行测试用例
        Pagination<DataMap> result = genericMapper.selectPage(
                new Pagination<DataMap>().setCurrentPage(1).setSize(2L),
                Pops.<DataMap>query().from(modelModel).eq("`relation`", "test")
        );
        Pagination<DataMap> orderResult = genericMapper.selectPage(
                new Pagination<DataMap>().setCurrentPage(2).setSize(2L),
                Pops.<DataMap>query().from(modelModel).eq("`relation`", "test").orderByAsc("module")
        );

        // 结果断言
        AssertionErrors.assertEquals("根据wrapper分页查询记录列表排序失败，排序错误",
                result.getContent().get(0).get("id"), orderResult.getContent().get(0).get("id"));
    }

}
