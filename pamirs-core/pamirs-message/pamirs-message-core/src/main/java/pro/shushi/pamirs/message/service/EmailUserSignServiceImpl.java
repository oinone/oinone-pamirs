package pro.shushi.pamirs.message.service;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.message.model.EmailUserSign;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

@Service
@Fun(EmailUserSignService.FUN_NAMESPACE)
public class EmailUserSignServiceImpl implements EmailUserSignService {

    @Function
    @Override
    public EmailUserSign queryById(Long id) {
        return new EmailUserSign().queryById(id);
    }
}
