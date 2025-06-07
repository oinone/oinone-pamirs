package pro.shushi.pamirs.boot.standard.checker.scene;

import org.springframework.beans.factory.annotation.Autowired;
import pro.shushi.pamirs.boot.orm.configure.BootConfiguration;
import pro.shushi.pamirs.boot.standard.checker.environment.AbstractPlatformEnvironmentChecker;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentCheckContext;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.domain.PlatformEnvironment;

import java.util.List;

/**
 * 抽象场景环境检查API
 *
 * @author Adamancy Zhang at 22:04 on 2024-10-14
 */
public abstract class AbstractSceneEnvironmentChecker extends AbstractPlatformEnvironmentChecker {

    @Autowired
    private BootConfiguration bootConfiguration;

    protected String module() {
        String name = this.getClass().getSimpleName();
        if (name.endsWith(DEFAULT_CHECKER_NAME_SUFFIX)) {
            return camelCase2Underline(name.substring(0, name.indexOf(DEFAULT_CHECKER_NAME_SUFFIX)));
        }
        if (log.isErrorEnabled()) {
            log.error("Automatic module encoding generation failed. class: {}", name);
        }
        return name;
    }

    protected abstract List<PlatformEnvironment> check(EnvironmentCheckContext context,
                                                       List<PlatformEnvironment> allEnvironments,
                                                       List<PlatformEnvironment> currentEnvironments,
                                                       List<PlatformEnvironment> historyEnvironments);

    @Override
    public List<PlatformEnvironment> checkAfter(EnvironmentCheckContext context,
                                                List<PlatformEnvironment> allEnvironments,
                                                List<PlatformEnvironment> currentEnvironments,
                                                List<PlatformEnvironment> historyEnvironments) {
        if (isBootModule(module())) {
            return check(context, allEnvironments, currentEnvironments, historyEnvironments);
        }
        return null;
    }

    protected boolean isBootModule(String module) {
        return bootConfiguration.getModules().contains(module);
    }

    private String camelCase2Underline(String fieldName) {
        int len = fieldName.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = fieldName.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                sb.append(CharacterConstants.SEPARATOR_UNDERLINE);
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }
}
