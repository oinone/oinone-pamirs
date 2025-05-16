package pro.shushi.pamirs.message.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = "mail.enmu.MessageTypeEnum", displayName = "")
public enum MessageTypeEnum implements IEnum<String> {

    COMMENT( "comment", "评论","评论"),
    NOTIFICATION( "notification", "通知","通知"),
    BROADCAST( "broadcast", "广播","广播"),
    WORKFLOW( "workflow", "工作流","工作流"),
    COPY( "copy", "抄送","抄送"),
    SHARING( "sharing", "工作流分享","工作流分享");

    private final String help;

    private final String value;

    private final String displayName;

    MessageTypeEnum(String value, String displayName, String help) {
        this.help = help;
        this.value = value;
        this.displayName = displayName;
    }


    public String getHelp() {
        return help;
    }


    public String getValue() {
        return value;
    }


    public String getDisplayName() {
        return displayName;
    }
}
