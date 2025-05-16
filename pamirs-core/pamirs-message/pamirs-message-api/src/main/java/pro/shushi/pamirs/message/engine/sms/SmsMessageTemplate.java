package pro.shushi.pamirs.message.engine.sms;

import pro.shushi.pamirs.message.enmu.SMSChannelEnum;
import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.Map;

@Data
public class SmsMessageTemplate {
    SMSChannelEnum sendType; // 发送类型
    //    String templateName;
    String phoneNum; // 电话号码
    Map<String, String> placeholders; // 外部模版占位符
    String templateId; // 外部模版id

}
