package pro.shushi.pamirs.message.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = "mail.enmu.ThirdSendMessageType", displayName = "")
public enum  ThirdSendMessageType implements IEnum<String> {
    TEXT( "text", "文本","文本"),
    IMG( "IMG", "图片","图片"),
    VOICE( "VOICE", "语音","语音"),
    VIDEO( "VIDEO", "视频","视频"),
    FILE( "FILE", "文件","文件"),
    TEXTCARD( "textcard", "文本卡片","文本卡片"),
    NEWS( "news", "图文消息","图文消息"),
    MD( "MD", "markdown消息","markdown消息")
            ;

    private String help;

    private String value;

    private String displayName;

    ThirdSendMessageType(String value, String displayName,String help) {
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
