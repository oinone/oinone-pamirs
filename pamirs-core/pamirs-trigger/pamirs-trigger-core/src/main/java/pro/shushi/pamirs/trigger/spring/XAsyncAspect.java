package pro.shushi.pamirs.trigger.spring;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.function.FunctionHelper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.faas.FunApi;
import pro.shushi.pamirs.meta.api.core.orm.systems.directive.SystemDirectiveEnum;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.bit.SessionMetaBit;
import pro.shushi.pamirs.meta.common.util.BitUtil;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.util.JsonUtils;
import pro.shushi.pamirs.middleware.schedule.common.Result;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;
import pro.shushi.pamirs.trigger.annotation.XAsync;
import pro.shushi.pamirs.trigger.model.ExecuteTaskAction;
import pro.shushi.pamirs.trigger.service.ExecuteTaskActionService;

import jakarta.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author Adamancy Zhang
 * @date 2020-11-11 16:09
 */
@Slf4j
@Aspect
@Component
@Fun(XAsyncExecutor.FUN_NAMESPACE)
public class XAsyncAspect implements XAsyncExecutor {

    @Autowired(required = false)
    private ExecuteTaskActionService executeTaskActionService;

    @Resource
    private FunApi funApi;

    @Pointcut("@annotation(pro.shushi.pamirs.trigger.annotation.XAsync)")
    protected void doWorker() {
    }

    @Around("doWorker()")
    private Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        Function function = funApi.fetch(method);
        XAsync xAsync = method.getDeclaredAnnotation(XAsync.class);
        SessionMetaBit sessionMetaBit = PamirsSession.directive();
        if (BitUtil.has(sessionMetaBit.bitValue(), SystemDirectiveEnum.SYNC.getValue()) || executeTaskActionService == null) {
            return joinPoint.proceed();
        } else {
            executeTaskActionService.submit((ExecuteTaskAction) new ExecuteTaskAction()
                    .setDelayTimeValue(xAsync.delayTime())
                    .setDelayTimeUnit(xAsync.delayTimeUnit())
                    .setNextRetryTimeUnit(xAsync.nextRetryTimeUnit())
                    .setNextRetryTimeValue(xAsync.nextRetryTimeValue())
                    .setLimitRetryNumber(xAsync.limitRetryNumber())
                    .setTaskType(xAsync.taskType())
                    .setDisplayName(xAsync.displayName())
                    .setExecuteNamespace(getInterfaceName())
                    .setExecuteFun(getMethodName())
                    .setExecuteFunction(new FunctionDefinition()
                            .setGroup(function.getGroup())
                            .setVersion(function.getVersion())
                            .setTimeout(function.getTimeout()))
                    .setContext(getContextData(function, joinPoint.getArgs())));
            return null;
        }
    }

    private String getContextData(Function function, Object[] args) {
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(FunctionHelper.getFunctionContext(function));
        jsonArray.addAll(Arrays.asList(args));
        return JsonUtils.toJSONString(jsonArray, JSON.DEFAULT_GENERATE_FEATURE & ~SerializerFeature.DisableCircularReferenceDetect.getMask());
    }

    @Override
    public String getInterfaceName() {
        return XAsyncExecutor.FUN_NAMESPACE;
    }

    @Override
    public String getMethodName() {
        return XAsyncExecutor.EXECUTE_INTERFACE_METHOD_NAME;
    }

    @pro.shushi.pamirs.meta.annotation.Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE})
    @Override
    public Result<Void> executeExecuteTaskAction(ScheduleItem task) {
        Result<Void> result = new Result<>();
        try {
            FunctionHelper.invoke(JsonUtils.parseObjectList(task.getContext()));
        } catch (Throwable e) {
            log.error("execute task action execute error.", e);
            result.setFail(e.getMessage());
        }
        return result;
    }
}
