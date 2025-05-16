package pro.shushi.pamirs.framework.connectors.data.dialect.holder;

import pro.shushi.pamirs.framework.connectors.data.dialect.Dialects;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.SQLBatchExecuteDialectService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 批量执行方言服务持有者
 *
 * @author Adamancy Zhang at 18:06 on 2024-10-18
 */
public class SQLBatchExecuteDialectServiceHolder {

    private static final Map<String, SQLBatchExecuteDialectService> CACHE = new ConcurrentHashMap<>();

    public static SQLBatchExecuteDialectService get(String dsKey) {
        return CACHE.computeIfAbsent(dsKey, (k) -> {
            SQLBatchExecuteDialectService executor = Dialects.component(SQLBatchExecuteDialectService.class, k);
            if (executor == null) {
                return EmptySQLBatchExecuteDialectService.INSTANCE;
            }
            return executor;
        });
    }

    private static class EmptySQLBatchExecuteDialectService implements SQLBatchExecuteDialectService {

        private static final SQLBatchExecuteDialectService INSTANCE = new EmptySQLBatchExecuteDialectService();

        @Override
        public <T> Integer batchSubmit(Function<List<T>, Integer> function, List<T> entityList) {
            return function.apply(entityList);
        }
    }
}
