package pro.shushi.pamirs.core.common.query;

import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.boot.base.model.ClientAction;
import pro.shushi.pamirs.boot.base.model.ServerAction;
import pro.shushi.pamirs.boot.base.model.UrlAction;
import pro.shushi.pamirs.boot.base.model.ViewAction;

/**
 * 查询动作帮助类
 *
 * @author Adamancy Zhang at 12:21 on 2024-01-16
 */
public class QueryActionHelper {

    private QueryActionHelper() {
        //reject create object
    }

    public static String getActionModel(ActionTypeEnum actionType) {
        switch (actionType) {
            case SERVER:
                return ServerAction.MODEL_MODEL;
            case VIEW:
                return ViewAction.MODEL_MODEL;
            case URL:
                return UrlAction.MODEL_MODEL;
            case CLIENT:
                return ClientAction.MODEL_MODEL;
            default:
                throw new IllegalArgumentException("Invalid action type.");
        }
    }
}
