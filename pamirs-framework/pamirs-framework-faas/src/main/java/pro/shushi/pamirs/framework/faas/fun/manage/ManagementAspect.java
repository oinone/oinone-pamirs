package pro.shushi.pamirs.framework.faas.fun.manage;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.api.SceneAnalysisDebugTraceApi;
import pro.shushi.pamirs.framework.common.utils.header.ExceptionHelper;
import pro.shushi.pamirs.framework.connectors.data.tx.transaction.Tx;
import pro.shushi.pamirs.framework.faas.computer.SpringComputer;
import pro.shushi.pamirs.framework.faas.debug.FunChainDebugTrace;
import pro.shushi.pamirs.framework.faas.debug.FunExceptionDebugTrace;
import pro.shushi.pamirs.framework.faas.debug.MainFunDebugTrace;
import pro.shushi.pamirs.framework.faas.enmu.FaasExpEnumerate;
import pro.shushi.pamirs.framework.faas.fun.FunctionManager;
import pro.shushi.pamirs.framework.faas.spi.api.remote.utils.RemoteFunctionHelperHolder;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.faas.FunApi;
import pro.shushi.pamirs.meta.api.core.faas.boot.ModulesApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.BaseModel;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.util.NamespaceAndFunUtils;

import jakarta.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Set;

import static pro.shushi.pamirs.framework.faas.enmu.FaasExpEnumerate.*;

