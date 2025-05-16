package pro.shushi.pamirs.framework.connectors.data.dialect.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.DialectIgnoredHintApi;

/**
 * 忽略方言SQL解析AOP
 *
 * @author Adamancy Zhang at 16:04 on 2024-11-07
 */
@Aspect
@Order(-10)
@Component
public class DialectIgnoredAspect {

    @Pointcut("@annotation(pro.shushi.pamirs.framework.connectors.data.dialect.api.Dialect.ignored) || @within(pro.shushi.pamirs.framework.connectors.data.dialect.api.Dialect.ignored)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object aroundPointcut(ProceedingJoinPoint joinPoint) throws Throwable {
        try (DialectIgnoredHintApi ignored = DialectIgnoredHintApi.ignored()) {
            return joinPoint.proceed();
        }
    }
}
