package pro.shushi.pamirs.eip.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Deprecated
@Base
@Dict(dictionary = "pamirs.eip.EipOpenConverterTypeEnum", displayName = "开放接口处理类型")
public enum EipOpenConverterTypeEnum implements IEnum<String> {

    EIP_FUNCTION("EIP_FUNCTION", "Eip函数-无返回", "调用指定eip参数的函数,不处理函数返回结果"),
    EIP_FUNCTION_WITH_RESULT("EIP_FUNCTION_WITH_RESULT", "Eip函数-有返回", "调用指定eip参数的函数,返回结果为OpenEipResult,自动放入finalResultKey"),
    MODEL_FUNCTION("MODEL_FUNCTION", "模型函数", "调用模型函数,入参必须为单参模型,请求入参转换为入参,返回结果自动包装成开放接口统一格式,放入finalResultKey"),
    FUNCTION("FUNCTION", "业务函数", "调用任意函数,根据函数入参从请求上下文中获取参数,返回结果自动包装成开放接口统一格式,放入finalResultKey"),
    ;
    private String value;

    private String displayName;

    private String help;

    EipOpenConverterTypeEnum(String value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

    public String getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getHelp() {
        return help;
    }
}
