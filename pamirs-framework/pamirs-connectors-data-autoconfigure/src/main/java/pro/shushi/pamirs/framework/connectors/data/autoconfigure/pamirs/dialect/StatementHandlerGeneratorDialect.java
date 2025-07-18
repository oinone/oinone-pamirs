package pro.shushi.pamirs.framework.connectors.data.autoconfigure.pamirs.dialect;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import pro.shushi.pamirs.framework.connectors.data.autoconfigure.pamirs.PamirsMybatisConfiguration;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.Dialect;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * StatementHandler Generator Dialect
 *
 * @author Adamancy Zhang at 16:31 on 2025-07-17
 */
@Dialect
@SPI(factory = SpringServiceLoaderFactory.class)
public interface StatementHandlerGeneratorDialect {

    StatementHandler newStatementHandler(PamirsMybatisConfiguration configuration, Executor executor, MappedStatement mappedStatement, Object parameterObject, RowBounds rowBounds, ResultHandler<?> resultHandler, BoundSql boundSql);

}
