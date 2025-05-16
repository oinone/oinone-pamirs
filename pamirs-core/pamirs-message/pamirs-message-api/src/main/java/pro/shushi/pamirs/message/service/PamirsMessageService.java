package pro.shushi.pamirs.message.service;

import pro.shushi.pamirs.message.model.PamirsMessage;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.List;

@Fun(PamirsMessageService.FUN_NAMESPACE)
public interface PamirsMessageService {
    String FUN_NAMESPACE = "message.PamirsMessageService";

    @Function
    List<PamirsMessage> createBatch(List<PamirsMessage> data);

    @Function
    PamirsMessage extendIconSetting(PamirsMessage data);
}
