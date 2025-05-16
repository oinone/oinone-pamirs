package pro.shushi.pamirs.file.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = TaskMessageLevelEnum.dictionary, displayName = "任务信息级别")
public enum TaskMessageLevelEnum implements IEnum<String> {

    TIP("TIP", "提示", "提示"),//控制台输出，不存储
    INFO("INFO", "信息", "信息"),//加入到信息列表
    WARNING("WARNING", "警告", "警告"),//不回滚
    ERROR("ERROR", "异常", "异常");//回滚

    public static final String dictionary = "file.TaskMessageLevelEnum";

    private String value;
    private String displayName;
    private String help;

    TaskMessageLevelEnum(String value, String displayName, String help) {
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
