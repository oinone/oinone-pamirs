package pro.shushi.pamirs.meta.dsl.init;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

public class ConfigServerHolder implements InitializingBean {

    private static String PLANFORM_NAME = "DEFAULT_PAMIRS_PLANFORM";

    private static Resource[] processLocations;

    public static String getPlatform() {
        return PLANFORM_NAME;
    }

    public void setPlatform(String planformName) {
        PLANFORM_NAME = planformName;
    }

    public static Resource[] getProcessLocations() {
        return processLocations;
    }

    public void setProcessLocations(Resource[] processLocations) {
        ConfigServerHolder.processLocations = processLocations;
    }

    public void afterPropertiesSet() throws Exception {
        if (null == processLocations) {
            throw new IllegalArgumentException("processLocations");
        }
    }

}
