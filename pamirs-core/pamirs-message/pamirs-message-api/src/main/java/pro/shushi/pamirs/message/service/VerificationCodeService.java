package pro.shushi.pamirs.message.service;

import pro.shushi.pamirs.message.model.VerificationCode;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

@Fun(VerificationCodeService.FUN_NAMESPACE)
public interface VerificationCodeService {
    String FUN_NAMESPACE = "message.VerificationCodeService";

    @Function
    void create(VerificationCode data);
}
