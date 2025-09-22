package pro.shushi.pamirs.boot.standard.service;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentCheckContext;
import pro.shushi.pamirs.boot.standard.entity.StartupEnvironmentInfo;
import pro.shushi.pamirs.boot.standard.utils.PlatformEnvironmentGenerator;
import pro.shushi.pamirs.meta.domain.PlatformEnvironment;
import pro.shushi.pamirs.meta.domain.PlatformEnvironmentHistoryRecord;
import pro.shushi.pamirs.meta.enmu.PlatformEnvironmentTypeEnum;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 平台环境记录
 *
 * @author Gesi at 14:41 on 2024/11/29
 */
@Component
public class EnvironmentHistoryRecordSaver {

    private static final String STARTUP_ENVIRONMENT_INFO = "startup-environment-info";

    @Autowired
    private Environment environment;

    @Resource
    private ApplicationArguments arguments;

    public List<PlatformEnvironmentHistoryRecord> collectionUpdate(EnvironmentCheckContext context, List<PlatformEnvironment> environments, Map<String, List<PlatformEnvironment>> recordHistoryEnvironmentMap) {
        Map<String, Map<String, PlatformEnvironment>> dbEnviromentsMap = groupPlatformEnvironmentMap(recordHistoryEnvironmentMap);

        List<PlatformEnvironmentHistoryRecord> historyRecords = new ArrayList<>();

        StartupEnvironmentInfo startupEnvironmentInfo = StartupEnvironmentInfo.getStartupEnvironmentInfo(this.environment, arguments);
        PlatformEnvironment startupEnvironment = PlatformEnvironmentGenerator.newInstance("platform", STARTUP_ENVIRONMENT_INFO, JSON.toJSONString(startupEnvironmentInfo));
        historyRecords.add(generatorEnvironmentHistory(startupEnvironment, startupEnvironment.getValue(), null, PlatformEnvironmentTypeEnum.CREATE));

        if (MapUtils.isNotEmpty(context.getCreate())) {
            context.getCreate().forEach((type, resultSet) -> {
                resultSet.forEach(result -> {
                    PlatformEnvironment environment = result.getEnvironment();
                    if (environment == null) {
                        return;
                    }
                    historyRecords.add(generatorEnvironmentHistory(type, environment.getCode(), environment.getKey(), environment.getValue(), null, PlatformEnvironmentTypeEnum.CREATE));
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
                    historyRecords.add(generatorEnvironmentHistory(type, environment.getCode(), environment.getKey(), environment.getValue(), Optional.ofNullable(dbEnvironment).map(PlatformEnvironment::getValue).orElse(null), PlatformEnvironmentTypeEnum.UPDATE));
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
                    historyRecords.add(generatorEnvironmentHistory(type, dbEnvironment.getCode(), dbEnvironment.getKey(), null, dbEnvironment.getValue(), PlatformEnvironmentTypeEnum.DELETE));
                });
            });
        }

        if (historyRecords.size() > 1) {
            return historyRecords;
        }
        return Collections.emptyList();
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

    protected PlatformEnvironmentHistoryRecord generatorEnvironmentHistory(PlatformEnvironment environment, String currentValue, String historyValue, PlatformEnvironmentTypeEnum alterType) {
        PlatformEnvironmentHistoryRecord platformEnvironmentHistoryRecord = new PlatformEnvironmentHistoryRecord();
        platformEnvironmentHistoryRecord.setStartupCode(StartupEnvironmentInfo.getCurrentStartupCode(this.environment, this.arguments));
        platformEnvironmentHistoryRecord.setHistoryValue(historyValue);
        platformEnvironmentHistoryRecord.setAlterType(alterType);
        platformEnvironmentHistoryRecord.setEnvironmentType(environment.getType());
        platformEnvironmentHistoryRecord.setEnvironmentCode(environment.getCode());
        platformEnvironmentHistoryRecord.setEnvironmentKey(environment.getKey());
        platformEnvironmentHistoryRecord.setCurrentValue(currentValue);
        return platformEnvironmentHistoryRecord;
    }

    protected PlatformEnvironmentHistoryRecord generatorEnvironmentHistory(String type, String code, String key, String currentValue, String historyValue, PlatformEnvironmentTypeEnum alterType) {
        PlatformEnvironmentHistoryRecord platformEnvironmentHistoryRecord = new PlatformEnvironmentHistoryRecord();
        platformEnvironmentHistoryRecord.setStartupCode(StartupEnvironmentInfo.getCurrentStartupCode(this.environment, this.arguments));
        platformEnvironmentHistoryRecord.setHistoryValue(historyValue);
        platformEnvironmentHistoryRecord.setAlterType(alterType);
        platformEnvironmentHistoryRecord.setEnvironmentType(type);
        platformEnvironmentHistoryRecord.setEnvironmentCode(code);
        platformEnvironmentHistoryRecord.setEnvironmentKey(key);
        platformEnvironmentHistoryRecord.setCurrentValue(currentValue);
        return platformEnvironmentHistoryRecord;
    }
}
