package pro.shushi.pamirs.message.action;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.message.engine.MessageEngine;
import pro.shushi.pamirs.message.engine.sms.SMSSender;
import pro.shushi.pamirs.message.enmu.MessageEngineTypeEnum;
import pro.shushi.pamirs.message.tmodel.SmsSend;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

/**
 * SmsSendAction
 *
 * @author yakir on 2021/05/08 17:11.
 */
@Model.model(SmsSend.MODEL_MODEL)
@Component
public class SmsSendAction {

    /**
     * 管理后台界面：获取验证码demo
     *
     * @param data SmsSend
     * @return SmsSend
     */
    @Action(displayName = "获取验证码", contextType = ActionContextTypeEnum.CONTEXT_FREE, bindingType = ViewTypeEnum.FORM)
    public SmsSend sendCode(SmsSend data) {
        MessageEngine.<SMSSender>get(MessageEngineTypeEnum.SMS_SEND)
                .get(null)
                .smsSend(data.getType(), data.getPhone(), null);
        return data;
    }

}
