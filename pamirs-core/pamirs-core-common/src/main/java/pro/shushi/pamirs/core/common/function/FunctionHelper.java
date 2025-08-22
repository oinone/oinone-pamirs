package pro.shushi.pamirs.core.common.function;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import pro.shushi.pamirs.core.common.function.context.ArgumentContext;
import pro.shushi.pamirs.core.common.function.context.FunctionContext;
import pro.shushi.pamirs.core.common.function.context.IArgument;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.framework.orm.json.PamirsDataUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.audit.spi.DataAuditApi;
import pro.shushi.pamirs.meta.api.core.orm.systems.directive.SystemDirectiveEnum;
import pro.shushi.pamirs.meta.api.dto.fun.Arg;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.enmu.UriType;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.api.session.cache.holder.RequestMetaDataCacheApiHolder;
import pro.shushi.pamirs.meta.base.bit.SessionMetaBit;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.util.ListUtils;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Adamancy Zhang
 * @date 2020-12-26 19:08
 */
@Slf4j
public class FunctionHelper {

    /**
     * 获取函数上下文
     *
     * @param namespace 命名空间
     * @param fun       函数名称
     * @return 函数上下文
     */
    public static FunctionContext getFunctionContext(String namespace, String fun) {
        return getFunctionContext(PamirsSession.getContext().getFunction(namespace, fun));
    }

    /**
     * 获取函数上下文
     *
     * @param function 函数定义
     * @return 函数上下文
     */
    public static FunctionContext getFunctionContext(Function function) {
        SessionMetaBit sessionMetaBit = PamirsSession.directive();
        long directive = 0L;
        if (sessionMetaBit != null) {
            directive = sessionMetaBit.bitValue();
        }
        List<ArgumentContext> arguments = new ArrayList<>();
        List<Arg> argList = function.getArguments();
        for (Arg arg : argList) {
            arguments.add(new ArgumentContext()
                    .setName(arg.getName())
                    .setModel(arg.getModel())
                    .setLtype(arg.getLtype())
                    .setLtypeT(arg.getLtypeT()));
        }
        return new FunctionContext()
                .setNamespace(function.getNamespace())
                .setFun(function.getFun())
                .setGroup(function.getGroup())
                .setVersion(function.getVersion())
                .setTimeout(function.getTimeout())
                .setDirective(directive)
                .setArgumentList(arguments)
                .setSessionContext(SessionHelper.generatorSession());
    }

    /**
     * [0] -> FunctionDefinition
     * [1...] -> Object[] args
     *
     * @param jsonArray 带函数的json数组
     */
    public static void invoke(List<Object> jsonArray) {
        FunctionContext function = JsonUtils.parseObject(JsonUtils.toJSONString(jsonArray.get(0)), FunctionContext.class);
        SessionHelper.clear();
        SessionHelper.fillSession(function.getSessionContext());

        List<Object> args;
        if (jsonArray.size() == 1) {
            args = Collections.emptyList();
        } else {
            args = ListUtils.sub(jsonArray, 1, jsonArray.size());
        }

        // 元数据批量获取
        RequestMetaDataCacheApiHolder.getCommonCacheApi().computeMetaData(UriType.SCHEDULE, function.getNamespace() + CharacterConstants.SEPARATOR_COLON + function.getFun());

        // 数据审计入口
        String traceId = PamirsSession.getRequestVariables().getTraceId();
        log.info("schedule-async/trigger-traceId:" + traceId);
        Spider.getDefaultExtension(DataAuditApi.class).computeDataAuditSession(UriType.SCHEDULE, function.getNamespace(), function.getFun(), traceId);

        invoke(function, args);
    }

    /**
     * 强制同步执行函数
     *
     * @param function  函数上下文
     * @param jsonArray 不带函数的json数组
     */
    public static void invoke(FunctionContext function, List<Object> jsonArray) {
        Long directive = function.getDirective();
        if (directive == null) {
            directive = 0L;
        }
        directive = directive | SystemDirectiveEnum.SYNC.getValue();
        int parameterCount = jsonArray.size();
        if (parameterCount == 0) {
            Models.directive().run(() -> Fun.run(function.getNamespace(), function.getFun()), directive);
        } else {
            List<Object> args = new ArrayList<>();
            List<ArgumentContext> arguments = function.getArgumentList();
            for (int i = 0; i < arguments.size(); i++) {
                Object arg = jsonArray.get(i);
                String _arg = serializationArgument(arg);
                Object argument = deserializationArgument(arguments.get(i), _arg);
                args.add(argument);
            }
            Models.directive().run(() -> Fun.run(function.getNamespace(), function.getFun(), args.toArray(new Object[parameterCount])), directive);
        }
    }

    public static String serializationArgument(Object arg) {
        if (arg == null) {
            return null;
        }
        if (arg instanceof String) {
            return (String) arg;
        }
        return JsonUtils.toJSONString(arg, JSON.DEFAULT_GENERATE_FEATURE & ~SerializerFeature.DisableCircularReferenceDetect.getMask());
    }

    public static Object deserializationArgument(IArgument argument, String arg) {
        if (arg == null) {
            return null;
        }
        try {
            Class<?> cls = Class.forName(argument.getLtype());
            if (String.class.equals(cls)) {
                return arg;
            } else {
                boolean isArray = false;
                if (JSONObject.isValidArray(arg)) {
                    isArray = true;
                }
                Class<?> clsT = null;
                if (isArray) {
                    clsT = Class.forName(argument.getLtypeT());
                }
                if (cls.isAssignableFrom(arg.getClass())) {
                    if (isArray) {
                        return JsonUtils.parseObjectList(arg, clsT);
                    } else {
                        return arg;
                    }
                } else {
                    if (isArray) {
                        return JsonUtils.parseObjectList(arg, clsT);
                    } else {
                        return JsonUtils.parseObject(arg, cls);
                    }
                }
            }
        } catch (Throwable e) {
            log.error("deserialization argument type convert error", e);
            return arg;
        }
    }

    public static Object deserializationClientArgument(IArgument argument, String arg) {
        if (arg == null) {
            return null;
        }
        try {
            boolean isArray = false;
            if (JSONObject.isValidArray(arg)) {
                isArray = true;
            }
            if (isArray) {
                List<String> argList = JsonUtils.parseObjectList(arg, String.class);
                return argList.stream().map(_arg -> PamirsDataUtils.parseModelObject(argument.getModel(), _arg)).collect(Collectors.toList());
            } else {
                String model = argument.getModel();
                if (IWrapper.MODEL_MODEL.equals(model)) {
                    return JsonUtils.parseObject(arg, QueryWrapper.class);
                }
                return PamirsDataUtils.parseModelObject(model, arg);
            }
        } catch (Throwable e) {
            log.error("deserialization client argument type convert error", e);
            return arg;
        }
    }
}
