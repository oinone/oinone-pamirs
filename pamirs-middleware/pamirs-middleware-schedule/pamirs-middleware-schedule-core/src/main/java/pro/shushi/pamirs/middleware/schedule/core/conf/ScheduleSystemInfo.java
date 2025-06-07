package pro.shushi.pamirs.middleware.schedule.core.conf;

import com.taobao.pamirs.schedule.ScheduleUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration(ScheduleSystemInfo.BEAN_NAME)
public class ScheduleSystemInfo implements InitializingBean {

    public static final String BEAN_NAME = "scheduleSystemInfo";

    public static String STATIC_OWN_SIGN_VALUE;

    @Value("${pamirs.distribution.session.ownSign:}")
    private String distributionOwnSign;

    @Value("${pamirs.event.schedule.ownSign:BASE}")
    private String ownSign;

    @Override
    public void afterPropertiesSet() throws Exception {
        String currentOwnSign;
        if (StringUtils.isNotBlank(distributionOwnSign)) {
            currentOwnSign = distributionOwnSign;
        } else if (StringUtils.isNotBlank(ownSign)) {
            currentOwnSign = ownSign;
        } else {
            currentOwnSign = ScheduleUtil.OWN_SIGN_BASE;
        }
        STATIC_OWN_SIGN_VALUE = currentOwnSign.trim();
    }
}
