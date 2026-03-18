package pro.shushi.pamirs.meta.dsl.signal;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.util.UnsafeUtil;
import pro.shushi.pamirs.meta.dsl.fun.LogicFunInvoker;
import pro.shushi.pamirs.meta.dsl.model.TxConfig;
import pro.shushi.pamirs.meta.dsl.utils.ArgUtils;

import java.util.List;
import java.util.Map;

import static pro.shushi.pamirs.meta.common.util.TypeReferences.TR_MAP_SO;
import static pro.shushi.pamirs.meta.dsl.enumeration.DslExpEnumerate.*;

public class Act extends Tx implements Exe {

    private final static Logger log = LoggerFactory.getLogger(Act.class);

    private String model;

    private String name;

    private String arg;

    @Override
    @SuppressWarnings({"unchecked"})
    public void dispatch(Map<String, Object> context) {
        try {
            List<String> args = ArgUtils.argArray(arg);
            Object[] fnParams = new Object[args.size()];
            for (int i = 0; i < args.size(); i++) {
                String _am = args.get(i);
                List<String> aml = ArgUtils.argModel(_am);
                String arg = aml.get(0);
                String am = aml.get(1);

                Object param = LogicFunInvoker.getArg(arg, context);
                if (null == param) {
                    throw PamirsException.construct(BASE_ACTION_PARAMS_IS_EMPTY_ERROR)
                            .appendMsg("Parameter is empty, parameter name: " + arg)
                            .errThrow();
                }

                Object newParam = param;
                try {
                    if (newParam instanceof List) {
                        List<Object> params = (List<Object>) newParam;
                        for (int j = 0; j < params.size(); j++) {
                            Object stringObjectMap = params.get(j);
                            if (stringObjectMap instanceof Map) {
                                params.set(j, LogicFunInvoker.lowcodeMapToModelMap(stringObjectMap, am));
                            } else {
                                params.set(j, stringObjectMap);
                            }
                        }
                        newParam = params;
                    } else {
                        if (newParam instanceof Map) {
                            newParam = LogicFunInvoker.lowcodeMapToModelMap(newParam, am);
                        } else {
                            newParam = newParam;
                        }
                    }
                } catch (Exception e) {
                    // 忽略异常
                    log.error("Action input parameter processing exception", e);
                    // TODO: 2021/10/25 先把异常抛出
                    throw e;
                }

                fnParams[i] = newParam;
            }
            // FIXME: 2021/10/25 不清楚判断的作用,但是现在调用一定是模型对象
//            if (!LogicFunInvoker.isDslFun(model, name)) {
//                newParam = LogicFunInvoker.lowcodeMapToModel(newParam, model);
//            }
            Object result = LogicFunInvoker.lowcodeModelToMap(invoke(fnParams), model);
            //action节点的执行结果,使用了入参对象,而不是action返回结果,为了保持引用一致?
//                copyProperties(result, param);
            LogicFunInvoker.putResult(context, result);
        } catch (Exception e) {
            throw PamirsException.construct(BASE_ACTION_HANDLE_ERROR, e).errThrow();
        }
    }

    private Object invoke(Object... param) {
        TxConfig txConfig = tx();
        if (null == txConfig) {
            return LogicFunInvoker.exe(model, name, param);
        } else {
            return LogicFunInvoker.exeWithTx(model, name, txConfig, param);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void copyProperties(Object source, Object dest) {
        if (null == source || null == dest) {
            return;
        }
        if (source instanceof List && dest instanceof List) {
            ((List) dest).clear();
            ((List) dest).addAll((List) source);
        } else if (source instanceof List && !(dest instanceof List) || !(source instanceof List) && dest instanceof List) {
            throw PamirsException.construct(BASE_COPY_DATA_ERROR).errThrow();
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
            Map<String, Object> sourceMap = JSON.parseObject(JSON.toJSONString(source), TR_MAP_SO);
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
