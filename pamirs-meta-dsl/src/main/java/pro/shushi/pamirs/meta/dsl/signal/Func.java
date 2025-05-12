package pro.shushi.pamirs.meta.dsl.signal;

import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.dsl.fun.LogicFunInvoker;
import pro.shushi.pamirs.meta.dsl.model.TxConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static pro.shushi.pamirs.meta.dsl.enumeration.DslExpEnumerate.BASE_FUNC_HANDLE_ERROR;

public class Func extends Tx implements Exe {

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

            Object result;
            TxConfig txConfig = tx();
            if(null == txConfig){
                result = LogicFunInvoker.exe(namespace, name, l.toArray());
            }else{
                result = LogicFunInvoker.exeWithTx(namespace, name, txConfig, l.toArray());
            }
            LogicFunInvoker.putResult(context, result);
        }catch(Exception e){
            throw PamirsException.construct(BASE_FUNC_HANDLE_ERROR, e).errThrow();
        }
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
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
