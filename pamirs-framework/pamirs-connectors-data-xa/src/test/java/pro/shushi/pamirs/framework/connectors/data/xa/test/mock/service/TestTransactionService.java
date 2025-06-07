package pro.shushi.pamirs.framework.connectors.data.xa.test.mock.service;

import org.springframework.stereotype.Component;
import org.springframework.test.util.AssertionErrors;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import pro.shushi.pamirs.framework.connectors.data.mapper.GenericMapper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.tx.interceptor.PamirsTransactional;
import pro.shushi.pamirs.framework.connectors.data.tx.transaction.Tx;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.config.TxConfig;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 事务测试
 * <p>
 * 2020/7/4 11:02 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
@Component
public class TestTransactionService {

    @Resource
    private GenericMapper genericMapper;

    @Resource
    private TestTransactionInnerNewService testTransactionInnerNewService;

    @Resource
    private TestTransactionInnerJoinService testTransactionInnerJoinService;

    @PamirsTransactional(enableXa = true)
    public void insertWithSpringTransaction(DataMap one) {
        genericMapper.insert(one);
        AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, one.get("id"));

        // 结果断言
        Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(one.getModel()).eq("`relation`", "test"));
        AssertionErrors.assertEquals("插入并查询单条记录失败", "yihui", result.get("module"));

        throw new RuntimeException("事务回滚");
    }

    @PamirsTransactional(enableXa = true)
    public void insertWithSpringTransactionCommit(DataMap one) {
        genericMapper.insert(one);
        AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, one.get("id"));

        // 结果断言
        Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(one.getModel()).eq("`relation`", "test"));
        AssertionErrors.assertEquals("插入并查询单条记录失败", "yihui", result.get("module"));
    }

    public void insertWithSpringTemplateTransaction(DataMap one) {
        Tx.build(new TxConfig().setEnableXa(true)).execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(@SuppressWarnings("NullableProblems") TransactionStatus status) {
                genericMapper.insert(one);
                AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, one.get("id"));

                // 结果断言
                Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(one.getModel()).eq("`relation`", "test"));
                AssertionErrors.assertEquals("插入并查询单条记录失败", "yihui", result.get("module"));
                throw new RuntimeException("事务回滚");
            }
        });
    }

    @PamirsTransactional(enableXa = true)
    public void insertWithTransactionInnerJoinService(DataMap one, DataMap ds0) {
        genericMapper.insert(one);
        AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, one.get("id"));

        // 结果断言
        Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(one.getModel()).eq("`module`", "yihui"));
        AssertionErrors.assertEquals("插入并查询单条记录失败", "yihui", result.get("module"));

        testTransactionInnerJoinService.insertMultiDsWithInnerSpringTransaction(ds0);
    }

    @PamirsTransactional(enableXa = true)
    public void insertWithTransactionInnerJoinServiceRollback(DataMap one, DataMap ds0) {

        genericMapper.insert(one);
        AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, one.get("id"));

        // 结果断言
        Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(one.getModel()).eq("`relation`", "test"));
        AssertionErrors.assertEquals("插入并查询单条记录失败", "yihui", result.get("module"));

        try {
            testTransactionInnerJoinService.insertMultiDsWithInnerSpringTransactionRollback(ds0);
        } catch (Exception e) {
            log.error("内层事务回滚", e);
        }

    }

    @PamirsTransactional(enableXa = true)
    public void insertWithTransactionOutterJoinServiceRollback(DataMap one, DataMap ds0) {

        genericMapper.insert(one);
        AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, one.get("id"));

        // 结果断言
        Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(one.getModel()).eq("`relation`", "test"));
        AssertionErrors.assertEquals("插入并查询单条记录失败", "yihui", result.get("module"));

        testTransactionInnerJoinService.insertMultiDsWithInnerSpringTransaction(ds0);

        throw new RuntimeException("外层事务回滚");

    }

    @PamirsTransactional(enableXa = true)
    public void insertWithTransactionInnerNestedServiceRollback(DataMap one, DataMap ds0) {

        genericMapper.insert(one);
        AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, one.get("id"));

        // 结果断言
        Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(one.getModel()).eq("`relation`", "test"));
        AssertionErrors.assertEquals("插入并查询单条记录失败", "yihui", result.get("module"));

        try {
            testTransactionInnerJoinService.insertMultiDsWithInnerNestedSpringTransactionRollback(ds0);
        } catch (Exception e) {
            log.error("内层事务回滚", e);
        }

    }

    @PamirsTransactional(enableXa = true)
    public void insertWithTransactionInnerNewService(DataMap one, DataMap ds0) {
        genericMapper.insert(one);
        AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, one.get("id"));

        // 结果断言
        Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(one.getModel()).eq("`module`", "yihui"));
        AssertionErrors.assertEquals("插入并查询单条记录失败", "yihui", result.get("module"));

        testTransactionInnerNewService.insertMultiDsWithInnerNewSpringTransaction(ds0);
    }

    @PamirsTransactional(enableXa = true)
    public void insertWithTransactionInnerNewServiceRollback(DataMap one, DataMap ds0) {

        genericMapper.insert(one);
        AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, one.get("id"));

        // 结果断言
        Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(one.getModel()).eq("`relation`", "test"));
        AssertionErrors.assertEquals("插入并查询单条记录失败", "yihui", result.get("module"));

        try {
            testTransactionInnerNewService.insertMultiDsWithInnerNewSpringTransactionRollback(ds0);
        } catch (Exception e) {
            log.error("内层事务回滚", e);
        }

    }

}
