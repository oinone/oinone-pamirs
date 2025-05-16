package pro.shushi.pamirs.framework.connectors.data.util;

import pro.shushi.pamirs.framework.connectors.data.constant.DbConstants;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * 逻辑删除工具类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/14 4:20 下午
 */
public class LogicDeleteUtils {

    public static final String NAME_LOGIC_DELETE_COLUMN = "logicDeleteColumn";

    public static final String NAME_LOGIC_DELETE_VALUE = "logicDeleteValue";

    public static final String NAME_LOGIC_NOT_DELETE_VALUE = "logicNotDeleteValue";

    public static void fillLogicDelete(PamirsTableInfo pamirsTableInfo, Map map) {
        String logicDeleteColumn = pamirsTableInfo.getLogicDeleteColumn();
        String logicDeleteValue = pamirsTableInfo.getLogicDeleteValue();
        String logicNotDeleteValue = pamirsTableInfo.getLogicNotDeleteValue();
        Map entity = (Map) map.getOrDefault(DbConstants.PARAM_ANNOTATION_LD, new HashMap<>());
        entity.put(NAME_LOGIC_DELETE_COLUMN, logicDeleteColumn);
        entity.put(NAME_LOGIC_DELETE_VALUE, logicDeleteValue);
        entity.put(NAME_LOGIC_NOT_DELETE_VALUE, logicNotDeleteValue);
        map.put(DbConstants.PARAM_ANNOTATION_LD, entity);
    }

}
