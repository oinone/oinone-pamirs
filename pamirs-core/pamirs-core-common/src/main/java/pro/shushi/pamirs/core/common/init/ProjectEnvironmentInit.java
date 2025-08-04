package pro.shushi.pamirs.core.common.init;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.InstallDataInit;
import pro.shushi.pamirs.boot.common.api.init.LifecycleCompletedAllInit;
import pro.shushi.pamirs.boot.common.api.init.ReloadDataInit;
import pro.shushi.pamirs.boot.common.api.init.UpgradeDataInit;
import pro.shushi.pamirs.boot.common.extend.MetaDataEditor;
import pro.shushi.pamirs.core.common.InitializationUtil;
import pro.shushi.pamirs.core.common.enmu.ModuleLifecycleEnum;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.compute.Prioritized;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import jakarta.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

/**
 * @author Adamancy Zhang on 2021-02-02 13:49
 */
@Slf4j
@Component
public class ProjectEnvironmentInit implements MetaDataEditor, InstallDataInit, UpgradeDataInit, ReloadDataInit, LifecycleCompletedAllInit {

    private static volatile EnvironmentInitHolder INIT_HOLDER = null;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public List<String> modules() {
        return initContext().moduleList;
    }

    @Override
    public int priority() {
        return 99;
    }

    @Override
    public void edit(AppLifecycleCommand command, Map<String, Meta> metaMap) {
        for (EnvironmentInit dataInit : initContext().initApis) {
            execute0(dataInit, selectEnvironmentTypes(dataInit), metaMap);
        }
    }

    @Override
    public boolean init(AppLifecycleCommand command, String version) {
        init0(ModuleLifecycleEnum.INSTALL, version, null);
        return true;
    }

    @Override
    public boolean upgrade(AppLifecycleCommand command, String version, String existVersion) {
        init0(ModuleLifecycleEnum.UPGRADE, version, existVersion);
        return true;
    }

    @Override
    public boolean reload(AppLifecycleCommand command, String version) {
        init0(ModuleLifecycleEnum.RELOAD, version, null);
        return true;
    }

    @Override
    public void process(AppLifecycleCommand command, Map<String, ModuleDefinition> runModuleMap) {
        INIT_HOLDER = null;
    }

    private EnvironmentInitHolder initContext() {
        if (ProjectEnvironmentInit.INIT_HOLDER == null) {
            synchronized (ProjectEnvironmentInit.class) {
                if (ProjectEnvironmentInit.INIT_HOLDER == null) {
                    Map<String, EnvironmentInit> tenantDataInitContext = applicationContext.getBeansOfType(EnvironmentInit.class);
                    int initialCapacity = tenantDataInitContext.size();
                    List<EnvironmentInit> tenantDataInitList = new ArrayList<>(initialCapacity);
                    Set<String> tenantInitModuleSet = new HashSet<>(initialCapacity);
                    for (EnvironmentInit dataInit : tenantDataInitContext.values()) {
                        verificationRequiredValue(dataInit);
                        tenantDataInitList.add(dataInit);
                        tenantInitModuleSet.add(dataInit.getBootModule());
                    }
                    if (CollectionUtils.isNotEmpty(tenantDataInitList)) {
                        tenantDataInitList.sort(Comparator.comparingInt(Prioritized::priority));
                    }
                    ProjectEnvironmentInit.INIT_HOLDER = new EnvironmentInitHolder(tenantDataInitList, tenantInitModuleSet);
                }
            }
        }
        return ProjectEnvironmentInit.INIT_HOLDER;
    }

    private void verificationRequiredValue(EnvironmentInit dataInit) {
        String module = dataInit.getBootModule();
        if (StringUtils.isBlank(module)) {
            throw new IllegalArgumentException("Invalid module value=" + module);
        }
        String moduleName = dataInit.getBootModuleName();
        if (StringUtils.isBlank(moduleName)) {
            throw new IllegalArgumentException("Invalid module name value=" + moduleName);
        }
        if (dataInit.getDevActiveSuffix() == null) {
            throw new IllegalArgumentException("Invalid dev active suffix value");
        }
        if (dataInit.getProdActiveSuffix() == null) {
            throw new IllegalArgumentException("Invalid prod active suffix value");
        }
        if (dataInit.getTestActiveSuffix() == null) {
            throw new IllegalArgumentException("Invalid test active suffix value");
        }
    }

