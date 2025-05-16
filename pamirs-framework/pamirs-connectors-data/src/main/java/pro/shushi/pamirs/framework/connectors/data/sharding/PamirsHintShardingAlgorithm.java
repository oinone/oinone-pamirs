package pro.shushi.pamirs.framework.connectors.data.sharding;

import org.apache.shardingsphere.sharding.api.sharding.hint.HintShardingAlgorithm;
import org.apache.shardingsphere.sharding.api.sharding.hint.HintShardingValue;

import java.util.Collection;
import java.util.Properties;

/**
 * pamirs 强制路由策略
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/15 12:36 下午
 */
public class PamirsHintShardingAlgorithm implements HintShardingAlgorithm {

    @Override
    public Collection<String> doSharding(Collection collection, HintShardingValue hintShardingValue) {
        hintShardingValue.getColumnName();
        hintShardingValue.getLogicTableName();
        hintShardingValue.getValues();
        return collection;
    }

    @Override
    public void init() {

    }

    @Override
    public String getType() {
        return "HINT_SHARDING_ALGORITHM";
    }

    @Override
    public Properties getProps() {
        return null;
    }

    @Override
    public void setProps(Properties props) {

    }

}
