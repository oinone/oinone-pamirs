package pro.shushi.pamirs.eip.api;

import java.io.Serializable;
import java.util.Map;

/**
 * 上下文
 *
 * @param <T> 上下文承载对象类型
 * @author Adamancy Zhang
 * @date 2020-07-06 10:01
 */
public interface IEipContext<T> extends Serializable {

    String DEFAULT_LIST_FLAG_KEY = "[*]";

    String DEFAULT_LIST_FIRST_FLAG_KEY = "[0]";

    String EXCHANGE_PROPERTY_KEY = "exchange.properties";

    String HEADER_PARAMS_KEY = "http.headers";

    String URL_DYNAMIC_PARAMS_KEY = "http.url.dynamic.params";

    String URL_QUERY_PARAMS_KEY = "http.url.query.params";

    String DEFAULT_ERROR_CODE_KEY = "eip.result.errorCode";

    String DEFAULT_ERROR_MESSAGE_KEY = "eip.result.errorMessage";

    String REQUEST_CONVERT_PREFIX = "config.request.convert.function.";

    String REQUEST_PARAM_CONVERT_PREFIX = "config.request.param-convert.function.";

    String RESPONSE_CALLBACK_PREFIX = "config.callback.function.";

    String RESPONSE_CALLBACK_TYPE_PREFIX = "config.callback.type.";

    String RESPONSE_CALLBACK_EACH_PREFIX = "config.callbackEach.function.";

    String RESPONSE_CALLBACK_EACH_TYPE_PREFIX = "config.callbackEach.type.";

    String RESPONSE_IDEMPOTENT_PROCESSOR_PREFIX = "config.idempotent.function.";

    String RESPONSE_IDEMPOTENT_PROCESSOR_TYPE_PREFIX = "config.idempotent.type.";

    String RESPONSE_IDEMPOTENT_PROCESSOR_EACH_PREFIX = "config.idempotentEach.function.";

    String RESPONSE_IDEMPOTENT_PROCESSOR_EACH_TYPE_PREFIX = "config.idempotentEach.type.";

    String PAGING_PREFIX = "config.paging.";

    String PAGING_ENABLED_SUFFIX = ".enabled";

    String PAGING_SIZE_SUFFIX = ".size";

    String PAGING_START_PAGE_SUFFIX = ".startPage";

    String PAGING_END_PAGE_SUFFIX = ".endPage";

    String PAGING_CURRENT_PAGE_SUFFIX = ".page";

    String PAGING_OFFSET_SUFFIX = ".offset";

    String REQUEST_STORE_PREFIX = "request.in.store.";

    String REQUEST_ALWAYS_USING_REQUEST_STORE_KEY = REQUEST_STORE_PREFIX + "alwaysUsingRequestParams";

    String LOG_STORE_KEY = REQUEST_STORE_PREFIX + "log";

    String LOG_INVOKE_MILLI_SECOND_KEY = REQUEST_STORE_PREFIX + "invokeMillisecond";

    String EIP_SQL_RESULT_SEPARATE = ".";

    String EIP_SQL_RESULT_IN = "list";

    String EIP_SQL_RESULT_OUT = "result";

    /**
     * <h>单个调用接口</h>
     * <p>在Processor中，从Exchange中拿到的返回值为上一个调用接口</p>
     *
     * @return 当前调用接口
     */
    IEipApi getApi();

    /**
     * 执行器上下文
     *
     * @return 上下文承载对象
     */
    T getExecutorContext();

    /**
     * 获取执行器上下文的值
     *
     * @param key 键
     * @return 值
     */
    Object getExecutorContextValue(String key);

    /**
     * 添加执行器上下文的值
     *
     * @param key   键
     * @param value 值
     */
    void putExecutorContextValue(String key, Object value);

    /**
     * 添加执行器上下文的值
     */
    void putAllExecutorContextValue(Map<? extends String, ?> map);

    /**
     * 接口上下文
     *
     * @return 上下文承载对象
     */
    T getInterfaceContext();

    /**
     * 获取接口上下文的值
     *
     * @param key 键
     * @return 值
     */
    Object getInterfaceContextValue(String key);

    /**
     * 添加接口上下文的值
     *
     * @param key   键
     * @param value 值
     */
    void putInterfaceContextValue(String key, Object value);

    /**
     * 添加接口上下文的值
     */
    void putAllInterfaceContextValue(Map<? extends String, ?> map);
}