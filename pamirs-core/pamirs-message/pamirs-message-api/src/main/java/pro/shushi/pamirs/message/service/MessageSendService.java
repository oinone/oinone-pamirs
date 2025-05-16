package pro.shushi.pamirs.message.service;

import pro.shushi.pamirs.message.enmu.MessageEngineTypeEnum;
import pro.shushi.pamirs.message.enmu.SMSTemplateTypeEnum;
import pro.shushi.pamirs.message.model.MessageSource;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.Map;

@Fun(MessageSendService.FUN_NAMESPACE)
public interface MessageSendService {

    String FUN_NAMESPACE = "message.MessageSendService";

    /**
     *
     * @param messageEngineTypeEnum
     * @param messageSource
     * @param smsTemplateType
     * @param phoneNum
     * @param placeholders
     * @return
     */
    @Function
    Boolean smsSend(MessageEngineTypeEnum messageEngineTypeEnum, MessageSource messageSource,
                    SMSTemplateTypeEnum smsTemplateType, String phoneNum, Map<String, String> placeholders);

}
