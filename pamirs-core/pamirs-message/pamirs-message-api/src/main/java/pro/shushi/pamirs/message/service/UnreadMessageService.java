package pro.shushi.pamirs.message.service;

import pro.shushi.pamirs.message.model.UnreadMessage;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.List;

@Fun(UnreadMessageService.FUN_NAMESPACE)
public interface UnreadMessageService {
    String FUN_NAMESPACE = "message.UnreadMessageService";

    @Function
    List<UnreadMessage> createBatch(List<UnreadMessage> data);
}
