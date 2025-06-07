package pro.shushi.pamirs.framework.connectors.data.configure.sharding.model;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.faas.script.ScriptRunner;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.List;
import java.util.Optional;

/**
 * sharding分库分表定义
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/11 1:36 下午
 */
@Data
public class ShardingTableDefinition {

    private String dsNodes;

    private String dsSeparator = CharacterConstants.SEPARATOR_EMPTY;

    private String tables;

    private String tableSeparator = CharacterConstants.SEPARATOR_UNDERLINE;

    public List<Object> getDatabaseNodes() {
        if (StringUtils.isBlank(dsNodes)) {
            return null;
        }
        return ScriptRunner.run(dsNodes);
    }

    public List<Object> getTableNodes() {
        if (StringUtils.isBlank(tables)) {
            return null;
        }
        return ScriptRunner.run(tables);
    }

    public boolean isSharding() {
        return Optional.ofNullable(getDatabaseNodes()).map(List::size).orElse(0) > 1
                || Optional.ofNullable(getTableNodes()).map(List::size).orElse(0) > 1;
    }

    public boolean isDsSharding() {
        return Optional.ofNullable(getDatabaseNodes()).map(List::size).orElse(0) > 1;
    }

    public boolean isTableSharding() {
        return Optional.ofNullable(getTableNodes()).map(List::size).orElse(0) > 1;
    }

}
