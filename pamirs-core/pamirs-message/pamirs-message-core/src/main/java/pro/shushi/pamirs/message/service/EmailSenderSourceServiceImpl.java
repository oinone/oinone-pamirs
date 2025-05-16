package pro.shushi.pamirs.message.service;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.message.model.EmailSenderSource;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.List;

@Service
@Fun(EmailSenderSourceService.FUN_NAMESPACE)
public class EmailSenderSourceServiceImpl implements EmailSenderSourceService {

    @Function
    @Override
    public EmailSenderSource queryById(Long id) {
        return new EmailSenderSource().queryById(id);
    }

    @Function
    @Override
    public List<EmailSenderSource> queryListByActive(Boolean b) {
        return new EmailSenderSource().setActive(b).queryList();
    }
}
