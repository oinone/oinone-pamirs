package pro.shushi.pamirs.framework.connectors.data.api.ddl;

import pro.shushi.pamirs.framework.connectors.data.api.domain.model.logic.LogicTable;
import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.Map;

/**
 * 表结构元数据映射上下文
 * <p>
 * 2020/8/12 4:29 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class ModelTableContext {

    private Map<String/*schema#table*/, LogicTable> logicTableMap;

    private Map<String/*model#sharding*/, String/*schema#table*/> modelMap;

    private Map<String/*model*/, String/*model*/> existTableModelMap;

    private Map<String/*new_schema#new_table*/, LogicTable> mappingLogicTableMap;

    private boolean supportDrop;

}
