package pro.shushi.pamirs.message.init;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.extend.MetaDataEditor;
import pro.shushi.pamirs.core.common.InitializationUtil;
import pro.shushi.pamirs.message.MessageModule;
import pro.shushi.pamirs.message.model.*;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.util.Arrays;
import java.util.Map;

/**
 * @author Adamancy Zhang
 * @date 2020-12-31 12:38
 */
@Component
public class MessageMetaDataEditor implements MetaDataEditor {

    @Override
    public void edit(AppLifecycleCommand command, Map<String, Meta> metaMap) {
        InitializationUtil util = InitializationUtil.get(metaMap, MessageModule.MODULE_MODULE, MessageModule.MODULE_NAME);
        if (util == null) {
            return;
        }
        viewActionInit(util);
        modifyViewAction(util);
        menuInit(util);
        homepageInit(util);
    }

    private void viewActionInit(InitializationUtil util) {
        //顶部栏消息详情 不要删除
        util.createViewAction("top_bar_message_channel_detail",
                "顶部栏消息详情", MessageChannel.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), MessageChannel.MODEL_MODEL, ViewTypeEnum.DETAIL, ActionContextTypeEnum.SINGLE, ActionTargetEnum.ROUTER, "message_channel_detail_4_topbar");
        util.createViewAction("频道管理detail",
                "频道管理详情", MessageChannel.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), MessageChannel.MODEL_MODEL, ViewTypeEnum.DETAIL, ActionContextTypeEnum.SINGLE, ActionTargetEnum.ROUTER, "频道管理detail");

        util.createViewAction("添加关注者", "添加关注者", MessageFollower.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.FORM), MessageFollower.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, null);
        util.createViewAction("添加频道", "添加频道", MessageFollower.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.FORM), MessageFollower.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, null);

    }

    private void modifyViewAction(InitializationUtil util) {
    }

    private void menuInit(InitializationUtil util) {
        util.createViewActionMenu("messageChannelManagement_menu", "频道管理", 1L, null, MessageChannel.MODEL_MODEL, "频道管理table", null);

        util.createViewActionMenu("emailConfigurationManagement_menu", "邮件配置管理", 2L, null);
        util.createViewActionMenu("emailSource_menu", "邮件服务器", 1L, "emailConfigurationManagement_menu", EmailSenderSource.MODEL_MODEL, "邮件服务器table", _viewAction -> {
            _viewAction.setDomain("type=='EMAIL_SEND'");
        });
        util.createViewActionMenu("emailSignature_menu", "邮件签名", 2L, "emailConfigurationManagement_menu", EmailUserSign.MODEL_MODEL, "邮件签名table", null);
        util.createViewActionMenu("emailTemplate_menu", "邮件模板", 3L, "emailConfigurationManagement_menu", EmailTemplate.MODEL_MODEL, "邮件模板table", null);
        util.createViewActionMenu("emailVerifyTemplate_menu", "邮箱验证模板", 4L, "emailConfigurationManagement_menu", EmailVerifyTemplate.MODEL_MODEL, "邮箱验证模板table", null);

        util.createViewActionMenu("smsConfigurationManagement_menu", "短信配置管理", 3L, null);
        util.createViewActionMenu("smsSource_menu", "短信通道", 1L, "smsConfigurationManagement_menu", SmsChannelConfig.MODEL_MODEL, "短信通道table",  _viewAction -> {
            _viewAction.setDomain("type=='SMS_SEND'");
        });
        util.createViewActionMenu("smsTemplate_menu", "短信模板", 2L, "smsConfigurationManagement_menu", SmsTemplate.MODEL_MODEL, "短信模板table", null);

        util.createViewActionMenu("myMessageReceive_menu", "消息列表", 4L, null, UnreadMessage.MODEL_MODEL, "消息列表table", null);
    }

    private void homepageInit(InitializationUtil util) {
        util.setHomepageByMenu("messageChannelManagement_menu");
    }

}
