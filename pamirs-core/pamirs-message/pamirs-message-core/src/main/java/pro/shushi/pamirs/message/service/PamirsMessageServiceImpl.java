package pro.shushi.pamirs.message.service;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.message.model.PamirsMessage;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.List;

@Service
@Fun(PamirsMessageService.FUN_NAMESPACE)
public class PamirsMessageServiceImpl implements PamirsMessageService {

    @Function
    @Override
    public List<PamirsMessage> createBatch(List<PamirsMessage> data) {
        return new PamirsMessage().createBatch(data);
    }

    @Function
    @Override
    public PamirsMessage extendIconSetting(PamirsMessage data) {
        data = data.queryById();
        PamirsMessage update = new PamirsMessage();
        update.setId(data.getId());
        update.setExtendIcon(data.getExtendIcon());
        update.updateById();
        return data;
    }
}
