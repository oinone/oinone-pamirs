package pro.shushi.pamirs.user.api.spi;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.resource.api.constants.DefaultResourceConstants;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.utils.UserPatternCheckUtils;

import javax.annotation.Nullable;


@SPI(factory = SpringServiceLoaderFactory.class)
public interface UserPatternCheckApi {

    //if ("eip_system".equals(login) || "workflow_system".equals(login) || "trigger_system".equals(login))
    default Boolean userPatternCheck(PamirsUser pamirsUser) {
        // 过滤掉系统用户（即系统用户的密码修改不受扩展点影响）8848:eip_system.; 10088L:workflow_system; 10086L:trigger_system
        if (pamirsUser.getId() != null && (8848L == pamirsUser.getId() || 10086L == pamirsUser.getId() || 10088L == pamirsUser.getId())) {
            return Boolean.TRUE;
        }

        UserPatternCheckApi checkApi = Spider.getLoader(UserPatternCheckApi.class).getDefaultExtension();
        Boolean result;
        result = checkApi.checkInitialPassword(pamirsUser.getInitialPassword());
        if (Boolean.FALSE.equals(result)) {
            return result;
        }

        result = checkApi.checkPassword(pamirsUser.getPassword());
        if (Boolean.FALSE.equals(result)) {
            return result;
        }

        result = checkApi.checkEmail(pamirsUser.getEmail());
        if (Boolean.FALSE.equals(result)) {
            return result;
        }

        result = checkApi.checkContactEmail(pamirsUser.getContactEmail());
        if (Boolean.FALSE.equals(result)) {
            return result;
        }

        result = checkApi.checkPhone(pamirsUser.getPhone());
        if (Boolean.FALSE.equals(result)) {
            return result;
        }

        result = checkApi.checkLogin(pamirsUser.getLogin());
        if (Boolean.FALSE.equals(result)) {
            return result;
        }

        result = checkApi.checkName(pamirsUser.getName());
        if (Boolean.FALSE.equals(result)) {
            return result;
        }

        result = checkApi.checkNickName(pamirsUser.getNickname());
        if (Boolean.FALSE.equals(result)) {
            return result;
        }

        result = checkApi.checkRealName(pamirsUser.getRealname());
        if (Boolean.FALSE.equals(result)) {
            return result;
        }

        result = checkApi.checkIdCard(pamirsUser.getIdCard());
        if (Boolean.FALSE.equals(result)) {
            return result;
        }
        return result;
    }

    default Boolean checkInitialPassword(String initPassword) {
        UserPatternCheckUtils.checkPassword(initPassword, Boolean.TRUE);
        return Boolean.TRUE;
    }

    default Boolean checkPassword(String password) {
        UserPatternCheckUtils.checkPassword(password, Boolean.TRUE);
        return Boolean.TRUE;
    }

    default Boolean checkEmail(String email) {
        UserPatternCheckUtils.checkEmail(email, Boolean.TRUE);
        return Boolean.TRUE;
    }

    default Boolean checkContactEmail(String contactEmail) {
        UserPatternCheckUtils.checkEmail(contactEmail, Boolean.TRUE);
        return Boolean.TRUE;
    }

    default Boolean checkLogin(String login) {
        UserPatternCheckUtils.checkLogin(login, Boolean.TRUE);
        return Boolean.TRUE;
    }

    default Boolean checkPhone(String phone) {
        UserPatternCheckUtils.checkPhone(phone, Boolean.TRUE);
        return Boolean.TRUE;
    }

    default Boolean checkPhone(String phone, @Nullable String phoneCode) {
        if (StringUtils.isBlank(phoneCode) || DefaultResourceConstants.CHINESE_PHONE_CODE.equals(phoneCode)) {
            return checkPhone(phone);
        }
        return Boolean.TRUE;
    }

    default Boolean checkName(String name) {
        UserPatternCheckUtils.checkName(name, Boolean.TRUE);
        return Boolean.TRUE;
    }

    default Boolean checkNickName(String nickname) {
        UserPatternCheckUtils.checkNickName(nickname, Boolean.TRUE);
        return Boolean.TRUE;
    }

    default Boolean checkRealName(String realname) {
        UserPatternCheckUtils.checkRealName(realname, Boolean.TRUE);
        return Boolean.TRUE;
    }

    default Boolean checkIdCard(String idCard) {
        UserPatternCheckUtils.checkIdCard(idCard, Boolean.TRUE);
        return Boolean.TRUE;
    }
}
