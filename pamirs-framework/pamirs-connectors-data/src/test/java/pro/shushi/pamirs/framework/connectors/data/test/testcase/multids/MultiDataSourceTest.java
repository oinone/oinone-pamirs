package pro.shushi.pamirs.framework.connectors.data.test.testcase.multids;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.AssertionErrors;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.AbstractBaseTest;
import pro.shushi.pamirs.framework.connectors.data.mapper.GenericMapper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.test.mock.model.ds.TestFixedDsModel;
import pro.shushi.pamirs.framework.connectors.data.test.mock.model.ds.TestMultiDsModel;
import pro.shushi.pamirs.framework.connectors.data.test.mock.service.TestMultiDsService;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.api.session.PamirsSession;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 多数据源测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@DisplayName("多数据源测试")
public class MultiDataSourceTest extends AbstractBaseTest {

    @Resource
    private GenericMapper genericMapper;

    @Resource
    private TestMultiDsService testMultiDsService;

    @Test
    @Order(0)
    @DisplayName("测试模型指定数据源功能")
    @Transactional
    public void testModelFixedDs() {
        // 执行测试用例
        DataMap one = one(TestFixedDsModel.modelModel);
        genericMapper.insert(one);
        AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, one.get("id"));

        // 结果断言
        Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(TestFixedDsModel.modelModel).eq("`relation`", "test"));
        AssertionErrors.assertEquals("插入并查询单条记录失败", "yihui", result.get("module"));

        result = genericMapper.selectOne(Pops.<DataMap>query().from(TestMultiDsModel.modelModel).eq("`relation`", "test"));
        AssertionErrors.assertEquals("插入并查询单条记录失败", null, result);
    }

    @Test
    @Order(0)
    @DisplayName("测试模型指定数据源功能")
    @Transactional
    public void testMethodFixedDs() {
        // 执行测试用例
        // ds0
        DataMap one = one(TestFixedDsModel.modelModel);
        testMultiDsService.insertDs0(one);
        AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, one.get("id"));

        // base
        one = one(TestMultiDsModel.modelModel);
        genericMapper.insert(one);
        AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, one.get("id"));

        // 结果断言
        Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(TestFixedDsModel.modelModel).eq("`relation`", "test"));
        AssertionErrors.assertEquals("插入并查询单条记录失败", "yihui", result.get("module"));

        result = genericMapper.selectOne(Pops.<DataMap>query().from(TestMultiDsModel.modelModel).eq("`relation`", "test"));
        AssertionErrors.assertEquals("插入并查询单条记录失败", "yihui", result.get("module"));
    }

    @Test
    @Order(0)
    @DisplayName("测试动态表达式指定数据源功能")
    @Transactional
    public void testDynamicExpressionDs() {
        // 准备数据
        PamirsSession.getRequestVariables().setParameterMap(new HashMap<>());
        PamirsSession.getRequestVariables().getParameterMap().put("test", new String[]{"s0"});

        // 执行测试用例
        // ds0
        DataMap one = one(TestMultiDsModel.modelModel);
        testMultiDsService.insertDynamicDsKey(one);
        AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, one.get("id"));

        // base
        one = one(TestMultiDsModel.modelModel);
        genericMapper.insert(one);
        AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, one.get("id"));

        // 结果断言
        testMultiDsService.selectDynamicExpressionDs();
        Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(TestMultiDsModel.modelModel).eq("`relation`", "test"));
        AssertionErrors.assertEquals("插入并查询单条记录失败", "yihui", result.get("module"));
    }

}
