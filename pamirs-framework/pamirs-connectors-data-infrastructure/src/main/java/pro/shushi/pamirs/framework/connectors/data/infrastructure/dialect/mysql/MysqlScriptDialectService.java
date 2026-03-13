package pro.shushi.pamirs.framework.connectors.data.infrastructure.dialect.mysql;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.api.datasource.DsHintApi;
import pro.shushi.pamirs.framework.connectors.data.configure.sharding.ShardingRuleConfiguration;
import pro.shushi.pamirs.framework.connectors.data.dialect.Dialects;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.Dialect;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.DsDialectComponent;
import pro.shushi.pamirs.framework.connectors.data.infrastructure.dialect.ScriptDialectService;
import pro.shushi.pamirs.framework.connectors.data.mapper.template.SqlTemplate;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;

import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import java.io.StringReader;
import java.sql.Connection;

import static pro.shushi.pamirs.framework.connectors.data.infrastructure.enmu.InfExpEnumerate.BASE_DIALECT_SCRIPT_RUN_ERROR;

/**
 * MYSQL脚本执行方言服务
 * <p>
 * 2020/7/16 1:47 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
@Order(0)
@Dialect.component
@SPI.Service
@Component
public class MysqlScriptDialectService implements ScriptDialectService {

    @Resource
    private SqlSessionTemplate sqlSessionTemplate;

    @Resource
    private ShardingRuleConfiguration shardingRuleConfiguration;

    @Override
    public void run(@NotBlank String dsKey, @NotBlank String script) {
        try (DsHintApi ignored = DsHintApi.use(dsKey)) {
            try (Connection connection = SqlSessionUtils.getSqlSession(sqlSessionTemplate.getSqlSessionFactory(), sqlSessionTemplate.getExecutorType(), sqlSessionTemplate.getPersistenceExceptionTranslator()).getConnection()) {
                ScriptRunner runner = new ScriptRunner(connection);
                runner.setStopOnError(true);
                runner.runScript(new StringReader(script));
            }
        } catch (Exception e) {
            log.error("Script execution error, dsKey:{}, script:{}", dsKey, script, e);
            tryUnlock(dsKey);
            throw PamirsException.construct(BASE_DIALECT_SCRIPT_RUN_ERROR, e).errThrow();
        }
    }

    protected void tryUnlock(String dsKey) {
        boolean isShardingConnection = shardingRuleConfiguration.containsKey(dsKey);
        if (!isShardingConnection) {
            unlockTables(dsKey);
        }
    }

    @Override
    public void ddl(@NotBlank String dsKey, @NotBlank String script) {
        String finalDsKey = DsHintApi.expression(dsKey);
        String database = Dialects.component(DsDialectComponent.class, dsKey).getDatabase(finalDsKey);
        String printScript = "# database[" + database + "]变更" + CharacterConstants.NEWLINE
                + "use " + database + ";" + CharacterConstants.NEWLINE + script;
        log.error(printScript);
    }

    @Override
    public String trimScript(String script) {
        if (script.startsWith(SqlTemplate.LOCK_TABLES)) {
            script = StringUtils.substringAfter(script, CharacterConstants.NEWLINE);
        }
        if (script.endsWith(SqlTemplate.UNLOCK_TABLES)
                || script.endsWith(SqlTemplate.UNLOCK_TABLES + CharacterConstants.NEWLINE)) {
            script = StringUtils.substringBefore(script, SqlTemplate.UNLOCK_TABLES);
        }
        return script;
    }

    @Override
    public void unlockTables(@NotBlank String dsKey) {
        run(dsKey, SqlTemplate.UNLOCK_TABLES);
    }

}
