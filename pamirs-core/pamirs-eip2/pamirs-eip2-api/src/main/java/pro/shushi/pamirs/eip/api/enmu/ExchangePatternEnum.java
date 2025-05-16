package pro.shushi.pamirs.eip.api.enmu;

import org.apache.camel.ExchangePattern;
import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = ExchangePatternEnum.dictionary, displayName = "交换模式", summary = "Camel支持的全部交换模式")
public enum ExchangePatternEnum implements IEnum<String> {

    InOnly("InOnly", "事件", "事件", ExchangePattern.InOnly),
    InOut("InOut", "消息交换", "消息交换", ExchangePattern.InOut),
    InOptionalOut("InOptionalOut", "消息选择交换", "消息选择交换", ExchangePattern.InOptionalOut);

    public static final String dictionary = "pamirs.eip.ExchangePatternEnum";

    private String value;

    private String displayName;

    private String help;

    private ExchangePattern exchangePattern;

    ExchangePatternEnum(String value, String displayName, String help, ExchangePattern exchangePattern) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
        this.exchangePattern = exchangePattern;
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

    public ExchangePattern getExchangePattern() {
        return exchangePattern;
    }
}
