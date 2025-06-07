package pro.shushi.pamirs.framework.faas.computer;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.compute.Prioritized;
import pro.shushi.pamirs.meta.api.core.faas.computer.FilterContext;
import pro.shushi.pamirs.meta.api.core.faas.computer.FunctionComputer;
import pro.shushi.pamirs.meta.api.dto.fun.Arg;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.enmu.ScriptType;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.dsl.constants.DSLDefineConstants;
import pro.shushi.pamirs.meta.dsl.definition.helper.DefinitionHelper;
import pro.shushi.pamirs.meta.dsl.process.AutoProcess;
import pro.shushi.pamirs.meta.enmu.FunctionSourceEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pro.shushi.pamirs.framework.faas.enmu.FaasExpEnumerate.BASE_DSL_RUN_ERROR;

/**
 * DSL计算
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@SuppressWarnings("unused")
@Slf4j
public class DSLComputer implements FunctionComputer, Prioritized {

    @Override
    public Object compute(Function function, Object... args) {
        try {
            DefinitionHelper.readProcess(function.getCodes(), Lists.newArrayList(function.fetchDslKey()));
            Map<String, Object> context = new HashMap<>();
            if (function.getSource().value().equals(FunctionSourceEnum.EXTPOINT.value())) {
                List<Arg> argList = function.getArguments();
                int i = 0;
                if (null != argList) {
                    for (Arg arg : argList) {
                        context.put(arg.getName(), args[i]);
                        i++;
                    }
                }
                context.put(DSLDefineConstants.DSL_RESULT_NAME, args);
            } else if (function.getSource().value().equals(FunctionSourceEnum.ACTION.value())) {
                List<Arg> arguments = function.getArguments();
                if (null != arguments && null != args && args.length >= arguments.size()) {
                    for (int i = 0; i < arguments.size(); i++) {
                        if (null != arguments.get(i) && StringUtils.isNotBlank(arguments.get(i).getName())) {
                            context.put(arguments.get(i).getName(), args[i]);
                        }
                    }
                }
                context.put(DSLDefineConstants.DSL_RESULT_NAME, args);
            }
            context = AutoProcess.run(function.fetchDslKey(), context, Boolean.TRUE);
            return result(context.get(DSLDefineConstants.DSL_RESULT_NAME));
        } catch (Exception e) {
            throw PamirsException.construct(BASE_DSL_RUN_ERROR, e).appendMsg(function.getNamespace()).errThrow();
        }
    }

    private Object result(Object result) {
        if (result instanceof Object[]) {
            return ((Object[]) result)[0];
        } else {
            return result;
        }
    }

    @Override
    public boolean filter(FilterContext filterContext, Function function) {
        return type().equals(function.getScriptType());
    }

    @Override
    public ScriptType type() {
        return ScriptType.DSL;
    }

    @Override
    public int priority() {
        return 2;
    }

}
