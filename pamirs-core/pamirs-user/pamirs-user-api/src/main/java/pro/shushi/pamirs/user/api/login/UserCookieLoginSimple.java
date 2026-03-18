package pro.shushi.pamirs.user.api.login;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.model.PamirsUserDTO;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.user.api.enmu.UserExpEnumerate;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserTransient;

/**
 * @author shier
 * date 2020/4/10
 */
@Slf4j
@Order
@Component(UserLoginFactory.DEFAULT_LOGIN)
public class UserCookieLoginSimple extends UserCookieLogin<PamirsUser> {

    private IUserLoginChecker checker;

    @Autowired(required = false)
    private IUserDataChecker dataChecker;

    @Override
    public PamirsUser resolveAndVerification(PamirsUserTransient user) {
        if (checker == null) {
            checker = BeanDefinitionUtils.getBean(IUserLoginChecker.class);
        }
        String phone = user.getPhone();
        String email = user.getEmail();
        if (StringUtils.isNotBlank(email)) {
            return dataChecker.checkEmailIsExist(user);
        } else if (StringUtils.isNotBlank(phone)) {
            return dataChecker.checkPhoneExist(user);
        }
        return checker.check4login(user);
    }

    @Override
    public PamirsUserDTO fetchUserIdByReq() {
        PamirsUserDTO pamirsUserDTO = super.fetchUserIdByReq();
        if (pamirsUserDTO == null || pamirsUserDTO.getUserId() == null) {
            return pamirsUserDTO;
        }
        PamirsUser user = UserInfoCache.queryUserById(pamirsUserDTO.getUserId());
        if (user != null && !Boolean.TRUE.equals(user.getActive())) {
            //清理下登录的cookie
            logout();

            log.error("{} current user is {},{}", UserExpEnumerate.USER_CAN_NOT_ACTIVE_ERROR, pamirsUserDTO.getUserId(), pamirsUserDTO.getLogin());
            throw PamirsException.construct(UserExpEnumerate.USER_CAN_NOT_ACTIVE_ERROR).errThrow();
        }
        return pamirsUserDTO;
    }

}