    private void init0(ModuleLifecycleEnum lifecycle, String version, @Nullable String existVersion) {
        for (EnvironmentInit dataInit : initContext().initApis) {
            execute0(dataInit, selectEnvironmentTypes(dataInit), lifecycle, version, existVersion);
        }
    }

    private List<EnvironmentType> selectEnvironmentTypes(EnvironmentInit dataInit) {
        List<EnvironmentType> result = new ArrayList<>();
        String env = applicationContext.getEnvironment().getProperty("spring.profiles.active");
        if (StringUtils.isBlank(env)) {
            log.warn("环境变量 [spring.profiles.active] 为空，无法进行初始化");
            return result;
        }
        for (EnvironmentType environmentType : EnvironmentType.values()) {
            appendEnvironmentType(result, selectEnvironmentType(dataInit, env, environmentType.getActiveSuffixGetter().apply(dataInit), environmentType));
        }
        return result;
    }

    private void appendEnvironmentType(List<EnvironmentType> environmentTypes, EnvironmentType environmentType) {
        if (environmentType == null) {
            return;
        }
        environmentTypes.add(environmentType);
    }

    private EnvironmentType selectEnvironmentType(EnvironmentInit dataInit, String env, String[] activeSuffix, EnvironmentType environmentType) {
        for (String devActiveSuffix : activeSuffix) {
            if (StringUtils.isBlank(devActiveSuffix)) {
                log.warn("自动跳过空的环境变量后缀定义 class={}", dataInit.getClass().getName());
                continue;
            }
            if (env.equals(devActiveSuffix)) {
                return environmentType;
            }
        }
        return null;
    }

    private void execute0(EnvironmentInit dataInit, List<EnvironmentType> environmentTypes, Map<String, Meta> metaMap) {
        InitializationUtil util = InitializationUtil.get(metaMap, dataInit.getBootModule(), dataInit.getBootModuleName());
        if (util == null) {
            return;
        }
        for (EnvironmentType environmentType : environmentTypes) {
            switch (environmentType) {
                case DEV:
                    dataInit.devMetadataInit(util);
                    break;
                case PROD:
                    dataInit.prodMetadataInit(util);
                    break;
                case TEST:
                    dataInit.testMetadataInit(util);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid environment type value=" + environmentType);
            }
        }
    }

    private void execute0(EnvironmentInit dataInit, List<EnvironmentType> environmentTypes, ModuleLifecycleEnum lifecycle, String version, @Nullable String existVersion) {
        for (EnvironmentType environmentType : environmentTypes) {
            switch (environmentType) {
                case DEV:
                    dataInit.devInit(lifecycle, version, existVersion);
                    break;
                case PROD:
                    dataInit.prodInit(lifecycle, version, existVersion);
                    break;
                case TEST:
                    dataInit.testInit(lifecycle, version, existVersion);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid environment type value=" + environmentType);
            }
        }
    }

    private enum EnvironmentType {
        DEV(EnvironmentInit::getDevActiveSuffix),
        TEST(EnvironmentInit::getTestActiveSuffix),
        PROD(EnvironmentInit::getProdActiveSuffix);

        private final Function<EnvironmentInit, String[]> activeSuffixGetter;

        EnvironmentType(Function<EnvironmentInit, String[]> activeSuffixGetter) {
            this.activeSuffixGetter = activeSuffixGetter;
        }

        public Function<EnvironmentInit, String[]> getActiveSuffixGetter() {
            return activeSuffixGetter;
        }
    }

    private static class EnvironmentInitHolder {

        private final List<EnvironmentInit> initApis;

        private final List<String> moduleList;

        public EnvironmentInitHolder(List<EnvironmentInit> initApis, Set<String> modules) {
            this.initApis = initApis;
            this.moduleList = new ArrayList<>(modules);
        }
    }
}
