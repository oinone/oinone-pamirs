package pro.shushi.pamirs.meta.dsl.signal;

import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.dsl.fun.LogicFunInvoker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static pro.shushi.pamirs.meta.dsl.enumeration.DslExpEnumerate.BASE_EXTD_HANDLE_ERROR;

public class ExtD implements Exe {

    private String namespace;

    private String name;

    private List<Field> fields = new ArrayList<>();

    @Override
    public void dispatch(Map<String, Object> context) {
        try{

            List l = new ArrayList();
            if(null != fields){
                for(Field field : fields){
                    l.add(LogicFunInvoker.exp(field.getExp(), context));
                }
            }
            Object result = LogicFunInvoker.extPoint(namespace, name, l.toArray());
            LogicFunInvoker.putResult(context, result);
        }catch(Exception e){
            throw PamirsException.construct(BASE_EXTD_HANDLE_ERROR, e).errThrow();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

}
