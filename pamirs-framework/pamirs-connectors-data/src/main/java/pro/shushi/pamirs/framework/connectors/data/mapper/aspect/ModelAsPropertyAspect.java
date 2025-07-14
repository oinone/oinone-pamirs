package pro.shushi.pamirs.framework.connectors.data.mapper.aspect;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.mapper.context.MapperContext;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.api.session.PamirsSession;

/**
 * Mapper获取AsProperty属性切面
 *
 * @author Adamancy Zhang at 11:41 on 2025-07-14
 */
@Slf4j
@Aspect
@Order(-10)
@Component
@Conditional(ModelAsPropertySwitchCondition.class)
public class ModelAsPropertyAspect {

    private static final String UNSUPPORTED_AS_PROPERTIES = "UnsupportedAsProperties";

    @Pointcut("@annotation(pro.shushi.pamirs.framework.connectors.data.mapper.aspect.ModelFill) || @within(pro.shushi.pamirs.framework.connectors.data.mapper.aspect.ModelFill)")
    public void pointcutMapper() {
    }

    @Before("pointcutMapper()")
    public void changeAsProperty(JoinPoint point) {
        String model = null;
        Class<?> modelClass = MapperAspect.getModelClass(point);
        Object[] args = point.getArgs();
        if (ArrayUtils.isNotEmpty(args)) {
            if (null == modelClass || DataMap.class.isAssignableFrom(modelClass)) {
                model = MapperContext.model(args);
            } else {
                model = Models.api().getModel(modelClass);
                MapperContext.setModel(model, args);
            }
        }
        if (StringUtils.isBlank(model)) {
            model = UNSUPPORTED_AS_PROPERTIES;
        }
        if (log.isDebugEnabled()) {
            log.debug("Using AsProperty. point signature: {}, model: {}", point.getSignature(), model);
        }
        PamirsSession.pushAsProperty(model);
    }

    @AfterReturning(value = "pointcutMapper()", returning = "retVal")
    public void restoreAsProperty(JoinPoint point, Object retVal) {
        String model = PamirsSession.clearAsProperty();
        if (log.isDebugEnabled()) {
            log.debug("Restore AsProperty. point signature: {}, model: {}", point.getSignature(), model);
        }
    }

    @AfterThrowing(value = "pointcutMapper()")
    public void restoreAsProperty(JoinPoint point) {
        String model = PamirsSession.clearAsProperty();
        if (log.isDebugEnabled()) {
            log.debug("Restore AsProperty for exception. point signature: {}, model: {}", point.getSignature(), model);
        }
    }
}
