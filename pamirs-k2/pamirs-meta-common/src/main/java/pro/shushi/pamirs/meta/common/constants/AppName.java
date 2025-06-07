package pro.shushi.pamirs.meta.common.constants;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * spring.application.name
 *
 * @author Adamancy Zhang at 12:27 on 2024-04-01
 */
@Component
public class AppName {

    private static final String PROPERTY = "spring.application.name";

    private static String APP_NAME = NamespaceConstants.pamirs;

    public AppName(Environment environment) {
        APP_NAME = Optional.ofNullable(environment.getProperty(PROPERTY)).filter(StringUtils::isNotBlank).orElse(NamespaceConstants.pamirs);
    }

    public static String get() {
        return APP_NAME;
    }
}
