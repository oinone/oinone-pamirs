package pro.shushi.pamirs.framework.faas.script.engine;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * ScriptEngine spring support
 *
 * @author Adamancy Zhang at 19:48 on 2024-07-17
 */
@Component
public class ScriptEngineSprintSupport implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        ScriptEngineSprintSupport.applicationContext = applicationContext;
    }

    public static boolean isUnsupported() {
        return ScriptEngineSprintSupport.applicationContext == null;
    }
}
