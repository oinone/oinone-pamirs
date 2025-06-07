package pro.shushi.pamirs.framework.faas.fun.manage;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import static pro.shushi.pamirs.framework.faas.enmu.FaasExpEnumerate.BASE_FUNCTION_MANAGEMENT_ERROR;

@Slf4j
@Aspect
@Order(-8)
@Component
public class ExtPointAspect {

    @Pointcut("@annotation(pro.shushi.pamirs.meta.annotation.ExtPoint.Implement)")
    public void pointcutMapper() {

    }

    @Around("pointcutMapper()")
    public Object around(ProceedingJoinPoint point) {
        if (PamirsSession.directive().isIgnoreFunManagement()) {
            PamirsSession.directive().disableIgnoreFunManagement();
        }
        Object result;
        try {
            result = point.proceed();
        } catch (PamirsException e) {
            throw e;
        } catch (Throwable throwable) {
            throw PamirsException.construct(BASE_FUNCTION_MANAGEMENT_ERROR, throwable).errThrow();
        }

        return result;
    }

}