package pro.shushi.pamirs.message.engine.email;

import pro.shushi.pamirs.message.engine.IMessageEngine;
import pro.shushi.pamirs.message.enmu.MessageExpEnumerate;
import pro.shushi.pamirs.message.model.EmailSenderSource;
import pro.shushi.pamirs.message.model.MessageSource;
import pro.shushi.pamirs.message.utils.MessageEmailUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

@Slf4j
public class EmailSendEngine implements IMessageEngine<EmailSender> {

    @Override
    public EmailSender get(MessageSource messageSource) {
        EmailSenderSource emailSenderSource = null;
        if (messageSource != null) {
            emailSenderSource = (EmailSenderSource) messageSource;
        } else {
            try {
                //按照邮件服务器创建时间获取第一个
                emailSenderSource = MessageEmailUtils.fetchEmailSendConfig();
                if (emailSenderSource == null) {
                    throw PamirsException.construct(MessageExpEnumerate.MAIL_SENDER_CONFIG_IS_NULL).errThrow();
                }
            } catch (Exception e) {
                throw PamirsException.construct(MessageExpEnumerate.MAIL_SQL_ERROR, e).errThrow();
            }
        }
        return new DefaultEmailSender(emailSenderSource);
    }
}
