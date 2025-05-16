package pro.shushi.pamirs.message.action;

import pro.shushi.pamirs.message.engine.MessageEngine;
import pro.shushi.pamirs.message.engine.email.EmailSender;
import pro.shushi.pamirs.message.enmu.MessageEngineTypeEnum;
import pro.shushi.pamirs.message.tmodel.EmailPoster;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

@Slf4j
@Model.model(EmailPoster.MODEL_MODEL)
public class EmailPosterAction {

    @Action( displayName = "直接发送一个邮件")
    public EmailPoster sendEmail(EmailPoster mailPoster) {
        EmailSender emailSender = MessageEngine.<EmailSender>get(MessageEngineTypeEnum.EMAIL_SEND).get(null);
        emailSender.send(mailPoster);
        return mailPoster;
    }

}
