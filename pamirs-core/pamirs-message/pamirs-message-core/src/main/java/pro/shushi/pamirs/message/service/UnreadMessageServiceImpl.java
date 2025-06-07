package pro.shushi.pamirs.message.service;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.message.model.UnreadMessage;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.List;

@Service
@Fun(UnreadMessageService.FUN_NAMESPACE)
public class UnreadMessageServiceImpl implements UnreadMessageService {

    @Function
    @Override
    public List<UnreadMessage> createBatch(List<UnreadMessage> data) {
        return new UnreadMessage().createBatch(data);
    }
}
