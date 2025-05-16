package pro.shushi.pamirs.message.service;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.message.enmu.SMSTemplateTypeEnum;
import pro.shushi.pamirs.message.model.EmailVerifyTemplate;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.List;

@Service
@Fun(EmailVerifyTemplateService.FUN_NAMESPACE)
public class EmailVerifyTemplateServiceImpl implements EmailVerifyTemplateService {

    @Function
    @Override
    public void createOrUpdateBatch(List<EmailVerifyTemplate> data) {
        new EmailVerifyTemplate().createOrUpdateBatch(data);
    }

    @Function
    @Override
    public List<EmailVerifyTemplate> queryListByTemplateType(SMSTemplateTypeEnum type) {
        return new EmailVerifyTemplate().setTemplateType(type).queryList();
    }
}
