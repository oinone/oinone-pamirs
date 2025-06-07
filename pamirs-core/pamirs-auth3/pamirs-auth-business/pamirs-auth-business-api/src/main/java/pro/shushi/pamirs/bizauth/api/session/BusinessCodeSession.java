package pro.shushi.pamirs.bizauth.api.session;

import pro.shushi.pamirs.bizauth.api.config.AuthBusinessProperties;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;


public class BusinessCodeSession {

    public static String getCode() {
        return PamirsSession.getTransmittableExtend().get(BeanDefinitionUtils.getBean(AuthBusinessProperties.class).getBusinessCodeKey());
    }
}
