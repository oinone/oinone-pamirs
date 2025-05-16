package pro.shushi.pamirs.message.service;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.message.enmu.MessageChannelTypeEnum;
import pro.shushi.pamirs.message.model.MessageChannel;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.List;

@Service
@Fun(MessageChannelService.FUN_NAMESPACE)
public class MessageChannelServiceImpl implements MessageChannelService {

    @Function
    @Override
    public List<MessageChannel> queryListByNameAndChannelType(String name, MessageChannelTypeEnum type) {
        return new MessageChannel().setName(name)
                .setChannelType(type)
                .queryList();
    }

    @Function
    @Override
    public MessageChannel create(MessageChannel data) {
        return data.create();
    }

    @Override
    public List<MessageChannel> queryListByName(String name) {
        return new MessageChannel().setName(name).queryList();
    }
}
