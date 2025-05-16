package pro.shushi.pamirs.message.engine.sms;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.message.enmu.MessageEngineTypeEnum;
import pro.shushi.pamirs.message.enmu.MessageExpEnumerate;
import pro.shushi.pamirs.message.enmu.SMSChannelEnum;
import pro.shushi.pamirs.message.enmu.SMSTemplateTypeEnum;
import pro.shushi.pamirs.message.model.SmsChannelConfig;
import pro.shushi.pamirs.message.model.SmsTemplate;
import pro.shushi.pamirs.message.tmodel.SmsTemplateAuditResponse;
import pro.shushi.pamirs.message.tmodel.SmsTemplateResponse;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * SMSSender
 *
 * @author yakir on 2019/08/22 15:44.
 */
@Slf4j
@SPI.Service
@Order(Ordered.LOWEST_PRECEDENCE)
public class DefaultSMSSender implements SMSSender {

    private static final String DFP = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String SIGNATURE = "Signature";
    private static final String CODE = "Code";
    private static final String MESSAGE = "Message";
    private static final String BIZID = "BizId";
    private static final String OK = "OK";

    @Override
    public Boolean smsSend(SMSTemplateTypeEnum smsTemplateType, String phoneNum, Map<String, String> placeholders) {

//        SMSTemplateTypeEnum tmpType      = SMSTemplateTypeEnum.valueOf(smsTemplateType);
        List<SmsTemplate> list = new SmsTemplate()
//                .setChannel(SMSChannelEnum.ALIYUN) 去除阿里云
                .setTemplateType(smsTemplateType)
                .queryList();
        SmsTemplate smsTemplate = Optional.ofNullable(list)
                .filter(_content -> _content.size() > 0)
                .map(_content -> _content.get(0))
                .orElseThrow(() -> PamirsException.construct(MessageExpEnumerate.MAIL_SMS_TEMPLATE_ERROR).errThrow());

        return smsSend0(smsTemplate, phoneNum, placeholders);
    }

    @Override
    public Boolean smsSendOrThrow(SMSTemplateTypeEnum smsTemplateType, String phoneNum, Map<String, String> placeholders) throws PamirsException {
        List<SmsTemplate> list = new SmsTemplate().setTemplateType(smsTemplateType).queryList();
        SmsTemplate smsTemplate = Optional.ofNullable(list)
                .filter(_content -> !_content.isEmpty())
                .map(_content -> _content.get(0))
                .orElseThrow(() -> PamirsException.construct(MessageExpEnumerate.MAIL_SMS_TEMPLATE_ERROR).errThrow());
        return smsSendOrThrow0(smsTemplate, phoneNum, placeholders);
    }

    @Override
    public Boolean smsSend(SmsTemplate smsTemplate, String phoneNum, Map<String, String> placeholders) {
        smsTemplate = fetchSmsTemplate(smsTemplate);
        return smsSend0(smsTemplate, phoneNum, placeholders);
    }

    @Override
    public Boolean smsSendOrThrow(SmsTemplate smsTemplate, String phoneNum, Map<String, String> placeholders) throws PamirsException {
        smsTemplate = fetchSmsTemplate(smsTemplate);
        return smsSendOrThrow0(smsTemplate, phoneNum, placeholders);
    }

    @Override
    public SmsTemplateResponse addSmsTemplate(SmsTemplate smsTemplate) {
        SmsChannelConfig smsChannel = getSmsChannelConfig(smsTemplate);

//        Ext.run(SMSSenderExtPoint::addTemplate, smsTemplate);
        SMSSenderExtPoint smsSenderExtPoint = (SMSSenderExtPoint) BeanDefinitionUtils.getBeansOfTypeByOrdered(SMSSenderExtPoint.class).get(0);
        return smsSenderExtPoint.addTemplate(smsTemplate, smsChannel);
//        return true;
    }

    @Override
    public SmsTemplateAuditResponse querySmsTemplate(String templateCode, SMSChannelEnum channel) {
        SmsChannelConfig smsChannel = getSmsChannelConfig(new SmsTemplate().setChannel(channel));

        SMSSenderExtPoint smsSenderExtPoint = (SMSSenderExtPoint) BeanDefinitionUtils.getBeansOfTypeByOrdered(SMSSenderExtPoint.class).get(0);
        return smsSenderExtPoint.queryTemplate(templateCode, smsChannel);
    }

    /**
     * 短信发送失败，抛出返回的异常信息
     */
    private Boolean smsSendOrThrow0(SmsTemplate smsTemplate, String phoneNum, Map<String, String> placeholders) {
        SmsChannelConfig smsChannel = getSmsChannelConfig(smsTemplate);
        SMSSenderExtPoint smsSenderExtPoint = (SMSSenderExtPoint) BeanDefinitionUtils.getBeansOfTypeByOrdered(SMSSenderExtPoint.class).get(0);
        return smsSenderExtPoint.sendOrThrow(smsTemplate, smsChannel, phoneNum, placeholders);
    }

    private Boolean smsSend0(SmsTemplate smsTemplate, String phoneNum, Map<String, String> placeholders) {
        SmsChannelConfig smsChannel = getSmsChannelConfig(smsTemplate);

//        Ext.run(SMSSenderExtPoint::send, smsTemplate, smsChannel, phoneNum, placeholders);
        SMSSenderExtPoint smsSenderExtPoint = (SMSSenderExtPoint) BeanDefinitionUtils.getBeansOfTypeByOrdered(SMSSenderExtPoint.class).get(0);
        return smsSenderExtPoint.send(smsTemplate, smsChannel, phoneNum, placeholders);
//        return true;
    }

    private SmsChannelConfig getSmsChannelConfig(SmsTemplate smsTemplate){
        if (smsTemplate == null) {
            throw PamirsException.construct(MessageExpEnumerate.MAIL_SMS_TEMPLATE_ERROR).errThrow();
        }
        SMSChannelEnum channel = smsTemplate.getChannel();
        List<SmsChannelConfig> channelConfigs = new SmsChannelConfig()
                .setChannel(channel)
                .setType(MessageEngineTypeEnum.SMS_SEND)
                .queryList();
        return Optional.ofNullable(channelConfigs)
                .filter(_content -> _content.size() > 0)
                .map(_content -> _content.get(0))
                .orElseThrow(() -> PamirsException.construct(MessageExpEnumerate.MAIL_SMS_TEMPLATE_CODE_CHANNEL_ERROR).errThrow());
    }

    private SmsTemplate fetchSmsTemplate(SmsTemplate smsTemplate) {
        return new SmsTemplate()
                .setTemplateType(smsTemplate.getTemplateType())
                .setTemplateCode(smsTemplate.getTemplateCode())
                .setId(smsTemplate.getId())
                .queryOne();
    }
}
