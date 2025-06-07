package pro.shushi.pamirs.message.init;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.InstallDataInit;
import pro.shushi.pamirs.boot.common.api.init.UpgradeDataInit;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.message.MessageModule;
import pro.shushi.pamirs.message.action.UnreadMessageAction;
import pro.shushi.pamirs.message.enmu.EmailSendSecurityEnum;
import pro.shushi.pamirs.message.enmu.MessageEngineTypeEnum;
import pro.shushi.pamirs.message.model.EmailSenderSource;
import pro.shushi.pamirs.message.model.EmailTemplate;
import pro.shushi.pamirs.message.model.PamirsMessage;
import pro.shushi.pamirs.message.model.UnreadMessage;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.base.manager.data.OriginDataManager;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.user.api.model.PamirsUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 消息模块的数据初始化
 *
 * @author haibo(xf.z @ shushi.pro)
 * @date 2022-09-15 14:15:14
 */
@Slf4j
@Component
@Order(0)
public class MessageDataInit implements InstallDataInit, UpgradeDataInit {


    @Override
    public boolean init(AppLifecycleCommand command, String version) {
        dataInit();
//        initEmailTemplate();
        return true;
    }

    @Override
    public boolean upgrade(AppLifecycleCommand command, String version, String existVersion) {
        dataInit();
//        initEmailTemplate();
        return true;
    }

    private void dataInit() {

        try {
            log.info("初始化消息维护数据");
            OriginDataManager dataManager = Models.origin();
            List<PamirsMessage> messages = dataManager.queryListByWrapper(Pops.<PamirsMessage>lambdaQuery().from(PamirsMessage.MODEL_MODEL)
                    .isNull(PamirsMessage::getSubject)
            );
            if (CollectionUtils.isNotEmpty(messages)) {
                for (PamirsMessage message : messages) {
                    PamirsMessage update = new PamirsMessage();
                    update.setId(message.getId());
                    update.setSubject(Optional.ofNullable(message.getName()).orElse(null));

                    if (StringUtils.isBlank(message.getSubject()) && StringUtils.isNotEmpty(message.getBody())) {
                        String originBody = message.getBody();
                        String messageSummary = originBody.replaceAll(UnreadMessageAction.RE_HTML_MARK, "");
                        if (messageSummary.length() > 16) {
                            messageSummary = messageSummary.substring(0, 16)
                                    .concat(CharacterConstants.SEPARATOR_DOT)
                                    .concat(CharacterConstants.SEPARATOR_DOT)
                                    .concat(CharacterConstants.SEPARATOR_DOT);
                        }
                        update.setSubject(messageSummary);
                    }
                    if (StringUtils.isBlank(message.getModule())) update.setModule(MessageModule.MODULE_MODULE);
                    update.updateById();
                }
            }

            List<UnreadMessage> unreadMessages = dataManager.queryListByWrapper(Pops.<UnreadMessage>lambdaQuery().from(UnreadMessage.MODEL_MODEL)
                    .isNull(UnreadMessage::getSubject)
            );
            if (CollectionUtils.isNotEmpty(unreadMessages)) {
                for (UnreadMessage unreadMessage : unreadMessages) {
                    unreadMessage = unreadMessage.fieldQuery(UnreadMessage::getMessage);
                    UnreadMessage update = new UnreadMessage();
                    update.setId(unreadMessage.getId());
                    update.setSubject(Optional.ofNullable(unreadMessage.getMessage()).map(PamirsMessage::getSubject).orElse(null));
                    if (StringUtils.isBlank(unreadMessage.getModule())) update.setModule(MessageModule.MODULE_MODULE);
                    update.updateById();
                }
            }
        } catch (Exception e) {
            log.error("堆栈:{}", e);
        }
    }

    private void initEmailTemplate() {
        EmailSenderSource emailSenderSource = new EmailSenderSource();
        emailSenderSource.setName("邮件发送服务");
        emailSenderSource.setType(MessageEngineTypeEnum.EMAIL_SEND);
        //优先级
        emailSenderSource.setSequence(10);
        //发送账号
        emailSenderSource.setSmtpUser("oinonesaas@shushi.pro");
        //发送密码
        emailSenderSource.setSmtpPassword("Oio@915");
        //发送服务器地址和端口
        emailSenderSource.setSmtpHost("smtp.exmail.qq.com");
        emailSenderSource.setSmtpPort(465);
        //" None: SMTP 对话用明文完成。" +
        //" TLS (STARTTLS): SMTP对话的开始时要求TLS 加密 (建议)" +
        //" SSL/TLS: SMTP对话通过专用端口用 SSL/TLS 加密 (默认是: 465)")
        emailSenderSource.setSmtpSecurity(EmailSendSecurityEnum.SSL);
        emailSenderSource.setActive(true);
        emailSenderSource.createOrUpdate();

        List<EmailTemplate> templates = new ArrayList<>();
        templates.add(new EmailTemplate().setName("重置密码邮件模板").setTitle("请确认你的密码修改请求").setBody("<p>Hi ${realname}，</p><p>请确认你的密码修改请求。如果不是你的密码修改请求，请忽略该邮件。</p><p><a href=\\\"www.oinone.top\\\" target=\\\"_blank\\\">点击链接确认</a>。</p><p>最好的祝愿，</p><p>Oinone</p>").setModel(PamirsUser.MODEL_MODEL).setEmailSenderSource(emailSenderSource));
        templates.add(new EmailTemplate().setName("修改邮箱邮件模板").setTitle("请确认你的邮箱修改请求").setBody("<p>Hi ${realname}，</p><p>请确认你的邮箱修改请求。如果不是你的邮箱修改请求，请忽略该邮件。</p><p><a href=\\\"www.oinone.top\\\" target=\\\"_blank\\\">点击链接确认</a>。</p><p>最好的祝愿，</p><p>Oinone</p>").setModel(PamirsUser.MODEL_MODEL).setEmailSenderSource(emailSenderSource));
        templates.add(new EmailTemplate().setName("确认修改邮箱邮件模板").setTitle("请确认你的新邮箱").setBody("<p>Hi ${realname}，</p><p>请确认你的新邮箱：cc@shushi.pro。</p><p>如果不是你的新邮箱修改请求，请忽略该邮件。</p><p><a href=\\\"www.oinone.top\\\" target=\\\"_blank\\\">点击链接确认</a>。</p><p>最好的祝愿，</p><p>Oinone</p>").setModel(PamirsUser.MODEL_MODEL).setEmailSenderSource(emailSenderSource));
        new EmailTemplate().createOrUpdateBatch(templates);
    }

    @Override
    public List<String> modules() {
        return Arrays.asList(MessageModule.MODULE_MODULE);
    }

    @Override
    public int priority() {
        return 0;
    }
}
