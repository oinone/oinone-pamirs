package pro.shushi.pamirs.framework.faas.fun;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.tx.transaction.Tx;
import pro.shushi.pamirs.framework.faas.extpoint.BuiltinExtPointExecutorApi;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.faas.hook.HookApi;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestVariables;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.bit.SessionMetaBit;
import pro.shushi.pamirs.meta.common.util.ArrayUtils;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 函数API实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
@Slf4j
@Component
public class FunctionManager {

    @Resource
    private HookApi hookApi;

    @Resource
    private BuiltinExtPointExecutorApi builtinExtPointExecutor;

    public Object runProxy(java.util.function.Function<Object[], Object> consumer, Function function, Object... args) {
        SessionMetaBit directive = PamirsSession.directive();
        if (directive.isFromClient() || directive.isBuiltAction()) {
            directive.disableFromClient();
            boolean isHook = directive.isHook();
            boolean isDoExtPoint = directive.isDoExtPoint();
            if (isHook) {
                hookApi.before(function.getNamespace(), function.getFun(), args);
            }
            Object result = Tx.build(function.getNamespace(), function.getFun()).execute((transactionStatus) -> {
                Object txResult;
                // 植入预制结果集
                String supplier = "ctxHack" + function.getNamespace() + function.getFun();
                Object supplierObj = Optional.ofNullable(PamirsSession.getRequestVariables())
                        .map(PamirsRequestVariables::getVariables)
                        .map(_variables -> _variables.get(supplier))
                        .orElse(null);
                if (null != supplierObj) {
                    try {
                        txResult = ((Supplier<?>) supplierObj).get();
                    } finally {
                        Optional.ofNullable(PamirsSession.getRequestVariables())
                                .map(PamirsRequestVariables::getVariables)
                                .ifPresent(_variables -> _variables.remove(supplier));
                    }
                } else if (isDoExtPoint) {
                    Object[] tArgs = ArrayUtils.toArray(builtinExtPointExecutor.before(function, args));
                    txResult = builtinExtPointExecutor.override(function, consumer, tArgs);
                    builtinExtPointExecutor.callback(function, args);
                    txResult = builtinExtPointExecutor.after(function, txResult);
                } else {
                    txResult = consumer.apply(args);
                }
                return txResult;
            });
            if (isHook) {
                hookApi.after(function.getNamespace(), function.getFun(), result);
            }
            return result;
        } else {
            return Tx.build(function.getNamespace(), function.getFun())
                    .execute((transactionStatus) -> consumer.apply(args));
        }
    }

}
