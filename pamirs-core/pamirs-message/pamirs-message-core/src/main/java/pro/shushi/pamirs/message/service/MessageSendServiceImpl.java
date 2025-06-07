package pro.shushi.pamirs.message.service;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.message.engine.MessageEngine;
import pro.shushi.pamirs.message.engine.sms.SMSSender;
import pro.shushi.pamirs.message.enmu.MessageEngineTypeEnum;
import pro.shushi.pamirs.message.enmu.SMSTemplateTypeEnum;
import pro.shushi.pamirs.message.model.MessageSource;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.Map;

/**
 * @author Nation
 * @cdate 2020-12-19 21:56
 */
@Fun(MessageSendService.FUN_NAMESPACE)
@Component
public class MessageSendServiceImpl implements MessageSendService {

    @Override
    @Function
    public Boolean smsSend(MessageEngineTypeEnum messageEngineTypeEnum, MessageSource messageSource, SMSTemplateTypeEnum smsTemplateType, String phoneNum, Map<String, String> placeholders) {
        return MessageEngine.<SMSSender>get(messageEngineTypeEnum).get(messageSource).smsSend(smsTemplateType, phoneNum, placeholders);
    }

}
