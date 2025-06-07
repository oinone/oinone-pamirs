package pro.shushi.pamirs.framework.connectors.data.test.testcase.generic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.AssertionErrors;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.AbstractBaseTest;
import pro.shushi.pamirs.framework.connectors.data.mapper.GenericMapper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.test.mock.model.TestDeleteModel;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;

import javax.annotation.Resource;
import java.util.List;

/**
 * 逻辑删除测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@DisplayName("逻辑删除测试")
public class LogicDeleteTest extends AbstractBaseTest {

    private final static String modelModel = TestDeleteModel.modelModel;

    @Resource
    private GenericMapper genericMapper;

    @Test
    @Order(0)
    @DisplayName("测试通用mapper删除单条记录功能")
    @Transactional
    public void testDeleteByPk() {
        // 准备数据
        genericMapper.insertBatch(batchList(modelModel));

        DataMap result = genericMapper.selectOne(Pops.<DataMap>query().from(TestDeleteModel.class).eq("`module`", "yihui1"));
        AssertionErrors.assertNotEquals("删除单条准备数据失败", null, result);

        // 执行测试用例
        genericMapper.deleteByPk(result);

        // 结果断言
        result = genericMapper.selectOne(Pops.<DataMap>query().from(TestDeleteModel.class).eq("`module`", "yihui1"));
        AssertionErrors.assertEquals("删除单条成功", null, result);
        Long count = genericMapper.selectCount(Pops.<DataMap>query().from(TestDeleteModel.class));
        AssertionErrors.assertEquals("删除单条失败", 2L, count);
    }

    @Test
    @Order(0)
    @DisplayName("测试通用mapper根据PK列表批量删除记录功能")
    @Transactional
    public void testDeleteByPks() {
        // 准备数据
        genericMapper.insertBatch(batchList(modelModel));
        List<DataMap> result = genericMapper.selectList(Pops.<DataMap>query().from(modelModel).eq("`relation`", "test"));
        AssertionErrors.assertEquals("批量删除记录失败", 3, result.size());

        // 执行测试用例
        result.remove(0);
        long count = genericMapper.deleteByPks(result);
        AssertionErrors.assertEquals("批量删除记录失败", 2L, count);

        // 结果断言
        count = genericMapper.selectCount(Pops.<DataMap>query().from(modelModel).eq("`version`", "1.0.0"));
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
        genericMapper.insertBatch(batchList(modelModel));
        List<DataMap> result = genericMapper.selectList(Pops.<DataMap>query().from(modelModel).eq("`relation`", "test"));
        AssertionErrors.assertEquals("条件删除准备数据失败", 3, result.size());

        // 执行测试用例
        genericMapper.delete(Pops.<DataMap>query().from(modelModel).eq("`module`", "yihui1"));

        // 结果断言
        DataMap oneResult = genericMapper.selectOne(Pops.<DataMap>query().from(modelModel).eq("`module`", "yihui1"));
        AssertionErrors.assertEquals("条件删除准备数据失败", null, oneResult);
        Long count = genericMapper.selectCount(Pops.<DataMap>query().from(modelModel).eq("`relation`", "test"));
        AssertionErrors.assertEquals("条件删除准备数据失败", 2L, count);
    }

}
