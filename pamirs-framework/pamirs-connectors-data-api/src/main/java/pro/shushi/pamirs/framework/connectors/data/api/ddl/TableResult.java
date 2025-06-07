package pro.shushi.pamirs.framework.connectors.data.api.ddl;

import pro.shushi.pamirs.framework.connectors.data.api.domain.model.logic.LogicTable;
import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.List;

/**
 * 含分库分表通配符的ddl计算结果
 * <p>
 * 2020/7/9 2:38 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class TableResult {

    private LogicTable logicTable;

    private List<String> ddl;

}
