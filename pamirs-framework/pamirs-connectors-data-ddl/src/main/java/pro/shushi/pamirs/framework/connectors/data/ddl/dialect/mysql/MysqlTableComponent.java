package pro.shushi.pamirs.framework.connectors.data.ddl.dialect.mysql;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.logic.LogicTable;
import pro.shushi.pamirs.framework.connectors.data.api.domain.wrapper.ModelWrapper;
import pro.shushi.pamirs.framework.connectors.data.configure.sharding.ShardingDefineConfiguration;
import pro.shushi.pamirs.framework.connectors.data.configure.sharding.model.ShardingTableDefinition;
import pro.shushi.pamirs.framework.connectors.data.ddl.constants.DdlConstants;
import pro.shushi.pamirs.framework.connectors.data.ddl.dialect.api.TableDialectComponent;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.Dialect;
import pro.shushi.pamirs.framework.connectors.data.dialect.constants.DataProductVersion;
import pro.shushi.pamirs.meta.api.prefix.DataPrefixManager;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.constant.SqlConstants;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * 表操作组件
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@Order(0)
@Dialect.component
@SPI.Service(DataProductVersion.PRODUCT_MYSQL)
@Component
public class MysqlTableComponent implements TableDialectComponent {

    @Resource
    private ShardingDefineConfiguration shardingDefineConfiguration;

    @Override
    public String tablePlaceholder(ModelWrapper modelDefinition) {
        Optional<ShardingTableDefinition> shardingTableDefinition = getShardingTableDefinition(modelDefinition.getModel());
        boolean isSharding = isSharding(shardingTableDefinition);
        String separator = getSeparator(shardingTableDefinition);
        String module = modelDefinition.getModule();
        String table = DataPrefixManager.tablePrefix(module, modelDefinition.getModel(), modelDefinition.getTable());
        return isSharding ? (table + separator + DdlConstants.SHARDING_PLACEHOLDER) : table;
    }

    @Override
    public String tablePlaceholder(LogicTable table) {
        Optional<ShardingTableDefinition> shardingTableDefinition = getShardingTableDefinition(table.getModel());
        boolean isSharding = isSharding(shardingTableDefinition);
        String separator = getSeparator(shardingTableDefinition);
        String logicalTableName = table.useModelTable().getLogicTableName();
        return isSharding ? (logicalTableName + separator + DdlConstants.SHARDING_PLACEHOLDER) : table.getTableName();
    }

    @Override
    public void fixDdl(List<String> ddlList) {
        // 表未变更
        if (ddlList.size() <= 2) {
            ddlList.clear();
            return;
        } else if (ddlList.size() == 3) {// 多余的锁表与解锁表
            if (ddlList.get(1).startsWith(lockTableCommandPrefix()) && ddlList.get(2).startsWith(unlockTableCommandPrefix())) {
                ddlList.remove(1);
                ddlList.remove(1);
            }
        }
        // 修改主键，修改主键字段语句需要后置
        int autoIncrementIndex = 0;
        int addPrimaryKeyIndex = 0;
        boolean containsAutoIncrement = false;
        boolean containsAddPk = false;
        for (String ddl : ddlList) {
            containsAutoIncrement = containsAutoIncrement || ddl.contains(SqlConstants.AUTO_INCREMENT);
            containsAddPk = containsAddPk || ddl.contains(SqlConstants.ADD_PRIMARY_KEY);
            if (!containsAutoIncrement) {
                autoIncrementIndex++;
            }
            if (!containsAddPk) {
                addPrimaryKeyIndex++;
            }
        }
        if (containsAutoIncrement && containsAddPk) {
            String ddl = ddlList.remove(autoIncrementIndex);
            ddlList.add(addPrimaryKeyIndex - 1, ddl.replace(CharacterConstants.SEPARATOR_SEMICOLON,
                    CharacterConstants.SEPARATOR_BLANK
                            + SqlConstants.PRIMARY_KEY + CharacterConstants.SEPARATOR_SEMICOLON));
            ddlList.remove(addPrimaryKeyIndex);
        }
    }

    private Optional<ShardingTableDefinition> getShardingTableDefinition(String model) {
        return Optional.ofNullable(shardingDefineConfiguration)
                .map(v -> v.getDefinitionForModel(model));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private boolean isSharding(Optional<ShardingTableDefinition> shardingTableDefinition) {
        return shardingTableDefinition.map(ShardingTableDefinition::isTableSharding).orElse(false);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private String getSeparator(Optional<ShardingTableDefinition> shardingTableDefinition) {
        return shardingTableDefinition.map(ShardingTableDefinition::getTableSeparator)
                .orElse(CharacterConstants.SEPARATOR_UNDERLINE);
    }

}
