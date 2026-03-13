package pro.shushi.pamirs.framework.gateways.graph.java.debug;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import graphql.execution.ExecutionPath;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.api.FrontRequestInitDeal;
import pro.shushi.pamirs.framework.common.api.StackTraceApi;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.compute.Prioritized;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.util.*;

/**
 * 请求堆栈追踪API
 * <p>
 * 2024/4/3 5:53 下午
 *
 * @author cpc@shushi.pro
 * @version 1.0.0
 */
@Component
@SPI.Service
@Slf4j
public class DefaultStackTraceApi implements StackTraceApi {

    @Override
    public void stackTraceException(DataFetcherExceptionHandlerParameters handlerParameters, DataFetcherExceptionHandlerResult result, Throwable e, ExecutionPath path) {
        Map<String, FrontRequestExceptionDeal> frontRequestExceptionDealMap = BeanDefinitionUtils.getBeansOfType(FrontRequestExceptionDeal.class);
        List<FrontRequestExceptionDeal> frontRequestExceptionDeals = new ArrayList<>(Objects.requireNonNull(frontRequestExceptionDealMap).values());
        frontRequestExceptionDeals.sort(Comparator.comparingInt(v -> ((Prioritized) v).priority()));
        for (FrontRequestExceptionDeal frontRequestExceptionDeal : frontRequestExceptionDeals) {
            try {
                frontRequestExceptionDeal.stackTrace(handlerParameters, result, e, path);
            } catch (Throwable throwable) {
                //忽略
                log.error("Exception trace class execution failed: [{}], exception:{}", frontRequestExceptionDeal.getClass().getTypeName(), ExceptionUtils.getStackTrace(throwable));
            }
        }
    }

    @Override
    public void stackTrace(ExecutionResult executionResult, ExecutionInput executionInput) {
        Map<String, FrontRequestResultDeal> frontRequestResultDealMap = BeanDefinitionUtils.getBeansOfType(FrontRequestResultDeal.class);
        List<FrontRequestResultDeal> frontRequestResultDeals = new ArrayList<>(Objects.requireNonNull(frontRequestResultDealMap).values());
        frontRequestResultDeals.sort(Comparator.comparingInt(v -> ((Prioritized) v).priority()));
        for (FrontRequestResultDeal frontRequestResultDeal : frontRequestResultDeals) {
            try {
                frontRequestResultDeal.stackTrace(executionResult, executionInput);
            } catch (Throwable throwable) {
                //忽略
                log.error("Exception trace class execution failed: [{}], exception:{}", frontRequestResultDeal.getClass().getTypeName(), ExceptionUtils.getStackTrace(throwable));
            }
        }
    }

    @Override
    public void init(ExecutionInput executionInput) {
        Map<String, FrontRequestInitDeal> frontRequestInitDealMap = BeanDefinitionUtils.getBeansOfType(FrontRequestInitDeal.class);
        List<FrontRequestInitDeal> frontRequestInitDeals = new ArrayList<>(Objects.requireNonNull(frontRequestInitDealMap).values());
        frontRequestInitDeals.sort(Comparator.comparingInt(Prioritized::priority));
        for (FrontRequestInitDeal frontRequestInitDeal : frontRequestInitDeals) {
            try {
                frontRequestInitDeal.init(executionInput);
            } catch (Throwable throwable) {
                //忽略
                log.error("Exception trace class initialization execution failed: [{}], exception:{}", frontRequestInitDeal.getClass().getTypeName(), ExceptionUtils.getStackTrace(throwable));
            }
        }
    }

    @Override
    public void init(Invoker<?> invoker, Invocation invocation) {
        Map<String, FrontRequestInitDeal> frontRequestInitDealMap = BeanDefinitionUtils.getBeansOfType(FrontRequestInitDeal.class);
        List<FrontRequestInitDeal> frontRequestInitDeals = new ArrayList<>(Objects.requireNonNull(frontRequestInitDealMap).values());
        frontRequestInitDeals.sort(Comparator.comparingInt(Prioritized::priority));
        for (FrontRequestInitDeal frontRequestInitDeal : frontRequestInitDeals) {
            try {
                frontRequestInitDeal.init(invoker, invocation);
            } catch (Throwable throwable) {
                //忽略
                log.error("Exception trace class initialization execution failed: [{}], exception:{}", frontRequestInitDeal.getClass().getTypeName(), ExceptionUtils.getStackTrace(throwable));
            }
        }
    }
}
