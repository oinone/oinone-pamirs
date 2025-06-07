package pro.shushi.pamirs.expression.enmu;

import pro.shushi.pamirs.meta.annotation.Errors;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

/**
 * ModelDesignerExp
 *
 * @author yakir on 2021/08/05 11:51.
 */
@Base
@Errors(displayName = "表达式错误枚举")
public enum ExpressionExpEnumerate implements ExpBaseEnum {

    EXPRESSION_OBJ_IS_NULL(ERROR_TYPE.BIZ_ERROR, 10044000, "表达式未指定作用对象"), EXPRESSION_BLOCK_FUN_BLANK(ERROR_TYPE.BIZ_ERROR, 10044001, "函数不存在");

    private final ERROR_TYPE type;

    private final int code;

    private final String msg;

    ExpressionExpEnumerate(ERROR_TYPE type, int code, String msg) {
        this.type = type;
        this.code = code;
        this.msg = msg;
    }

    @Override
    public ERROR_TYPE type() {
        return type;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String msg() {
        return msg;
    }
}
