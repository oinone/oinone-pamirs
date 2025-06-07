package pro.shushi.pamirs.message.engine.message;

import pro.shushi.pamirs.message.engine.IMessageEngine;
import pro.shushi.pamirs.message.model.MessageSource;

public class MessageSendEngine implements IMessageEngine {

    @Override
    public Object get(MessageSource messageSource) {
        return new DefaultMessageSender();
    }

}
