package pro.shushi.pamirs.framework.connectors.data.plugin.sql;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import pro.shushi.pamirs.framework.connectors.data.dialect.Dialects;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.DialectIgnoredHintApi;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.SQLExecuteDialectService;
import pro.shushi.pamirs.framework.connectors.data.optimize.AbstractSqlParserHandler;
import pro.shushi.pamirs.framework.connectors.data.util.DataConfigurationHelper;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.D;

import java.sql.Connection;
import java.util.*;

/**
 * SQL方言执行
 *
 * @author Adamancy Zhang at 15:34 on 2023-06-26
 */
@Intercepts({@Signature(
        type = StatementHandler.class,
        method = "prepare",
        args = {Connection.class, Integer.class}
)})
public class SqlDialectInterceptor extends AbstractSqlParserHandler implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);

        // SQL 解析
        this.sqlParser(metaObject);

        // 先判断是不是SELECT操作  (2019-04-10 00:37:31 跳过存储过程)
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        if (Arrays.asList(SqlCommandType.UNKNOWN, SqlCommandType.INSERT, SqlCommandType.FLUSH).contains(mappedStatement.getSqlCommandType())
                || StatementType.CALLABLE == mappedStatement.getStatementType()) {
            return invocation.proceed();
        }

        if (DialectIgnoredHintApi.isIgnored()) {
            return invocation.proceed();
        }

        // 针对定义了rowBounds，做为mapper接口方法的参数
        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");

        String dsKey = DataConfigurationHelper.getDsKey();

        String newBoundSql = Dialects.component(SQLExecuteDialectService.class, dsKey).resolve(boundSql.getSql(), findWrapperModelConfig(boundSql, mappedStatement));

        metaObject.setValue("delegate.boundSql.sql", newBoundSql);
        return invocation.proceed();
    }

    protected ModelConfig findWrapperModelConfig(BoundSql boundSql, MappedStatement mappedStatement) {
        boolean isLeafAllocModel = Optional.ofNullable(mappedStatement.getId()).map(v -> v.startsWith("pro.shushi.pamirs.sequence.mapper.ISequenceMapper")).orElse(false);
        if (isLeafAllocModel) {
            return PamirsSession.getContext().getSimpleModelConfig("base.LeafAlloc");
        }
        Object parameterObject = boundSql.getParameterObject();
        if (parameterObject instanceof Map) {
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) parameterObject).entrySet()) {
                if ("ew".equals(entry.getKey())) {
                    Object value = entry.getValue();
                    if (value instanceof IWrapper) {
                        String model = ((IWrapper<?>) value).getModel();
                        if (StringUtils.isNotBlank(model)) {
                            return PamirsSession.getContext().getSimpleModelConfig(model);
                        }
                    }
                } else if ("et".equals(entry.getKey())) {
                    Object value = entry.getValue();
                    if (value instanceof D || value instanceof DataMap) {
                        String model = Models.api().getModel(value);
                        if (StringUtils.isNotBlank(model)) {
                            return PamirsSession.getContext().getSimpleModelConfig(model);
                        }
                    }
                }
            }
        }
        return null;
    }
}
