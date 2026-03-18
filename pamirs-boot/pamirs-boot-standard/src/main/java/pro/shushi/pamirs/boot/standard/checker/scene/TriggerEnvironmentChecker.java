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
import pro.shushi.pamirs.locale.utils.I18nUtils;
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
            addError(context, generatorEnvironmentProperty(PAMIRS_META_PACKAGES_KEY, null), I18nUtils.getMessage("TriggerEnvironmentChecker.meta.packages.error", PAMIRS_META_PACKAGES_KEY, TRIGGER_META_PACKAGE));
            printTriggerTip = true;
        }
        String pamirsScheduleShardingDefine = Optional.ofNullable(shardingDefineConfiguration.getDefinitionForModel("trigger.PamirsSchedule"))
                .map(ShardingTableDefinition::getTables)
                .orElse(null);
        if (StringUtils.isBlank(pamirsScheduleShardingDefine) || !PAMIRS_SCHEDULE_SHARDING_DEFINE_VALUE.equals(pamirsScheduleShardingDefine)) {
            addError(context, generatorEnvironmentProperty(PAMIRS_SCHEDULE_SHARDING_DEFINE_KEY, pamirsScheduleShardingDefine), I18nUtils.getMessage("TriggerEnvironmentChecker.sharding.error", PAMIRS_SCHEDULE_SHARDING_DEFINE_KEY, PAMIRS_SCHEDULE_SHARDING_DEFINE_VALUE));
            printTriggerTip = true;
        }
        String eventEnabled = getProperty(PAMIRS_EVENT_ENABLED_KEY, Boolean.TRUE.toString());
        if (!Boolean.TRUE.toString().equals(eventEnabled)) {
            addWarning(context, generatorEnvironmentProperty(PAMIRS_EVENT_ENABLED_KEY, eventEnabled), I18nUtils.getMessage("TriggerEnvironmentChecker.event.disabled.warn"));
        }
        String scheduleEnabled = getProperty(PAMIRS_EVENT_SCHEDULE_ENABLED_KEY, Boolean.TRUE.toString());
        if (!Boolean.TRUE.toString().equals(scheduleEnabled)) {
            addWarning(context, generatorEnvironmentProperty(PAMIRS_EVENT_SCHEDULE_ENABLED_KEY, scheduleEnabled), I18nUtils.getMessage("TriggerEnvironmentChecker.schedule.disabled.warn"));
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
                context.addWarning(currentEnvironment, I18nUtils.getMessage("TriggerEnvironmentChecker.ownSign.warn"));
            }
        }
        return currentEnvironment;
    }

    private String triggerModuleTip() {
        return I18nUtils.getMessage("TriggerEnvironmentChecker.triggerModuleTip");
    }
}
