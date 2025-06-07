package pro.shushi.pamirs.message.service;

import pro.shushi.pamirs.message.model.EmailSenderSource;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.List;

@Fun(EmailSenderSourceService.FUN_NAMESPACE)
public interface EmailSenderSourceService {
    String FUN_NAMESPACE = "message.EmailSenderSourceService";

    @Function
    EmailSenderSource queryById(Long id);

    @Function
    List<EmailSenderSource> queryListByActive(Boolean b);
}
