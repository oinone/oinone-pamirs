package pro.shushi.pamirs.eip.api.builder;

import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipIdempotentProcessor;
import pro.shushi.pamirs.eip.api.IEipProcessCallback;

import java.lang.reflect.Type;

/**
 * @author Adamancy Zhang
 * @date 2020-11-30 11:21
 */
public class DefaultEipExecutorContextBuilder {

    private final SuperMap executorContext;

    private DefaultEipExecutorContextBuilder(SuperMap executorContext) {
        this.executorContext = executorContext;
    }

    public static DefaultEipExecutorContextBuilder newInstance() {
        return new DefaultEipExecutorContextBuilder(new SuperMap());
    }

    public static DefaultEipExecutorContextBuilder newInstance(SuperMap executorContext) {
        return new DefaultEipExecutorContextBuilder(executorContext);
    }

    public DefaultEipExecutorContextBuilder putExecutorContext(String key, Object value) {
        this.executorContext.putIteration(key, value);
        return this;
    }

    public EipInterfaceSetting setting(String interfaceName) {
        return new EipInterfaceSetting(this, interfaceName);
    }

    public SuperMap build() {
        return this.executorContext;
    }

    /**
     * 接口设置器
     */
    public static class EipInterfaceSetting {

        private final DefaultEipExecutorContextBuilder contextBuilder;

        private final String interfaceName;

        private EipInterfaceSetting(DefaultEipExecutorContextBuilder contextBuilder, String interfaceName) {
            this.contextBuilder = contextBuilder;
            this.interfaceName = interfaceName;
        }

        /**
         * 返回到执行器，并继续
         *
         * @return 执行器
         */
        public DefaultEipExecutorContextBuilder and() {
            return this.contextBuilder;
        }

        /**
         * 设置幂等处理
         *
         * @param idempotentProcessor 幂等处理
         * @return 接口设置器
         */
        public EipInterfaceSetting setIdempotentProcessor(IEipIdempotentProcessor<SuperMap> idempotentProcessor) {
            contextBuilder.executorContext.putIteration(IEipContext.RESPONSE_IDEMPOTENT_PROCESSOR_PREFIX + interfaceName, idempotentProcessor);
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
            contextBuilder.executorContext.putIteration(IEipContext.RESPONSE_IDEMPOTENT_PROCESSOR_TYPE_PREFIX + interfaceName, type);
            contextBuilder.executorContext.putIteration(IEipContext.RESPONSE_IDEMPOTENT_PROCESSOR_PREFIX + interfaceName, idempotentProcessor);
            return this;
        }

        /**
         * 设置单次幂等处理
         *
         * @param idempotentProcessorEach 幂等单次处理
         * @return 接口设置器
         */
        public EipInterfaceSetting setIdempotentProcessorEach(IEipIdempotentProcessor<SuperMap> idempotentProcessorEach) {
            contextBuilder.executorContext.putIteration(IEipContext.RESPONSE_IDEMPOTENT_PROCESSOR_EACH_PREFIX + interfaceName, idempotentProcessorEach);
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
            contextBuilder.executorContext.putIteration(IEipContext.RESPONSE_IDEMPOTENT_PROCESSOR_EACH_TYPE_PREFIX + interfaceName, cls);
            contextBuilder.executorContext.putIteration(IEipContext.RESPONSE_IDEMPOTENT_PROCESSOR_EACH_PREFIX + interfaceName, idempotentProcessorEach);
            return this;
        }

        /**
         * 设置响应回调
         *
         * @param responseCallback 响应回调
         * @return 接口设置器
         */
        public EipInterfaceSetting setResponseCallback(IEipProcessCallback<SuperMap> responseCallback) {
            contextBuilder.executorContext.putIteration(IEipContext.RESPONSE_CALLBACK_PREFIX + interfaceName, responseCallback);
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
            contextBuilder.executorContext.putIteration(IEipContext.RESPONSE_CALLBACK_TYPE_PREFIX + interfaceName, type);
            contextBuilder.executorContext.putIteration(IEipContext.RESPONSE_CALLBACK_PREFIX + interfaceName, responseCallback);
            return this;
        }

        /**
         * 设置响应单次回调
         *
         * @param responseCallbackEach 响应单次回调
         * @return 接口设置器
         */
        public EipInterfaceSetting setResponseCallbackEach(IEipProcessCallback<SuperMap> responseCallbackEach) {
            contextBuilder.executorContext.putIteration(IEipContext.RESPONSE_CALLBACK_EACH_PREFIX + interfaceName, responseCallbackEach);
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
            contextBuilder.executorContext.putIteration(IEipContext.RESPONSE_CALLBACK_EACH_TYPE_PREFIX + interfaceName, cls);
            contextBuilder.executorContext.putIteration(IEipContext.RESPONSE_CALLBACK_EACH_PREFIX + interfaceName, responseCallbackEach);
            return this;
        }
    }
}
