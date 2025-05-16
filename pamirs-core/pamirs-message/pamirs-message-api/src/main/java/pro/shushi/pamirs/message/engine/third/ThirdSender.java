package pro.shushi.pamirs.message.engine.third;

import pro.shushi.pamirs.message.model.third.ThirdMailMessageTransientModel;

public interface ThirdSender {
    boolean send(ThirdMailMessageTransientModel mailMessage);
}
