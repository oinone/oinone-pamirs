package pro.shushi.pamirs.eip.api.strategy.spi.impl;

import com.alibaba.fastjson.JSON;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import pro.shushi.pamirs.core.common.LetterHelper;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipApi;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipIntegrationInterface;
import pro.shushi.pamirs.eip.api.IEipOpenInterface;
import pro.shushi.pamirs.eip.api.auth.OpenApiConstant;
import pro.shushi.pamirs.eip.api.constant.EipLogConstant;
import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;
import pro.shushi.pamirs.eip.api.helper.EipRetryHelper;
import pro.shushi.pamirs.eip.api.model.EipLog;
import pro.shushi.pamirs.eip.api.model.EipOpenInterface;
import pro.shushi.pamirs.eip.api.service.alarm.EipAlarmService;
import pro.shushi.pamirs.eip.api.strategy.context.EipLogStrategyContext;
import pro.shushi.pamirs.eip.api.strategy.entity.EipLogStrategyEntity;
import pro.shushi.pamirs.eip.api.strategy.spi.EipLogSaveApi;
import pro.shushi.pamirs.eip.api.strategy.spi.EipLogStrategyHandler;
import pro.shushi.pamirs.eip.api.util.EipHelper;
import pro.shushi.pamirs.framework.connectors.cdn.factory.FileClientFactory;
import pro.shushi.pamirs.framework.connectors.cdn.pojo.CdnFile;
import pro.shushi.pamirs.framework.connectors.data.tx.transaction.Tx;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.config.TxConfig;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Adamancy Zhang at 17:12 on 2025-08-16
 */
@Slf4j
@Order
@SPI.Service
@Component
public class DefaultLogStrategyHandler implements EipLogStrategyHandler {

    // 日志内容最大存储长度，超过则使用文件存储
    private static final Integer MAX_CHARACTER_LENGTH = 65536;

    // 日志文件后缀
    private static final String LOG_FILENAME_ORIGIN_PREFIX = "eip_original_request";
    private static final String LOG_FILENAME_TARGET_PREFIX = "eip_real_request";
    private static final String LOG_FILENAME_RESP_PREFIX = "eip_response";
    private static final String LOG_FILENAME_ERROR_PREFIX = "eip_error";

    private static final String MULTIPART_MSG = EipLogConstant.MULTIPART_MSG;
    private static final String FREQUENCY_LOG_MSG_PREFIX = EipLogConstant.FREQUENCY_LOG_MSG_PREFIX;
    private static final String ALL_NUMBER_LIST = "0123456789";

    @Override
    public boolean isEnabled(IEipContext<?> context, Exchange exchange) {
        IEipApi eipApi = context.getApi();
        return EipLogStrategyContext.get(eipApi.getType(), eipApi.getInterfaceName()).isEnabled();
    }

    @Override
    public EipLog get(IEipContext<?> context, Exchange exchange) {
        return (EipLog) context.getExecutorContextValue(IEipContext.LOG_STORE_KEY);
    }

    @Override
    public EipLog create(IEipContext<?> context, Exchange exchange) {
        EipLog retryLog = EipRetryHelper.getRetryLog();
        if (retryLog != null) {
            retryLog.setInvokeDate(new Date());
            retryLog.setRequestHeaderData(EipHelper.getStringJSONString(exchange.getMessage().getHeaders()));
            String requestOriginData = buildRequestData(context.getInterfaceContext());
            retryLog.setRequestOriginData(requestOriginData);
            context.putExecutorContextValue(IEipContext.LOG_STORE_KEY, retryLog);
            return retryLog;
        }

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
                .setInvokeDate(new Date())
                .setRequestHeaderData(EipHelper.getStringJSONString(exchange.getMessage().getHeaders()))
                .setRequestOriginData(requestOriginData);
        context.putExecutorContextValue(IEipContext.LOG_STORE_KEY, eipLog);
        return eipLog;
    }

    @Override
    public void updateRequestTargetData(IEipContext<?> context, Exchange exchange) {
        EipLog eipLog = get(context, exchange);
        if (eipLog == null) {
            return;
        }
        eipLog.setRequestHeaderData(EipHelper.getStringJSONString(exchange.getMessage().getHeaders()));
        String requestTargetData = buildRequestData(exchange.getMessage().getBody());
        if (null != requestTargetData) {
            eipLog.setRequestTargetData(requestTargetData);
        }
    }

    @Override
    public void updateResponseData(IEipContext<?> context, Exchange exchange) {
        EipLog eipLog = get(context, exchange);
        if (eipLog == null) {
            return;
        }
        eipLog.setResponseHeaderData(EipHelper.getStringJSONString(exchange.getMessage().getHeaders()))
                .setResponseData(EipHelper.getStringBody(exchange))
                .setInvokeEndDate(new Date());
    }

    @Override
    public void success(IEipContext<?> context, Exchange exchange) {
        EipLog eipLog = get(context, exchange);
        if (eipLog == null) {
            return;
        }

        if (EipRetryHelper.isRetrying()) {
            uploadEipLog(eipLog, true, null);
            eipLog.setIsSuccess(true);
            return;
        }
        IEipApi eipApi = context.getApi();
        EipLogStrategyEntity logStrategy = EipLogStrategyContext.get(eipApi.getType(), eipApi.getInterfaceName());
        if (logStrategy.isIgnoreLogFrequency()) {
            uploadEipLog(eipLog, true, null);
        } else {
            double frequency = logStrategy.getFrequency();
            boolean record = ThreadLocalRandom.current().nextDouble() < frequency;
            uploadEipLog(eipLog, record, frequency);
        }
        Tx.build(new TxConfig().setPropagation(Propagation.REQUIRES_NEW.value())).executeWithoutResult(status -> {
            EipLog savedLog = eipLog;
            savedLog.setIsSuccess(true);
            savedLog = Spider.getDefaultExtension(EipLogSaveApi.class).saveLog(savedLog, (IEipContext<SuperMap>) context);

            // alarm
            Spider.getDefaultExtension(EipAlarmService.class).alarm(savedLog, (IEipContext<SuperMap>) context);
        });
    }

    @Override
    public void failure(IEipContext<?> context, Exchange exchange) {
        EipLog eipLog = get(context, exchange);
        if (eipLog == null) {
            return;
        }
        uploadEipLog(eipLog, true, null);
        eipLog.setIsSuccess(false)
                .setErrorMsg(EipHelper.getStringBody(exchange))
                .setInvokeEndDate(new Date());
        if (EipRetryHelper.isRetrying()) {
            return;
        }
        Tx.build(new TxConfig().setPropagation(Propagation.REQUIRES_NEW.value())).executeWithoutResult(status -> {
            Spider.getDefaultExtension(EipLogSaveApi.class).saveLog(eipLog, (IEipContext<SuperMap>) context);
            // alarm
            Spider.getDefaultExtension(EipAlarmService.class).alarm(eipLog, (IEipContext<SuperMap>) context);
        });

    }

    @Override
    public void openApiFailure(Exchange exchange, String errorMsg, String resultString) {
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

    protected String buildRequestData(Object requestContext) {
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
    protected void uploadEipLog(EipLog eipLog, boolean restrictions, Double frequency) {
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

    protected String uploadEipLog(String fileName, String data) {
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
