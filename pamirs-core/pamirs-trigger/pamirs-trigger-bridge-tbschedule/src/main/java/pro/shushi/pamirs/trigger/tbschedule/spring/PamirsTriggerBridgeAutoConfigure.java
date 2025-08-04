package pro.shushi.pamirs.trigger.tbschedule.spring;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.framework.connectors.data.configure.sharding.ShardingDefineConfiguration;
import pro.shushi.pamirs.framework.connectors.data.configure.sharding.model.ShardingTableDefinition;
import pro.shushi.pamirs.trigger.tbschedule.model.PamirsSchedule;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Trigger自动配置
 *
 * @author Adamancy Zhang at 16:12 on 2025-03-24
 */
@Configuration
public class PamirsTriggerBridgeAutoConfigure {

    @Autowired
    private ShardingDefineConfiguration shardingDefineConfiguration;

    @PostConstruct
    public void init() {
        initSharding();
    }

    private void initSharding() {
        Map<String, ShardingTableDefinition> models = shardingDefineConfiguration.getModels();
        if (models == null) {
            models = new HashMap<>();
            shardingDefineConfiguration.setModels(models);
        }
        ShardingTableDefinition shardingTableDefinition = models.get(PamirsSchedule.MODEL_MODEL);
        if (shardingTableDefinition == null) {
            shardingTableDefinition = new ShardingTableDefinition();
            models.put(PamirsSchedule.MODEL_MODEL, shardingTableDefinition);
        }
        if (StringUtils.isBlank(shardingTableDefinition.getTables())) {
            shardingTableDefinition.setTables("0..13");
        }
    }
}
