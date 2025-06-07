package pro.shushi.pamirs.resource.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = "currencyroundingenum", displayName = "")
public enum CurrencyRoundingEnum implements IEnum<String> {

    ROUND_UP("ROUND_UP", "直接向上", "直接向上"),
    ROUND_DOWN("ROUND_DOWN", "直接向下", "直接向下"),
    ROUND_CEILING("ROUND_CEILING", "正数进位向上，负数舍位向上", "正数进位向上，负数舍位向上"),
    ROUND_FLOOR("ROUND_FLOOR", "正数舍位向下，负数进位向下", "正数舍位向下，负数进位向下"),
    ROUND_HALF_UP("ROUND_HALF_UP", "四舍五入，若舍弃部分>=.5，就进位", "四舍五入，若舍弃部分>=.5，就进位"),
    ROUND_HALF_DOWN("ROUND_HALF_DOWN", "四舍五入 若舍弃部分>.5", "四舍五入 若舍弃部分>.5,就进位"),
    ROUND_HALF_EVEN("ROUND_HALF_EVEN", "如果舍弃部分左边的数字为偶数，则作ROUND_HALF_DOWN，如果舍弃部分左边的数字为奇数，则作 ROUND_HALF_UP", "如果舍弃部分左边的数字为偶数，则作ROUND_HALF_DOWN，如果舍弃部分左边的数字为奇数，则作 ROUND_HALF_UP"),
    ROUND_UNNECESSARY("ROUND_UNNECESSARY", "断言请求的操作具有精确的结果，因此不需要舍入", "断言请求的操作具有精确的结果，因此不需要舍入");

    private String help;

    private String value;

    private String displayName;

    CurrencyRoundingEnum(String value, String displayName, String help) {
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
