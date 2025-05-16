package pro.shushi.pamirs.message.service;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.message.enmu.SMSChannelEnum;
import pro.shushi.pamirs.message.model.SmsChannelConfig;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.List;

@Service
@Fun(SmsChannelConfigService.FUN_NAMESPACE)
public class SmsChannelConfigServiceImpl implements SmsChannelConfigService {

    @Function
    @Override
    public List<SmsChannelConfig> queryListByChannel(SMSChannelEnum channel) {
        return new SmsChannelConfig().setChannel(channel).queryList();
    }
}
