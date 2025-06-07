package pro.shushi.pamirs.boot.standard.checker.environment.finder;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.MapUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.standard.checker.environment.AbstractEnvironmentRecordFinder;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentCheckContext;
import pro.shushi.pamirs.boot.standard.entity.StartupEnvironmentInfo;
import pro.shushi.pamirs.meta.domain.PlatformEnvironment;
import pro.shushi.pamirs.meta.domain.PlatformEnvironmentHistoryRecord;
import pro.shushi.pamirs.meta.enmu.PlatformEnvironmentTypeEnum;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 平台环境记录
 *
 * @author Gesi at 14:41 on 2024/11/29
 */
@Component
public class PlatformEnvironmentRecordFinder extends AbstractEnvironmentRecordFinder {

    public static final String STARTUP_ENVIRONMENT_INFO = "startup-environment-info";

    @Resource
    private ApplicationArguments arguments;

    @Override
    public List<PlatformEnvironmentHistoryRecord> collectionUpdate(EnvironmentCheckContext context, List<PlatformEnvironment> environments, Map<String, List<PlatformEnvironment>> recordHistoryEnvironmentMap) {
        Map<String, Map<String, PlatformEnvironment>> dbEnviromentsMap = groupPlatformEnvironmentMap(recordHistoryEnvironmentMap);

        List<PlatformEnvironmentHistoryRecord> historyRecords = new ArrayList<>();

        StartupEnvironmentInfo startupEnvironmentInfo = StartupEnvironmentInfo.getStartupEnvironmentInfo(getEnvironment(), arguments);
        PlatformEnvironment startupEnvironment = generatorEnvironmentProperty(STARTUP_ENVIRONMENT_INFO, JSON.toJSONString(startupEnvironmentInfo));
        historyRecords.add(generatorEnvironmentHistory(startupEnvironment, startupEnvironment.getValue(), null, PlatformEnvironmentTypeEnum.CREATE));

        if (MapUtils.isNotEmpty(context.getCreate())) {
            context.getCreate().forEach((type, resultSet) -> {
                resultSet.forEach(result -> {
                    PlatformEnvironment environment = result.getEnvironment();
                    if (environment == null) {
                        return;
                    }
                    generatorEnvironmentHistory(type, environment.getCode(), environment.getKey(), environment.getValue(), null, PlatformEnvironmentTypeEnum.CREATE);
                });
            });
        }
        if (MapUtils.isNotEmpty(context.getUpdate())) {
            context.getUpdate().forEach((type, resultSet) -> {
                resultSet.forEach(result -> {
                    PlatformEnvironment environment = result.getEnvironment();
                    PlatformEnvironment dbEnvironment =
                            Optional.ofNullable(dbEnviromentsMap.get(Optional.ofNullable(environment).map(PlatformEnvironment::getType).orElse(null)))
                                    .map(it -> it.get(Optional.ofNullable(environment).map(PlatformEnvironment::getKey).orElse(null))).orElse(null);
                    if (environment == null) {
                        return;
                    }
                    generatorEnvironmentHistory(type, environment.getCode(), environment.getKey(), environment.getValue(), Optional.ofNullable(dbEnvironment).map(PlatformEnvironment::getValue).orElse(null), PlatformEnvironmentTypeEnum.UPDATE);
                });
            });
        }
        if (MapUtils.isNotEmpty(context.getDelete())) {
            context.getDelete().forEach((type, resultSet) -> {
                resultSet.forEach(result -> {
                    PlatformEnvironment environment = result.getEnvironment();
                    PlatformEnvironment dbEnvironment =
                            Optional.ofNullable(dbEnviromentsMap.get(Optional.ofNullable(environment).map(PlatformEnvironment::getType).orElse(null)))
                                    .map(it -> it.get(Optional.ofNullable(environment).map(PlatformEnvironment::getKey).orElse(null))).orElse(null);
                    if (dbEnvironment == null) {
                        return;
                    }
                    generatorEnvironmentHistory(type, dbEnvironment.getCode(), dbEnvironment.getKey(), null, dbEnvironment.getValue(), PlatformEnvironmentTypeEnum.DELETE);
                });
            });
        }

        historyRecords = historyRecords.stream().filter(Objects::nonNull).collect(Collectors.toList());
        if (historyRecords.size() > 1) {
            return historyRecords;
        }
        return new ArrayList<>(0);
    }

    private Map<String, Map<String, PlatformEnvironment>> groupPlatformEnvironmentMap(Map<String, List<PlatformEnvironment>> environments) {
        if (environments == null || environments.isEmpty()) {
            return new HashMap<>(0);
        }
        Map<String, Map<String, PlatformEnvironment>> enviromentsMap = new HashMap<>(environments.size());
        environments.forEach((type, envList) -> {
            enviromentsMap.put(type, envList.stream().collect(Collectors.toMap(PlatformEnvironment::getKey, a -> a, (a, b) -> b)));
        });
        return enviromentsMap;
    }

}
