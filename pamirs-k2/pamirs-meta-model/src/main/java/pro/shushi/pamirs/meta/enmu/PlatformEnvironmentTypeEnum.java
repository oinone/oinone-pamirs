package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * Pamirs平台环境变更日志记录类型
 *
 * @author Gesi at 9:30 on 2024/11/22
 */
@Dict(dictionary = PlatformEnvironmentTypeEnum.DICTIONARY, displayName = "平台环境变更日志记录类型", summary = "平台环境变更日志记录类型")
public enum PlatformEnvironmentTypeEnum implements IEnum<String> {

    CREATE("CREATE", "创建"),
    UPDATE("UPDATE", "修改"),
    DELETE("DELETE", "删除"),
    ;

    public static final String DICTIONARY = "base.PlatformEnvironmentTypeEnum";

    private final String value;
    private final String displayName;

    PlatformEnvironmentTypeEnum(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public String displayName() {
        return displayName;
    }

}

