package pro.shushi.pamirs.framework.connectors.data.test.mock.service.pamirs;

import org.springframework.stereotype.Component;
import org.springframework.test.util.AssertionErrors;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import pro.shushi.pamirs.framework.connectors.data.api.constants.DataBeanNameConstants;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.framework.connectors.data.mapper.GenericMapper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.test.mock.model.ds.TestFixedDsModel;
import pro.shushi.pamirs.framework.connectors.data.tx.interceptor.PamirsTransactional;
import pro.shushi.pamirs.framework.connectors.data.tx.transaction.Tx;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 事务测试
 *
 * 2020/7/4 11:02 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
@Component
public class TestPamirsTransactionService {

    @Resource
    private GenericMapper genericMapper;

    @PamirsTransactional
    public void insertWithSpringTransactionCommit(DataMap one){
        genericMapper.insert(one);
        AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, one.get("id"));

        // 结果断言
        Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(TestFixedDsModel.modelModel).eq("`relation`", "test"));
        AssertionErrors.assertEquals("插入并查询单条记录失败", "yihui", result.get("module"));
    }

    @PamirsTransactional
    public void insertWithSpringTransaction(DataMap one){
        genericMapper.insert(one);
        AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, one.get("id"));

        // 结果断言
        Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(TestFixedDsModel.modelModel).eq("`relation`", "test"));
        AssertionErrors.assertEquals("插入并查询单条记录失败", "yihui", result.get("module"));

        throw new RuntimeException("事务回滚");
    }

    public void insertWithSpringTemplateTransaction(DataMap one){
        Tx.build().execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(@SuppressWarnings("NullableProblems") TransactionStatus status) {
                genericMapper.insert(one);
                AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, one.get("id"));

                // 结果断言
                Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(TestFixedDsModel.modelModel).eq("`relation`", "test"));
                AssertionErrors.assertEquals("插入并查询单条记录失败", "yihui", result.get("module"));
                throw new RuntimeException("事务回滚");
            }
        });
    }

    public void insertWithSpringTemplateBuildTransaction(DataMap one){
        Tx.build(DataBeanNameConstants.TRANSACTION_MANAGER_BEAN_NAME).execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(@SuppressWarnings("NullableProblems") TransactionStatus status) {
                genericMapper.insert(one);
                AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, one.get("id"));

                // 结果断言
                Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(TestFixedDsModel.modelModel).eq("`relation`", "test"));
                AssertionErrors.assertEquals("插入并查询单条记录失败", "yihui", result.get("module"));
                throw new RuntimeException("事务回滚");
            }
        });
    }

}
