package pro.shushi.pamirs.framework.connectors.data.test.mock.service.pamirs;

import org.springframework.stereotype.Component;
import org.springframework.test.util.AssertionErrors;
import pro.shushi.pamirs.framework.connectors.data.mapper.GenericMapper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.test.mock.model.ds.TestFixedDsModel;
import pro.shushi.pamirs.framework.connectors.data.tx.interceptor.PamirsTransactional;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
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
@PamirsTransactional
@Component
public class TestTypePamirsTransactionService {

    @Resource
    private GenericMapper genericMapper;

    public void insertWithSpringTransaction(DataMap one) {
        genericMapper.insert(one);
        AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, one.get("id"));

        // 结果断言
        Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(TestFixedDsModel.modelModel).eq("`relation`", "test"));
        AssertionErrors.assertEquals("插入并查询单条记录失败", "yihui", result.get("module"));

        throw new RuntimeException("事务回滚");
    }

    public void insertWithSpringTransactionCommit(DataMap one) {
        genericMapper.insert(one);
        AssertionErrors.assertNotEquals("插入并查询单条记录失败，未返回pk", null, one.get("id"));

        // 结果断言
        Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(TestFixedDsModel.modelModel).eq("`relation`", "test"));
        AssertionErrors.assertEquals("插入并查询单条记录失败", "yihui", result.get("module"));
    }

}
