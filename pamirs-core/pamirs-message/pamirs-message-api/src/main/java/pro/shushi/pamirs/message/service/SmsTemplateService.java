package pro.shushi.pamirs.message.service;

import pro.shushi.pamirs.message.enmu.SMSChannelEnum;
import pro.shushi.pamirs.message.enmu.SMSTemplateStatusEnum;
import pro.shushi.pamirs.message.enmu.SMSTemplateTypeEnum;
import pro.shushi.pamirs.message.model.SmsTemplate;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.List;

/**
 * @author drome
 * @date 2021/9/15:40 下午
 */
@Fun(SmsTemplateService.FUN_NAMESPACE)
public interface SmsTemplateService {

    String FUN_NAMESPACE = "message.SmsTemplateService";

    @Function
    List<SmsTemplate> queryListByTemplateType(SMSTemplateTypeEnum type);

    @Function
    Integer modifyStatus(String templateCode, SMSChannelEnum channel, SMSTemplateStatusEnum status);

    @Function
    Integer modifyStatusWithReason(String templateCode, SMSChannelEnum channel, SMSTemplateStatusEnum status, String reason);


}
