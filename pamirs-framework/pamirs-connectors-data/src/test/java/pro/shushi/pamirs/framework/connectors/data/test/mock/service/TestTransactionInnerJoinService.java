package pro.shushi.pamirs.framework.connectors.data.test.mock.service;

import org.springframework.stereotype.Component;
import org.springframework.test.util.AssertionErrors;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.framework.connectors.data.mapper.GenericMapper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 嵌套事务测试
 * <p>
 * 2020/7/4 11:02 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Component
public class TestTransactionInnerJoinService {

    @Resource
    private GenericMapper genericMapper;

    @Transactional
    public void insertMultiDsWithInnerSpringTransaction(DataMap ds0) {

        // ds0
        genericMapper.insert(ds0);
        AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, ds0.get("id"));

        // 结果断言
        Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(ds0.getModel()).eq("`module`", "yihui1"));
        AssertionErrors.assertEquals("插入并查询单条记录失败", "yihui1", result.get("module"));

    }

    @Transactional
    public void insertMultiDsWithInnerSpringTransactionRollback(DataMap ds0) {

        // ds0
        genericMapper.insert(ds0);
        AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, ds0.get("id"));

        // 结果断言
        Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(ds0.getModel()).eq("`module`", "yihui1"));
        AssertionErrors.assertEquals("插入并查询单条记录失败", "yihui1", result.get("module"));

        throw new RuntimeException("事务回滚");

    }

    @Transactional(propagation = Propagation.NESTED)
    public void insertMultiDsWithInnerNestedSpringTransactionRollback(DataMap ds0) {

        // ds0
        genericMapper.insert(ds0);
        AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, ds0.get("id"));

        // 结果断言
        Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(ds0.getModel()).eq("`module`", "yihui1"));
        AssertionErrors.assertEquals("插入并查询单条记录失败", "yihui1", result.get("module"));

        throw new RuntimeException("事务回滚");

    }

}
