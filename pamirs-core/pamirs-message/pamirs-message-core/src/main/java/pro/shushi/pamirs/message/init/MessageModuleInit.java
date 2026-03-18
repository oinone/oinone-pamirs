package pro.shushi.pamirs.message.init;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.LifecycleCompletedInit;
import pro.shushi.pamirs.core.common.constant.CommonConstants;
import pro.shushi.pamirs.framework.connectors.cdn.factory.FileClientFactory;
import pro.shushi.pamirs.message.conf.EmailSmtpConfig;
import pro.shushi.pamirs.message.conf.SmsAliyunConfig;
import pro.shushi.pamirs.message.enmu.*;
import pro.shushi.pamirs.message.model.*;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.util.FileUtils;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class MessageModuleInit implements LifecycleCompletedInit {

    @Autowired
    private SmsAliyunConfig smsAliyunConfig;
    @Autowired
    private EmailSmtpConfig emailSmtpConfig;

    private static final String DEFAULT_EMAIL_NAME = "Email Sending Service";

    @Override
    public void process(AppLifecycleCommand command, List<ModuleDefinition> installModules, List<ModuleDefinition> upgradeModules, List<ModuleDefinition> reloadModules) {

        initMessageChannel();

        initSmsConfig(); // 初始化短信配置 & 短信模板

        initEmailConfig();
    }

    private void initEmailConfig() {
        if (null == emailSmtpConfig) {
            log.warn("No email server configured.");
            return;
        }

        if (StringUtils.isBlank(emailSmtpConfig.getSmtpHost()) || StringUtils.isBlank(emailSmtpConfig.getSmtpPassword())
                || StringUtils.isBlank(emailSmtpConfig.getSmtpUser())
                || Objects.isNull(emailSmtpConfig.getSmtpPort())) {
            log.warn("The email server configuration is invalid.");
            return;
        }
        EmailSenderSource senderSource = new EmailSenderSource().setSmtpUser(emailSmtpConfig.getSmtpUser()).setSmtpHost(emailSmtpConfig.getSmtpHost()).queryOne();

        if (null == senderSource) {
            senderSource = generateEmailChannelConfig(senderSource);
            senderSource.create();
        } else {
            EmailSenderSource update = generateEmailChannelConfig(senderSource);
            update.setId(senderSource.getId());
            update.updateById();
        }
        Long senderSourceId = senderSource.getId();
        if (senderSourceId != null) {
            List<EmailTemplate> emailTemplates = Optional.of(emailSmtpConfig)
                    .map(EmailSmtpConfig::getTemplates)
                    .map(Collection::stream)
                    .orElse(Stream.empty())
                    .map(_tmpConfig -> {
                        EmailTemplate emailTemplate = new EmailTemplate();
                        String body = Optional.ofNullable(_tmpConfig.getBody()).orElse("");
                        if (body.trim().startsWith(CommonConstants.CLASSPATH_PROTOCOL)){
                            body = FileUtils.read(body.trim());
                        }
                        emailTemplate.setName(_tmpConfig.getName())
                                .setTitle(_tmpConfig.getTitle())
                                .setBody(body)
                                .setEmailSenderSource((EmailSenderSource) new EmailSenderSource().setId(senderSourceId));
                        return emailTemplate;
                    })
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(emailTemplates)) {
                log.warn("No email template configured.");
                return;
            }

            new EmailTemplate().createOrUpdateBatch(emailTemplates);
        }
    }

    private EmailSenderSource generateEmailChannelConfig(EmailSenderSource senderSource) {
        if (senderSource == null) senderSource = new EmailSenderSource();
        senderSource.setName(StringUtils.isBlank(senderSource.getName()) ? DEFAULT_EMAIL_NAME : senderSource.getName());
        senderSource.setType(MessageEngineTypeEnum.EMAIL_SEND);
        //优先级
        senderSource.setSequence(10);
        //发送账号
        senderSource.setSmtpUser(emailSmtpConfig.getSmtpUser());
        //发送密码
        senderSource.setSmtpPassword(emailSmtpConfig.getSmtpPassword());
        //发送服务器地址和端口
        senderSource.setSmtpHost(emailSmtpConfig.getSmtpHost());
        senderSource.setSmtpPort(emailSmtpConfig.getSmtpPort());
        //" None: SMTP 对话用明文完成。" +
        //" TLS (STARTTLS): SMTP对话的开始时要求TLS 加密 (建议)" +
        //" SSL/TLS: SMTP对话通过专用端口用 SSL/TLS 加密 (默认是: 465)")
        String smtpSecurity = Optional.ofNullable(emailSmtpConfig.getSmtpSecurity()).orElse("SSL");
        senderSource.setSmtpSecurity(EmailSendSecurityEnum.valueOf(smtpSecurity));
        senderSource.setActive(senderSource.getActive() == null || senderSource.getActive());
        return senderSource;
    }

    private void initSmsConfig() {
        if (null == smsAliyunConfig) {
            log.warn("No SMS channel configured.");
            return;
        }

        if (StringUtils.isBlank(smsAliyunConfig.getAccessKeyId()) || StringUtils.isBlank(smsAliyunConfig.getSignName())) {
            log.warn("The SMS channel configuration is invalid.");
            return;
        }

        SmsChannelConfig smsChannelConfig = new SmsChannelConfig()
                .setChannel(SMSChannelEnum.ALIYUN)
                .setAccessKeyId(smsAliyunConfig.getAccessKeyId())
                .setSignName(smsAliyunConfig.getSignName())
                .queryOne();

        if (null == smsChannelConfig) {
            smsChannelConfig = generateSmsChannelConfig(smsAliyunConfig);
            smsChannelConfig.setChannel(SMSChannelEnum.ALIYUN);
            smsChannelConfig.setType(MessageEngineTypeEnum.SMS_SEND);
            smsChannelConfig.setActive(Boolean.TRUE);
            smsChannelConfig.create();
        } else {
            SmsChannelConfig update = generateSmsChannelConfig(smsAliyunConfig);
            update.setId(smsChannelConfig.getId());
            update.updateById();
        }

        List<SmsTemplate> smsTemplates = Optional.of(smsAliyunConfig)
                .map(SmsAliyunConfig::getTemplates)
                .map(Collection::stream)
                .orElse(Stream.empty())
                .map(_tmpConfig -> {
                    SmsTemplate smsTemplate = new SmsTemplate();
                    smsTemplate.setChannel(Optional.ofNullable(_tmpConfig.getChannel()).map(SMSChannelEnum::valueOf).orElse(SMSChannelEnum.ALIYUN));
                    SMSTemplateTypeEnum typeEnum = Optional.ofNullable(_tmpConfig.getTemplateType())
                            .map(_type -> SMSTemplateTypeEnum.getEnum(SMSTemplateTypeEnum.class, _type))
                            .orElseThrow(() -> PamirsException.construct(MessageExpEnumerate.MAIL_CONFIG_ERROR).errThrow());
                    smsTemplate.setTemplateType(typeEnum);
                    smsTemplate.setTemplateCode(_tmpConfig.getTemplateCode());
                    smsTemplate.setTemplateContent(_tmpConfig.getTemplateContent());
                    smsTemplate.setTimeInterval(_tmpConfig.getTimeInterval());
                    smsTemplate.setHasVerifyCode(_tmpConfig.getHasVerifyCode());
                    return smsTemplate;
                })
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(smsTemplates)) {
            log.warn("No SMS template configured.");
            return;
        }

        new SmsTemplate().createOrUpdateBatch(smsTemplates);
    }

    private SmsChannelConfig generateSmsChannelConfig(SmsAliyunConfig smsAliyunConfig) {
        SmsChannelConfig config = new SmsChannelConfig();
        config.setSignName(smsAliyunConfig.getSignName());
        config.setAccessKeyId(smsAliyunConfig.getAccessKeyId());
        config.setAccessKeySecret(smsAliyunConfig.getAccessKeySecret());
        config.setEndpoint(smsAliyunConfig.getEndpoint());
        config.setRegionId(smsAliyunConfig.getRegionId());
        config.setTimeZone(smsAliyunConfig.getTimeZone());
        config.setSignatureMethod(smsAliyunConfig.getSignatureMethod());
        config.setSignatureVersion(smsAliyunConfig.getSignatureVersion());
        config.setVersion(smsAliyunConfig.getVersion());
        return config;
    }

    private void initMessageChannel() {
        MessageChannel messageChannel = new MessageChannel();
        messageChannel.setId(99L);
        messageChannel.setName("systemmailbroadcast");
        messageChannel.setChannelType(MessageChannelTypeEnum.SYSTEM_MAIL_BROADCAST);
        messageChannel.setIconUrl(FileClientFactory.getClient().getStaticUrl() + "/oinone/img/channel_default_icon.png");
        messageChannel.setOpenType(MessageChannelOpenTypeEnum.PRIVATE);
        messageChannel.createOrUpdate();

        MessageChannel testChannel = new MessageChannel();
        testChannel.setId(100L);
        testChannel.setName("channel");
        testChannel.setChannelType(MessageChannelTypeEnum.CHANNEL);
        testChannel.setIconUrl(FileClientFactory.getClient().getStaticUrl() + "/oinone/img/channel_default_icon.png");
        testChannel.setOpenType(MessageChannelOpenTypeEnum.PUBLIC);
        testChannel.createOrUpdate();
    }

}
