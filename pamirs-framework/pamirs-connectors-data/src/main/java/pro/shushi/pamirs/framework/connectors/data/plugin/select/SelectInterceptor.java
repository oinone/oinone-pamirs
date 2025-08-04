package pro.shushi.pamirs.framework.connectors.data.plugin.select;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.mapper.context.MapperContext;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.constant.FunctionConstants;

import java.util.Map;
import java.util.Properties;

@Slf4j
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
                RowBounds.class, ResultHandler.class})
})
@Component
@Order(99)
public class SelectInterceptor implements Interceptor {

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        if (SqlCommandType.SELECT != ms.getSqlCommandType()) {
            return invocation.proceed();
        }

        Object param = args[1];
        if (param instanceof Map) {
            Map map = (Map) param;
            // 获取配置信息
            String model = MapperContext.model(map);
            if (StringUtils.isBlank(model) || model.startsWith("base.")) {
                return invocation.proceed();
            }
            Function function = PamirsSession.getContext().getFunctionAllowNull(model, FunctionConstants.queryFilters);
            if (function == null || "defaultReadFiltersApi".equals(function.getBeanName())) {
                return invocation.proceed();
            }
            String filter = Fun.run(function);
            if (StringUtils.isBlank(filter)) {
                return invocation.proceed();
            }

            BoundSql boundSql = ms.getBoundSql(param);
            String sql = boundSql.getSql();
            // 通过jsqlparser解析SQL，此处的statement是封装过后的Insert/Update/Query等SQL语句
            Statement statement = CCJSqlParserUtil.parse(sql);

            Statement select = prepareSelectSql(statement, model, filter.trim());
            BoundSql selectBoundSql = new BoundSql(ms.getConfiguration(), select.toString(), boundSql.getParameterMappings(), boundSql.getParameterObject());
            MappedStatement selectMs = buildMappedStatement(ms, new BoundSqlSqlSource(selectBoundSql));
            // 更新 MappedStatement 对象
            args[0] = selectMs;

        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
        // to do nothing
    }

    private MappedStatement buildMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length > 0) {
            builder.keyProperty(ms.getKeyProperties()[0]);
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.cache(ms.getCache());
        builder.useCache(ms.isUseCache());
        return builder.build();
    }

    private static class BoundSqlSqlSource implements SqlSource {
        BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }

    private Statement prepareSelectSql(Statement statement, String model, String filter) throws JSQLParserException {
        Select select = (Select) statement;
        PlainSelect plain = (PlainSelect) select.getSelectBody();
        FromItem fromItem = plain.getFromItem();

        StringBuffer whereSql = new StringBuffer();
        //增加sql语句的逻辑部分处理
        if (fromItem.getAlias() != null) {
            whereSql.append(fromItem.getAlias().getName()).append(".").append(filter);
        } else {
            whereSql.append(filter);
        }
        Expression where = plain.getWhere();
        if (where == null) {
            if (whereSql.length() > 0) {
                Expression expression = CCJSqlParserUtil.parseCondExpression(whereSql.toString());
                Expression whereExpression = (Expression) expression;
                plain.setWhere(whereExpression);
            }
        } else {
            if (whereSql.length() > 0) {
                //where条件之前存在，需要重新进行拼接
                whereSql.append(" and ( " + where.toString() + " )");
            } else {
                //新增片段不存在，使用之前的sql
                whereSql.append(where.toString());
            }
            Expression expression = CCJSqlParserUtil.parseCondExpression(whereSql.toString());
            plain.setWhere(expression);
        }

        return select;
    }

}