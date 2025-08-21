package pro.shushi.pamirs.eip.api.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.core.common.function.FunctionHelper;
import pro.shushi.pamirs.core.common.function.context.ArgumentContext;
import pro.shushi.pamirs.core.common.function.context.FunctionContext;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;

import java.util.List;

/**
 * 走前端序列化处理
 */
public class EipOpenFunctionHelper {

    public static Object[] convertArguments(String namespace, String fun, SuperMap interfaceContext) {
        Function function = PamirsSession.getContext().getFunction(namespace, fun);
        return convertArguments(function, interfaceContext);
    }

    public static Object[] convertArguments(Function function, SuperMap interfaceContext) {
        FunctionContext functionContext = FunctionHelper.getFunctionContext(function);
        //请求入参处理
        List<ArgumentContext> args = functionContext.getArgumentList();
        Object[] argObjs = null;
        if (CollectionUtils.isNotEmpty(args)) {
            argObjs = new Object[args.size()];
            for (int i = 0; i < args.size(); i++) {
                ArgumentContext argumentContext = args.get(i);
                Object argObj = interfaceContext == null ? null : interfaceContext.getIteration(argumentContext.getName());
                if (argObj != null) {
                    String argJson = FunctionHelper.serializationArgument(argObj);
                    if (StringUtils.isEmpty(argumentContext.getModel())) {
                        argObj = FunctionHelper.deserializationArgument(argumentContext, argJson);
                    } else {
                        argObj = FunctionHelper.deserializationClientArgument(argumentContext, argJson);
                    }
                }
                if (argObj instanceof Pagination) {
                    ((Pagination<?>) argObj).setModel(functionContext.getNamespace());
                } else if (argObj instanceof IWrapper) {
                    ((IWrapper<?>) argObj).setModel(functionContext.getNamespace());
                }
                argObjs[i] = argObj;
            }
        }
        return argObjs;
    }

}
