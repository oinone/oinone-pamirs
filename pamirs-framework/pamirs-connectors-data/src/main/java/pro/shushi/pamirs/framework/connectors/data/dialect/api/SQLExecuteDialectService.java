package pro.shushi.pamirs.framework.connectors.data.dialect.api;

import org.apache.ibatis.reflection.MetaObject;
import pro.shushi.pamirs.framework.connectors.data.util.CountSQLParserUtils;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 脚本执行方言服务
 * <p>
 * 2023/06/25
 *
 * @author wx@shushi.pro
 * @version 1.0.0
 */
@Dialect
@SPI(factory = SpringServiceLoaderFactory.class)
public interface SQLExecuteDialectService {

    default String resolve(String sql) {
        return resolve(sql, null);
    }

    String resolve(String sql, ModelConfig modelConfig);

    default String countSQL(boolean optimizeCountSql, String originalSql, MetaObject metaObject) {
        return CountSQLParserUtils.INSTANCE.getOptimizeCountSql(optimizeCountSql, metaObject, originalSql);
    }
}
