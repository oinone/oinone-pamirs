package pro.shushi.pamirs.meta.dsl.signal;

import pro.shushi.pamirs.meta.dsl.fun.LogicFunInvoker;
import pro.shushi.pamirs.meta.dsl.utils.StringUtils;

import java.util.Map;

public class End implements Exe {

    private String name;

    private String exp;

    @Override
    public void dispatch(Map<String, Object> context) {
        try{
            if(!StringUtils.isBlank(exp)){
                Object result = LogicFunInvoker.exp(exp, context);
                LogicFunInvoker.putReturn(context, result);
            }
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

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

}
