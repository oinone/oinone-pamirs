package pro.shushi.pamirs.eip.api.constant;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.util.concurrent.Executor;

@Slf4j
public class EipConfigurationConstant {

    public static final String AUTOMATIC_RECOGNITION_HOST = "0.0.0.0";

    public static final String SINGLE_THREAD_POOL_CALLBACK_TASK_EXECUTOR = "eipCallbackTaskExecutor";

    public static final String PAMIRS_EIP_PREFIX = "pamirs.eip";

    public static final String PAMIRS_EIP_OPEN_API_PREFIX = PAMIRS_EIP_PREFIX + ".open-api";

    public static final String PAMIRS_EIP_LOG_PREFIX = PAMIRS_EIP_PREFIX + ".log";

    public static final String ENDPOINT_REST = "rest";

    public static final String STREAM_URI_PREFIX_MARK = "$streamable-uri://";

    //region EIP 系统用户常量

    public static final Long EIP_SYSTEM_USER_ID = 8848L;

    public static final String EIP_SYSTEM_USER_CODE = "eip_system";

    public static final String EIP_SYSTEM_USER_NAME = "集成接口系统用户";

    //endregion

    /**
     * <h>创建EIP专用回调单线程方法</h>
     * <p>
     * 1、请在Bean管理类中使用该方法创建线程池，并使用{@link org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean}进行单个实例控制
     * 2、创建的Bean名称，务必使用{@link EipConfigurationConstant#SINGLE_THREAD_POOL_CALLBACK_TASK_EXECUTOR}常量进行约束
     * </p>
     *
     * @return 固定单线程池
     */
    public static Executor createEipCallbackTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        log.info("注册EIP专用回调单线程池成功 [CoreSize {}] [MaxPoolSize {}]", executor.getCorePoolSize(), executor.getMaxPoolSize());
        return executor;
    }

    /**
     * @see EipFunctionConstant#FUNCTION_NAMESPACE
     * @deprecated 2.3.0
     */
    @Deprecated
    public static final String FUNCTION_NAMESPACE = EipFunctionConstant.FUNCTION_NAMESPACE;

    @Deprecated
    public static final String REQ_FUNCTION_NAMESPACE = "pamirs.eip.default.req.namespace";
    @Deprecated
    public static final String RESP_FUNCTION_NAMESPACE = "pamirs.eip.default.resp.namespace";
    @Deprecated
    public static final String EXP_FUNCTION_NAMESPACE = "pamirs.eip.default.exp.namespace";
    @Deprecated
    public static final String AUTHENTICATION_FUNCTION_NAMESPACE = "pamirs.eip.default.authentication.namespace";

    /**
     * @see EipContextConstant#RESULT_KEY
     * @deprecated 2.3.0
     */
    @Deprecated
    public static final String DEFAULT_RESULT_KEY = EipContextConstant.RESULT_KEY;

    /**
     * @see EipContextConstant#LIST_KEY
     * @deprecated 2.3.0
     */
    @Deprecated
    public static final String DEFAULT_LIST_KEY = EipContextConstant.LIST_KEY;

    /**
     * @see EipFunctionConstant#IN_OUT_CONVERTER_PREFIX
     * @deprecated 2.3.0
     */
    @Deprecated
    public static final String IN_OUT_CONVERTER_PREFIX = "EIP_IN_OUT_CONVERTER_";

    /**
     * @see EipFunctionConstant#AUTHENTICATION_PROCESSOR_PREFIX
     * @deprecated 2.3.0
     */
    @Deprecated
    public static final String AUTHENTICATION_PROCESSOR_PREFIX = "EIP_AUTHENTICATION_PROCESSOR_";

    /**
     * @see EipFunctionConstant#DEFAULT_NO_ENCRYPT_AUTHENTICATION_PROCESSOR_FUN
     * @deprecated 2.3.0
     */
    @Deprecated
    public static final String DEFAULT_NO_ENCRYPT_AUTHENTICATION_PROCESSOR_FUN = AUTHENTICATION_PROCESSOR_PREFIX + "defaultNoEncryptAuthenticationProcessor";

    /**
     * @see EipFunctionConstant#SERIALIZABLE_PREFIX
     * @deprecated 2.3.0
     */
    @Deprecated
    public static final String SERIALIZABLE_PREFIX = "EIP_SERIALIZABLE_";

    /**
     * @see EipFunctionConstant#EXCEPTION_PREDICT_PREFIX
     * @deprecated 2.3.0
     */
    @Deprecated
    public static final String EXCEPTION_PREDICT_PREFIX = "EIP_EXCEPTION_PREDICT_";
}
