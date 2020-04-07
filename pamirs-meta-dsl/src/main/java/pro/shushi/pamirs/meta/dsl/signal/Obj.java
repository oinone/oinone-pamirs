package pro.shushi.pamirs.meta.dsl.signal;

import pro.shushi.pamirs.meta.dsl.fun.LogicFunInvoker;
import pro.shushi.pamirs.meta.dsl.utils.ObjectUtils;
import pro.shushi.pamirs.meta.dsl.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Obj implements Exe{

    private String model;

    private String arg;

    private List<Field> fields = new ArrayList<>();

    @Override
    public void dispatch(Map<String, Object> context) {
        try{
            Object obj = context.get(arg);
            if(StringUtils.isBlank(arg) || null == obj){
                obj = new HashMap<>();
            }else{
                if(obj instanceof List){
                    throw new RuntimeException(LogicFunInvoker.fetchCurrentStateName(context) + "节点入参格式错误，不能为集合");
                }
            }
            for(Field field : fields){
                Object value = LogicFunInvoker.exp(field.getExp(), context);
                obj = ObjectUtils.setValue(obj, field.getName(),value);
            }
            LogicFunInvoker.putResult(context, obj);
        }catch (Exception e){
            throw new RuntimeException(e);
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
