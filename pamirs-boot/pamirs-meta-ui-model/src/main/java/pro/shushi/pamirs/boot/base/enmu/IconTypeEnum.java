package pro.shushi.pamirs.boot.base.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = "base.IconType", displayName = "icon类型")
public enum IconTypeEnum implements IEnum<String> {

    ICONFONT("iconfont", "图标字体", "图标字体"),
    SVG("svg", "SVG格式", "SVG格式"),
    IMAGE("image", "普通图片格式", "普通图片格式");

    private final String help;
    private final String value;
    private final String displayName;

    IconTypeEnum(String value, String displayName, String help) {
        this.help = help;
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

    @Override
    public String help() {
        return help;
    }

}
