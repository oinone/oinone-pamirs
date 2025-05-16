package pro.shushi.pamirs.sys.setting.enmu;

import pro.shushi.pamirs.meta.annotation.Errors;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * @author ranjingnian
 */
@Errors(displayName = "首页配置错误枚举")
public enum HomePageExpEnumerate implements ExpBaseEnum, IEnum<Integer> {

    APPS_MANAGEMENT_PARAMS_ILLEGAL_MENU_UNBINDING_VIEW(ERROR_TYPE.BIZ_ERROR, 10024000, "{} 菜单未绑定页面"), EMPTY_MENU_EXCEPTION(ERROR_TYPE.BIZ_ERROR, 10024001, "菜单不能为空"), EMPTY_PAGE_BINDING_EXCEPTION(ERROR_TYPE.BIZ_ERROR, 10024002, "绑定页面不能为空"), EMPTY_APP_BINDING_EXCEPTION(ERROR_TYPE.BIZ_ERROR, 10024003, "绑定应用不能为空"), RULE_NAME_NOT_EMPTY_EXCEPTION(ERROR_TYPE.BIZ_ERROR, 10024004, "规则名称不能为空");

    private final ExpBaseEnum.ERROR_TYPE type;

    private final int code;

    private final String msg;

    private HomePageExpEnumerate(ExpBaseEnum.ERROR_TYPE type, int code, String msg) {
        this.type = type;
        this.code = code;
        this.msg = msg;
    }

    public ExpBaseEnum.ERROR_TYPE type() {
        return this.type;
    }

    public int code() {
        return this.code;
    }

    public String msg() {
        return this.msg;
    }
}
