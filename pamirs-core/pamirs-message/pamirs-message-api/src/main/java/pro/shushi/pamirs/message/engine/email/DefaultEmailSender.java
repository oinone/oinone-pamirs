package pro.shushi.pamirs.message.engine.email;

import com.sun.mail.util.MailSSLSocketFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;
import pro.shushi.pamirs.boot.base.resource.PamirsFile;
import pro.shushi.pamirs.message.enmu.EmailSendSecurityEnum;
import pro.shushi.pamirs.message.enmu.MessageEngineTypeEnum;
import pro.shushi.pamirs.message.enmu.MessageExpEnumerate;
import pro.shushi.pamirs.message.enmu.SMSTemplateTypeEnum;
import pro.shushi.pamirs.message.model.*;
import pro.shushi.pamirs.message.tmodel.EmailPoster;
import pro.shushi.pamirs.message.utils.VerificationCodeUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Exp;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

@Slf4j
public class DefaultEmailSender implements EmailSender {


    public static final String PLACEHOLDER_PREFIX = "${";
    public static final String PLACEHOLDER_SUFFIX = "}";

    private EmailSenderSource emailSenderSource;

    public DefaultEmailSender(EmailSenderSource emailSenderSource) {
        this.emailSenderSource = emailSenderSource;
    }

    @Override
    public Boolean send(EmailTemplate emailTemplate, Map<String, Object> objectData, String sendTo, String copyTo) throws Exception {
        EmailUserSign emailUserSign = emailTemplate.getEmailUserSign();
        if (emailUserSign != null && emailUserSign.getId() != null) {
            emailUserSign = emailUserSign.queryById();
        }
        EmailSenderSource emailSenderSource = emailTemplate.getEmailSenderSource();
        if (emailSenderSource != null && emailSenderSource.getId() != null) {
            emailSenderSource = emailSenderSource.queryById();
            this.emailSenderSource = emailSenderSource;
        }
        String emailSignature = Optional.ofNullable(emailUserSign).map(EmailUserSign::getEmailSignature).orElse("");
        String body = Optional.ofNullable((emailTemplate.getBody())).orElse("");
        EmailPoster emailPoster = new EmailPoster()
                .setTitle(format(objectData, emailTemplate.getTitle()))
                .setBody(format(objectData, body.concat(emailSignature)))
                .setSendTo(sendTo)
                .setCopyTo(copyTo);
        return send(emailPoster);
    }

    @Override
    public Boolean sendVerify(String smsTemplateType, String mailAddr) {
        SMSTemplateTypeEnum tmpType = SMSTemplateTypeEnum.getEnumByValue(SMSTemplateTypeEnum.dictionary, smsTemplateType);
        List<EmailVerifyTemplate> list = new EmailVerifyTemplate().setTemplateType(tmpType).queryList();
        EmailVerifyTemplate emailVerifyTemplate = Optional.ofNullable(list)
                .filter(_context -> _context.size() > 0)
                .map(_context -> _context.get(0))
                .orElseThrow(() -> PamirsException.construct(MessageExpEnumerate.MAIL_EMAIL_VERIFY_TEMPLATE_ERROR).errThrow());

        EmailPoster mailPoster = new EmailPoster();
        mailPoster.setSendTo(mailAddr);
        mailPoster.setTitle(emailVerifyTemplate.getTitle());

        String code = VerificationCodeUtils.code();
        VerificationCode verificationCode = new VerificationCode()
                .setVerifyType(tmpType)
                .setSource(mailAddr)
                .setCode(code)
                .setExpirationTime(VerificationCodeUtils.plusSec(emailVerifyTemplate.getTimeInterval()))
                .setIsUsed(false)
                .setInvalid(false)
                .setSourceType(MessageEngineTypeEnum.EMAIL_SEND);
        try {
            verificationCode.create();
        } catch (Exception e) {
            throw PamirsException.construct(MessageExpEnumerate.MAIL_EMAIL_VERIFY_CODE_SAVE_ERROR, e).errThrow();
        }

        String body = VerificationCodeUtils.placeHolderExec(emailVerifyTemplate.getBody(), code);
        mailPoster.setBody(body);
        return send(mailPoster);
    }

