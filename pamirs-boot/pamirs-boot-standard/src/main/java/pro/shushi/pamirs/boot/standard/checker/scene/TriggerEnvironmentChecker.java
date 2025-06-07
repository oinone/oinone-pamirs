package pro.shushi.pamirs.boot.standard.checker.scene;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.standard.checker.PlatformEnvironmentChecker;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentCheckContext;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentKey;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentKeySet;
import pro.shushi.pamirs.framework.configure.MetaConfiguration;
import pro.shushi.pamirs.framework.connectors.data.configure.sharding.ShardingDefineConfiguration;
import pro.shushi.pamirs.framework.connectors.data.configure.sharding.model.ShardingTableDefinition;
import pro.shushi.pamirs.meta.domain.PlatformEnvironment;

import java.util.List;
import java.util.Optional;

/**
 * 触发器环境检查
 *
 * @author Adamancy Zhang at 20:42 on 2024-10-14
 */
@Component
public class TriggerEnvironmentChecker extends AbstractSceneEnvironmentChecker implements PlatformEnvironmentChecker {

    private static final String PAMIRS_META_PACKAGES_KEY = "pamirs.meta.meta-packages";

    private static final String TRIGGER_META_PACKAGE = "pro.shushi.pamirs.trigger.model";

    private static final String PAMIRS_SCHEDULE_SHARDING_DEFINE_KEY = "pamirs.sharding.define.models[trigger.PamirsSchedule].tables";

    private static final String PAMIRS_SCHEDULE_SHARDING_DEFINE_VALUE = "0..13";

    private static final String PAMIRS_EVENT_ENABLED_KEY = "pamirs.event.enabled";

    private static final String PAMIRS_EVENT_SCHEDULE_ENABLED_KEY = "pamirs.event.schedule.enabled";

    @Autowired
    private MetaConfiguration metaConfiguration;

    @Autowired
    private ShardingDefineConfiguration shardingDefineConfiguration;

    @Override
    protected EnvironmentKeySet propertyKeys() {
        return newEnvironmentKeySet(EnvironmentKey.Level.IMMUTABLE,
                EnvironmentKey.immutable("pamirs.event.schedule.own-sign", "BASE", null, TriggerEnvironmentChecker::checkOwnSign)
        );
    }

    @Override
    protected List<PlatformEnvironment> check(EnvironmentCheckContext context,
                                              List<PlatformEnvironment> allEnvironments,
                                              List<PlatformEnvironment> currentEnvironments,
                                              List<PlatformEnvironment> historyEnvironments) {
        List<String> metaPackages = metaConfiguration.getMetaPackages();
        boolean printTriggerTip = false;
        if (CollectionUtils.isEmpty(metaPackages) || !metaPackages.contains(TRIGGER_META_PACKAGE)) {
            addError(context, generatorEnvironmentProperty(PAMIRS_META_PACKAGES_KEY, null), "请配置元数据包路径: " + PAMIRS_META_PACKAGES_KEY + "=" + TRIGGER_META_PACKAGE);
            printTriggerTip = true;
        }
        String pamirsScheduleShardingDefine = Optional.ofNullable(shardingDefineConfiguration.getDefinitionForModel("trigger.PamirsSchedule"))
                .map(ShardingTableDefinition::getTables)
                .orElse(null);
        if (StringUtils.isBlank(pamirsScheduleShardingDefine) || !PAMIRS_SCHEDULE_SHARDING_DEFINE_VALUE.equals(pamirsScheduleShardingDefine)) {
            addError(context, generatorEnvironmentProperty(PAMIRS_SCHEDULE_SHARDING_DEFINE_KEY, pamirsScheduleShardingDefine), "请配置PamirsSchedule分表定义: " + PAMIRS_SCHEDULE_SHARDING_DEFINE_KEY + "=" + PAMIRS_SCHEDULE_SHARDING_DEFINE_VALUE);
            printTriggerTip = true;
        }
        String eventEnabled = getProperty(PAMIRS_EVENT_ENABLED_KEY, Boolean.TRUE.toString());
        if (!Boolean.TRUE.toString().equals(eventEnabled)) {
            addWarning(context, generatorEnvironmentProperty(PAMIRS_EVENT_ENABLED_KEY, eventEnabled), "未开启PamirsEvent功能，消息队列、工作流等功能将无法使用");
        }
        String scheduleEnabled = getProperty(PAMIRS_EVENT_SCHEDULE_ENABLED_KEY, Boolean.TRUE.toString());
        if (!Boolean.TRUE.toString().equals(scheduleEnabled)) {
            addWarning(context, generatorEnvironmentProperty(PAMIRS_EVENT_SCHEDULE_ENABLED_KEY, scheduleEnabled), "未开启PamirsSchedule功能，异步执行、导出、工作流等功能将无法正常使用");
        } else {
            checkScheduleDialect(allEnvironments);
        }
        if (printTriggerTip) {
            addError(context, generatorEnvironmentProperty(module(), null), triggerModuleTip());
        }
        return null;
    }

    private void checkScheduleDialect(List<PlatformEnvironment> allEnvironments) {
        String triggerDsMapKey = "pamirs.framework.data.dsMap[trigger]";
        PlatformEnvironment triggerDsMap = findEnvironmentByKey(allEnvironments, triggerDsMapKey);
    }

    private static PlatformEnvironment checkOwnSign(EnvironmentCheckContext context, PlatformEnvironment currentEnvironment, PlatformEnvironment historyEnvironment) {
        String oldValue = historyEnvironment.getValue();
        String newValue = currentEnvironment.getValue();
        if (context.isCollaborativeDevelopmentEnvironment()) {
            if (newValue.equals(oldValue)) {
                context.addWarning(currentEnvironment, "协同开发模式下，pamirs.event.schedule.ownSign需要与公共环境配置不一致。如果配置相同，将无法正常调试异步任务、定时任务相关功能");
            }
        }
        return currentEnvironment;
    }

    private String triggerModuleTip() {
        return "\n\ntrigger模块启动时需要添加如下配置项\n\n" +
                "启动工程pom:\n\n" +
                "<dependency>\n" +
                "    <groupId>pro.shushi.pamirs.core</groupId>\n" +
                "    <artifactId>pamirs-trigger-core</artifactId>\n" +
                "</dependency>\n" +
                "<dependency>\n" +
                "    <groupId>pro.shushi.pamirs.core</groupId>\n" +
                "    <artifactId>pamirs-trigger-bridge-tbschedule</artifactId>\n" +
                "</dependency>\n\n" +
                "yaml:\n\n" +
                "pamirs:\n" +
                "  meta:\n" +
                "    metaPackages:\n" +
                "      - pro.shushi.pamirs.trigger.model\n" +
                "  sharding:\n" +
                "    define:\n" +
                "      models:\n" +
                "        \"[trigger.PamirsSchedule]\":\n" +
                "          tables: 0..13\n" +
                "  event:\n" +
                "    enabled: true\n" +
                "    schedule:\n" +
                "      enabled: true\n\n";
    }
}
