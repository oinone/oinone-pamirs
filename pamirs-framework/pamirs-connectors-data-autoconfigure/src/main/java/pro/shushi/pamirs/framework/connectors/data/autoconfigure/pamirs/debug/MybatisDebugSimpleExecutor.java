package pro.shushi.pamirs.framework.connectors.data.autoconfigure.pamirs.debug;

import com.baomidou.mybatisplus.core.executor.MybatisSimpleExecutor;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Adamancy Zhang at 15:39 on 2025-10-31
 */
@Slf4j
public class MybatisDebugSimpleExecutor extends MybatisSimpleExecutor {

    public MybatisDebugSimpleExecutor(Configuration configuration, Transaction transaction) {
        super(configuration, transaction);
    }

    @Override
    public int doUpdate(MappedStatement ms, Object parameter) throws SQLException {
        long start = System.currentTimeMillis();
        int result = super.doUpdate(ms, parameter);
        log.debug("doUpdate cost time: {}ms", System.currentTimeMillis() - start);
        return result;
    }

    @Override
    public <E> List<E> doQuery(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        long start = System.currentTimeMillis();
        List<E> result = super.doQuery(ms, parameter, rowBounds, resultHandler, boundSql);
        log.debug("doQuery cost time: {}ms", System.currentTimeMillis() - start);
        return result;
    }

    @Override
    protected <E> Cursor<E> doQueryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds, BoundSql boundSql) throws SQLException {
        long start = System.currentTimeMillis();
        Cursor<E> result = super.doQueryCursor(ms, parameter, rowBounds, boundSql);
        log.debug("doQueryCursor cost time: {}ms", System.currentTimeMillis() - start);
        return result;
    }
}
