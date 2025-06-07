package pro.shushi.pamirs.message.engine;

import pro.shushi.pamirs.message.engine.email.EmailSendEngine;
import pro.shushi.pamirs.message.engine.message.MessageSendEngine;
import pro.shushi.pamirs.message.engine.sms.SMSSendEngine;
import pro.shushi.pamirs.message.engine.third.ThirdSendEngine;
import pro.shushi.pamirs.message.enmu.MessageEngineTypeEnum;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageEngine {

    private static Map<MessageEngineTypeEnum, IMessageEngine> engineMaps = new ConcurrentHashMap<>();

    static {
        engineMaps.put(MessageEngineTypeEnum.EMAIL_SEND, new EmailSendEngine());
        engineMaps.put(MessageEngineTypeEnum.SMS_SEND, new SMSSendEngine());
        engineMaps.put(MessageEngineTypeEnum.MAIL_SEND, new MessageSendEngine());
        engineMaps.put(MessageEngineTypeEnum.THIRD_PUSH, new ThirdSendEngine());
    }

    public static <T> IMessageEngine<T> get(MessageEngineTypeEnum type) {
        return engineMaps.<T>get(type);
    }

}
