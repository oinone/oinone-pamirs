package pro.shushi.pamirs.framework.faas.computer;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.faas.FunEngine;
import pro.shushi.pamirs.framework.faas.guard.ScriptInvokeGuard;
import pro.shushi.pamirs.framework.faas.script.ScriptRunner;
import pro.shushi.pamirs.meta.api.core.compute.Prioritized;
import pro.shushi.pamirs.meta.api.core.faas.computer.FilterContext;
import pro.shushi.pamirs.meta.api.core.faas.computer.FunctionComputer;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.enmu.ScriptType;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.util.ExpressionUtils;

/**
 * 脚本计算
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@SuppressWarnings("unused")
public class ScriptComputer implements FunctionComputer, Prioritized {

    @Override
    public Object compute(Function function, Object... args) {
        String expression = function.getCodes();
        if (StringUtils.isBlank(expression)) {
            return null;
        }
        // 为了提高性能，如果发现是Java方法调用，则采取反射模式
        if (ExpressionUtils.isValidSingleMethod(expression)) {
            String methodSign = StringUtils.substringBeforeLast(expression, "(");
            Function javaFunction = PamirsSession.getContext().getFunctionAllowNull(NamespaceConstants.pamirs, methodSign);
            if (null != javaFunction) {
                ScriptInvokeGuard.judgeAllow(function);
                function = javaFunction;
                return FunEngine.get().run(function, args);
            }
        }
        // 计算表达式
        return ScriptRunner.run(function, args);
    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public boolean filter(FilterContext filterContext, Function function) {
        return type().equals(function.getScriptType());
    }

    @Override
    public ScriptType type() {
        return ScriptType.SCRIPT;
    }

}
