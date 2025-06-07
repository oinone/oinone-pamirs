package pro.shushi.pamirs.framework.connectors.data.ddl.constants;

import pro.shushi.pamirs.framework.connectors.data.api.domain.model.system.FieldColumn;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.system.ModelTable;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.system.ModuleIndex;
import pro.shushi.pamirs.meta.common.util.SetUtils;

import java.util.Set;

/**
 * 系统表常量
 * <p>
 * 2020/9/4 1:48 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface SystemTableConstants {

    String[] tableModels = new String[]{
            ModelTable.MODEL_MODEL,
            FieldColumn.MODEL_MODEL,
            ModuleIndex.MODEL_MODEL,
    };

    Set<String> tableModelSet = SetUtils.asSet(tableModels);

    String[] tables = new String[]{
            ModelTable.TABLE_NAME,
            FieldColumn.TABLE_NAME,
            ModuleIndex.TABLE_NAME,
    };

}
