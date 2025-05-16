package pro.shushi.pamirs.eip.api.enmu.connector;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = TestConnStatus.dictionary, displayName = "测试连接状态", summary = "测试连接状态")
public enum TestConnStatus implements IEnum<String> {

    UN_CONN_TEST("UN_CONN_TEST", "还未进行连接测试","还未进行连接测试"),
    CONN_SUCCESS("CONN_SUCCESS", "连接成功","连接成功"),
    CONN_ERROR("CONN_ERROR", "连接失败","连接失败"),
    ;

    public static final String dictionary = "designer.TestConnStatus";

    private final String value;
    private final String displayName;
    private final String help;

    TestConnStatus(String value, String displayName, String help) {
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
