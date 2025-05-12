package pro.shushi.pamirs.meta.api.dto.protocol;

/**
 * 环境枚举
 * <p>
 * 2020/7/27 8:47 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public enum EnvWithAbbrEnum {

    product("p"),
    preview("t");

    private String abbr;

    EnvWithAbbrEnum(String abbr) {
        this.abbr = abbr;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

}
