package pro.shushi.pamirs.record.sql.plugin;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import pro.shushi.pamirs.framework.connectors.data.configure.sharding.ShardingDefineConfiguration;
import pro.shushi.pamirs.framework.connectors.data.mapper.GenericMapper;
import pro.shushi.pamirs.framework.connectors.data.mapper.context.MapperContext;
import pro.shushi.pamirs.framework.connectors.data.sql.AbstractWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.config.Configs;
import pro.shushi.pamirs.framework.connectors.data.sql.config.ModelFieldConfigWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.tx.transaction.Tx;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.TxConfig;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.common.util.UnsafeUtil;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.util.JsonUtils;
import pro.shushi.pamirs.meta.util.TypeUtils;
import pro.shushi.pamirs.record.sql.enmu.FilterType;
import pro.shushi.pamirs.record.sql.manager.RecordFilterManager;
import pro.shushi.pamirs.record.sql.manager.SQLRecordSessionManager;
import pro.shushi.pamirs.record.sql.pojo.SQLRecord;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.baomidou.mybatisplus.core.toolkit.Constants.*;
import static pro.shushi.pamirs.framework.connectors.data.constant.DbConstants.PARAM_ANNOTATION_EXT;
import static pro.shushi.pamirs.framework.connectors.data.constant.StatementConstants.CONDITION_COLLECTION;
import static pro.shushi.pamirs.meta.constant.SqlConstants.ID;

/**
 * SQLUpdateInterceptor
 *
 * @author yakir on 2023/06/28 10:52.
 */
@Slf4j
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
@Component
@Order(-999)
@SuppressWarnings({"unchecked", "rawtypes"})
public class SQLUpdateInterceptor implements Interceptor {

    @Autowired
    private SQLRecordSessionManager sqlRecordSessionManager;
    @Autowired
    private RecordFilterManager recordFilterManager;
    @Autowired(required = false)
    private ShardingDefineConfiguration shardingDefineConfiguration;

    private final ConcurrentHashMap<String, String> databaseMap = new ConcurrentHashMap<>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        SqlCommandType commandType = mappedStatement.getSqlCommandType();
        if (!SqlCommandType.INSERT.equals(commandType) && !SqlCommandType.UPDATE.equals(commandType) && !SqlCommandType.DELETE.equals(commandType)) {
            return invocation.proceed();
        }

        Object parameter = null;
        // 获取参数，if语句成立，表示sql语句有参数，参数格式是map形式
        if (invocation.getArgs().length > 1) {
            parameter = invocation.getArgs()[1];
            log.debug("parameter = {}", parameter);
        }

        if (null == parameter) {
            return invocation.proceed();
        }

        boolean isMap = parameter instanceof Map;
        if (!isMap) {
            return invocation.proceed();
        }

        Map<String, Object> map = (Map<String, Object>) parameter;
        String model = MapperContext.model(map);
        if (StringUtils.isBlank(model)) {
            return invocation.proceed();
        }

        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(model);
        if (null == modelConfig) {
            return invocation.proceed();
        }
        if (ModelTypeEnum.PROXY.equals(modelConfig.getType())) {
            model = modelConfig.getProxy();
        }
        if (StringUtils.isBlank(model)) {
            return invocation.proceed();
        }

        String database = modelConfig.getDsKey();
        String table = modelConfig.getTable();
        FilterType filterType = recordFilterManager.filter(database, table);
        if (null == filterType) {
            return invocation.proceed();
        }

        List<SQLRecord> sqlRecords = new ArrayList<>();
        String command = commandType.name();
        String tenant = tenant(map);

