package pro.shushi.pamirs.framework.faas.computer;

import pro.shushi.pamirs.framework.faas.script.ScriptRunner;
import pro.shushi.pamirs.meta.api.core.compute.Prioritized;
import pro.shushi.pamirs.meta.api.core.faas.computer.FilterContext;
import pro.shushi.pamirs.meta.api.core.faas.computer.FunctionComputer;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.enmu.ScriptType;

/**
 * js计算
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@SuppressWarnings("unused")
public class JsComputer implements FunctionComputer, Prioritized {

    @Override
    public Object compute(Function function, Object... args) {
        return ScriptRunner.run(function, args);
    }

    @Override
    public int priority() {
        return 2;
    }

    @Override
    public boolean filter(FilterContext filterContext, Function function) {
        return type().equals(function.getScriptType());
    }

    @Override
    public ScriptType type() {
        return ScriptType.JS;
    }

}
