package pro.shushi.pamirs.eip.api.executor;

import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.*;
import pro.shushi.pamirs.eip.api.entity.EipResult;
import pro.shushi.pamirs.eip.api.service.EipExecuteService;
import pro.shushi.pamirs.meta.api.CommonApiFactory;

import java.lang.reflect.Type;

/**
 * 默认SuperMap上下文执行器
 *
 * @author Adamancy Zhang at 16:51 on 2025-08-16
 */
public class EipExecutor {

    private final SuperMap executorContext;

    private EipExecutor(SuperMap executorContext) {
        this.executorContext = executorContext;
    }

    /**
     * 获取执行器，并自动构建执行器上下文
     *
     * @return 执行器
     */
    public static EipExecutor newInstance() {
        return newInstance(new SuperMap());
    }

    /**
     * 获取执行器，并指定执行器上下文
     *
     * @param executorContext 执行器上下文
     * @return 执行器
     */
    public static EipExecutor newInstance(SuperMap executorContext) {
        if (executorContext == null) {
            executorContext = new SuperMap();
        }
        return new EipExecutor(executorContext);
    }

    /**
     * 添加上下文内容
     *
     * @param key   键
     * @param value 值
     * @return 执行器
     */
    public EipExecutor putExecutorContext(String key, Object value) {
        executorContext.putIteration(key, value);
        return this;
    }

    /**
     * 为指定接口设置自定义内容
     *
     * @param interfaceName 接口名称
     * @return 接口设置器
     */
    public EipInterfaceSetting setting(String interfaceName) {
        return new EipInterfaceSetting(this, interfaceName);
    }

    /**
     * 无参调用
     *
     * @param interfaceName 接口名称
     * @return 结果集
     */
    public EipResult<SuperMap> call(String interfaceName) {
        return call(interfaceName, null);
    }

    /**
     * 有参调用
     *
     * @param interfaceName 接口名称
     * @param body          入参
     * @return 结果集
     */
    @SuppressWarnings("unchecked")
    public EipResult<SuperMap> call(String interfaceName, Object body) {
        return CommonApiFactory.getApi(EipExecuteService.class).callByInterfaceName(interfaceName, executorContext, body);
    }

    /**
     * 接口设置器
     */
    public static class EipInterfaceSetting {

        private final EipExecutor executor;

        private final String interfaceName;

        private EipInterfaceSetting(EipExecutor executor, String interfaceName) {
            this.executor = executor;
            this.interfaceName = interfaceName;
        }

        /**
         * 返回到执行器，并继续
         *
         * @return 执行器
         */
        public EipExecutor and() {
            return this.executor;
        }

        public EipInterfaceSetting setRequestConvertFunction(IEipConverter<SuperMap> converter) {
            executor.executorContext.putIteration(IEipContext.REQUEST_CONVERT_PREFIX + interfaceName, converter);
            return this;
        }

        public EipInterfaceSetting setRequestParamConvertFunction(IEipParamConverter<SuperMap> converter) {
            executor.executorContext.putIteration(IEipContext.REQUEST_PARAM_CONVERT_PREFIX + interfaceName, converter);
            return this;
        }

        /**
         * 设置幂等处理
         *
         * @param idempotentProcessor 幂等处理
         * @return 接口设置器
         */
        public EipInterfaceSetting setIdempotentProcessor(IEipIdempotentProcessor<SuperMap> idempotentProcessor) {
            executor.executorContext.putIteration(IEipContext.RESPONSE_IDEMPOTENT_PROCESSOR_PREFIX + interfaceName, idempotentProcessor);
            return this;
        }

        /**
         * 设置幂等处理（已序列化完成）
         *
         * @param type                类
         * @param idempotentProcessor 幂等处理
         * @return 接口设置器
         */
        public EipInterfaceSetting setIdempotentProcessor(Type type, IEipIdempotentProcessor<SuperMap> idempotentProcessor) {
            executor.executorContext.putIteration(IEipContext.RESPONSE_IDEMPOTENT_PROCESSOR_TYPE_PREFIX + interfaceName, type);
            executor.executorContext.putIteration(IEipContext.RESPONSE_IDEMPOTENT_PROCESSOR_PREFIX + interfaceName, idempotentProcessor);
            return this;
        }

        /**
         * 设置单次幂等处理
         *
         * @param idempotentProcessorEach 幂等单次处理
         * @return 接口设置器
         */
        public EipInterfaceSetting setIdempotentProcessorEach(IEipIdempotentProcessor<SuperMap> idempotentProcessorEach) {
            executor.executorContext.putIteration(IEipContext.RESPONSE_IDEMPOTENT_PROCESSOR_EACH_PREFIX + interfaceName, idempotentProcessorEach);
            return this;
        }

        /**
         * 设置单次幂等处理（已序列化完成）
         *
         * @param cls                     类
         * @param idempotentProcessorEach 幂等单次处理
         * @return 接口设置器
         */
        public EipInterfaceSetting setIdempotentProcessorEach(Class<?> cls, IEipIdempotentProcessor<SuperMap> idempotentProcessorEach) {
            executor.executorContext.putIteration(IEipContext.RESPONSE_IDEMPOTENT_PROCESSOR_EACH_TYPE_PREFIX + interfaceName, cls);
            executor.executorContext.putIteration(IEipContext.RESPONSE_IDEMPOTENT_PROCESSOR_EACH_PREFIX + interfaceName, idempotentProcessorEach);
            return this;
        }

        /**
         * 设置响应回调
         *
         * @param responseCallback 响应回调
         * @return 接口设置器
         */
        public EipInterfaceSetting setResponseCallback(IEipProcessCallback<SuperMap> responseCallback) {
            executor.executorContext.putIteration(IEipContext.RESPONSE_CALLBACK_PREFIX + interfaceName, responseCallback);
            return this;
        }

        /**
         * 设置响应回调（已序列化完成）
         *
         * @param type             类
         * @param responseCallback 响应回调
         * @return 接口设置器
         */
        public EipInterfaceSetting setResponseCallback(Type type, IEipProcessCallback<SuperMap> responseCallback) {
            executor.executorContext.putIteration(IEipContext.RESPONSE_CALLBACK_TYPE_PREFIX + interfaceName, type);
            executor.executorContext.putIteration(IEipContext.RESPONSE_CALLBACK_PREFIX + interfaceName, responseCallback);
            return this;
        }

        /**
         * 设置响应单次回调
         *
         * @param responseCallbackEach 响应单次回调
         * @return 接口设置器
         */
        public EipInterfaceSetting setResponseCallbackEach(IEipProcessCallback<SuperMap> responseCallbackEach) {
            executor.executorContext.putIteration(IEipContext.RESPONSE_CALLBACK_EACH_PREFIX + interfaceName, responseCallbackEach);
            return this;
        }

        /**
         * 设置响应单次回调（已序列化完成）
         *
         * @param cls                  类
         * @param responseCallbackEach 响应单次回调
         * @return 接口设置器
         */
        public EipInterfaceSetting setResponseCallbackEach(Class<?> cls, IEipProcessCallback<SuperMap> responseCallbackEach) {
            executor.executorContext.putIteration(IEipContext.RESPONSE_CALLBACK_EACH_TYPE_PREFIX + interfaceName, cls);
            executor.executorContext.putIteration(IEipContext.RESPONSE_CALLBACK_EACH_PREFIX + interfaceName, responseCallbackEach);
            return this;
        }
    }
}
