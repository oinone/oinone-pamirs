package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 系统来源枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.SystemSource", displayName = "系统来源")
public enum SystemSourceEnum implements IEnum<String> {

    KERNEL("KERNEL", "内核", "内核"),
    BASE("BASE", "系统原生", "系统原生"),
    ABSTRACT_INHERITED("ABSTRACT_INHERITED", "抽象继承", "抽象继承"),
    TRANSIENT_INHERITED("TRANSIENT_INHERITED", "临时继承", "临时继承"),
    EXTEND_INHERITED("EXTEND_INHERITED", "同表继承", "同表继承"),
    MULTI_TABLE_INHERITED("MULTI_TABLE_INHERITED", "多表继承", "多表继承"),
    PROXY_INHERITED("PROXY_INHERITED", "代理继承", "代理继承"),
    RELATION("RELATION", "关联关系生成", "关联关系生成"),
    SYSTEM("SYSTEM", "系统生成", "系统生成"),
    MANUAL("MANUAL", "手工新增", "手工新增"),
    UI("UI", "界面新增", "界面新增"),
    UPSTREAM("UPSTREAM", "上游继承", "上游继承");

    private final String value;

    private final String displayName;

    private final String help;

    SystemSourceEnum(String value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public String displayName() {
        return displayName;
    }

    @Override
    public String help() {
        return help;
    }

    public static boolean isInherited(SystemSourceEnum systemSourceEnum) {
        return SystemSourceEnum.ABSTRACT_INHERITED.equals(systemSourceEnum)
                || SystemSourceEnum.TRANSIENT_INHERITED.equals(systemSourceEnum)
                || SystemSourceEnum.EXTEND_INHERITED.equals(systemSourceEnum)
                || SystemSourceEnum.MULTI_TABLE_INHERITED.equals(systemSourceEnum)
                || SystemSourceEnum.PROXY_INHERITED.equals(systemSourceEnum);
    }

    public static boolean isGenerated(SystemSourceEnum systemSourceEnum) {
        return SystemSourceEnum.KERNEL.equals(systemSourceEnum)
                || SystemSourceEnum.BASE.equals(systemSourceEnum)
                || SystemSourceEnum.MANUAL.equals(systemSourceEnum);
    }

    public static boolean isBase(SystemSourceEnum systemSourceEnum) {
        return SystemSourceEnum.KERNEL.equals(systemSourceEnum)
                || SystemSourceEnum.BASE.equals(systemSourceEnum);
    }

}
