package pro.shushi.pamirs.framework.connectors.data.ddl.model;

import pro.shushi.pamirs.framework.connectors.data.api.ddl.DdlResult;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.logic.LogicTable;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;

/**
 * 数据表差量对象
 * <p>
 * 2020/8/12 4:29 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class TableDiff {

    private DdlResult result;

    private LogicTable logicTable;

    private ModelDefinition modelDefinition;

    private String model;

    private String leafDsKey;

    private Object tableNode;

    private String tableSchema;

    private String tableName;

}
