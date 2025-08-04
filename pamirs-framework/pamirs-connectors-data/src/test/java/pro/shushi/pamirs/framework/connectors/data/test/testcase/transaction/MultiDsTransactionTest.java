package pro.shushi.pamirs.framework.connectors.data.test.testcase.transaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.AssertionErrors;
import pro.shushi.pamirs.AbstractBaseTest;
import pro.shushi.pamirs.framework.connectors.data.mapper.GenericMapper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.test.mock.model.ds.TestFixedDsModel;
import pro.shushi.pamirs.framework.connectors.data.test.mock.model.ds.TestMultiDsModel;
import pro.shushi.pamirs.framework.connectors.data.test.mock.service.TestTransactionService;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;

import jakarta.annotation.Resource;
import java.util.Map;

/**
 * 事务测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@Slf4j
@DisplayName("多数据源事务测试")
public class MultiDsTransactionTest extends AbstractBaseTest {

    @Resource
    private GenericMapper genericMapper;

    @Resource
    private TestTransactionService testTransactionService;

    @Test
    @Order(0)
    @DisplayName("测试多数据源Spring声明式新嵌套事务功能")
    public void testInsertWithTransactionInnerNewService() {
        // 执行测试用例
        try {
            DataMap one = one(TestMultiDsModel.modelModel);
            DataMap ds0 = one(TestFixedDsModel.modelModel);
            ds0.setValue("module", "yihui1");
            testTransactionService.insertWithTransactionInnerNewService(one, ds0);
        } catch (Exception e) {
            log.error("事务回滚", e);
        } finally {
            // 结果断言
            Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(TestMultiDsModel.modelModel).eq("`module`", "yihui"));
            AssertionErrors.assertNotEquals("插入并查询单条记录失败", null, result);

            result = genericMapper.selectOne(Pops.<DataMap>query().from(TestFixedDsModel.modelModel).eq("`module`", "yihui1"));
            AssertionErrors.assertNotEquals("插入并查询单条记录失败", null, result);
        }
    }

    @Test
    @Order(0)
    @DisplayName("测试多数据源Spring声明式新嵌套内层回滚事务功能")
    public void testInsertWithTransactionInnerNewServiceRollback() {
        // 执行测试用例
        try {
            DataMap one = one(TestMultiDsModel.modelModel);
            DataMap ds0 = one(TestFixedDsModel.modelModel);
            ds0.setValue("module", "yihui1");
            testTransactionService.insertWithTransactionInnerNewServiceRollback(one, ds0);
        } catch (Exception e) {
            log.error("事务回滚", e);
        } finally {
            // 结果断言
            Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(TestMultiDsModel.modelModel).eq("`module`", "yihui"));
            AssertionErrors.assertNotEquals("插入并查询单条记录失败", null, result);

            result = genericMapper.selectOne(Pops.<DataMap>query().from(TestFixedDsModel.modelModel).eq("`module`", "yihui1"));
            AssertionErrors.assertEquals("插入并查询单条记录失败", null, result);
        }
    }

    @Test
    @Order(0)
    @DisplayName("测试多数据源Spring声明式join嵌套事务功能")
    public void testInsertWithTransactionInnerJoinService() {
        // 执行测试用例
        try {
            DataMap one = one(TestFixedDsModel.modelModel);
            DataMap ds0 = one(TestMultiDsModel.modelModel);
            ds0.setValue("module", "yihui1");
            testTransactionService.insertWithTransactionInnerJoinService(one, ds0);
        } catch (Exception e) {
            log.error("事务回滚", e);
        } finally {
            // 结果断言
            Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(TestFixedDsModel.modelModel).eq("`module`", "yihui"));
            AssertionErrors.assertNotEquals("插入并查询单条记录失败", null, result);

            result = genericMapper.selectOne(Pops.<DataMap>query().from(TestMultiDsModel.modelModel).eq("`module`", "yihui1"));
            AssertionErrors.assertNotEquals("插入并查询单条记录失败", null, result);
        }
    }

    @Test
    @Order(0)
    @DisplayName("测试多数据源Spring声明式join嵌套内层回滚事务功能")
    public void testInsertWithTransactionInnerJoinServiceRollback() {
        // 执行测试用例
        try {
            DataMap one = one(TestFixedDsModel.modelModel);
            DataMap ds0 = one(TestMultiDsModel.modelModel);
            ds0.setValue("module", "yihui1");
            testTransactionService.insertWithTransactionInnerJoinServiceRollback(one, ds0);
        } catch (Exception e) {
            log.error("事务回滚", e);
        } finally {
            // 结果断言
            Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(TestFixedDsModel.modelModel).eq("`module`", "yihui"));
            AssertionErrors.assertEquals("插入并查询单条记录失败", null, result);

            result = genericMapper.selectOne(Pops.<DataMap>query().from(TestMultiDsModel.modelModel).eq("`module`", "yihui1"));
            AssertionErrors.assertEquals("插入并查询单条记录失败", null, result);
        }
    }

    @Test
    @Order(1)
    @DisplayName("测试多数据源Spring声明式join嵌套外层回滚事务功能")
    public void testInsertWithTransactionOutterJoinServiceRollback() {
        // 执行测试用例
        try {
            DataMap one = one(TestFixedDsModel.modelModel);
            DataMap ds0 = one(TestMultiDsModel.modelModel);
            ds0.setValue("module", "yihui1");
            testTransactionService.insertWithTransactionOutterJoinServiceRollback(one, ds0);
        } catch (Exception e) {
            log.error("事务回滚", e);
        } finally {
            // 结果断言
            Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(TestFixedDsModel.modelModel).eq("`module`", "yihui"));
            AssertionErrors.assertEquals("插入并查询单条记录失败", null, result);

            result = genericMapper.selectOne(Pops.<DataMap>query().from(TestMultiDsModel.modelModel).eq("`module`", "yihui1"));
            AssertionErrors.assertEquals("插入并查询单条记录失败", null, result);
        }
    }

}
