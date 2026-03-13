package pro.shushi.pamirs.framework.faas.computer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ReflectionUtils;
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
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.util.FunctionUtils;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.lang.reflect.Method;

import static pro.shushi.pamirs.framework.faas.enmu.FaasExpEnumerate.BASE_CLASS_NOT_FOUNT_ERROR;
import static pro.shushi.pamirs.framework.faas.enmu.FaasExpEnumerate.BASE_LOCAL_CALLER_UNKNOWN_ERROR;

/**
 * spring bean方法调用
 * <p>
 * 若jar包或inJVM不存在 spring bean 但存在 dubbo 服务，则RPC远程调用
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Slf4j
public class SpringComputer implements FunctionComputer, Prioritized {

    @Override
    public Object compute(Function function, Object... args) {
        try {
            Method method = FunctionUtils.getMethod(function, args);
            args = ArgUtils.handleArgs(function.getArguments(), args);
            Object bean = BeanDefinitionUtils.getBean(function.getBeanName());
            PamirsSession.directive().enableIgnoreFunManagement();
            return ReflectionUtils.invokeMethod(method, bean, args);
        } catch (ClassNotFoundException e) {
            log.error("Exception {}-function:{}", BASE_CLASS_NOT_FOUNT_ERROR.msg(), JsonUtils.toJSONString(function), e);
            throw PamirsException.construct(BASE_CLASS_NOT_FOUNT_ERROR, e).errThrow();
        } catch (PamirsException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception {}-function:{}", BASE_LOCAL_CALLER_UNKNOWN_ERROR.msg(), JsonUtils.toJSONString(function), e);
            throw PamirsException.construct(BASE_LOCAL_CALLER_UNKNOWN_ERROR, e).errThrow();
        }
    }

    @Override
    public int priority() {
        return 3;
    }

    @Override
    public boolean filter(FilterContext filterContext, Function function) {
        if (RemoteFunctionHelperHolder.get().isRemoteFunction(function)) {
            if (null == filterContext.getExcludeTypes() || !filterContext.getExcludeTypes().contains(ScriptType.REMOTE)) {
                filterContext.setHintType(ScriptType.REMOTE);
                return false;
            }
        }
        return StringUtils.isNotBlank(function.getBeanName()) && BeanDefinitionUtils.containsBean(function.getBeanName());
    }

    @Override
    public ScriptType type() {
        return ScriptType.SPRING;
    }

}
