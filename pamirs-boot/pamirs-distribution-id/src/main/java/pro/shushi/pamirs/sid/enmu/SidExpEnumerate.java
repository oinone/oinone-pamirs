package pro.shushi.pamirs.sid.enmu;

import pro.shushi.pamirs.meta.annotation.Errors;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * ExpEnumSid
 *
 * @author yakir on 2019/08/01 10:03.
 */
@Base
@Errors(displayName = "分布式ID错误枚举")
public enum SidExpEnumerate implements ExpBaseEnum, IEnum<Integer> {

    SYSTEM_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10022000, "错误: 系统异常"), BASE_GENERATE_UNIQUE_ID_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10022001, "生成唯一ID失败"), BASE_GENERATE_UNIQUE_ID_ADD_WORKNODE_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10022002, "ID生成服务添加工作节点失败"), BASE_GENERATE_MODEL_SEQUENCE_CONFIG_NOT_EXIST_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10022003, "模型编码生成规则不存在"), BASE_GENERATE_FIELD_SEQUENCE_CONFIG_NOT_EXIST_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10022004, "字段编码生成规则不存在");

    private ERROR_TYPE type;

    private int code;

    private String msg;

    SidExpEnumerate(ERROR_TYPE type, int code, String msg) {
        this.type = type;
        this.code = code;
        this.msg = msg;
    }
}
