package pro.shushi.pamirs.message.engine.third;

import pro.shushi.pamirs.message.engine.IMessageEngine;
import pro.shushi.pamirs.message.model.MessageSource;

public class ThirdSendEngine implements IMessageEngine<ThirdSender> {
    @Override
    public ThirdSender get(MessageSource messageSource) {
        return new DefaultThirdSender();
    }
}
