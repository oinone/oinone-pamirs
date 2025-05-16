package pro.shushi.pamirs.framework.connectors.data.test.testcase.transaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.AssertionErrors;
import pro.shushi.pamirs.AbstractBaseTest;
import pro.shushi.pamirs.framework.connectors.data.mapper.GenericMapper;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.test.mock.model.ds.TestFixedDsModel;
import pro.shushi.pamirs.framework.connectors.data.test.mock.model.ds.TestMultiDsModel;
import pro.shushi.pamirs.framework.connectors.data.test.mock.service.pamirs.TestPamirsTransactionService;
import pro.shushi.pamirs.framework.connectors.data.test.mock.service.pamirs.TestTypePamirsTransactionService;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Pamirs事务测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@Slf4j
@DisplayName("Pamirs事务测试")
public class PamirsTransactionTest extends AbstractBaseTest {

    @Resource
    private GenericMapper genericMapper;

    @Resource
    private TestPamirsTransactionService testPamirsTransactionService;

    @Resource
    private TestTypePamirsTransactionService testTypePamirsTransactionService;

    @Test
    @Order(0)
    @DisplayName("测试单一数据源Pamirs方法声明式事务提交功能")
    public void testInsertWithTransactionCommit(){
        // 执行测试用例
        try{
            DataMap one = one(TestFixedDsModel.modelModel);
            testPamirsTransactionService.insertWithSpringTransactionCommit(one);
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
    @DisplayName("测试单一数据源Pamirs方法声明式事务功能")
    public void testInsertWithTransaction(){
        // 执行测试用例
        try{
            DataMap one = one(TestFixedDsModel.modelModel);
            testPamirsTransactionService.insertWithSpringTransaction(one);
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
    @DisplayName("测试单一数据源Pamirs类声明式事务回滚功能")
    public void testInsertWithTypeTransaction(){
        // 执行测试用例
        try{
            DataMap one = one(TestFixedDsModel.modelModel);
            testTypePamirsTransactionService.insertWithSpringTransaction(one);
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
    @DisplayName("测试单一数据源Pamirs类声明式事务提交功能")
    public void testInsertWithTypeTransactionCommit(){
        // 执行测试用例
        try{
            DataMap one = one(TestFixedDsModel.modelModel);
            testTypePamirsTransactionService.insertWithSpringTransactionCommit(one);
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
    @DisplayName("测试单一数据源Pamirs编程式事务功能")
    public void testInsertWithTemplateTransaction(){
        // 执行测试用例
        try{
            DataMap one = one(TestFixedDsModel.modelModel);
            testPamirsTransactionService.insertWithSpringTemplateTransaction(one);
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
    @DisplayName("测试单一数据源Pamirs编程式事务build功能")
    public void testInsertWithTemplateBuildTransaction(){
        // 执行测试用例
        try{
            DataMap one = one(TestFixedDsModel.modelModel);
            testPamirsTransactionService.insertWithSpringTemplateBuildTransaction(one);
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

}
