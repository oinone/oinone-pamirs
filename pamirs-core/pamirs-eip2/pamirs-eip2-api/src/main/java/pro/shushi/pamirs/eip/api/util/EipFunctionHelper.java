package pro.shushi.pamirs.eip.api.util;

import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.core.common.function.FunctionHelper;
import pro.shushi.pamirs.core.common.function.context.ArgumentContext;
import pro.shushi.pamirs.core.common.function.context.FunctionContext;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;

import java.util.List;

public class EipFunctionHelper {

    public static Object[] convertArguments(String namespace, String fun, SuperMap interfaceContext) {
        Function function = PamirsSession.getContext().getFunction(namespace, fun);
        FunctionContext functionContext = FunctionHelper.getFunctionContext(function);
        //请求入参处理
        List<ArgumentContext> args = functionContext.getArgumentList();
        Object[] argObjs = null;
        if (CollectionUtils.isNotEmpty(args)) {
            argObjs = new Object[args.size()];
            for (int i = 0; i < args.size(); i++) {
                ArgumentContext argumentContext = args.get(i);
                Object argObj = interfaceContext == null ? null : interfaceContext.getIteration(argumentContext.getName());
                argObjs[i] = FunctionHelper.deserializationArgument(argumentContext, FunctionHelper.serializationArgument(argObj));
            }
        }
        return argObjs;
    }


}
