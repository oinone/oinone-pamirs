package pro.shushi.pamirs.trigger.tbschedule.spi;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.core.common.function.SessionHelper;
import pro.shushi.pamirs.core.common.function.context.FunctionContext;
import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.audit.spi.DataAuditApi;
import pro.shushi.pamirs.meta.api.enmu.UriType;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.api.session.cache.holder.RequestMetaDataCacheApiHolder;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.util.JsonUtils;
import pro.shushi.pamirs.middleware.schedule.common.Result;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;
import pro.shushi.pamirs.middleware.schedule.spi.ScheduleTaskActionExecuteAroundApi;
import pro.shushi.pamirs.trigger.constant.TriggerUserConfiguration;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * @author Adamancy Zhang on 2021-04-27 16:38
 */
@Slf4j
@Order(99)
@SPI.Service
public class ScheduleTaskActionExecuteSession implements ScheduleTaskActionExecuteAroundApi {

    @Override
    public Result<Void> around(ScheduleItem task, String ownSign, Supplier<Result<Void>> supplier) {
        SessionHelper.clear();
        fillSession(task);

        FunctionContext function = fetchTaskUniqueName(task);

        try {
            String traceId = PamirsSession.getRequestVariables().getTraceId();
            if (StringUtils.isBlank(traceId)) {
                traceId = UUID.randomUUID().toString();
            }
            log.debug("schedule-traceId: {}", traceId);
            Spider.getDefaultExtension(DataAuditApi.class).computeDataAuditSession(UriType.SCHEDULE, function.getNamespace(), function.getFun(), traceId);
        } catch (Exception e) {
            log.error("DataAuditRequestInfo Error", e);
        }

        //元数据批量获取
        RequestMetaDataCacheApiHolder.getCommonCacheApi().computeMetaData(UriType.SCHEDULE, function.getNamespace() + CharacterConstants.SEPARATOR_COLON + function.getFun());
        try {
            return supplier.get();
        } finally {
            SessionHelper.clear();
        }
    }

    private void fillSession(ScheduleItem task) {
        PamirsTenantSession.setTenant(task.getTenant());
        PamirsTenantSession.setEnv(task.getEnv());
        Long userId = task.getUserId();
        if (userId == null) {
            userId = TriggerUserConfiguration.TRIGGER_SYSTEM_USER_ID;
        }
        PamirsTenantSession.setUserId(userId);
        String username = task.getUsername();
        if (username == null) {
            username = TriggerUserConfiguration.TRIGGER_SYSTEM_USER_NAME;
        }
        PamirsTenantSession.setUserName(username);
    }

    private FunctionContext fetchTaskUniqueName(ScheduleItem task) {
        if (task.getInterfaceName().equals("pro.shushi.pamirs.trigger.spring.XAsyncExecutor")
                || task.getInterfaceName().equals("pro.shushi.pamirs.trigger.service.TriggerTaskActionExecutor")) {
            FunctionContext function = JsonUtils.parseObject(JsonUtils.toJSONString(JsonUtils.parseObjectList(task.getContext()).get(0)), FunctionContext.class);
            return new FunctionContext().setNamespace(function.getNamespace()).setFun(function.getFun());
        }
        return new FunctionContext().setNamespace(task.getInterfaceName()).setFun(task.getMethodName());
    }
}
