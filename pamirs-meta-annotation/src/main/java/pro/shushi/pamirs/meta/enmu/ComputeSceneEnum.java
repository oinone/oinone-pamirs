package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = "base.ComputeScene", displayName = "计算场景")
public enum ComputeSceneEnum implements IEnum<String> {

    VALIDATE("validate", "校验型", "校验型计算，返回布尔型的结果"),
    COMPUTE("compute", "计算型", "取值与逻辑计算"),
    RSQL("rsql", "查询条件型", "组装查询数据的条件");

    private final String value;
    private final String displayName;
    private final String help;

    ComputeSceneEnum(String value, String displayName, String help) {
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

}