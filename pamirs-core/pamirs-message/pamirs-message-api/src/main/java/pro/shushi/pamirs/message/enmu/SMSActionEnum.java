package pro.shushi.pamirs.message.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * SMSActionEnum 发送形式
 *
 * @author yakir on 2019/08/23 14:05.
 */
@Dict(dictionary = "mail.enmu.SMSActionEnum", displayName = "")
public enum SMSActionEnum implements IEnum<String> {


    SEND_SMS( "SendSms", "单条短信","单条短信"),
    SEND_BATCH_SMS( "SendBatchSms", "批量短信","批量短信"),

    //短信模板
    QUERY_SMS_TEMPLATE( "QuerySmsTemplate", "查询短信模板","查询短信模板"),
    ADD_SMS_TEMPLATE( "AddSmsTemplate", "新增短信模板","新增短信模板"),
    MODIFY_SMS_TEMPLATE( "ModifySmsTemplate", "更新短信模板","更新短信模板"),
    ;

    private String help;
    private String value;
    private String displayName;

    SMSActionEnum(String value, String displayName,String help) {
        this.help         = help;
        this.value       = value;
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
