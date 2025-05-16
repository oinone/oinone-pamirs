package pro.shushi.pamirs.boot.standard.version;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.api.PlatformJarVersionCheckerApi;
import pro.shushi.pamirs.framework.common.entry.SimplePackageVersion;
import pro.shushi.pamirs.framework.configure.simulate.api.MetaSimulateService;
import pro.shushi.pamirs.framework.configure.simulate.service.MetaSimulator;
import pro.shushi.pamirs.framework.connectors.data.dialect.Dialects;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.TableMetaDialectService;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.data.DsApi;
import pro.shushi.pamirs.meta.api.prefix.DataPrefixManager;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.domain.PlatformVersion;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * PlatformJarVersionChecker
 *
 * @author yakir on 2024/07/22 11:35.
 */
@Slf4j
@Component
public class PlatformJarVersionChecker implements PlatformJarVersionCheckerApi {

    private static final Pattern PATTERN_VER = Pattern.compile("(\\d[.])+(\\d+)");

    private static Boolean needDo;

    private static Map<String, PlatformVersion> versionMap;
    private static List<PlatformVersion> storeList;

    @Override
    public void init(Boolean goBack) {
        versionMap = new HashMap<>();
        if (null != needDo) {
            return;
        }
        if (null == goBack) {
            needDo = true;
            return;
        }
        needDo = !goBack;
    }

    @Override
    public void jarVersion(Set<Class<?>> moduleClazzSet) {
        for (Class<?> clazz : moduleClazzSet) {
            SimplePackageVersion packageVersion = SimplePackageVersion.getPackageVersion(clazz);
            if (packageVersion == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Jar名称获取异常:[{}]", clazz);
                }
                continue;
            }
            String jarName = packageVersion.getImplementationTitle();
            String jarVersion = packageVersion.getImplementationVersion();
            boolean isPlatform = PlatformVersionFilter.contains(jarName);
            if (!isPlatform) {
                continue;
            }

            Matcher matcherVer = PATTERN_VER.matcher(jarVersion);
            if (matcherVer.find()) {
                jarVersion = matcherVer.group();
            }

            PlatformVersion platformVersion = new PlatformVersion();
            platformVersion.setJar(jarName);
            platformVersion.setVersion(jarVersion);
            log.debug("JarName:[{}] JarVersion:[{}]", jarName, jarVersion);
            versionMap.putIfAbsent(jarName, platformVersion);
        }
    }

    @Override
    public void compare() {
        try {
            List<PlatformVersion> allDb = BeanDefinitionUtils.getBean(MetaSimulateService.class)
                    .transientStaticExecute(MetaSimulator.simulate(), () -> {
                        String dsKey = DsApi.get().baseDsKey(PlatformVersion.MODEL_MODEL);
                        ModelDefinition model = PamirsSession.getContext().getModelConfig(PlatformVersion.MODEL_MODEL).getModelDefinition();
                        String tableName = DataPrefixManager.tablePrefix(ModuleConstants.MODULE_BASE, model.getModel(), model.getTable());
                        boolean isTableExist = Dialects.component(TableMetaDialectService.class, dsKey).existTable(dsKey, tableName);
                        if (!isTableExist) {
                            return new ArrayList<>();
                        }
                        return new PlatformVersion().queryList(-1);
                    });
            Map<String, String> jarDBVerMap = Optional.ofNullable(allDb)
                    .map(List::stream)
                    .orElse(Stream.empty())
                    .collect(Collectors.toMap(PlatformVersion::getJar, PlatformVersion::getVersion, (_a, _b) -> _a));

            storeList = new ArrayList<>(versionMap.values());

            if (!Boolean.TRUE.equals(needDo)) {
                return;
            }

            Map<String, String> jarVerMap = storeList.stream()
                    .collect(Collectors.toMap(PlatformVersion::getJar, PlatformVersion::getVersion, (_a, _b) -> _a));

            boolean hasError = false;
            StringBuilder msg = new StringBuilder("\n\n\n依赖包版本不匹配\n");
            a:
            for (Map.Entry<String, String> entry : jarVerMap.entrySet()) {
                String jarName = entry.getKey();
                String dbVersion = jarDBVerMap.get(jarName);
                if (null == dbVersion) {
                    continue;
                }

                String version = entry.getValue();
                if (StringUtils.equalsAnyIgnoreCase(dbVersion, version)) {
                    continue;
                }

                String[] jarDBVer = dbVersion.split("\\.");
                String[] jarVer = version.split("\\.");

                int jarDBVerLen = jarDBVer.length;
                int jarVerLen = jarVer.length;

                // Math.max(jarVerLen, jarDBVerLen)
                // 只比3位
                for (int i = 0; i < 3; i++) {
                    int j = (i < jarVerLen ? Integer.parseInt(jarVer[i]) : 0);
                    int k = (i < jarDBVerLen ? Integer.parseInt(jarDBVer[i]) : 0);
                    if (j > k) {
                        continue a;
                    } else if (j < k) {
                        hasError = true;
                        msg.append(jarName).append("已安装版本:[").append(dbVersion).append("],启动中包含的版本:[").append(version).append("]\n");
                    }
                }
            }

            if (hasError) {
                msg.append("\n请确认版本配置后重新启动, 系统即将退出。\n\n\n");
                log.error(msg.toString());
                System.exit(-1);
            }
        } catch (Throwable t) {
            log.error("对比Jar版本异常", t);
        }
    }

    @Override
    public void store() {
        if (CollectionUtils.isNotEmpty(storeList)) {
            new PlatformVersion().createOrUpdateBatch(storeList);
        }
        clear();
    }

    private static void clear() {
        if (null != versionMap) versionMap.clear();
        if (null != storeList) storeList.clear();
    }
}
