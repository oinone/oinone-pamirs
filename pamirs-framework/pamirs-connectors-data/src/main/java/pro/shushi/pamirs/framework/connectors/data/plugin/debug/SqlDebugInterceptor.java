package pro.shushi.pamirs.framework.connectors.data.plugin.debug;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;
import pro.shushi.pamirs.framework.common.api.SceneAnalysisDebugTraceApi;

import java.sql.Connection;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * debug调试收集SQL
 *
 * @author cpc@shushi.pro
 * @version 1.0.0
 * date 2024/4/5 1:51 下午
 */
@Intercepts({@Signature(
        type = StatementHandler.class,
        method = "prepare",
        args = {Connection.class, Integer.class}
)})
public class SqlDebugInterceptor implements Interceptor, SceneAnalysisDebugTraceApi {

    private static final String sqlDebugScene = "SQL调试";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (!SceneAnalysisDebugTraceApi.isDebug()) {
            //非调试状态，直接过
            return invocation.proceed();
        }
        StatementHandler statementHandler = PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        MappedStatement ms = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        if (SqlCommandType.DELETE != ms.getSqlCommandType()
                && SqlCommandType.SELECT != ms.getSqlCommandType()
                && SqlCommandType.INSERT != ms.getSqlCommandType()
                && SqlCommandType.UPDATE != ms.getSqlCommandType()) {
            return invocation.proceed();
        }
        long start = System.currentTimeMillis();
        Object result = null;
        long end = 0;
        try {
            result = invocation.proceed();
            end = System.currentTimeMillis();
        } finally {
            //搜集debug调试信息
            long time = end - start;
            addDebugTrace(() -> {
                BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
                Configuration configuration = ms.getConfiguration();
                String originalSql = showSql(configuration, boundSql);
                return ImmutablePair.of(originalSql, time);
            });
        }

        return result;
    }

    public String showSql(Configuration configuration, BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        if (parameterMappings.size() > 0 && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    }
                }
            }
        }
        sql.replaceAll("\'", "").replace("\"", "");
        return sql;
    }

    private String getParameterValue(Object obj) {
        String value = null;
        if (obj instanceof String) {
            value = "'" + obj.toString() + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + formatter.format(obj) + "'";
        } else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "";
            }
        }
        return value;
    }

    @Override
    public String scene() {
        return sqlDebugScene;
    }
}
