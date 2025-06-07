//package pro.shushi.pamirs.mail.service;
//
//
//import org.springframework.stereotype.Service;
//import pro.shushi.pamirs.mail.api.MessageService;
//import pro.shushi.pamirs.mail.engine.MailEngine;
//import pro.shushi.pamirs.mail.engine.mail.MailSender;
//import pro.shushi.pamirs.mail.engine.third.ThirdSender;
//import pro.shushi.pamirs.mail.enmu.MailEngineTypeEnum;
//import pro.shushi.pamirs.mail.model.MailSource;
//
//@Service("MessageServie")
//public class MessageServieImpl<T> implements MessageService<T> {
//
//
//    @Override
//    public void sendMailMessage(String msg,  MailSource mailSource) {
//        MailSender mailSender = MailEngine.<MailSender>get(MailEngineTypeEnum.MAIL_SEND).get(mailSource);
//        mailSender.sendModelMail()
//    }
//}
