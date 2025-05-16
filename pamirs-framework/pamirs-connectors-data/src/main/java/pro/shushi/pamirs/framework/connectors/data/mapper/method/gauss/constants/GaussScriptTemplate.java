package pro.shushi.pamirs.framework.connectors.data.mapper.method.gauss.constants;

import com.baomidou.mybatisplus.core.toolkit.Constants;
import pro.shushi.pamirs.framework.connectors.data.mapper.template.ScriptTemplate;
import pro.shushi.pamirs.framework.connectors.data.plugin.util.FieldUtil;

/**
 * GaussDB 脚本模版
 *
 * @author paidaxing
 * @version 1.0.0
 * @date 2024/03/14 14:08:55
 */
public interface GaussScriptTemplate extends ScriptTemplate {

    String ON_DUPLICATE_KEY_UPDATE = "ON DUPLICATE KEY UPDATE %s";

    String LOGIC_DELETE = "\"${ld.logicDeleteColumn}\" = ${ld.logicNotDeleteValue}";

    String OPTIMISTIC_LOCKER_COLUMN = "\"${" + Constants.MP_OPTLOCK_VERSION_COLUMN + "}\"";

    String OPTIMISTIC_LOCKER_VALUE = "${" + FieldUtil.NAME + ".get(%s ,\"" + Constants.MP_OPTLOCK_VERSION_ORIGINAL + "\")}";

    String OPTIMISTIC_LOCKER = OPTIMISTIC_LOCKER_COLUMN + " = " + OPTIMISTIC_LOCKER_VALUE;

    String LOGIC_DELETE_SQL = "UPDATE %s SET \"${ld.logicDeleteColumn}\" = ${ld.logicDeleteValue} %s";

}
