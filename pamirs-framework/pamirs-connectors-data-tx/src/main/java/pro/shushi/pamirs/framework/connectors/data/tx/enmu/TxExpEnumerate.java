package pro.shushi.pamirs.framework.connectors.data.tx.enmu;

import pro.shushi.pamirs.meta.annotation.Errors;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

@Errors(displayName = "系统连接器事务错误枚举")
public enum TxExpEnumerate implements ExpBaseEnum {

    SYSTEM_ERROR(ExpBaseEnum.ERROR_TYPE.SYSTEM_ERROR, 1001500, "系统异常"), BASE_TRANSACTION_MANAGER_ERROR(ERROR_TYPE.SYSTEM_ERROR, 1001501, "事务处理异常");

    private ERROR_TYPE type;

    private int code;

    private String msg;

    TxExpEnumerate(ERROR_TYPE type, int code, String msg) {
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
