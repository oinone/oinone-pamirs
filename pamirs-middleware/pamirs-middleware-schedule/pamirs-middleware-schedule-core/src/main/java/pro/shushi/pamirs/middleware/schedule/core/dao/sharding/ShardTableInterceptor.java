package pro.shushi.pamirs.middleware.schedule.core.dao.sharding;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.shushi.pamirs.middleware.schedule.core.dialect.ScheduleSQLDialectService;
import pro.shushi.pamirs.middleware.schedule.core.dialect.entity.DialectVersion;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleEnvironment;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;
import pro.shushi.pamirs.middleware.zookeeper.util.SpringStaticContextManager;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.*;


@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class ShardTableInterceptor implements Interceptor {

    private final static Logger log = LoggerFactory.getLogger(ShardTableInterceptor.class);

    private static final String TAG = ShardTableInterceptor.class.getName();

    private final DialectVersion dialectVersion;

    public ShardTableInterceptor(DialectVersion dialectVersion) {
        this.dialectVersion = dialectVersion;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaStatementHandler = SystemMetaObject.forObject(statementHandler);
        MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");
        String sqlId = mappedStatement.getId();

        String className = sqlId.substring(0, sqlId.lastIndexOf("."));
        Class<?> classObj = Class.forName(className);

        TableSeg tableSeg = classObj.getAnnotation(TableSeg.class);
        if (null == tableSeg) {
            //不需要分表，直接传递给下一个拦截器处理
            return invocation.proceed();
        }

        BoundSql boundSql = (BoundSql) metaStatementHandler.getValue("delegate.boundSql");//获取sql语句
        Object parameterObject = metaStatementHandler.getValue("delegate.boundSql.parameterObject");//获取参数

        //根据配置获取分表字段，生成分表SQL
        String newSql = doShared(boundSql, parameterObject, tableSeg.tableName(), tableSeg.shardBy());

        ScheduleSQLDialectService service = getDialectService(mappedStatement);
        if (service != null) {
            newSql = service.resolve(newSql, boundSql, getResultMaps(mappedStatement));
        }

        if (newSql != null) {
            log.debug("{} Sharded SQL =====> {} ", TAG, newSql);
            metaStatementHandler.setValue("delegate.boundSql.sql", newSql);
        }

        // 传递给下一个拦截器处理
        return invocation.proceed();
    }

    protected ScheduleSQLDialectService getDialectService(MappedStatement mappedStatement) {
        List<ScheduleSQLDialectService> services = SpringStaticContextManager.getContextManager().getBeansOfTypeByOrdered(ScheduleSQLDialectService.class);
        for (ScheduleSQLDialectService service : services) {
            if (service.isSupported(dialectVersion, mappedStatement)) {
                return service;
            }
        }
        return null;
    }

    protected static ResultMap SCHEDULE_ITEM_RESULT_MAP;

    protected List<ResultMap> getResultMaps(MappedStatement mappedStatement) {
        List<ResultMap> resultMaps = mappedStatement.getResultMaps();
        if (mappedStatement.getId().startsWith("pro.shushi.pamirs.middleware.schedule.core.dao.mapper.ScheduleItemMapper")) {
            if (resultMaps.isEmpty()) {
                return getScheduleItemResultMap(mappedStatement);
            }
            if (resultMaps.size() == 1) {
                ResultMap resultMap = resultMaps.get(0);
                if (CollectionUtils.isEmpty(resultMap.getResultMappings())) {
                    return getScheduleItemResultMap(mappedStatement);
                }
            }
        }
        return resultMaps;
    }

    protected List<ResultMap> getScheduleItemResultMap(MappedStatement mappedStatement) {
        if (SCHEDULE_ITEM_RESULT_MAP == null) {
            SCHEDULE_ITEM_RESULT_MAP = new ResultMap.Builder(mappedStatement.getConfiguration(), String.valueOf(System.currentTimeMillis()), ScheduleItem.class,
                    Arrays.asList(
                            new ResultMapping.Builder(mappedStatement.getConfiguration(), "isCycle", "is_cycle", Boolean.class).build(),
                            new ResultMapping.Builder(mappedStatement.getConfiguration(), "isTransfer", "is_transfer", Boolean.class).build(),
                            new ResultMapping.Builder(mappedStatement.getConfiguration(), "isCanceled", "is_canceled", Boolean.class).build()
                    )).build();
        }
        return Collections.singletonList(SCHEDULE_ITEM_RESULT_MAP);
    }

    @Override
    public Object plugin(Object target) {
        // 当目标类是StatementHandler类型时，才包装目标类，否者直接返回目标本身,减少目标被代理的次数
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {
        log.info("scribeDbNames: {}", properties.getProperty("scribeDbNames"));
    }

    public String doShared(BoundSql boundSql, Object parameterObject, String tableName, String shardeBy) throws Exception {
        String originSql = boundSql.getSql();
        if (parameterObject instanceof String) {
            originSql = originSql.replaceAll(tableName, tableName + "_" + parameterObject);
        } else if (parameterObject instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) parameterObject;
            Set<String> set = map.keySet();
            String value = "";
            for (String key : set) {
                if (key.equals(shardeBy)) {
                    value = map.get(shardeBy).toString();
                    break;
                }
                Object objectValue = map.get(key);
                if (objectValue instanceof ScheduleEnvironment) {
                    value = ((ScheduleEnvironment) objectValue).getTableNum().toString();
                    break;
                }
            }
            originSql = originSql.replaceAll(tableName, tableName + "_" + value);
        } else {
            Class<?> clazz = parameterObject.getClass();
            String value = "";
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                if (fieldName.equals(shardeBy)) {
                    value = field.get(parameterObject).toString();
                    break;
                }
            }
            originSql = originSql.replaceAll(tableName, tableName + "_" + value);
        }
        return originSql;
    }

}
