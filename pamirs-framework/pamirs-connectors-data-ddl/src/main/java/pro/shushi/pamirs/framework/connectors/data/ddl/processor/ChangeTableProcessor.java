package pro.shushi.pamirs.framework.connectors.data.ddl.processor;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.logic.LogicTable;
import pro.shushi.pamirs.framework.connectors.data.api.domain.wrapper.ModelWrapper;
import pro.shushi.pamirs.framework.connectors.data.ddl.component.ColumnComponent;
import pro.shushi.pamirs.framework.connectors.data.ddl.component.TableComponent;
import pro.shushi.pamirs.framework.connectors.data.ddl.model.DdlContext;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 修改表
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@Slf4j
@Component
public class ChangeTableProcessor extends AbstractTableProcessor {

    @Resource
    private ChangeColumnProcessor changeColumnProcessor;

    @Resource
    private TableComponent tableComponent;

    @Resource
    private ColumnComponent columnComponent;

    @Resource
    private IndexProcessor indexProcessor;

    @Override
    public List<String> mainProcess(ModelWrapper modelDefinition, DdlContext ddlContext) {
        List<String> ddlList = new ArrayList<>();
        // alter 变更
        LogicTable table = ddlContext.getExistLogicTable();

        // rename
        tableComponent.rename(ddlContext, ddlList, modelDefinition, table);

        // 锁表
        tableComponent.lock(ddlList, modelDefinition, table.getDsKey());

        // 修改备注
        tableComponent.changeRemark(ddlContext, ddlList, modelDefinition, table);

        // 修改表字符集
        tableComponent.changeCharset(ddlContext, ddlList, modelDefinition, table);

        // 删除索引
        indexProcessor.deleteIndex(ddlContext, ddlList, modelDefinition, table);

        // 修改字段
        changeColumnProcessor.change(ddlContext, ddlList, modelDefinition, table);

        // 更改索引
        indexProcessor.changeIndex(ddlContext, ddlList, modelDefinition, table);

        // 删除字段
        columnComponent.deleteColumn(ddlContext, ddlList, table.getDsKey(), table);

        // 解锁表
        tableComponent.unlock(ddlList, table.getDsKey());

        // 语法校正
        tableComponent.fixDdl(ddlList, table.getDsKey());

        return ddlList;
    }

}
