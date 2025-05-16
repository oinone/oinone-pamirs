package pro.shushi.pamirs.message.service;

import pro.shushi.pamirs.message.enmu.SMSTemplateTypeEnum;
import pro.shushi.pamirs.message.model.EmailVerifyTemplate;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.List;

@Fun(EmailVerifyTemplateService.FUN_NAMESPACE)
public interface EmailVerifyTemplateService {
    String FUN_NAMESPACE = "message.EmailVerifyTemplateService";

    @Function
    void createOrUpdateBatch(List<EmailVerifyTemplate> data);

    @Function
    List<EmailVerifyTemplate> queryListByTemplateType(SMSTemplateTypeEnum type);
}
