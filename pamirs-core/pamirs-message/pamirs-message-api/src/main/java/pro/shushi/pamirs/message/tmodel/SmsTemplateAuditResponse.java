package pro.shushi.pamirs.message.tmodel;

import pro.shushi.pamirs.message.enmu.SMSTemplateStatusEnum;
import pro.shushi.pamirs.meta.annotation.fun.Data;


/**
 * @author xzf (xzf@shushi.pro)
 * @date 2022/6/27 12:16 下午
 */
@Data
public class SmsTemplateAuditResponse {
    private Boolean success;
    private String errorMessage;

    private String templateCode;
    private String templateContent;

    private SMSTemplateStatusEnum status;

    private String reason;

}
