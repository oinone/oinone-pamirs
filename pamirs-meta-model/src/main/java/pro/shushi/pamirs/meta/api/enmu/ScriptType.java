package pro.shushi.pamirs.meta.api.enmu;

/**
 * 函数运行时语言类型枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/14 10:45 下午
 */
public enum ScriptType {

    SPRING("SPRING"/* spring bean or dubbo bean */),
    LOCAL("LOCAL"/* local bean or non bean object */),
    DSL("DSL"/* USER DEFINE CODE */),
    REMOTE("REMOTE"/* rpc */),
    EL("EL"/* expression language */),
    GROOVY("GROOVY"/* groovy */),
    JS("JS"/* javascript */),
    SCRIPT("SCRIPT"/* script */);

    private String type;

    public String getType() {
        return type;
    }

    ScriptType(String value) {
        this.type = value;
    }

}
