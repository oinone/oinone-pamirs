package pro.shushi.pamirs.framework.connectors.data.test.testcase.transaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.AssertionErrors;
import org.springframework.transaction.annotation.Propagation;
import pro.shushi.pamirs.AbstractBaseTest;
import pro.shushi.pamirs.framework.connectors.data.tx.transaction.Tx;
import pro.shushi.pamirs.meta.api.dto.config.TxConfig;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.framework.connectors.data.mapper.GenericMapper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.test.mock.model.ds.TestFixedDsModel;
import pro.shushi.pamirs.framework.connectors.data.test.mock.model.ds.TestMultiDsModel;
import pro.shushi.pamirs.framework.connectors.data.test.mock.service.TestTransactionService;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 事务测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@Slf4j
@DisplayName("事务测试")
public class TransactionTest extends AbstractBaseTest {

    @Resource
    private GenericMapper genericMapper;

    @Resource
    private TestTransactionService testTransactionService;

    @Test
    @Order(0)
    @DisplayName("测试单一数据源Spring方法声明式事务提交功能")
    public void testInsertWithTransactionCommit(){
        // 执行测试用例
        try{
            DataMap one = one(TestFixedDsModel.modelModel);
            testTransactionService.insertWithSpringTransactionCommit(one);
        }catch (Exception e){
            log.error("事务回滚", e);
        }finally {
            // 结果断言
            Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(TestFixedDsModel.modelModel).eq("`relation`", "test"));
            AssertionErrors.assertNotEquals("插入并查询单条记录失败", null, result);

            result = genericMapper.selectOne(Pops.<DataMap>query().from(TestMultiDsModel.modelModel).eq("`relation`", "test"));
            AssertionErrors.assertEquals("插入并查询单条记录失败", null, result);
        }
    }

    @Test
    @Order(0)
    @DisplayName("测试单一数据源Spring声明式事务功能")
    public void testInsertWithSpringTransaction(){
        // 执行测试用例
        try{
            DataMap one = one(TestFixedDsModel.modelModel);
            testTransactionService.insertWithSpringTransaction(one);
        }catch (Exception e){
            log.error("事务回滚", e);
        }finally {
            // 结果断言
            Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(TestFixedDsModel.modelModel).eq("`relation`", "test"));
            AssertionErrors.assertEquals("插入并查询单条记录失败", null, result);

            result = genericMapper.selectOne(Pops.<DataMap>query().from(TestMultiDsModel.modelModel).eq("`relation`", "test"));
            AssertionErrors.assertEquals("插入并查询单条记录失败", null, result);
        }
    }

    @Test
    @Order(0)
    @DisplayName("测试单一数据源在tx中Spring声明式事务功能")
    public void testInsertWithSpringTransactionInTx(){
        // 执行测试用例
        try{
            Tx.build(new TxConfig().setPropagation(Propagation.REQUIRES_NEW.value())).executeWithoutResult(transactionStatus -> {
                DataMap one = one(TestFixedDsModel.modelModel);
                testTransactionService.insertWithSpringTransaction(one);
            });
        }catch (Exception e){
            log.error("事务回滚", e);
        }finally {
            // 结果断言
            Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(TestFixedDsModel.modelModel).eq("`relation`", "test"));
            AssertionErrors.assertEquals("插入并查询单条记录失败", null, result);

            result = genericMapper.selectOne(Pops.<DataMap>query().from(TestMultiDsModel.modelModel).eq("`relation`", "test"));
            AssertionErrors.assertEquals("插入并查询单条记录失败", null, result);
        }
    }

    @Test
    @Order(0)
    @DisplayName("测试单一数据源Spring编程式事务功能")
    public void testInsertWithSpringTemplateTransaction(){
        // 执行测试用例
        try{
            DataMap one = one(TestFixedDsModel.modelModel);
            testTransactionService.insertWithSpringTemplateTransaction(one);
        }catch (Exception e){
            log.error("事务回滚", e);
        }finally {
            // 结果断言
            Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(TestFixedDsModel.modelModel).eq("`relation`", "test"));
            AssertionErrors.assertEquals("插入并查询单条记录失败", null, result);

            result = genericMapper.selectOne(Pops.<DataMap>query().from(TestMultiDsModel.modelModel).eq("`relation`", "test"));
            AssertionErrors.assertEquals("插入并查询单条记录失败", null, result);
        }
    }

    @Test
    @Order(0)
    @DisplayName("测试单一数据源Spring声明式新嵌套事务功能")
    public void testInsertWithTransactionInnerNewService(){
        // 执行测试用例
        try{
            DataMap one = one(TestFixedDsModel.modelModel);
            DataMap ds0 = one(TestFixedDsModel.modelModel);
            ds0.setValue("module", "yihui1");
            testTransactionService.insertWithTransactionInnerNewService(one, ds0);
        }catch (Exception e){
            log.error("事务回滚", e);
        }finally {
            // 结果断言
            Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(TestFixedDsModel.modelModel).eq("`module`", "yihui"));
            AssertionErrors.assertNotEquals("插入并查询单条记录失败", null, result);

            result = genericMapper.selectOne(Pops.<DataMap>query().from(TestFixedDsModel.modelModel).eq("`module`", "yihui1"));
            AssertionErrors.assertNotEquals("插入并查询单条记录失败", null, result);
        }
    }

    @Test
    @Order(0)
    @DisplayName("测试单一数据源Spring声明式新嵌套内层回滚事务功能")
    public void testInsertWithTransactionInnerNewServiceRollback(){
        // 执行测试用例
        try{
            DataMap one = one(TestFixedDsModel.modelModel);
            DataMap ds0 = one(TestFixedDsModel.modelModel);
            ds0.setValue("module", "yihui1");
            testTransactionService.insertWithTransactionInnerNewServiceRollback(one, ds0);
        }catch (Exception e){
            log.error("事务回滚", e);
        }finally {
            // 结果断言
            Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(TestFixedDsModel.modelModel).eq("`module`", "yihui"));
            AssertionErrors.assertNotEquals("插入并查询单条记录失败", null, result);

            result = genericMapper.selectOne(Pops.<DataMap>query().from(TestFixedDsModel.modelModel).eq("`module`", "yihui1"));
            AssertionErrors.assertEquals("插入并查询单条记录失败", null, result);
        }
    }

    @Test
    @Order(0)
    @DisplayName("测试单一数据源Spring声明式join嵌套事务功能")
    public void testInsertWithTransactionInnerJoinService(){
        // 执行测试用例
        try{
            DataMap one = one(TestFixedDsModel.modelModel);
            DataMap ds0 = one(TestFixedDsModel.modelModel);
            ds0.setValue("module", "yihui1");
            testTransactionService.insertWithTransactionInnerJoinService(one, ds0);
        }catch (Exception e){
            log.error("事务回滚", e);
        }finally {
            // 结果断言
            Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(TestFixedDsModel.modelModel).eq("`module`", "yihui"));
            AssertionErrors.assertNotEquals("插入并查询单条记录失败", null, result);

            result = genericMapper.selectOne(Pops.<DataMap>query().from(TestFixedDsModel.modelModel).eq("`module`", "yihui1"));
            AssertionErrors.assertNotEquals("插入并查询单条记录失败", null, result);
        }
    }

    @Test
    @Order(0)
    @DisplayName("测试单一数据源Spring声明式join嵌套内层回滚事务功能")
    public void testInsertWithTransactionInnerJoinServiceRollback(){
        // 执行测试用例
        try{
            DataMap one = one(TestFixedDsModel.modelModel);
            DataMap ds0 = one(TestFixedDsModel.modelModel);
            ds0.setValue("module", "yihui1");
            testTransactionService.insertWithTransactionInnerJoinServiceRollback(one, ds0);
        }catch (Exception e){
            log.error("事务回滚", e);
        }finally {
            // 结果断言
            Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(TestFixedDsModel.modelModel).eq("`module`", "yihui"));
            AssertionErrors.assertEquals("插入并查询单条记录失败", null, result);

            result = genericMapper.selectOne(Pops.<DataMap>query().from(TestFixedDsModel.modelModel).eq("`module`", "yihui1"));
            AssertionErrors.assertEquals("插入并查询单条记录失败", null, result);
        }
    }

    @Test
    @Order(0)
    @DisplayName("测试单一数据源Spring声明式nested嵌套内层回滚事务功能")
    public void testInsertWithTransactionNestedJoinServiceRollback(){
        // 执行测试用例
        try{
            DataMap one = one(TestFixedDsModel.modelModel);
            DataMap ds0 = one(TestFixedDsModel.modelModel);
            ds0.setValue("module", "yihui1");
            testTransactionService.insertWithTransactionInnerNestedServiceRollback(one, ds0);
        }catch (Exception e){
            log.error("事务回滚", e);
        }finally {
            // 结果断言
            Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(TestFixedDsModel.modelModel).eq("`module`", "yihui"));
            AssertionErrors.assertNotEquals("插入并查询单条记录失败", null, result);

            result = genericMapper.selectOne(Pops.<DataMap>query().from(TestFixedDsModel.modelModel).eq("`module`", "yihui1"));
            AssertionErrors.assertEquals("插入并查询单条记录失败", null, result);
        }
    }

}
