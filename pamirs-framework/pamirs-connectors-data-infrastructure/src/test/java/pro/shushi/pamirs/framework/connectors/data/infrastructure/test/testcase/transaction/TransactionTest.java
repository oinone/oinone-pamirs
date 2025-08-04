package pro.shushi.pamirs.framework.connectors.data.infrastructure.test.testcase.transaction;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.AbstractBaseTest;
import pro.shushi.pamirs.framework.connectors.data.infrastructure.test.mock.mapper.SchemaMapper;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import jakarta.annotation.Resource;

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
    private SqlSessionFactory sqlSessionFactory;

    @Test
    @Order(0)
    @DisplayName("测试预提交功能")
    public void testConnectionIsInvalid() {
        SqlSession sqlSession = sqlSessionFactory.openSession(false);
        try {
            sqlSession.getConnection().setAutoCommit(false);
            SchemaMapper databaseMapper = sqlSession.getMapper(SchemaMapper.class);
            databaseMapper.insert(one().setSchemaName(databaseName));
            databaseMapper.insert(one().setSchemaName(databaseName));
            sqlSession.commit();
        } catch (Exception e) {
            log.debug("测试预提交回滚", e);
            sqlSession.rollback();
        } finally {
            if (null != sqlSession) {
                sqlSession.close();
            }
        }
    }

}
