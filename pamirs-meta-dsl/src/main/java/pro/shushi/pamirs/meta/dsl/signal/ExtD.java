package pro.shushi.pamirs.meta.dsl.signal;

import pro.shushi.pamirs.meta.dsl.fun.LogicFunInvoker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExtD implements Exe {

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
            Object result = LogicFunInvoker.extPoint(name, l.toArray());
            LogicFunInvoker.putResult(context, result);
        }catch(Exception e){
            throw new RuntimeException(e);
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
