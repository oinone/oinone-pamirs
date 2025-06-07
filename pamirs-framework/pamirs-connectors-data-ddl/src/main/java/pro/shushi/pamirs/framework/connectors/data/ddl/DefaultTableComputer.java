package pro.shushi.pamirs.framework.connectors.data.ddl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.api.ddl.SchemaTableKey;
import pro.shushi.pamirs.framework.connectors.data.api.ddl.TableComputer;
import pro.shushi.pamirs.framework.connectors.data.api.ddl.TableResult;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.logic.LogicTable;
import pro.shushi.pamirs.framework.connectors.data.api.domain.wrapper.ModelWrapper;
import pro.shushi.pamirs.framework.connectors.data.ddl.check.TableChecker;
import pro.shushi.pamirs.framework.connectors.data.ddl.model.DdlContext;
import pro.shushi.pamirs.framework.connectors.data.ddl.processor.ChangeTableProcessor;
import pro.shushi.pamirs.framework.connectors.data.ddl.processor.CreateTableProcessor;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spi.SPI;

import javax.annotation.Resource;
import java.util.List;

/**
 * 数据表配置计算
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@Slf4j
@Order
@Component
@SPI.Service
public class DefaultTableComputer extends AbstractTableComputer implements TableComputer {

    @Resource
    private ChangeTableProcessor changeTableProcessor;

    @Resource
    private CreateTableProcessor createTableProcessor;

    @Resource
    private TableChecker tableChecker;

    @Override
    public TableResult compute(SchemaTableKey schemaTableKey, ModelWrapper modelDefinition, LogicTable logicTable) {
        TableResult result = new TableResult();
        // 表检查
        if (!tableChecker.check(modelDefinition)) {
            return null;
        }
        DdlContext ddlContext = new DdlContext();
        String tableSchema = SchemaTableKey.fetchTableSchema(schemaTableKey);
        String tableName = SchemaTableKey.fetchTableName(schemaTableKey);
        boolean isExistTable = StringUtils.isNotBlank(logicTable.getTableName());
        // 组装上下文
        ddlContext.setDsKey(SchemaTableKey.fetchDsKey(schemaTableKey)).setModule(modelDefinition.getModule());
        ddlContext.setExistLogicTable(logicTable);
        ddlContext.setTableShardingNode(SchemaTableKey.fetchTableShardingNode(schemaTableKey));
        ddlContext.useLogicTable().setDsKey(ddlContext.getDsKey()).setModule(modelDefinition.getModule()).setModel(modelDefinition.getModel())
                .setSchemaAndTable(tableSchema, tableName);
        ddlContext.unDropTable();
        // 生成表脚本
        List<String> tableDdl;
        if (isExistTable) {
            tableDdl = changeTableProcessor.process(modelDefinition, ddlContext);
        } else {
            tableDdl = createTableProcessor.process(modelDefinition, ddlContext);
        }
        result.setDdl(tableDdl);
        result.setLogicTable(ddlContext.getExistLogicTable());
        return result;
    }

}
