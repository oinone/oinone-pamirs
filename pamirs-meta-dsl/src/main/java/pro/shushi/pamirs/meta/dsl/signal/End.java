package pro.shushi.pamirs.meta.dsl.signal;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.dsl.fun.LogicFunInvoker;

import java.util.Map;

import static pro.shushi.pamirs.meta.dsl.enumeration.DslExpEnumerate.BASE_END_HANDLE_ERROR;

public class End implements Exe {

    private String name;

    private String exp;

    @Override
    public void dispatch(Map<String, Object> context) {
        try{
            if(StringUtils.isNotBlank(exp)){
                Object result = LogicFunInvoker.exp(exp, context);
                LogicFunInvoker.putReturn(context, result);
            }
        }catch(Exception e){
            throw PamirsException.construct(BASE_END_HANDLE_ERROR, e).errThrow();
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
