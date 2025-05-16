package pro.shushi.pamirs.framework.connectors.data.api.domain.model.logic;

import pro.shushi.pamirs.framework.connectors.data.api.domain.model.system.FieldColumn;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.system.ModelTable;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.system.ModuleIndex;
import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.List;

/**
 * 表结构与元数据映射关系封装类
 * <p>
 * 2020/8/7 8:55 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class SchemaMataData {

    private List<ModelTable> modelTableList;

    private List<FieldColumn> fieldColumnList;

    private List<ModuleIndex> moduleIndexList;

}