        Object result = null;
        switch (commandType) {
            case INSERT: {
                result = invocation.proceed();
                Object coll = map.getOrDefault(COLLECTION, null);
                Object cc = map.getOrDefault(CONDITION_COLLECTION, null);
                if (null != coll) {
                    for (Object item : (List) coll) {
                        SQLRecord record = getRecord(filterType, model, command, (Map) item, null, database, table, tenant);
                        sqlRecords.add(record);
                    }
                } else if (null != cc) {
                    for (Object item : (List) cc) {
                        SQLRecord record = getRecord(filterType, model, command, (Map) item, null, database, table, tenant);
                        sqlRecords.add(record);
                    }
                } else {
                    Object et = map.getOrDefault(ENTITY, null);
                    Map etm;
                    if (et instanceof Map) {
                        etm = (Map) et;
                    } else {
                        etm = ((D) et).get_d();
                    }
                    SQLRecord record = getRecord(filterType, model, command, etm, null, database, table, tenant);
                    sqlRecords.add(record);
                }
            }
            break;
            case UPDATE: {
                QueryWrapper<DataMap> queryWrapper;
                String idColumn = Optional.ofNullable(PamirsSession.getContext().getModelField(model, ID))
                        .map(Configs::wrap)
                        .map(ModelFieldConfigWrapper::getColumn)
                        .orElse(null);
                if (StringUtils.isBlank(idColumn)) {
                    log.warn("没有ID列");
                    return invocation.proceed();
                }
                if (map.containsKey("param2")) {
                    queryWrapper = Pops.<DataMap>query().from(model);
                    AbstractWrapper luw = (AbstractWrapper) map.get("param2");
                    UnsafeUtil.setValue(queryWrapper, "paramNameSeq", UnsafeUtil.getValue(luw, "paramNameSeq"));
                    UnsafeUtil.setValue(queryWrapper, "paramNameValuePairs", luw.getParamNameValuePairs());
                    UnsafeUtil.setValue(queryWrapper, "expression", luw.getExpression());
                } else {
                    Object et = map.getOrDefault(ENTITY, null);
                    if (null == et) {
                        log.warn("没有et");
                        result = invocation.proceed();
                        return result;
                    }
                    Object idObj = ((Map) et).get(ID);
                    if (null == idObj) {
                        log.warn("没有ID");
                        result = invocation.proceed();
                        return result;
                    }
                    Long id = Long.valueOf(String.valueOf(idObj));
                    queryWrapper = Pops.<DataMap>query().from(model);
                    queryWrapper.eq(idColumn, id);
                }

                List<DataMap> beforeList = Tx.build(new TxConfig().setPropagation(Propagation.REQUIRES_NEW.value()))
                        .execute(new RecordTransactionCallback(queryWrapper) {
                        });

                if (null == beforeList || beforeList.isEmpty()) {
                    // 兼容同一个事务，Insert+Update
                    List<DataMap> beforeInsertUpdateList = BeanDefinitionUtils.getBean(GenericMapper.class).selectList(queryWrapper);
                    if (null == beforeInsertUpdateList || beforeInsertUpdateList.isEmpty()) {
                        result = invocation.proceed();
                        return result;
                    } else {
                        beforeList = beforeInsertUpdateList;
                    }
                }

                result = invocation.proceed();

                List<Long> beforeIds = new ArrayList<>();
                for (DataMap before : beforeList) {
                    Object idObj = before.get(ID);
                    if (null != idObj) {
                        Long id = TypeUtils.createLong(idObj);
                        if (null != id) {
                            beforeIds.add(id);
                        }
                    }
                }

                if (beforeIds.isEmpty()) {
                    return result;
                }

                QueryWrapper<DataMap> afterQw = Pops.<DataMap>query().from(model).in(idColumn, beforeIds);
                List<DataMap> afterList = BeanDefinitionUtils.getBean(GenericMapper.class).selectList(afterQw);
                Map<String, DataMap> afterMap = Optional.ofNullable(afterList)
                        .map(List::stream)
                        .orElse(Stream.empty())
                        .collect(Collectors.toMap(_entity -> String.valueOf(_entity.get(ID)),
                                Function.identity(), (_a, _b) -> _a));

                for (DataMap entry : beforeList) {
                    String id = String.valueOf(entry.get(ID));
                    DataMap after = afterMap.get(id);
                    SQLRecord record = getRecord(filterType, model, command, after, entry, database, table, tenant);
                    sqlRecords.add(record);
                }
            }
            break;
            case DELETE: {
                Object collDel = map.getOrDefault(COLLECTION, null);
                Object ccDel = map.getOrDefault(CONDITION_COLLECTION, null);
                Object et = map.getOrDefault(ENTITY, null);
                Object ew = map.getOrDefault(WRAPPER, null);
                String idColumn = Optional.ofNullable(PamirsSession.getContext().getModelField(model, ID))
                        .map(Configs::wrap)
                        .map(ModelFieldConfigWrapper::getColumn)
                        .orElse(null);
                if (StringUtils.isBlank(idColumn)) {
                    log.warn("没有ID列");
                    return invocation.proceed();
                }
                if (null != collDel) {
                    for (Object item : (List) collDel) {
                        Map dataMap = (Map) item;
                        QueryWrapper<DataMap> beforeQw = Pops.<DataMap>query().from(model).eq(idColumn, dataMap.get(ID));
                        DataMap before = BeanDefinitionUtils.getBean(GenericMapper.class).selectOne(beforeQw);
                        if (null == before || null == before.get(ID)) {
                            break;
                        }
                        SQLRecord record = getRecord(filterType, model, command, before, before, database, table, tenant);
                        sqlRecords.add(record);
                    }
                } else if (null != ccDel) {
                    for (Object item : (List) ccDel) {
                        Map dataMap = (Map) item;
                        QueryWrapper<DataMap> beforeQw = Pops.<DataMap>query().from(model).eq(idColumn, dataMap.get(ID));
                        DataMap before = BeanDefinitionUtils.getBean(GenericMapper.class).selectOne(beforeQw);
                        if (null == before || null == before.get(ID)) {
                            continue;
                        }
                        SQLRecord record = getRecord(filterType, model, command, before, before, database, table, tenant);
                        sqlRecords.add(record);
                    }
                } else if (null != et) {
                    Map etm;
                    if (et instanceof Map) {
                        etm = (Map) et;
                    } else {
                        etm = ((D) et).get_d();
                    }
                    QueryWrapper<DataMap> beforeQw = Pops.<DataMap>query().from(model).eq(idColumn, etm.get(ID));
                    DataMap before = BeanDefinitionUtils.getBean(GenericMapper.class).selectOne(beforeQw);
                    if (null == before || null == before.get(ID)) {
                        break;
                    }
                    SQLRecord record = getRecord(filterType, model, command, before, before, database, table, tenant);
                    sqlRecords.add(record);
                } else if (null != ew) {
                    QueryWrapper<DataMap> queryWrapper = Pops.<DataMap>query().from(model);
                    AbstractWrapper luw = (AbstractWrapper) map.get("param1");
                    UnsafeUtil.setValue(queryWrapper, "paramNameSeq", UnsafeUtil.getValue(luw, "paramNameSeq"));
                    UnsafeUtil.setValue(queryWrapper, "paramNameValuePairs", luw.getParamNameValuePairs());
                    UnsafeUtil.setValue(queryWrapper, "expression", luw.getExpression());

                    List<DataMap> beforeList = Tx.build(new TxConfig().setPropagation(Propagation.REQUIRES_NEW.value()))
                            .execute(new RecordTransactionCallback(queryWrapper) {
                            });

                    if (null == beforeList || beforeList.isEmpty()) {
                        // 兼容同一个事务，Delete
                        List<DataMap> beforeDeleteList = BeanDefinitionUtils.getBean(GenericMapper.class).selectList(queryWrapper);
                        if (null == beforeDeleteList || beforeDeleteList.isEmpty()) {
                            result = invocation.proceed();
                            return result;
                        } else {
                            beforeList = beforeDeleteList;
                        }
                    }
                    if (CollectionUtils.isNotEmpty(beforeList)) {
                        for (DataMap before : beforeList) {
                            if (null == before || null == before.get(ID)) {
                                continue;
                            }
                            SQLRecord record = getRecord(filterType, model, command, before, before, database, table, tenant);
                            sqlRecords.add(record);
                        }
                    }
                }
                result = invocation.proceed();
            }
            break;
            default:
                // do nothing ...
                break;
        }
        if (CollectionUtils.isEmpty(sqlRecords)) {
            return result;
        }
        sqlRecordSessionManager.set(sqlRecords);
        return result;
    }

    public String tenant(Map<String, Object> param) {
        Object tenantObj = param.getOrDefault(PARAM_ANNOTATION_EXT, null);
        String tenant = null;
        if (null != tenantObj) {
            tenantObj = ((Map) tenantObj).getOrDefault("t", null);
            tenant = (null == tenantObj ? "" : String.valueOf(tenantObj));
            return tenant;
        } else {
            return "";
        }
    }

    public SQLRecord getRecord(FilterType filterType, String model, String command, Map entity, Map old, String database, String table, String tenant) {
        SQLRecord record = new SQLRecord();
        record.setSchema(database);
        record.setTable(table);
        record.setFilterType(filterType);
        record.setModel(model);
        record.setEventType(command);
        if (null != entity) {
            record.setNow(JsonUtils.toJSONString(entity));
        }
        if (null != old) {
            record.setOld(JsonUtils.toJSONString(old));
        }
        record.setuT(new Date());
        record.setT(tenant);
        return record;
    }

    @Override
    public Object plugin(Object target) {
        return Interceptor.super.plugin(target);
    }

    @Override
    public void setProperties(Properties properties) {
        Interceptor.super.setProperties(properties);
    }
}
