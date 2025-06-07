package pro.shushi.pamirs.message.engine.email;

import pro.shushi.pamirs.message.model.EmailTemplate;
import pro.shushi.pamirs.message.tmodel.EmailPoster;

import java.util.Map;

public interface EmailSender {

    /**
     * 根据邮件模板发送邮件
     *
     * @param sendTo
     * @param copyTo
     * @param emailTemplate
     * @param objectData
     * @return
     * @throws Exception
     */
    Boolean send(EmailTemplate emailTemplate, Map<String, Object> objectData, String sendTo, String copyTo) throws Exception;

    /**
     * 直接发送邮件
     *
     * @param mailPoster
     * @return
     */
    Boolean send(EmailPoster mailPoster);

    Boolean sendVerify(String smsTemplateType, String mailAddr);

}
