package pro.shushi.pamirs.meta.dsl.signal;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.dsl.fun.LogicFunInvoker;
import pro.shushi.pamirs.meta.dsl.model.TxConfig;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static pro.shushi.pamirs.meta.dsl.enumeration.DslExpEnumerate.BASE_READ_HANDLE_ERROR;
import static pro.shushi.pamirs.meta.dsl.enumeration.DslExpEnumerate.BASE_READ_RSQL_HANDLE_ERROR;

public class Read extends Tx implements Exe {

    private String model;

    private String rsql;

    private String aggs;

    private Integer page;

    private Integer size;

    private String groupBy;

    private String resultSchema;

    @Override
    public void dispatch(Map<String, Object> context) {
        Pagination pagination = new Pagination();
        if (size != null) {
            pagination.setSize((long) size);
        }
        if (page != null) {
            pagination.setCurrentPage(page);
        }
        // FIXME: 2021/10/25 设置了好像没效果.
        pagination.setAggs(aggs);
        pagination.setGroupBy(groupBy);

        try {
            Object result = LogicFunInvoker.lowcodePageToMapList(invoke(pagination, buildQueryWrapper(context)), model);
            LogicFunInvoker.putResult(context, result);
        } catch (Exception e) {
            throw PamirsException.construct(BASE_READ_HANDLE_ERROR, e).errThrow();
        }
    }

    private Pagination invoke(Pagination pagination, Object condition) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        TxConfig txConfig = tx();
        if (null == txConfig) {
            return (Pagination) LogicFunInvoker.exe(model, FunctionConstants.queryPage, pagination, condition);
        } else {
            return (Pagination) LogicFunInvoker.exeWithTx(model, FunctionConstants.queryPage, txConfig, pagination, condition);
        }
    }

    private Object buildQueryWrapper(Map<String, Object> context) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        String className = "pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper";
        Object queryWrapper = Class.forName(className).newInstance();

        Method method = Class.forName(className)
                .getMethod("from", String.class);
        method.invoke(queryWrapper, model);

        if (StringUtils.isNotBlank(rsql)) {
//            method = Class.forName(className)
//                    .getMethod("setRsql", String.class);
//            method.invoke(queryWrapper, rsql);

            method = Class.forName(className)
                    .getMethod("apply", String.class, Object[].class);
            method.invoke(queryWrapper, LogicFunInvoker.parseRsql2Sql(rsql, model, context), new String[0]);
        }
        return queryWrapper;
    }

    @Deprecated
    private void dealRsql(Map<String, Object> context) {
        try {
            rsql = TypeUtils.stringValueOf(LogicFunInvoker.rsql(rsql, fetchParam(context)));
        } catch (Exception e) {
            throw PamirsException.construct(BASE_READ_RSQL_HANDLE_ERROR, e)
                    .appendMsg(LogicFunInvoker.fetchCurrentStateName(context) + "read失败:" + rsql).errThrow();
        }
    }

    private Map<String, Object> fetchParam(Object param) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (Objects.isNull(param)) {
            return new HashMap<>(0);
        }
        if (param instanceof Map) {
            return ((Map<String, Object>) param);
        } else if (param instanceof Object[]) {
            return fetchParam(((Object[]) param)[0]);
        } else if (param instanceof List) {
            return fetchParam(((List) param).get(0));
        } else if (param instanceof Set) {
            return fetchParam(((Set) param).toArray());
        }
        return fetchParam(LogicFunInvoker.jsonParseObject(LogicFunInvoker.jsonToString(param), Map.class));
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getRsql() {
        return rsql;
    }

    public void setRsql(String rsql) {
        this.rsql = rsql;
    }

    public String getAggs() {
        return aggs;
    }

    public void setAggs(String aggs) {
        this.aggs = aggs;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    public String getResultSchema() {
        return resultSchema;
    }

    public void setResultSchema(String resultSchema) {
        this.resultSchema = resultSchema;
    }

}
