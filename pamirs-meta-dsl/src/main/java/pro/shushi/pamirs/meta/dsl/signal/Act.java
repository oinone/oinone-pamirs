package pro.shushi.pamirs.meta.dsl.signal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import pro.shushi.pamirs.meta.common.util.UnsafeUtil;
import pro.shushi.pamirs.meta.dsl.fun.LogicFunInvoker;
import pro.shushi.pamirs.meta.dsl.model.TxConfig;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Act extends Tx implements Exe {

    private String model;

    private String name;

    private String arg;

    @Override
    public void dispatch(Map<String, Object> context) {
        try {
            Object param = context.get(arg);
            if (null == param) {
                throw new RuntimeException("参数为空，参数名：" + arg);
            }
            Object newParam = param;
            try {
                if (newParam instanceof List) {
                    List<Object> params = (List<Object>) newParam;
                    for (int i = 0; i < params.size(); i++) {
                        Object stringObjectMap = params.get(i);
                        if (stringObjectMap instanceof Map) {
                            params.set(i, LogicFunInvoker.lowcodeMapToModelMap(stringObjectMap, model));
                        } else {
                            params.set(i, LogicFunInvoker.lowcodeModelToMap(stringObjectMap, model));
                        }
                    }
                    newParam = params;
                } else {
                    if (newParam instanceof Map) {
                        newParam = LogicFunInvoker.lowcodeMapToModelMap(newParam, model);
                    } else {
                        newParam = LogicFunInvoker.lowcodeModelToMap(newParam, model);
                    }
                }
            } catch (Exception e) {
                // 忽略异常
                e.printStackTrace();
            }
            Object result;
            if (LogicFunInvoker.isCUD(name)) {
                if (!(newParam instanceof List)) {
                    newParam = new ArrayList<>(Arrays.asList(newParam));
                    result = ((List) invokeCUD(newParam)).get(0);
                } else {
                    result = invokeCUD(newParam);
                }
            } else {
                if (!LogicFunInvoker.isDslFun(model, name)) {
                    newParam = LogicFunInvoker.lowcodeMapToModel(newParam, model);
                }
                result = LogicFunInvoker.lowcodeModelToMap(invoke(newParam), model);
            }
            copyProperties(result, param);
            LogicFunInvoker.putResult(context, param);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object invokeCUD(Object... param) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String namespace = "PAMIRS";
        TxConfig txConfig = tx();
        if(null == txConfig){
            return LogicFunInvoker.exe(namespace, name, model, param);
        }else{
            return LogicFunInvoker.exeWithTx(namespace, name, txConfig, model, param);
        }
    }

    private Object invoke(Object... param) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        TxConfig txConfig = tx();
        if(null == txConfig){
            return LogicFunInvoker.exe(model, name, param);
        }else{
            return LogicFunInvoker.exeWithTx(model, name, txConfig, param);
        }
    }

    private void copyProperties(Object source, Object dest) {
        if (null == source || null == dest) {
            return;
        }
        if (source instanceof List && dest instanceof List) {
            ((List) dest).clear();
            ((List) dest).addAll((List) source);
        } else if (source instanceof List && !(dest instanceof List) || !(source instanceof List) && dest instanceof List) {
            throw new RuntimeException("必须同为集合，才能进行数据拷贝");
        } else if (source instanceof Map && dest instanceof Map) {
            ((Map) dest).clear();
            for (Object key : ((Map) source).keySet()) {
                ((Map) dest).put(key, ((Map) source).get(key));
            }
        } else if (source instanceof Map) {// 不会出现这种情况，所以source缺失字段dest可能会多出值的bug不会触发
            for (Object key : ((Map) source).keySet()) {
                UnsafeUtil.setValue(dest, (String) key, ((Map) source).get(key));
            }
        } else if (dest instanceof Map) {
            ((Map) dest).clear();
            Map sourceMap = JSON.parseObject(JSON.toJSONString(source), new TypeReference<Map<String, Object>>() {
            }.getType());
            for (Object key : sourceMap.keySet()) {
                ((Map) dest).put(key, sourceMap.get(key));
            }
        }
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArg() {
        return arg;
    }

    public void setArg(String arg) {
        this.arg = arg;
    }

}
