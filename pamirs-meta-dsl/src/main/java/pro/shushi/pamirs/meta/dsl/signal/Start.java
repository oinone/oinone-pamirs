package pro.shushi.pamirs.meta.dsl.signal;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.dsl.fun.LogicFunInvoker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pro.shushi.pamirs.meta.dsl.enumeration.DslExpEnumerate.BASE_START_HANDLE_ERROR;

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
            Object objs = LogicFunInvoker.getArg(arg, context);
            Object obj = null != objs && objs.getClass().isArray() ? ((Object[]) objs)[0] : objs;
            if (StringUtils.isBlank(arg) || null == obj) {
                obj = new HashMap<>();
            }
            if (obj instanceof Map) {
//                obj = LogicFunInvoker.lowcodeMapToModelMap(obj, model);
                // TODO: 2021/10/25 待确认
                obj = obj;
            } else if (obj instanceof List) {
                obj = LogicFunInvoker.lowcodeModelToMapList((List<Object>) obj, model);
            } else {
                obj = LogicFunInvoker.lowcodeModelToMap(obj, model);
            }
            LogicFunInvoker.putResult(context, obj);
        } catch (Exception e) {
            throw PamirsException.construct(BASE_START_HANDLE_ERROR, e).errThrow();
        }
    }
}
