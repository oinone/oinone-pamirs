package pro.shushi.pamirs.meta.dsl.signal;

import pro.shushi.pamirs.meta.dsl.fun.LogicFunInvoker;
import pro.shushi.pamirs.meta.dsl.model.TxConfig;
import pro.shushi.pamirs.meta.dsl.utils.StringUtils;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

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

        dealRsql(context);

        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("rsql", StringUtils.isBlank(rsql)?"id>0":"id>0;" + rsql);
        conditionMap.put("page", null == page?1:page);
        conditionMap.put("size", null == size?1:size);
        conditionMap.put("aggs", aggs);
        conditionMap.put("groupBy", groupBy);
        try{
            Class beanClass = Class.forName("pro.shushi.pamirs.base.data.domain.Condition");
            Object condition = JsonUtils.parseObject(JsonUtils.toJSONString(conditionMap), beanClass);
            Object result = LogicFunInvoker.lowcodePageToMapList(invoke(condition), model);
            LogicFunInvoker.putResult(context, result);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    private Object invoke(Object condition) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        TxConfig txConfig = tx();
        if(null == txConfig){
            return LogicFunInvoker.exe(model, "pageAll", condition);
        }else{
            return LogicFunInvoker.exeWithTx(model, "pageAll", txConfig, condition);
        }
    }

    private void dealRsql(Map<String, Object> context) {
        try {
            rsql = String.valueOf(LogicFunInvoker.rsql(rsql, fetchParam(context)));
        } catch (Exception e) {
            throw new RuntimeException(LogicFunInvoker.fetchCurrentStateName(context) + "read失败:" + rsql, e);
        }
    }

    private Map<String, Object> fetchParam(Object param) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (Objects.isNull(param)) {
            return new HashMap<>(0);
        }
        if (param instanceof Map) {
            return ((Map<String, Object>) param);
        } else if(param instanceof Object[]) {
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
