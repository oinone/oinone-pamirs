package pro.shushi.pamirs.eip.api.util;

import com.alibaba.fastjson.JSON;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.transaction.annotation.Propagation;
import pro.shushi.pamirs.core.common.LetterHelper;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipApi;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipIntegrationInterface;
import pro.shushi.pamirs.eip.api.IEipOpenInterface;
import pro.shushi.pamirs.eip.api.auth.OpenApiConstant;
import pro.shushi.pamirs.eip.api.auth.api.EipLogSaveApi;
import pro.shushi.pamirs.eip.api.cache.EipLogCountCacheApi;
import pro.shushi.pamirs.eip.api.config.PamirsEipLogProperties;
import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;
import pro.shushi.pamirs.eip.api.model.EipLog;
import pro.shushi.pamirs.eip.api.model.EipOpenInterface;
import pro.shushi.pamirs.framework.connectors.cdn.factory.FileClientFactory;
import pro.shushi.pamirs.framework.connectors.cdn.pojo.CdnFile;
import pro.shushi.pamirs.framework.connectors.data.tx.transaction.Tx;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.dto.config.TxConfig;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class EipLogUtil {

    // 日志内容最大存储长度，超过则使用文件存储
    private static final Integer MAX_CHARACTER_LENGTH = 65536;

    // 日志文件后缀
    private static final String LOG_FILENAME_ORIGIN_PREFIX = "eip_original_request";
    private static final String LOG_FILENAME_TARGET_PREFIX = "eip_real_request";
    private static final String LOG_FILENAME_RESP_PREFIX = "eip_response";
    private static final String LOG_FILENAME_ERROR_PREFIX = "eip_error";

    private static final String MULTIPART_MSG = "通过 multipart/form-data 方式传输的数据会转化成流的形式进行传递为二进制数据，不在此展示";
    private static final String FREQUENCY_LOG_MSG_PREFIX = "本次请求因频率限制未记录请求和返回报文日志详情，当前频率：";
    private static final String ALL_NUMBER_LIST = "0123456789";

    public static <V> EipLog getEipLog(IEipContext<V> context) {
        return (EipLog) context.getExecutorContextValue(IEipContext.LOG_STORE_KEY);
    }

    public static <V> EipLog createEipLog(IEipContext<V> context, Exchange exchange) {
        IEipApi eipApi = context.getApi();
        String interfaceName = eipApi.getInterfaceName();
        InterfaceTypeEnum type = null;
        if (eipApi instanceof IEipIntegrationInterface) {
            type = InterfaceTypeEnum.INTEGRATION;
        } else if (eipApi instanceof IEipOpenInterface) {
            type = InterfaceTypeEnum.OPEN;
        } else {
            log.warn("无法识别的接口类型不支持日志功能 [InterfaceName {}]", interfaceName);
            return null;
        }
        String requestOriginData = buildRequestData(context.getInterfaceContext());
        EipLog eipLog = new EipLog()
                .setInterfaceName(interfaceName)
                .setInterfaceType(type)
                .setCategory(eipApi.catagory())
                .setInvokeDate(new Date())
                .setRequestHeaderData(EipHelper.getStringJSONString(exchange.getMessage().getHeaders()))
                .setRequestOriginData(requestOriginData);
        context.putExecutorContextValue(IEipContext.LOG_STORE_KEY, eipLog);
        return eipLog;
    }

    public static void updateRequestTargetData(EipLog eipLog, Exchange exchange) {
        eipLog.setRequestHeaderData(EipHelper.getStringJSONString(exchange.getMessage().getHeaders()));
        String requestTargetData = buildRequestData(exchange.getMessage().getBody());
        if (null != requestTargetData) {
            eipLog.setRequestTargetData(requestTargetData);
        }
    }

    public static void updateResponseData(EipLog eipLog, Exchange exchange) {
        eipLog.setResponseHeaderData(EipHelper.getStringJSONString(exchange.getMessage().getHeaders()))
                .setResponseData(EipHelper.getStringBody(exchange))
                .setInvokeEndDate(new Date());
    }

    public static <V> void success(IEipContext<V> context, EipLog eipLog) {
        IEipApi eipApi = context.getApi();
        if (eipApi.getIsIgnoreLogFrequency()) {
            uploadEipLog(eipLog, true, null);
        } else {
            Double frequency = BeanDefinitionUtils.getBean(PamirsEipLogProperties.class).getFrequency();
            boolean record = ThreadLocalRandom.current().nextDouble() < frequency;
            uploadEipLog(eipLog, record, frequency);
        }

        context.putExecutorContextValue(IEipContext.LOG_STORE_KEY, null);
        putInvokeMillisecond(context, eipLog);
        Tx.build(new TxConfig().setPropagation(Propagation.REQUIRES_NEW.value())).executeWithoutResult(status -> {
            EipLog savedLog = eipLog;
            savedLog.setIsSuccess(true);
            savedLog = Spider.getDefaultExtension(EipLogSaveApi.class).saveLog(savedLog, (IEipContext<SuperMap>) context);
            CommonApiFactory.getApi(EipLogCountCacheApi.class).addLogCount(savedLog);
        });
    }

    public static <V> void failure(IEipContext<V> context, EipLog eipLog, Exchange exchange) {
        uploadEipLog(eipLog, true, null);
        context.putExecutorContextValue(IEipContext.LOG_STORE_KEY, null);
        putInvokeMillisecond(context, eipLog);
        Tx.build(new TxConfig().setPropagation(Propagation.REQUIRES_NEW.value())).executeWithoutResult(status -> {
            EipLog savedLog = eipLog;
            savedLog.setIsSuccess(false)
                    .setErrorMsg(EipHelper.getStringBody(exchange))
                    .setInvokeEndDate(new Date());
            savedLog = Spider.getDefaultExtension(EipLogSaveApi.class).saveLog(savedLog, (IEipContext<SuperMap>) context);
            CommonApiFactory.getApi(EipLogCountCacheApi.class).addLogCount(savedLog);
        });
    }

    /**
     * 开放接口失败日志，用于记录预处理函数发生错误
     */
    public static void openApiFailure(Exchange exchange, String errorMsg, String resultString) {
        EipOpenInterface openInterface = (EipOpenInterface) exchange.getProperties().get(OpenApiConstant.EIP_OPEN_INTERFACE);
        EipLog eipLog = new EipLog();
        eipLog.setInterfaceType(InterfaceTypeEnum.OPEN);
        if (openInterface != null) {
            eipLog.setInterfaceName(openInterface.getInterfaceName());
        }
        String header = EipHelper.getStringJSONString(exchange.getMessage().getHeaders());
        String body = StringUtils.replaceChars(exchange.getMessage().getBody(String.class), "\r\n", null);
        eipLog.setRequestHeaderData(header);
        eipLog.setRequestOriginData(body);
        eipLog.setRequestTargetData(body);
        eipLog.setResponseData(resultString);
        eipLog.setIsSuccess(false);
        eipLog.setErrorMsg(errorMsg);
        eipLog.setInvokeDate(new Date());
        eipLog.setInvokeEndDate(new Date());
        eipLog.create();
    }

    private static <V> void putInvokeMillisecond(IEipContext<V> context, EipLog eipLog) {
        if (eipLog.getInvokeDate() != null && eipLog.getInvokeEndDate() != null) {
            long endTime = eipLog.getInvokeEndDate().getTime();
            long startTime = eipLog.getInvokeDate().getTime();
            long invokeMillisecond = endTime - startTime;
            context.putExecutorContextValue(IEipContext.LOG_INVOKE_MILLI_SECOND_KEY, invokeMillisecond);
        }
    }

    /**
     * 将参数转换成JSON格式，处理参数可能包含的流信息
     */
    private static String buildRequestData(Object requestContext) {
        String requestOriginData;
        if (requestContext instanceof SuperMap) {
            SuperMap bodyMap = new SuperMap((SuperMap) requestContext);
            for (Map.Entry<String, Object> entry : bodyMap.entrySet()) {
                if (entry.getValue() instanceof ByteArrayResource) {
                    ByteArrayResource resource = (ByteArrayResource) entry.getValue();
                    entry.setValue(StringUtils.isNotBlank(resource.getFilename()) ?
                            resource.getFilename() : "传输内容为流形式（图片、文件等）");
                }
            }
            requestOriginData = JSON.toJSONString(bodyMap);
        } else if (requestContext instanceof HttpEntity) {
            requestOriginData = MULTIPART_MSG;
        } else if (requestContext instanceof String) {
            requestOriginData = (String) requestContext;
        } else if (null != requestContext) {
            requestOriginData = JSON.toJSONString(requestContext);
        } else {
            requestOriginData = null;
        }
        return requestOriginData;
    }

    /**
     * 除请求头报文外，其他报文超过最大长度限制，转为文件存储
     * 如果不记录请求详情，清空报文内容
     *
     * @param eipLog       日志信息
     * @param restrictions 是否记录日志
     * @param frequency    日志记录频率
     */
    private static void uploadEipLog(EipLog eipLog, boolean restrictions, Double frequency) {
        if (restrictions) {
            eipLog.setRequestOriginData(uploadEipLog(LOG_FILENAME_ORIGIN_PREFIX, eipLog.getRequestOriginData()));
            eipLog.setRequestTargetData(uploadEipLog(LOG_FILENAME_TARGET_PREFIX, eipLog.getRequestTargetData()));
            eipLog.setResponseData(uploadEipLog(LOG_FILENAME_RESP_PREFIX, eipLog.getResponseData()));
        } else {
            eipLog.setRequestOriginData(FREQUENCY_LOG_MSG_PREFIX + frequency);
            eipLog.unsetRequestTargetData();
            eipLog.unsetResponseData();
        }
        eipLog.setErrorMsg(uploadEipLog(LOG_FILENAME_ERROR_PREFIX, eipLog.getErrorMsg()));
    }

    private static String uploadEipLog(String fileName, String data) {
        if (StringUtils.isBlank(data) || data.length() <= MAX_CHARACTER_LENGTH) {
            return data;
        }

        fileName = fileName + CharacterConstants.SEPARATOR_UNDERLINE +
                System.currentTimeMillis() + CharacterConstants.SEPARATOR_UNDERLINE +
                LetterHelper.getRandomString(ALL_NUMBER_LIST, 3) + ".log";
        CdnFile cdnFile = FileClientFactory.getClient().upload(fileName, data.getBytes(StandardCharsets.UTF_8));

        if (null == cdnFile) {
            log.error("EipLog上传失败，CDN配置为null，改用DB直接存储");
            return data;
        }
        return cdnFile.getUrl();
    }
}
