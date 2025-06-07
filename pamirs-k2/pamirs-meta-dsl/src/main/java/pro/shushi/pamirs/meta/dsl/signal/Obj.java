package pro.shushi.pamirs.meta.dsl.signal;

import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.base.BaseModel;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.dsl.fun.LogicFunInvoker;
import pro.shushi.pamirs.meta.dsl.utils.ObjectUtils;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.util.*;

import static pro.shushi.pamirs.meta.common.constants.VariableNameConstants.entityModel;
import static pro.shushi.pamirs.meta.dsl.enumeration.DslExpEnumerate.BASE_OBJ_HANDLE_ERROR;
import static pro.shushi.pamirs.meta.dsl.enumeration.DslExpEnumerate.BASE_OBJ_PARAMS_ERROR;

public class Obj implements Exe {

    private String model;

    private String arg;

    private List<Field> fields = new ArrayList<>();

    @Override
    public void dispatch(Map<String, Object> context) {
        try {
            Object obj = LogicFunInvoker.getArg(arg, context);
            if (null == obj) {
                obj = new HashMap<>();
            }
            if (obj instanceof List) {
                throw PamirsException.construct(BASE_OBJ_PARAMS_ERROR)
                        .appendMsg(LogicFunInvoker.fetchCurrentStateName(context) + "节点入参格式错误，不能为集合").errThrow();
            }
            for (Field field : fields) {
                Object value = LogicFunInvoker.exp(field.getExp(), context);
                obj = ObjectUtils.setValue(obj, field.getName(), value);
            }

            if (IWrapper.MODEL_MODEL.equals(model)) {
                String rsql = (String) FieldUtils.getFieldValue(obj, "rsql");
                @SuppressWarnings({"unchecked"})
                List<String> selects = (List<String>) FieldUtils.getFieldValue(obj, "selects");
                String _entityModel = Optional.ofNullable(context.get(entityModel))
                        .map(String::valueOf)
                        .orElse(BaseModel.MODEL_MODEL);
                obj = Models.pops().<IWrapper<?>>construct(_entityModel).setSelects(selects).setRsql(rsql);
            }

            LogicFunInvoker.putResult(context, obj);
        } catch (Exception e) {
            throw PamirsException.construct(BASE_OBJ_HANDLE_ERROR, e).errThrow();
        }
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getArg() {
        return arg;
    }

    public void setArg(String arg) {
        this.arg = arg;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

}
