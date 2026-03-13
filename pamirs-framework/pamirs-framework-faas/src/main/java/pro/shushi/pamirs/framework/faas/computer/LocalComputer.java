package pro.shushi.pamirs.framework.faas.computer;

import pro.shushi.pamirs.framework.faas.spi.api.remote.utils.RemoteFunctionHelperHolder;
import pro.shushi.pamirs.framework.faas.utils.ArgUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.compute.Prioritized;
import pro.shushi.pamirs.meta.api.core.faas.computer.FilterContext;
import pro.shushi.pamirs.meta.api.core.faas.computer.FunctionComputer;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.enmu.ScriptType;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.util.FunctionUtils;
import pro.shushi.pamirs.meta.util.JsonUtils;
import pro.shushi.pamirs.meta.util.MethodUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static pro.shushi.pamirs.framework.faas.enmu.FaasExpEnumerate.*;

/**
 * 本地方法调用
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Slf4j
public class LocalComputer implements FunctionComputer, Prioritized {

    private static final ConcurrentMap<String/*fun class*/, Object> FUN_OBJECT = new ConcurrentHashMap<>(256);

    @Override
    public Object compute(Function function, Object... args) {
        try {
            Method method = FunctionUtils.getMethod(function, args);
            args = ArgUtils.handleArgs(function.getArguments(), args);
            boolean isStatic = MethodUtils.isStatic(method);
            if (isStatic) {
                return method.invoke(null, args);
            } else {
                Object invokeObject = FUN_OBJECT.get(function.getClazz());
                if (null == invokeObject) {
                    synchronized (LocalComputer.class) {
                        invokeObject = FUN_OBJECT.get(function.getClazz());
                        if (null == invokeObject) {
                            FUN_OBJECT.put(function.getClazz(), Class.forName(function.getClazz()).newInstance());
                            invokeObject = FUN_OBJECT.get(function.getClazz());
                        }
                    }
                }
                return method.invoke(invokeObject, args);
            }
        } catch (PamirsException e) {
            throw e;
        } catch (InvocationTargetException | IllegalArgumentException e) {
            log.error("Exception:{} function:{}", BASE_INVOCATION_TARGET_ERROR.msg(), JsonUtils.toJSONString(function), e);
            throw PamirsException.construct(BASE_INVOCATION_TARGET_ERROR, e).errThrow();
        } catch (IllegalAccessException e) {
            log.error("Exception:{} function:{}", BASE_ILLEGAL_ACCESS_ERROR.msg(), JsonUtils.toJSONString(function), e);
            throw PamirsException.construct(BASE_ILLEGAL_ACCESS_ERROR, e).errThrow();
        } catch (ClassNotFoundException e) {
            log.error("Exception:{} function:{}", BASE_CLASS_NOT_FOUNT_ERROR.msg(), JsonUtils.toJSONString(function), e);
            throw PamirsException.construct(BASE_CLASS_NOT_FOUNT_ERROR, e).errThrow();
        } catch (Exception e) {
            log.error("Exception:{} function:{}", BASE_LOCAL_CALLER_UNKNOWN_ERROR.msg(), JsonUtils.toJSONString(function), e);
            throw PamirsException.construct(BASE_LOCAL_CALLER_UNKNOWN_ERROR, e).errThrow();
        }
    }

    @Override
    public int priority() {
        return 4;
    }

    @Override
    public boolean filter(FilterContext filterContext, Function function) {
        if (null == filterContext.getHintType()) {
            if (RemoteFunctionHelperHolder.get().isRemoteFunction(function)) {
                if (null == filterContext.getExcludeTypes() || !filterContext.getExcludeTypes().contains(ScriptType.REMOTE)) {
                    filterContext.setHintType(ScriptType.REMOTE);
                    return false;
                }
            }
        }
        return null != PamirsSession.getContext().getFunctionAllowNull(function.getNamespace(), function.getFun());
    }

    @Override
    public ScriptType type() {
        return ScriptType.LOCAL;
    }

}
