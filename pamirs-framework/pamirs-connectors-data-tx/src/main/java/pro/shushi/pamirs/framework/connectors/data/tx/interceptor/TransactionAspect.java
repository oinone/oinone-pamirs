package pro.shushi.pamirs.framework.connectors.data.tx.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.framework.connectors.data.tx.transaction.Tx;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.util.NamespaceAndFunUtils;

import java.lang.reflect.Method;

/**
 * 事务切面
 * <p>
 * 2020/6/4 2:04 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
@Aspect
@Order(-10)
// 暂不启用，交由ManagementAspect拦截
//@Component
public class TransactionAspect {

    @SuppressWarnings("unused")
    @Around("@within(pamirsTransactional)||@annotation(pamirsTransactional)")
    public Object transactionInterceptor(ProceedingJoinPoint point, PamirsTransactional pamirsTransactional) {
        Signature signature = point.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        String namespace = NamespaceAndFunUtils.namespace(method);
        String fun = NamespaceAndFunUtils.fun(method);
        return Tx.build(namespace, fun).execute((transactionStatus) -> {
            try {
                return point.proceed();
            } catch (Throwable throwable) {
                throw new TxException(throwable);
            }
        });
    }

}