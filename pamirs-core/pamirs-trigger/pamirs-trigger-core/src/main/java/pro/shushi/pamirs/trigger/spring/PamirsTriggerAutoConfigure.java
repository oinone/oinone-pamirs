package pro.shushi.pamirs.trigger.spring;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.framework.configure.MetaConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Trigger自动配置
 *
 * @author Adamancy Zhang at 16:12 on 2025-03-24
 */
@Configuration
public class PamirsTriggerAutoConfigure {

    private static final String META_PACKAGE = "pro.shushi.pamirs.trigger.model";

    @Autowired
    private MetaConfiguration metaConfiguration;

    @PostConstruct
    public void init() {
        initMetaPackages();
    }

    private void initMetaPackages() {
        List<String> metaPackages = metaConfiguration.getMetaPackages();
        if (metaPackages == null) {
            metaPackages = new ArrayList<>();
            metaConfiguration.setMetaPackages(metaPackages);
        }
        if (!metaPackages.contains(META_PACKAGE)) {
            metaPackages.add(META_PACKAGE);
        }
    }
}
