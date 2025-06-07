package pro.shushi.pamirs.message.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.message.enmu.SMSChannelEnum;
import pro.shushi.pamirs.message.enmu.SMSTemplateStatusEnum;
import pro.shushi.pamirs.message.enmu.SMSTemplateTypeEnum;
import pro.shushi.pamirs.message.model.SmsTemplate;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.List;

/**
 * @author drome
 * @date 2021/9/15:45 下午
 */
@Service
@Fun(SmsTemplateService.FUN_NAMESPACE)
public class SmsTemplateServiceImpl implements SmsTemplateService {

    @Function
    @Override
    public List<SmsTemplate> queryListByTemplateType(SMSTemplateTypeEnum type) {
        return new SmsTemplate().setTemplateType(type).queryList();
    }

    @Function
    @Override
    public Integer modifyStatus(String templateCode, SMSChannelEnum channel, SMSTemplateStatusEnum status) {
        if (StringUtils.isBlank(templateCode) || channel == null || status == null) {
            return null;
        }
        SmsTemplate smsTemplate = new SmsTemplate().setTemplateCode(templateCode).setChannel(channel).queryOne();
        if (smsTemplate == null) {
            return null;
        }
        SmsTemplate update = new SmsTemplate();
        update.setId(smsTemplate.getId());
        update.setStatus(status);
        return update.updateById();
    }

    @Function
    @Override
    public Integer modifyStatusWithReason(String templateCode, SMSChannelEnum channel, SMSTemplateStatusEnum status, String reason) {
        if (StringUtils.isBlank(templateCode) || channel == null || status == null) {
            return null;
        }
        SmsTemplate smsTemplate = new SmsTemplate().setTemplateCode(templateCode).setChannel(channel).queryOne();
        if (smsTemplate == null) {
            return null;
        }
        SmsTemplate update = new SmsTemplate();
        update.setId(smsTemplate.getId());
        update.setStatus(status);
        update.setReason(reason);
        return update.updateById();
    }
}