    @Override
    public Boolean send(EmailPoster mailPoster) {
        Session session = getSession();
        try {
            //创建默认的 MimeMessage 对象
            MimeMessage message = new MimeMessage(session);
            //Set From: 头部头字段
            if (StringUtils.isNotBlank(mailPoster.getSender())) {
                message.setFrom(new InternetAddress(emailSenderSource.getSmtpUser(), mailPoster.getSender()));
            } else {
                message.setFrom(new InternetAddress(emailSenderSource.getSmtpUser()));
            }
            //Set To: 头部头字段
            // 设置收件人
            if (StringUtils.isNotBlank(mailPoster.getSendTo())) {
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailPoster.getSendTo()));
            }
            // 设置抄送人
            if (StringUtils.isNotBlank(mailPoster.getCopyTo())) {
                message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(mailPoster.getCopyTo()));
            }
            // 设置回复人
            if (StringUtils.isNotBlank(mailPoster.getReplyTo())) {
                message.setReplyTo(InternetAddress.parse(mailPoster.getReplyTo()));
            }
            //Set Subject: 头字段
            message.setSubject(mailPoster.getTitle());
            //创建消息部分
            BodyPart messageBodyPart = new MimeBodyPart();
            //创建多重消息
            Multipart multipart = new MimeMultipart();
            //设置文本消息部分
            messageBodyPart.setContent(mailPoster.getBody(), "text/html; charset=utf-8");
            multipart.addBodyPart(messageBodyPart);
            List<PamirsFile> files = mailPoster.getResourceFiles();
            if (CollectionUtils.isNotEmpty(files)) {
                for (PamirsFile file : files) {
                    //附件部分
                    messageBodyPart = new MimeBodyPart();
                    DataSource source = new FileDataSource(file.getUrl());
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName(file.getName());
                    multipart.addBodyPart(messageBodyPart);
                }
            }
            //发送完整消息
            message.setContent(multipart);
            //发送消息
            Transport.send(message);
            log.debug("Sent message successfully....");
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw PamirsException.construct(MessageExpEnumerate.SYSTEM_ERROR, e).errThrow();
        }
        return true;
    }

    private Session getSession() {
        try {
            //发件人电子邮箱
            String from = emailSenderSource.getSmtpUser();
            //指定发送邮件的主机为 localhost
            String host = emailSenderSource.getSmtpHost();
            //邮件服务器端口
            Integer port = emailSenderSource.getSmtpPort();
            //加密方式
            String smtpSecurity = emailSenderSource.getSmtpSecurity().getValue();
            // 获取系统属性
            Properties properties = System.getProperties();
            // 设置邮件服务器
            properties.setProperty("mail.smtp.host", host);
            if (!ObjectUtils.isEmpty(port)) {
                properties.setProperty("mail.smtp.port", port.toString());
            }
            properties.put("mail.smtp.auth", "true");
            if (EmailSendSecurityEnum.SSL.getValue().equals(smtpSecurity)) {
                MailSSLSocketFactory sf = new MailSSLSocketFactory();
                sf.setTrustAllHosts(true);
                properties.put("mail.smtp.ssl.enable", "true");
                properties.put("mail.smtp.ssl.socketFactory", sf);
            }
            if (EmailSendSecurityEnum.STARTTLS.getValue().equals(smtpSecurity)) {
                properties.put("mail.smtp.starttls.enable", "true");
            }
            // 获取session对象
            return Session.getInstance(properties, new Authenticator() {

                @Override
                public PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailSenderSource.getSmtpUser(), emailSenderSource.getSmtpPassword()); //发件人邮件用户名、密码
                }
            });
        } catch (Exception e) {
            throw PamirsException.construct(MessageExpEnumerate.SYSTEM_ERROR, e).errThrow();
        }
    }

    private String format(Map<String, Object> parameter, String template) {
        if (StringUtils.isBlank(template)) {
            return template;
        }
        StringBuffer buf = new StringBuffer(template);
        int startIndex = buf.indexOf(PLACEHOLDER_PREFIX);
        while (startIndex != -1) {
            int endIndex = buf.indexOf(PLACEHOLDER_SUFFIX, startIndex + PLACEHOLDER_PREFIX.length());
            if (endIndex != -1) {
                String placeholder = buf.substring(startIndex + PLACEHOLDER_PREFIX.length(), endIndex);
                int nextIndex = endIndex + PLACEHOLDER_SUFFIX.length();
                try {
                    String propVal = Optional.ofNullable((Exp.run(placeholder, parameter)).toString()).orElse("");
                    buf.replace(startIndex, endIndex + PLACEHOLDER_SUFFIX.length(), propVal);
                    nextIndex = startIndex + propVal.length();
                } catch (Exception e) {
                    throw PamirsException.construct(MessageExpEnumerate.MAIL_EMAIL_REPLACE_ERROR, e).errThrow();
                }
                startIndex = buf.indexOf(PLACEHOLDER_PREFIX, nextIndex);
            } else {
                startIndex = -1;
            }
        }
        return buf.toString();
    }

}
