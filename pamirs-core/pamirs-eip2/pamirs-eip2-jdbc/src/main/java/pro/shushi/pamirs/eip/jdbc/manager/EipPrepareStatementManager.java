package pro.shushi.pamirs.eip.jdbc.manager;

import org.apache.camel.CamelContext;
import org.apache.camel.component.sql.SqlPrepareStatementStrategy;
import pro.shushi.pamirs.core.common.entry.Holder;
import pro.shushi.pamirs.eip.api.context.EipCamelContext;
import pro.shushi.pamirs.eip.jdbc.camel.EipSqlPrepareStatementStrategy;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * JDBC预处理策略管理器
 *
 * @author Adamancy Zhang at 21:24 on 2024-06-05
 */
public class EipPrepareStatementManager {

    private static final Map<String, SqlPrepareStatementStrategy> SQL_PREPARE_STATEMENT_STRATEGY_MAP = new ConcurrentHashMap<>();

    public static String generatorId(String dbType) {
        return EipSqlPrepareStatementStrategy.NAME + CharacterConstants.SEPARATOR_UNDERLINE + dbType;
    }

    public static boolean register(String dbType, char separator, String defaultKey) {
        return register(dbType, () -> new EipSqlPrepareStatementStrategy(separator, defaultKey));
    }

    public static boolean register(String dbType, Supplier<SqlPrepareStatementStrategy> prepareStatementStrategySupplier) {
        CamelContext context = EipCamelContext.getContext().getCamelContext();
        Holder<Boolean> resultHolder = new Holder<>(false);
        SQL_PREPARE_STATEMENT_STRATEGY_MAP.computeIfAbsent(dbType, (key) -> {
            SqlPrepareStatementStrategy sqlPrepareStatementStrategy = prepareStatementStrategySupplier.get();
            context.getRegistry().bind(generatorId(key), sqlPrepareStatementStrategy);
            return sqlPrepareStatementStrategy;
        });
        return resultHolder.get();
    }
}
