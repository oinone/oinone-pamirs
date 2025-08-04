package pro.shushi.pamirs.framework.connectors.data.ddl.component;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.logic.LogicTable;
import pro.shushi.pamirs.framework.connectors.data.api.domain.wrapper.ModelWrapper;
import pro.shushi.pamirs.framework.connectors.data.configure.sharding.ShardingRuleConfiguration;
import pro.shushi.pamirs.framework.connectors.data.ddl.check.TableChecker;
import pro.shushi.pamirs.framework.connectors.data.ddl.constants.DdlConstants;
import pro.shushi.pamirs.framework.connectors.data.ddl.dialect.api.TableDialectComponent;
import pro.shushi.pamirs.framework.connectors.data.ddl.model.DdlContext;
import pro.shushi.pamirs.framework.connectors.data.ddl.utils.DdlUtils;
import pro.shushi.pamirs.framework.connectors.data.dialect.Dialects;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.session.cache.spi.SessionFillOwnSignApi;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.util.ParserUtil;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表操作组件
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@Slf4j
@Component
public class TableComponent {

    @Resource
    private ShardingRuleConfiguration shardingRuleConfiguration;

    @Resource
    private TableChecker tableChecker;

    public String tablePlaceholder(String dsKey, ModelWrapper modelDefinition) {
        return Dialects.component(TableDialectComponent.class, dsKey).tablePlaceholder(modelDefinition);
    }

    public String tablePlaceholder(LogicTable table) {
        return Dialects.component(TableDialectComponent.class, table.getDsKey()).tablePlaceholder(table);
    }

    public void createTable(List<String> ddlList, DdlContext ddlContext, ModelWrapper modelDefinition, ColumnCreator columnCreator) {
        TableDialectComponent tableDialectComponent = Dialects.component(TableDialectComponent.class, ddlContext.getDsKey());
        ddlContext.useLogicTable().setTableComment(modelDefinition.getRemark())
                .setCharacterSetName(tableDialectComponent.charset(modelDefinition))
                .setTableCollation(tableDialectComponent.collation(modelDefinition));
        ddlContext.updateModelTable(modelDefinition.getModule(), modelDefinition.getModel());
        ddlContext.changeTable();
        // 表名
        ddlList.add(tableDialectComponent.createTableHead(modelDefinition));
        // 字段处理
        ddlList.addAll(columnCreator.create());
        // 表引擎与备注
        ddlList.add(tableDialectComponent.createTableTail(modelDefinition));
    }

    public List<String> dropTable(LogicTable deprecatedTable) {
        if (Spider.getDefaultExtension(SessionFillOwnSignApi.class).handleOwnSign()) {
            return null;
        }
        return Dialects.component(TableDialectComponent.class, deprecatedTable.getDsKey()).dropTable(deprecatedTable);
    }

    public void lock(List<String> ddlList, ModelWrapper modelDefinition, String dsKey) {
        boolean isShardingConnection = shardingRuleConfiguration.containsKey(dsKey);
        if (!isShardingConnection) {
            ddlList.add(Dialects.component(TableDialectComponent.class, dsKey).lock(modelDefinition));
        }
    }

    public void unlock(List<String> ddlList, String dsKey) {
        boolean isShardingConnection = shardingRuleConfiguration.containsKey(dsKey);
        if (!isShardingConnection) {
            ddlList.add(Dialects.component(TableDialectComponent.class, dsKey).unlock());
        }
    }

    public void rename(DdlContext ddlContext, List<String> ddlList, ModelWrapper modelDefinition, LogicTable table) {
        if (!tableChecker.change(modelDefinition, table.useModelTable())) {
            return;
        }
        Map<String, Object> context = new HashMap<>();
        context.put(DdlConstants.SHARDING_PLACEHOLDER_NAME, ddlContext.getTableShardingNode());
        String newTableName = ParserUtil.replaceWithMap(tablePlaceholder(table.getDsKey(), modelDefinition), context);
        if (!newTableName.equals(table.getTableName())) {
            ddlList.add(Dialects.component(TableDialectComponent.class, table.getDsKey()).rename(modelDefinition, table));
            ddlContext.useLogicTable().setTableName(newTableName);
            ddlContext.updateModelTable(modelDefinition.getModule(), modelDefinition.getModel()).setTableName(newTableName);
            ddlContext.changeTable();
        }
    }

    public void changeRemark(DdlContext ddlContext, List<String> ddlList, ModelWrapper modelDefinition, LogicTable table) {
        if (!tableChecker.change(modelDefinition, table.useModelTable())) {
            return;
        }
        String comment = table.getTableComment();
        if (DdlUtils.notEqualsIgnoreNull(comment, modelDefinition.getRemark())) {
            ddlContext.useLogicTable().setTableComment(modelDefinition.getRemark());
            ddlContext.updateModelTable(modelDefinition.getModule(), modelDefinition.getModel());
            ddlContext.changeTable();
            ddlList.add(Dialects.component(TableDialectComponent.class, table.getDsKey()).changeRemark(modelDefinition));
        }
    }

    public void changeCharset(DdlContext ddlContext, List<String> ddlList, ModelWrapper modelDefinition, LogicTable table) {
        if (!tableChecker.change(modelDefinition, table.useModelTable())) {
            return;
        }
        String characterSetName = table.getCharacterSetName();
        String tableCollation = table.getTableCollation();
        if (!Dialects.component(TableDialectComponent.class, table.getDsKey()).equalsCharset(modelDefinition, characterSetName, tableCollation)) {
            String newCharset = Dialects.component(TableDialectComponent.class, table.getDsKey()).charset(modelDefinition);
            String newCollation = Dialects.component(TableDialectComponent.class, table.getDsKey()).collation(modelDefinition);
            ddlContext.useLogicTable().setTableComment(modelDefinition.getRemark()).setCharacterSetName(newCharset).setTableCollation(newCollation);
            ddlContext.updateModelTable(modelDefinition.getModule(), modelDefinition.getModel());
            ddlContext.changeTable();
            ddlList.add(Dialects.component(TableDialectComponent.class, table.getDsKey()).changeCharset(modelDefinition));
        }
    }

    public interface ColumnCreator {
        List<String> create();
    }

    public void fixDdl(List<String> ddlList, String dsKey) {
        Dialects.component(TableDialectComponent.class, dsKey).fixDdl(ddlList);
    }

}
