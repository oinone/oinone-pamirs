package pro.shushi.pamirs.message.service;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.message.model.VerificationCode;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

@Service
@Fun(VerificationCodeService.FUN_NAMESPACE)
public class VerificationCodeServiceImpl implements VerificationCodeService {

    @Function
    @Override
    public void create(VerificationCode data) {
        data.create();
    }
}
