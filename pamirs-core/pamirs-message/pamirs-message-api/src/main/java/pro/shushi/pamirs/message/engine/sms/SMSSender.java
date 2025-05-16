package pro.shushi.pamirs.message.engine.sms;

import pro.shushi.pamirs.message.enmu.SMSChannelEnum;
import pro.shushi.pamirs.message.enmu.SMSTemplateTypeEnum;
import pro.shushi.pamirs.message.model.SmsTemplate;
import pro.shushi.pamirs.message.tmodel.SmsTemplateAuditResponse;
import pro.shushi.pamirs.message.tmodel.SmsTemplateResponse;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.Map;

/**
 * SMSSender
 *
 * @author yakir on 2019/08/23 16:49.
 */
@SPI
public interface SMSSender {


    Boolean smsSend(SMSTemplateTypeEnum smsTemplateType, String phoneNum, Map<String, String> placeholders);

    default Boolean smsSendOrThrow(SMSTemplateTypeEnum smsTemplateType, String phoneNum, Map<String, String> placeholders) throws PamirsException {
        return smsSend(smsTemplateType, phoneNum, placeholders);
    }

    /**
     * 原本的考虑应该是枚举和短信模板一一对应? 现在工作流支持短信模板申请和阿里云打通,都是通知类短信,所以需要直接指定模板
     *
     * @param smsTemplate
     * @param phoneNum
     * @param placeholders
     * @return
     */
    Boolean smsSend(SmsTemplate smsTemplate, String phoneNum, Map<String, String> placeholders);

    /**
     * 调用第三方平台发送短信失败，抛出返回的异常信息
     */
    default Boolean smsSendOrThrow(SmsTemplate smsTemplate, String phoneNum, Map<String, String> placeholders) throws PamirsException {
        return smsSend(smsTemplate, phoneNum, placeholders);
    }


    /**
     * 向第三方平台申请短信模板.
     * todo 应该写到third-part里去,但是短信集成配置在message,就先写这里了
     *
     * @param smsTemplate
     * @return
     */
    SmsTemplateResponse addSmsTemplate(SmsTemplate smsTemplate);

    /**
     * 本接口的单用户QPS限制为5000次/秒。超过限制，API调用会被限流
     *
     * @param templateCode
     * @param channel
     * @return
     */
    SmsTemplateAuditResponse querySmsTemplate(String templateCode, SMSChannelEnum channel);
}
