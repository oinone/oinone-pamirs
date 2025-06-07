package pro.shushi.pamirs.framework.connectors.data.ddl.sharding;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.api.ddl.ShardingTableComputer;
import pro.shushi.pamirs.framework.connectors.data.configure.sharding.ShardingDefineConfiguration;
import pro.shushi.pamirs.framework.connectors.data.configure.sharding.model.ShardingDsDefinition;
import pro.shushi.pamirs.framework.connectors.data.configure.sharding.model.ShardingTableDefinition;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 分表配置计算
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@Slf4j
@Component
public class DefaultShardingTableComputer implements ShardingTableComputer {

    @Resource
    private ShardingDefineConfiguration shardingDefineConfiguration;

    @Override
    public void computeShardingTable(String module, String model,
                                     ShardingComputer shardingTableComputer,
                                     ShardingComputer shardingDatabaseComputer,
                                     ShardingComputer noShardingTableComputer
    ) {
        ShardingTableDefinition shardingTableDefinition = shardingDefineConfiguration.getDefinitionForModel(model);
        if (null != shardingTableDefinition) {
            List<Object> dataNodes = shardingTableDefinition.getDatabaseNodes();
            List<Object> tableNodes = shardingTableDefinition.getTableNodes();
            String dsSeparator = shardingTableDefinition.getDsSeparator();
            String tableSeparator = shardingTableDefinition.getTableSeparator();
            dataNodes = CollectionUtils.isEmpty(dataNodes) ? Lists.newArrayList(CharacterConstants.SEPARATOR_EMPTY) : dataNodes;
            tableNodes = CollectionUtils.isEmpty(tableNodes) ? Lists.newArrayList(CharacterConstants.SEPARATOR_EMPTY) : tableNodes;
            dsSeparator = dataNodes.size() > 1 ? dsSeparator : CharacterConstants.SEPARATOR_EMPTY;
            tableSeparator = tableNodes.size() > 1 ? tableSeparator : CharacterConstants.SEPARATOR_EMPTY;
            for (Object dataNode : dataNodes) {
                for (Object tableNode : tableNodes) {
                    shardingTableComputer.compute(String.valueOf(dataNode), dsSeparator, tableNode, tableSeparator);
                }
            }
        } else {
            ShardingDsDefinition shardingDsDefinition = shardingDefineConfiguration.getDefinitionForModule(module);
            if (null != shardingDsDefinition && (null == shardingDsDefinition.getExcludeModels()
                    || !shardingDsDefinition.getExcludeModels().contains(model))) {
                List<Object> dataNodes = shardingDsDefinition.getDatabaseNodes();
                String dsSeparator = shardingDsDefinition.getSeparator();
                dataNodes = CollectionUtils.isEmpty(dataNodes) ? Lists.newArrayList(CharacterConstants.SEPARATOR_EMPTY) : dataNodes;
                dsSeparator = dataNodes.size() > 1 ? dsSeparator : CharacterConstants.SEPARATOR_EMPTY;
                for (Object dataNode : dataNodes) {
                    shardingDatabaseComputer.compute(String.valueOf(dataNode), dsSeparator,
                            CharacterConstants.SEPARATOR_EMPTY, CharacterConstants.SEPARATOR_EMPTY);
                }
            } else {
                noShardingTableComputer.compute(CharacterConstants.SEPARATOR_EMPTY, CharacterConstants.SEPARATOR_EMPTY,
                        CharacterConstants.SEPARATOR_EMPTY, CharacterConstants.SEPARATOR_EMPTY);
            }
        }
    }

    @Override
    public List<String> completedDataNodes(String prefix, String separator, List<Object> dataNodes) {
        dataNodes = CollectionUtils.isEmpty(dataNodes) ? Lists.newArrayList(CharacterConstants.SEPARATOR_EMPTY) : dataNodes;
        return dataNodes.stream()
                .map(v -> completedDataNode(prefix, separator, v))
                .collect(Collectors.toList());
    }

    @Override
    public String completedDataNode(String prefix, String separator, Object dataNode) {
        if (CharacterConstants.SEPARATOR_EMPTY.equals(dataNode)) {
            return prefix;
        }
        return StringUtils.join(new String[]{prefix, String.valueOf(dataNode)}, separator);
    }

}
