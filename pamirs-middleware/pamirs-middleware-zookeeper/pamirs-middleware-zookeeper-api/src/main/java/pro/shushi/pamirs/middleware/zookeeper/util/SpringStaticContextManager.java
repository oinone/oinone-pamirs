package pro.shushi.pamirs.middleware.zookeeper.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.middleware.zookeeper.service.SpringContextManager;

/**
 * @author Adamancy Zhang
 * @date 2020-11-17 23:02
 */
@Component
public class SpringStaticContextManager implements ApplicationContextAware {

    private static ApplicationContext CONTEXT;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        SpringStaticContextManager.CONTEXT = applicationContext;
    }

    public static SpringContextManager getContextManager() {
        return CONTEXT.getBean(SpringContextManager.class);
    }
}
