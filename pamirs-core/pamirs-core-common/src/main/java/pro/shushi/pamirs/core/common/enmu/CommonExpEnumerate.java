package pro.shushi.pamirs.core.common.enmu;

import pro.shushi.pamirs.meta.annotation.Errors;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

@Errors(displayName = "公共包错误枚举")
public enum CommonExpEnumerate implements ExpBaseEnum {

    //region DataStatusError 20010001-20010004
    DATA_STATUS_DRAFT_TO_ENABLED_ERROR(ERROR_TYPE.BIZ_ERROR, 10057000, "草稿状态无法启用"),
    DATA_STATUS_DRAFT_TO_DISABLED_ERROR(ERROR_TYPE.BIZ_ERROR, 10057001, "草稿状态无法禁用"),
    DATA_STATUS_ENABLED_TO_ENABLED_ERROR(ERROR_TYPE.BIZ_ERROR, 10057002, "已启用，无法重复操作"),
    DATA_STATUS_DISABLED_TO_DISABLED_ERROR(ERROR_TYPE.BIZ_ERROR, 10057003, "已禁用，无法重复操作"),
    //endregion
    SELECT_NULL(ERROR_TYPE.BIZ_ERROR, 10057004, "未选择数据行"),
    PLEASE_REFRESH_PAGE(ERROR_TYPE.BIZ_ERROR, 10057005, "请刷新页面后重试"),
    PK_UNIQUE_HAS_NULL(ERROR_TYPE.BIZ_ERROR, 10057006, "主键和唯一键的值无法满足更新要求，请至少传入一组主键或唯一键进行更新操作"),
    DELETE_PK_UNIQUE_HAS_NULL(ERROR_TYPE.BIZ_ERROR, 10057007, "主键和唯一键的值无法满足删除要求，请至少传入一组主键或唯一键进行删除操作"),
    UNSUPPORTED_OPERATION_ERROR(ERROR_TYPE.BIZ_ERROR, 10057008, "不被允许的操作"),
    SYSTEM_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10057009, "未处理的系统异常");

    private final ERROR_TYPE type;

    private final int code;

    private final String msg;

    CommonExpEnumerate(ERROR_TYPE type, int code, String msg) {
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
