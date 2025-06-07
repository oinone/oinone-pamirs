package pro.shushi.pamirs.file.api.enmu;

import pro.shushi.pamirs.meta.annotation.Errors;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

@Errors(displayName = "文件模块错误枚举")
public enum FileExpEnumerate implements ExpBaseEnum {

    FILE_SERVER_NOT_FOUND_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10026000, "找不到文件服务器"),
    FILE_NOT_EXIST(ERROR_TYPE.BIZ_ERROR, 10026001, "文件路径不存在"),
    FILE_NOT_SUPPORTED(ERROR_TYPE.SYSTEM_ERROR, 10026002, "暂不支持的文件处理方式，请联系开发人员提供技术支持"),
    FILE_DOWNLOAD_ERROR(ERROR_TYPE.BIZ_ERROR, 10026003, "文件下载失败"),
    FILENAME_IS_NULL_ERROR(ERROR_TYPE.BIZ_ERROR, 10026004, "文件名不允许为空"),
    UPLOAD_COMPLETED_DATA_IS_NULL_ERROR(ERROR_TYPE.BIZ_ERROR, 10026005, "上传完成数据不允许为空"),
    IMPORT_TEMPLATE_NOT_EXIST(ERROR_TYPE.BIZ_ERROR, 10026006, "导入模板不存在"),
    EXPORT_TEMPLATE_NOT_EXIST(ERROR_TYPE.BIZ_ERROR, 10026007, "导出模板不存在"),
    EXPORT_MODEL_NOT_ALLOWED(ERROR_TYPE.BIZ_ERROR, 10026008, "非存储模型不允许使用默认导出，请联系开发人员提供技术支持"),
    EXPORT_METHOD_IS_ERROR(ERROR_TYPE.BIZ_ERROR, 10026009, "导出方式错误"),
    EXPORT_REQUEST_NOT_EXIST(ERROR_TYPE.BIZ_ERROR, 10026010, "导出请求失效，请重新导出"),
    EXPORT_MODEL_IS_NULL(ERROR_TYPE.BIZ_ERROR, 10026011, "导出模型编码不允许为空"),
    EXPORT_MODEL_NOT_EXIST(ERROR_TYPE.BIZ_ERROR, 10026012, "导出模型不存在"),
    EXPORT_FIELD_IS_NOT_SELECTED(ERROR_TYPE.BIZ_ERROR, 10026013, "未选择导出字段"),
    EXPORT_MODEL_FIELD_NOT_EXIST(ERROR_TYPE.BIZ_ERROR, 10026014, "导出模型字段不存在"),
    TEMPLATE_NOT_EXIST(ERROR_TYPE.BIZ_ERROR, 10026015, "未找到模板信息"),
    SYSTEM_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10026016, "系统异常");

    private final ERROR_TYPE type;

    private final int code;

    private final String msg;

    FileExpEnumerate(ERROR_TYPE type, int code, String msg) {
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
