package pro.shushi.pamirs.message.tmodel;

import pro.shushi.pamirs.message.enmu.SMSTemplateStatusEnum;
import pro.shushi.pamirs.meta.annotation.fun.Data;

/**
 * 申请短信模板结果对象
 *
 * @author drome
 * @date 2021/8/315:55 下午
 */
@Data
public class SmsTemplateResponse {
    private Boolean success;
    private String errorMessage;

    private String templateCode;
    private String templateContent;

    private SMSTemplateStatusEnum status;
}