/**
 * 函数管理器切面
 * <p>
 * 2020/6/4 2:04 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SuppressWarnings("unused")
@Slf4j
@Aspect
@Order(-8)
@Component
public class ManagementAspect {

    private static final SpringComputer SPRING_COMPUTER_INSTANCE = new SpringComputer();

    @Resource
    private FunctionManager functionManager;

    @Resource
    private FunApi funApi;

    @Pointcut("@annotation(pro.shushi.pamirs.meta.annotation.Function) || @annotation(pro.shushi.pamirs.meta.annotation.Action)")
    public void pointcutMapper() {

    }

    @Around("pointcutMapper()")
    public Object around(ProceedingJoinPoint point) {
        if (PamirsSession.directive().isIgnoreFunManagement()) {
            PamirsSession.directive().disableIgnoreFunManagement();
            Function function = fetchFunction(point);
            if (function == null) {
                return proceed(point, BASE_FUNCTION_MANAGEMENT_ERROR);
            }
            return Tx.build(function.getNamespace(), function.getFun()).execute((status) -> proceed(point, BASE_FUNCTION_MANAGEMENT_ERROR));
        }
        Function function = fetchFunction(point);
        if (null == function) {
            return proceed(point, BASE_FUNCTION_MANAGEMENT2_ERROR);
        }
        String namespace = function.getNamespace();
        String fun = function.getFun();
        ModelConfig modelConfig = PamirsSession.getContext().getSimpleModelConfig(namespace);
        if (null != modelConfig && ModelTypeEnum.ABSTRACT.equals(modelConfig.getType())) {
            try {
                if (BaseModel.MODEL_MODEL.equals(namespace)) {
                    Object[] args = point.getArgs();
                    String actualModel = Models.api().getDataModel(args);
                    if (null != actualModel) {
                        Function actualFunction = funApi.fetchAllowNull(actualModel, fun);

                        //数据管理器模型
                        Set<String> moduleSet = Spider.getDefaultExtension(ModulesApi.class).modules();
                        if (RemoteFunctionHelperHolder.get().isRemoteFunction(actualFunction)) {
                            return RemoteFunctionHelperHolder.get().run(actualFunction, point.getArgs());
                        } else {
                            //本模块实体模型，直接
                            return proceed(point, function);
                        }
                    }
                }
                return proceed(point, function);
            } catch (PamirsException e) {
                throw throwPamirsExceptionCause(e);
            } catch (Throwable e) {
                PamirsException pamirsException = ExceptionHelper.fetchPamirsException(e);
                if (pamirsException != null) {
                    if (log.isWarnEnabled()) {
                        log.warn("catch exception code: {}, msg: {}", BASE_FUNCTION_MANAGEMENT3_ERROR.code(), BASE_FUNCTION_MANAGEMENT3_ERROR.msg(), e);
                    }
                    throw throwPamirsExceptionCause(pamirsException);
                }
                throw PamirsException.construct(BASE_FUNCTION_MANAGEMENT3_ERROR, e).errThrow();
            }
        }

        Object[] args = point.getArgs();
        return functionManager.runProxy(arguments -> Models.directive().run(() -> {
            try {
                return proceed(point, function);
            } catch (PamirsException e) {
                throw throwPamirsExceptionCause(e);
            } catch (Throwable e) {
                PamirsException pamirsException = ExceptionHelper.fetchPamirsException(e);
                if (pamirsException != null) {
                    if (log.isWarnEnabled()) {
                        log.warn("catch exception code: {}, msg: {}", BASE_FUNCTION_MANAGEMENT4_ERROR.code(), BASE_FUNCTION_MANAGEMENT4_ERROR.msg(), e);
                    }
                    throw throwPamirsExceptionCause(pamirsException);
                }
                throw PamirsException.construct(BASE_FUNCTION_MANAGEMENT4_ERROR, e).errThrow();
            }
        }), function, args);
    }

    private Function fetchFunction(ProceedingJoinPoint point) {
        Signature signature = point.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        String namespace = NamespaceAndFunUtils.namespace(method);
        String fun = NamespaceAndFunUtils.fun(method);
        return funApi.fetchAllowNull(namespace, fun);
    }

    private Object proceed(ProceedingJoinPoint point, Function function) throws Throwable {
        if (!SceneAnalysisDebugTraceApi.isDebug()) {
            return point.proceed();
        }
        Object result = null;
        //增加追踪信息
        long start = System.currentTimeMillis();
        int anchorIndex = FunChainDebugTrace.anchor();
        try {
            FunChainDebugTrace.push();
            result = point.proceed();
        } catch (Throwable e) {
            FunExceptionDebugTrace.debug(function, SPRING_COMPUTER_INSTANCE, null);
            throw e;
        } finally {
            FunChainDebugTrace.debug(function, SPRING_COMPUTER_INSTANCE, start, anchorIndex);
            MainFunDebugTrace.debug(function, SPRING_COMPUTER_INSTANCE, result);
            FunChainDebugTrace.pop();
        }
        return result;
    }

    private Object proceed(ProceedingJoinPoint point, FaasExpEnumerate exp) {
        try {
            return point.proceed();
        } catch (PamirsException e) {
            throw throwPamirsExceptionCause(e);
        } catch (Throwable e) {
            PamirsException pamirsException = ExceptionHelper.fetchPamirsException(e);
            if (pamirsException != null) {
                if (log.isWarnEnabled()) {
                    log.warn("catch exception code: {}, msg: {}", exp.code(), exp.msg(), e);
                }
                throw throwPamirsExceptionCause(pamirsException);
            }
            throw PamirsException.construct(exp, e).errThrow();
        }
    }

    private PamirsException throwPamirsExceptionCause(PamirsException pamirsException) {
        int code = pamirsException.getCode();
        if (BASE_FUNCTION_MANAGEMENT_ERROR.value().equals(code) ||
                BASE_FUNCTION_MANAGEMENT2_ERROR.value().equals(code) ||
                BASE_FUNCTION_MANAGEMENT3_ERROR.value().equals(code) ||
                BASE_FUNCTION_MANAGEMENT4_ERROR.value().equals(code)) {
            PamirsException pamirsExceptionCause = ExceptionHelper.fetchPamirsException(pamirsException.getCause());
            if (pamirsExceptionCause != null) {
                pamirsException = pamirsExceptionCause;
            }
        }
        return pamirsException;
    }
}
