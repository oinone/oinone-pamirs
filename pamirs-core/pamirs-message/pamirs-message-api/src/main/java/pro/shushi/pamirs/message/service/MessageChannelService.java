package pro.shushi.pamirs.message.service;

import pro.shushi.pamirs.message.enmu.MessageChannelTypeEnum;
import pro.shushi.pamirs.message.model.MessageChannel;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.List;

@Fun(MessageChannelService.FUN_NAMESPACE)
public interface MessageChannelService {
    String FUN_NAMESPACE = "message.MessageChannelService";

    @Function
    List<MessageChannel> queryListByNameAndChannelType(String name, MessageChannelTypeEnum type);

    @Function
    MessageChannel create(MessageChannel data);

    @Function
    List<MessageChannel> queryListByName(String name);
}
