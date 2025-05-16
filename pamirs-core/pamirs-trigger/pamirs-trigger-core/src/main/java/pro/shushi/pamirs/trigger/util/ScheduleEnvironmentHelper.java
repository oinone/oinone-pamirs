package pro.shushi.pamirs.trigger.util;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.api.session.cache.spi.SessionFillOwnSignApi;
import pro.shushi.pamirs.meta.common.constants.AppName;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleEnvironment;

/**
 * @author Adamancy Zhang on 2021-04-27 15:07
 */
public class ScheduleEnvironmentHelper {

    private ScheduleEnvironmentHelper() {
        //reject create object
    }

    public static ScheduleEnvironment generatorEnvironment() {
        String ownSign = Spider.getDefaultExtension(SessionFillOwnSignApi.class).getCdOwnSign();
        if (StringUtils.isBlank(ownSign)) {
            ownSign = null;
        }
        return new ScheduleEnvironment()
                .setTenant(PamirsTenantSession.getTenant())
                .setEnv(PamirsTenantSession.getEnv())
                .setUserId(PamirsSession.getUserId())
                .setUsername(PamirsSession.getUserName())
                .setOwnSign(ownSign)
                .setApplication(AppName.get());
    }
}
