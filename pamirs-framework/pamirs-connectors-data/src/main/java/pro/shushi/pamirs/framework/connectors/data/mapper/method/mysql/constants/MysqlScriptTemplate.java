package pro.shushi.pamirs.framework.connectors.data.mapper.method.mysql.constants;

import com.baomidou.mybatisplus.core.toolkit.Constants;
import pro.shushi.pamirs.framework.connectors.data.mapper.template.ScriptTemplate;
import pro.shushi.pamirs.framework.connectors.data.plugin.util.FieldUtil;

/**
 * 脚本 模板
 * <p>
 * 2023/06/25
 *
 * @author wx@shushi.pro
 * @version 1.0.0
 */
public interface MysqlScriptTemplate extends ScriptTemplate {

    String ON_DUPLICATE_KEY_UPDATE = "ON DUPLICATE KEY UPDATE %s";

    String LOGIC_DELETE = "`${ld.logicDeleteColumn}` = ${ld.logicNotDeleteValue}";

    String OPTIMISTIC_LOCKER_COLUMN = "`${" + UPDATED_VERSION_VAL_KEY + "}`";

    String OPTIMISTIC_LOCKER_VALUE = "${" + FieldUtil.NAME + ".get(%s ,\"" + Constants.MP_OPTLOCK_VERSION_ORIGINAL + "\")}";

    String OPTIMISTIC_LOCKER = OPTIMISTIC_LOCKER_COLUMN + " = " + OPTIMISTIC_LOCKER_VALUE;

    String LOGIC_DELETE_SQL = "UPDATE %s SET `${ld.logicDeleteColumn}` = ${ld.logicDeleteValue} %s";

}
