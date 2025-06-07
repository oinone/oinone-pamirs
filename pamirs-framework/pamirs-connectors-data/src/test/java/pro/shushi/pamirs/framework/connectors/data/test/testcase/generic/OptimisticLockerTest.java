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
import pro.shushi.pamirs.framework.connectors.data.test.mock.model.TestOptimisticLockerModel;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;

import javax.annotation.Resource;
import java.util.List;

/**
 * 乐观锁测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@DisplayName("乐观锁测试")
public class OptimisticLockerTest extends AbstractBaseTest {

    private final static String modelModel = TestOptimisticLockerModel.modelModel;

    @Resource
    private GenericMapper genericMapper;

    @Test
    @Order(0)
    @DisplayName("测试通用mapper根据主键更新单条记录功能")
    @Transactional
    public void testUpdateByPk() {
        // 准备数据
        genericMapper.insert(one(modelModel));
        DataMap result = genericMapper.selectOne(Pops.<DataMap>query().from(modelModel).eq("module", "yihui"));

        // 执行测试用例
        result.put("version", "2.0.0");
        genericMapper.updateByPk(result);
        result.setValue("optVersion", 0).put("version", "3.0.0");
        genericMapper.updateByPk(result);

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
        result.forEach(v -> v.put("optVersion", 0));
        genericMapper.updateByPks(
                new DataMap(modelModel).setValue("version", "3.0.0"),
                result
        );

        // 结果断言
        Long count = genericMapper.selectCount(Pops.<DataMap>query().from(modelModel).eq("`version`", "2.0.0"));
        AssertionErrors.assertEquals("批量更新记录失败", 3L, count);
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
                new DataMap(modelModel).setValue("optVersion", 0).setValue("version", "2.0.0"),
                Pops.<DataMap>query().from(modelModel).in("id", result.get(0).get("id"), result.get(1).get("id"))
        );
        genericMapper.update(
                new DataMap(modelModel).setValue("optVersion", 0).setValue("version", "3.0.0"),
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
                new DataMap(modelModel).setValue("optVersion", 0).setValue("version", "2.0.0"),
                Pops.query(new DataMap(modelModel).setValue("version", "1.0.0")).in("id", result.get(0).get("id"), result.get(1).get("id"))
        );
        genericMapper.update(
                new DataMap(modelModel).setValue("optVersion", 0).setValue("version", "3.0.0"),
                Pops.query(new DataMap(modelModel).setValue("version", "1.0.0")).in("id", result.get(0).get("id"), result.get(1).get("id"))
        );

        // 结果断言
        Long count = genericMapper.selectCount(Pops.<DataMap>query().from(TestModel.class).eq("`version`", "2.0.0"));
        AssertionErrors.assertEquals("批量更新记录失败", 2L, count);
    }

}
