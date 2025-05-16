package pro.shushi.pamirs.framework.connectors.cdn.enmu;

import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

/**
 * CDNExpEnum
 *
 * @author yakir on 2019/07/12 15:32.
 */
public enum CDNExpEnum implements ExpBaseEnum {

    SYSTEM_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10059000, "系统异常"),
    FILE_RELATE_FIELD_ERROR(ERROR_TYPE.BIZ_ERROR, 10059001, "无法处理关系字段"),
    FILE_SQL_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10059002, "SQL错误"),
    FILE_SERVER_NOT_FOUND_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10059003, "找不到文件服务器"),
    FILE_EXPORT_NULL_FIELD_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10059004, "导出字段不能为空"),
    FILE_ENCODEDPOLICY_FIELD_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10059005, "OSS加密异常"),
    FILE_NON_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10059006, "没有找到上传的文件,请先上传文件"),
    FILE_EXPORT_DATA_NULL_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10059007, "导出数据为空，请检查查询条件"),
    FILE_EXPORT_TEMPLATE_NULL_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10059008, "未配置导出模板，导出模板为空"),
    FILE_EXPORT_MODEL_NULL_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10059009, "导出模型为空"),
    FILE_CDN_RM_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10059010, "删除文件失败"),
    CDN_TYPE_NOT_MATCH_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10059011, "文件服务器类型未匹配"),
    CDN_UPYUN_SIGN_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10059012, "又拍云上传文件加签失败"),
    CDN_UPYUN_RM_DIR_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10059013, "又拍云删除文件夹失败"),
    CDN_UPYUN_RM_FILE_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10059014, "又拍云删除文件失败"),
    CDN_UPYUN_UPLOAD_FILE_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10059015, "又拍云文件上传失败"),
    CDN_UPYUN_UPLOAD_FILE_FETCH_INFO_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10059016, "又拍云文件上传获取文件信息失败"),
    MINIO_GET_FORM_DATA_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10059017, "获取上传信息失败");

    private ERROR_TYPE type;

    private int code;

    private String msg;

    CDNExpEnum(ERROR_TYPE type, int code, String msg) {
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
