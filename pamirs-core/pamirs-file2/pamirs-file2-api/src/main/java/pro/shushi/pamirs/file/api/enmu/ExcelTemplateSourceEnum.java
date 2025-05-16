package pro.shushi.pamirs.file.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * @author Adamancy Zhang on 2021-04-15 11:24
 */
@Base
@Dict(dictionary = ExcelTemplateSourceEnum.DICTIONARY, displayName = "模板来源枚举", summary = "模板来源枚举")
public enum ExcelTemplateSourceEnum implements IEnum<String> {

    SYSTEM("SYSTEM", "系统生成", "当前模型不存在模板时通过系统自动生成，在创建新模板后将自动删除，进行编辑操作将保留该模板"),
    INITIALIZATION("INITIALIZATION", "初始化生成", "该模板不允许被编辑或删除"),
    CUSTOM("CUSTOM", "自定义", "由用户创建/编辑的模板"),
    ;

    public static final String DICTIONARY = "resource.ExcelTemplateSourceEnum";

    private final String value;
    private final String displayName;
    private final String help;

    ExcelTemplateSourceEnum(String value, String displayName, String help) {
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
