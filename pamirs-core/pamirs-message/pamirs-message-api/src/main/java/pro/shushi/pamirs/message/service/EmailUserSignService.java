package pro.shushi.pamirs.message.service;

import pro.shushi.pamirs.message.model.EmailUserSign;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

@Fun(EmailUserSignService.FUN_NAMESPACE)
public interface EmailUserSignService {
    String FUN_NAMESPACE = "message.EmailUserSignService";

    @Function
    EmailUserSign queryById(Long id);
}
