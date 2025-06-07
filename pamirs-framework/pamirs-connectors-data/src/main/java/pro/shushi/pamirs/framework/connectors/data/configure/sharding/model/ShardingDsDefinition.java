package pro.shushi.pamirs.framework.connectors.data.configure.sharding.model;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.faas.script.ScriptRunner;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.List;
import java.util.Optional;

/**
 * sharding数据源定义
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/11 1:36 下午
 */
@Data
public class ShardingDsDefinition {

    private String dsNodes;

    private String separator = CharacterConstants.SEPARATOR_EMPTY;

    private List<String> excludeModels;

    public List<Object> getDatabaseNodes() {
        if (StringUtils.isBlank(dsNodes)) {
            return null;
        }
        return ScriptRunner.run(dsNodes);
    }

    public boolean isSharding() {
        return Optional.ofNullable(getDatabaseNodes()).map(List::size).orElse(0) > 1;
    }

}
