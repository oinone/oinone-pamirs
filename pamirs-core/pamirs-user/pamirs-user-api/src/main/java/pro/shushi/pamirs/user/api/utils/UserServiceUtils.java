package pro.shushi.pamirs.user.api.utils;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserTransient;

import java.util.Optional;

/**
 * @author shier
 * date  2022/8/17 6:51 下午
 */
@Slf4j
public class UserServiceUtils {

    public static void broken(PamirsUserTransient userTransient) {
        userTransient.setRawPassword(null)
                .setConfirmPassword(null)
                .setPassword(null)
                .setBroken(true);
        log.warn("Break: code:[{}], field:[{}], msg:[{}]", userTransient.getErrorCode(),
                userTransient.getErrorField(), userTransient.getErrorMsg());
    }

    public static String getPhoneCode(PamirsUserTransient userTransient) {
        return Optional.ofNullable(userTransient)
                .map(PamirsUserTransient::getPhoneCode)
                .filter(StringUtils::isNotBlank)
                .orElseGet(() -> Optional.ofNullable(userTransient)
                        .map(PamirsUserTransient::getInviteCode)
                        .filter(StringUtils::isNotBlank)
                        .orElse(null));
    }
}
