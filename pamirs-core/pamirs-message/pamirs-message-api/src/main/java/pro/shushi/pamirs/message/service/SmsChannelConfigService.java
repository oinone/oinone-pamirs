package pro.shushi.pamirs.message.service;

import pro.shushi.pamirs.message.enmu.SMSChannelEnum;
import pro.shushi.pamirs.message.model.SmsChannelConfig;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.List;

@Fun(SmsChannelConfigService.FUN_NAMESPACE)
public interface SmsChannelConfigService {

    String FUN_NAMESPACE = "message.SmsChannelConfigService";

    @Function
    List<SmsChannelConfig> queryListByChannel(SMSChannelEnum channel);
}
