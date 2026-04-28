package pro.shushi.pamirs.eip.core.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.eip.api.constant.EipLogConstant;
import pro.shushi.pamirs.eip.api.context.EipInterfaceContext;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;
import pro.shushi.pamirs.eip.api.helper.EipRetryHelper;
import pro.shushi.pamirs.eip.api.model.EipLog;
import pro.shushi.pamirs.eip.api.model.EipLogRetryHistory;
import pro.shushi.pamirs.eip.api.service.EipLogRetryService;
import pro.shushi.pamirs.eip.api.strategy.cache.EipLongRedisTemplate;
import pro.shushi.pamirs.framework.common.config.AsyncTaskExecutorConfiguration;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * @author yeshenyue on 2026/4/3 17:00.
 */
@Slf4j
@Service
public class EipLogRetryServiceImpl implements EipLogRetryService {

    @Autowired
    @Qualifier(EipLongRedisTemplate.REDIS_TEMPLATE_BEAN_NAME)
    private EipLongRedisTemplate eipLongRedisTemplate;

    @Autowired
    @Qualifier(AsyncTaskExecutorConfiguration.FIXED_THREAD_POOL_EXECUTOR)
    private Executor globalFixedThreadPoolExecutor;

    private static final long RETRY_LOCK_EXPIRE_SECONDS = 300L;

    @Override
    public void retryOne(Long logId) {
        if (!tryLock(logId)) {
            throw PamirsException.construct(EipExpEnumerate.EIP_RETRY_LOCK_FAILED).errThrow();
        }
        try {
            EipLog eipLog = new EipLog().setId(logId).queryOne();
            if (eipLog == null) {
                throw PamirsException.construct(EipExpEnumerate.INTEGRATION_INTERFACE_NULL_ERROR).errThrow();
            }
            validate(eipLog);
            doRetryOne(eipLog);
        } finally {
            releaseLock(logId);
        }
    }

    @Override
    public void retryBatch(List<Long> logIds) {
        if (logIds == null || logIds.isEmpty()) {
            return;
        }

        for (int i = 0; i < logIds.size(); i++) {
            if (!tryLock(logIds.get(i))) {
                releaseLockAll(logIds.subList(0, i));
                throw PamirsException.construct(EipExpEnumerate.EIP_RETRY_LOCK_FAILED).errThrow();
            }
        }

        IWrapper<EipLog> query = Pops.<EipLog>lambdaQuery()
                .from(EipLog.MODEL_MODEL)
                .in(EipLog::getId, logIds);
        List<EipLog> eipLogs = new EipLog().queryList(query);

        try {
            if (eipLogs.size() < logIds.size()) {
                throw PamirsException.construct(EipExpEnumerate.INTEGRATION_INTERFACE_NULL_ERROR).errThrow();
            }
            eipLogs.forEach(this::validate);
        } catch (Exception e) {
            releaseLockAll(logIds);
            throw e;
        }

        try {
            globalFixedThreadPoolExecutor.execute(() -> {
                for (EipLog eipLog : eipLogs) {
                    try {
                        doRetryOne(eipLog);
                    } catch (Exception e) {
                        log.warn("Batch retry failed for logId [{}]: {}", eipLog.getId(), e.getMessage());
                    } finally {
                        releaseLock(eipLog.getId());
                    }
                }
            });
        } catch (Exception e) {
            releaseLockAll(logIds);
            throw PamirsException.construct(EipExpEnumerate.EIP_RETRY_LOCK_FAILED).errThrow();
        }
    }

    private void doRetryOne(EipLog eipLog) {
        saveHistory(eipLog);
        EipRetryHelper.markRetry(eipLog);
        try {
            EipInterfaceContext.call(eipLog.getInterfaceName(), null, eipLog.getRequestOriginData());
            eipLog = EipRetryHelper.getRetryLog();
        } finally {
            EipRetryHelper.clearRetry();
        }

        if (Boolean.TRUE.equals(eipLog.getIsSuccess())) {
            int successCount = Optional.ofNullable(eipLog.getRetrySuccessCount()).orElse(0);
            eipLog.setRetrySuccessCount(successCount + 1);
        } else {
            int failCount = Optional.ofNullable(eipLog.getRetryFailCount()).orElse(0);
            eipLog.setRetryFailCount(failCount + 1);
        }
        eipLog.updateById();
    }

    private void validate(EipLog eipLog) {
        if (InterfaceTypeEnum.OPEN.equals(eipLog.getInterfaceType())) {
            throw PamirsException.construct(EipExpEnumerate.EIP_UNSUPPORTED_INTERFACE_TYPE).errThrow();
        }
        String requestBody = eipLog.getRequestOriginData();
        if (StringUtils.isNotBlank(requestBody)) {
            if (requestBody.startsWith(EipLogConstant.FREQUENCY_LOG_MSG_PREFIX)
                    || EipLogConstant.MULTIPART_MSG.equals(requestBody)) {
                throw PamirsException.construct(EipExpEnumerate.EIP_RETRY_PARAM_NULL).errThrow();
            }
        }
    }

    private void releaseLockAll(List<Long> logIds) {
        for (Long logId : logIds) {
            eipLongRedisTemplate.delete(EipLogRetryService.RETRY_LOCK_KEY_PREFIX + logId);
        }
    }

    private void saveHistory(EipLog eipLog) {
        EipLogRetryHistory history = new EipLogRetryHistory();
        history.setLogId(eipLog.getId());
        history.setRequestHeaderData(eipLog.getRequestHeaderData());
        history.setRequestOriginData(eipLog.getRequestOriginData());
        history.setRequestTargetData(eipLog.getRequestTargetData());
        history.setResponseHeaderData(eipLog.getResponseHeaderData());
        history.setResponseData(eipLog.getResponseData());
        history.setIsSuccess(eipLog.getIsSuccess());
        history.setErrorMsg(eipLog.getErrorMsg());
        history.setInvokeDate(eipLog.getInvokeDate());
        history.setInvokeEndDate(eipLog.getInvokeEndDate());
        if (eipLog.getInvokeDate() != null && eipLog.getInvokeEndDate() != null) {
            history.setInvokeMillisecond(eipLog.getInvokeEndDate().getTime() - eipLog.getInvokeDate().getTime());
        }
        history.create();
    }

    private boolean tryLock(Long logId) {
        String lockKey = EipLogRetryService.RETRY_LOCK_KEY_PREFIX + logId;
        Boolean result = eipLongRedisTemplate.opsForValue().setIfAbsent(
                lockKey,
                System.currentTimeMillis(),
                RETRY_LOCK_EXPIRE_SECONDS,
                TimeUnit.SECONDS
        );
        return Boolean.TRUE.equals(result);
    }

    private void releaseLock(Long logId) {
        eipLongRedisTemplate.delete(EipLogRetryService.RETRY_LOCK_KEY_PREFIX + logId);
    }
}
