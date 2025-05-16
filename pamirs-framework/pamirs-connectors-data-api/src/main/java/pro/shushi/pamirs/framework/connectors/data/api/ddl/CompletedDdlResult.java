package pro.shushi.pamirs.framework.connectors.data.api.ddl;

import pro.shushi.pamirs.framework.connectors.data.api.domain.model.logic.LogicTable;
import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.List;
import java.util.Map;

/**
 * 包含计算分库分表的ddl结果
 * 2020/6/23 10:23 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class CompletedDdlResult {

    private Map<String, LogicTable> newTableColumns;

    private Map<String/*dsKey for single database*/, List<String>/*true table ddl*/> ddlMap;

}
