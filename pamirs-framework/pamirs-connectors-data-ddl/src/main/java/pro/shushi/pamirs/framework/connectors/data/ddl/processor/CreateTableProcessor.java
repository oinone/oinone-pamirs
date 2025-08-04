package pro.shushi.pamirs.framework.connectors.data.ddl.processor;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.api.domain.wrapper.FieldWrapper;
import pro.shushi.pamirs.framework.connectors.data.api.domain.wrapper.ModelWrapper;
import pro.shushi.pamirs.framework.connectors.data.ddl.check.ColumnChecker;
import pro.shushi.pamirs.framework.connectors.data.ddl.component.ColumnComponent;
import pro.shushi.pamirs.framework.connectors.data.ddl.component.IndexComponent;
import pro.shushi.pamirs.framework.connectors.data.ddl.component.TableComponent;
import pro.shushi.pamirs.framework.connectors.data.ddl.model.DdlContext;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建表
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@Slf4j
@Component
public class CreateTableProcessor extends AbstractTableProcessor {

    @Resource
    private FieldProcessor fieldProcessor;

    @Resource
    private TableComponent tableComponent;

    @Resource
    private ColumnComponent columnComponent;

    @Resource
    private IndexComponent indexComponent;

    @Resource
    private ColumnChecker columnChecker;

    @Override
    public List<String> mainProcess(ModelWrapper modelDefinition, DdlContext ddlContext) {
        List<String> ddlList = new ArrayList<>();
        // create table
        tableComponent.createTable(ddlList, ddlContext, modelDefinition, () -> {// 字段处理
            String table = tableComponent.tablePlaceholder(ddlContext.getDsKey(), modelDefinition);
            List<String> columnDdlList = new ArrayList<>();
            // 字段排序
            modelDefinition.getModelFields().sort((o1, o2) -> (int) (o1.getPriority() - o2.getPriority()));

            // 字段
            List<String> newStoreColumns = new ArrayList<>();
            for (FieldWrapper modelField : modelDefinition.getModelFields()) {
                if (!columnChecker.store(modelField)) {
                    continue;
                }
                ddlContext.unDropColumn(modelField.getField());
                newStoreColumns.add(modelField.getColumn());
                String columnDefinition = fieldProcessor.prepareModelFields(ddlContext.getDsKey(), modelDefinition, modelField);
                columnComponent.addColumn(columnDdlList, ddlContext.getDsKey(), Boolean.TRUE, table, modelField.getColumn(), columnDefinition,
                        modelField.getSummary(), null);
                ddlContext.refreshColumn(modelField, columnDefinition);
                // 更新列元数据和创建字段索引时忽略同表扩展继承字段
                if (!columnChecker.extend(modelField) || modelDefinition.getIsChangeTable()) {
                    ddlContext.updateFieldColumn(modelField, modelDefinition.getModule());
                    ddlContext.changeColumn(modelField.getField());
                    indexComponent.prepareIndexes(ddlContext.getLogicColumns(),
                            ddlContext.getIndexColumnSet(), ddlContext.getUniqueColumnSet(), modelField);
                }
            }

            // 处理逻辑字段
            columnComponent.handleLogicField(ddlContext, columnDdlList, Boolean.TRUE, ddlContext.getDsKey(), modelDefinition, table, null, newStoreColumns);
            // 主键
            indexComponent.createPrimaryKey(ddlContext, modelDefinition, columnDdlList);
            return columnDdlList;
        });

        // 索引
        indexComponent.createIndex(ddlContext, modelDefinition, ddlContext.getIndexColumnSet(),
                ddlList, Boolean.FALSE, ddlContext.getLogicDeleteColumn());
        // 唯一索引
        indexComponent.createIndex(ddlContext, modelDefinition, ddlContext.getUniqueColumnSet(),
                ddlList, Boolean.TRUE, ddlContext.getLogicDeleteColumn());

        return ddlList;
    }

}
