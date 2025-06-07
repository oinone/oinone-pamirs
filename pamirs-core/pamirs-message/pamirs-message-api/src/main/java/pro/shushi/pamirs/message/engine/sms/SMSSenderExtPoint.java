package pro.shushi.pamirs.message.engine.sms;

import pro.shushi.pamirs.message.model.SmsChannelConfig;
import pro.shushi.pamirs.message.model.SmsTemplate;
import pro.shushi.pamirs.message.tmodel.SmsTemplateAuditResponse;
import pro.shushi.pamirs.message.tmodel.SmsTemplateResponse;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;

import java.util.Map;

/**
 * @author Adamancy Zhang
 * @date 2020-11-21 16:21
 */
@Ext(SMSSender.class)
public interface SMSSenderExtPoint {

    // FIXME: zbh 20220331 @drome
    @ExtPoint(displayName = "自定义短信发送扩展点")
    Boolean send(SmsTemplate smsTemplate, SmsChannelConfig smsChannel, String phoneNum, Map<String, String> placeholders);

    @ExtPoint(displayName = "自定义短信发送扩展点", summary = "短信发送失败会抛出平台的异常提示信息")
    default Boolean sendOrThrow(SmsTemplate smsTemplate, SmsChannelConfig smsChannel, String phoneNum, Map<String, String> placeholders) {
        return send(smsTemplate, smsChannel, phoneNum, placeholders);
    }

    // FIXME: zbh 20220331 @drome
    @ExtPoint(displayName = "自定义短信模板申请扩展点")
    SmsTemplateResponse addTemplate(SmsTemplate smsTemplate, SmsChannelConfig smsChannel);

    @ExtPoint(displayName = "自定义短信模板查询审核状态扩展点")
    SmsTemplateAuditResponse queryTemplate(String templateCode, SmsChannelConfig smsChannel);
}
