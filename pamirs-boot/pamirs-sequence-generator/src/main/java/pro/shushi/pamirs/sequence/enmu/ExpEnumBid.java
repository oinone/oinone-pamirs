package pro.shushi.pamirs.sequence.enmu;

import pro.shushi.pamirs.meta.annotation.Errors;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * ExpEnumBid
 *
 * @author yakir on 2019/08/01 10:03.
 */
@Errors(displayName = "序列模块错误枚举", summary = "序列模块错误枚举")
public enum ExpEnumBid implements ExpBaseEnum, IEnum<Integer> {

    ID_GEN_ERROR(ERROR_TYPE.BIZ_ERROR, 10047000, "错误: ID生成失败"),
    ID_GEN_NOT_EXIST_LEAFALLOC_CONFIG_ERROR(ERROR_TYPE.BIZ_ERROR, 10047001, "错误: 使用ID生成器之前请先配置序列(LeafAlloc)规则"),
    ID_GEN_NOT_EXIST_SEQUENCE_CONFIG_CONFIG_ERROR(ERROR_TYPE.BIZ_ERROR, 10047002, "错误: 使用ID生成器之前请先配置序列生成配置(SequenceConfig)"),
    // -1
    ID_GEN_EXCEPTION_ID_IDCACHE_INIT_FALSE(ERROR_TYPE.SYSTEM_ERROR, 10047003, "错误: IDCache未初始化成功"),
    // -2
    ID_GEN_EXCEPTION_ID_KEY_NOT_EXISTS(ERROR_TYPE.SYSTEM_ERROR, 10047004, "错误: key不存在"),
    // -3
    ID_GEN_EXCEPTION_ID_TWO_SEGMENTS_ARE_NULL(ERROR_TYPE.SYSTEM_ERROR, 10047005, "错误: SegmentBuffer中的两个Segment均未从DB中装载时"),
    ID_GEN_LEAF_NOT_INIT_OK_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10047006, "错误: ID生成器未初始化完成"),
    SEQUENCE_TYPE_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10047007, "错误: 序列类型声明错误，请使用 SequenceNameConstants 常量使用序列。"),
    SYSTEM_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10047007, "错误: 系统异常");

    private ERROR_TYPE type;

    private int code;

    private String msg;

    ExpEnumBid(ERROR_TYPE type, int code, String msg) {
        this.type = type;
        this.code = code;
        this.msg = msg;
    }
}
