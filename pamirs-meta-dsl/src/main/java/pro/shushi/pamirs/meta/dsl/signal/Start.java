package pro.shushi.pamirs.meta.dsl.signal;

import pro.shushi.pamirs.meta.dsl.fun.LogicFunInvoker;
import pro.shushi.pamirs.meta.dsl.utils.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Start implements Exe {

    private String arg;

    private String model;

    public String getArg() {
        return arg;
    }

    public void setArg(String arg) {
        this.arg = arg;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public void dispatch(Map<String, Object> context) {
        try {
            Object objs = context.get(arg);
            Object obj = null != objs && objs.getClass().isArray()?((Object[])objs)[0]:objs;
            if (StringUtils.isBlank(arg) || null == obj) {
                obj = new HashMap<>();
            }
            if (obj instanceof Map) {
                obj = LogicFunInvoker.lowcodeMapToModelMap(obj, model);
            } else if (obj instanceof List) {
                obj = LogicFunInvoker.lowcodeModelToMapList((List<Object>)obj, model);
            } else {
                obj = LogicFunInvoker.lowcodeModelToMap(obj, model);
            }
            LogicFunInvoker.putResult(context, obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
