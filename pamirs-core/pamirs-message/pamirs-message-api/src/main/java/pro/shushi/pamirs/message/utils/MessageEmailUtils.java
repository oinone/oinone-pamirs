package pro.shushi.pamirs.message.utils;

import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.message.enmu.MessageEngineTypeEnum;
import pro.shushi.pamirs.message.model.EmailSenderSource;
import pro.shushi.pamirs.meta.api.Models;

/**
 * @author yeshenyue on 2024/9/2 11:10.
 */
public class MessageEmailUtils {

    /**
     * 获取邮件服务器配置，使用已启用状态下，最新创建的作为邮件服务器
     */
    public static EmailSenderSource fetchEmailSendConfig() {
        return Models.origin().queryOneByWrapper(Pops.<EmailSenderSource>lambdaQuery()
                .from(EmailSenderSource.MODEL_MODEL)
                .eq(EmailSenderSource::getActive, Boolean.TRUE)
                .eq(EmailSenderSource::getType, MessageEngineTypeEnum.EMAIL_SEND)
                .orderByDesc(EmailSenderSource::getCreateDate)
                .last(FetchUtil.LIMIT_1)
        );
    }
}
