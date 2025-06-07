package pro.shushi.pamirs.framework.connectors.data.configure.sharding.model;

import org.apache.shardingsphere.encrypt.yaml.config.YamlEncryptRuleConfiguration;
import org.apache.shardingsphere.infra.yaml.config.YamlRootRuleConfigurations;
import org.apache.shardingsphere.replicaquery.yaml.config.YamlReplicaQueryRuleConfiguration;
import org.apache.shardingsphere.shadow.yaml.config.YamlShadowRuleConfiguration;
import org.apache.shardingsphere.sharding.yaml.config.YamlShardingRuleConfiguration;

import java.util.Collection;

/**
 * 租户sharding配置
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/11 1:36 下午
 */
public class ShardingSpecificConfiguration extends YamlRootRuleConfigurations {

    private String[] actualDs;

    public String[] getActualDs() {
        return actualDs;
    }

    public void setActualDs(String[] actualDs) {
        this.actualDs = actualDs;
    }

    public void setShardingRules(Collection<YamlShardingRuleConfiguration> shardingRules) {
        this.getRules().addAll(shardingRules);
    }

    public void setReplicaQueryRules(Collection<YamlReplicaQueryRuleConfiguration> replicaQueryRules) {
        this.getRules().addAll(replicaQueryRules);
    }

    public void setEncryptRules(Collection<YamlEncryptRuleConfiguration> encryptRules) {
        this.getRules().addAll(encryptRules);
    }

    public void setShadowRules(Collection<YamlShadowRuleConfiguration> shadowRules) {
        this.getRules().addAll(shadowRules);
    }

}
