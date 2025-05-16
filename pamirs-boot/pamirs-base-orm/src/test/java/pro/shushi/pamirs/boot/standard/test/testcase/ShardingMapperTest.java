package pro.shushi.pamirs.boot.standard.test.testcase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.AssertionErrors;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.AbstractBaseTest;
import pro.shushi.pamirs.framework.connectors.data.mapper.GenericMapper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.test.data.dependency1.model.TestShardingModel;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 分库分表mapper测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@DisplayName("分库分表mapper测试")
public class ShardingMapperTest extends AbstractBaseTest {

    private final static String modelModel = TestShardingModel.MODEL_MODEL;

    @Resource
    private GenericMapper genericMapper;

    @Test
    @Order(0)
    @DisplayName("测试通用mapper无事务插入单条记录功能")
    public void testInsertOneWithoutTx() {
        // 执行测试用例
        DataMap one = data();
        genericMapper.insert(one);
        AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, one.get("id"));

        // 结果断言
        Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(modelModel).eq("`create_uid`", 100L));
        AssertionErrors.assertEquals("插入并查询单条记录失败", 100L, result.get("createUid"));
        genericMapper.deleteByEntity(one);
    }

    @Test
    @Order(0)
    @DisplayName("测试通用mapper插入单条记录功能")
    @Transactional
    public void testInsertOne() {
        // 执行测试用例
        DataMap one = data();
        genericMapper.insert(one);
        AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, one.get("id"));

        // 结果断言
        Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(modelModel).eq("`create_uid`", 100L));
        AssertionErrors.assertEquals("插入并查询单条记录失败", 100L, result.get("createUid"));
    }

    @Test
    @Order(0)
    @DisplayName("测试通用mapper批量插入记录功能")
    @Transactional
    public void testInsertBatch() {
        // 执行测试用例
        List<DataMap> dataList = dataList();
        genericMapper.insertBatch(dataList);
        dataList.forEach(v -> AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, v.get("id")));

        // 结果断言
        List<DataMap> result = genericMapper.selectList(Pops.<DataMap>query().from(modelModel).eq("`write_uid`", 100L));
        System.out.println(result.get(0).get("id"));
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第一条记录未插入成功", "f0", result.get(0).get("module"));
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第二条记录未插入成功", "f16", result.get(1).get("module"));
        AssertionErrors.assertEquals("根据ID列表查询记录列表失败,查询第三条记录未插入成功", "f32", result.get(2).get("module"));
    }

    protected DataMap data() {
        // 准备数据
        return new DataMap(modelModel).setValue("module", "f1").setValue("createUid", 100L).setValue("writeUid", 100L);
    }

    protected List<DataMap> dataList() {
        List<DataMap> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(new DataMap(modelModel).setValue("module", "f" + i).setValue("createUid", (i % 50)).setValue("writeUid", 100L));
        }
        return list;
    }

    @SuppressWarnings("unused")
    protected List<DataMap> reverseDataList() {
        List<DataMap> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(new DataMap(modelModel).setValue("field", "f" + (99 - i)).setValue("createUid", ((99 - i) % 50)).setValue("writeUid", 100L));
        }
        return list;
    }

}
