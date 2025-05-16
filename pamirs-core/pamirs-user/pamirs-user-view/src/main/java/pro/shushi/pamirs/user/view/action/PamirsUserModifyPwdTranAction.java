package pro.shushi.pamirs.user.view.action;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.user.api.enmu.UserExpEnumerate;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserModifyPwdTran;
import pro.shushi.pamirs.user.api.service.UserService;
import pro.shushi.pamirs.user.api.utils.RandomPassword;

/**
 * @author shier
 * date  2022/7/4 下午8:18
 */
@Model.model(PamirsUserModifyPwdTran.MODEL_MODEL)
@Component
public class PamirsUserModifyPwdTranAction {

    @Autowired
    private UserService userService;

    @Function(openLevel = FunctionOpenEnum.API)
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public PamirsUserModifyPwdTran construct(PamirsUserModifyPwdTran pamirsUserModifyPwdTran, PamirsUser user) {
        PamirsUserModifyPwdTran result = new PamirsUserModifyPwdTran();
        if(user != null && user.getId() != null ) {
            user = user.queryById();
            result.setId(user.getId());
            result.setLogin(user.getLogin());
        }
        return result;
    }

    @Action(displayName = "重置密码")
    public PamirsUserModifyPwdTran resetPwd(PamirsUserModifyPwdTran data) {
        if (StringUtils.isBlank(data.getLogin())) {
            throw PamirsException.construct(UserExpEnumerate.USER_RESET_PWD_NULL_LOGIN_ERROR).errThrow();
        }
        PamirsUser user = userService.queryById(data.getId());
        if (user == null) {
            throw PamirsException.construct(UserExpEnumerate.USER_RESET_PWD_USER_NOT_EXIST_ERROR).errThrow();
        }
        if (!user.getLogin().equals(data.getConfirmLogin())) {
            throw PamirsException.construct(UserExpEnumerate.USER_RESET_PWD_ERROR_LOGIN_ERROR).errThrow();
        }
        String password = RandomPassword.genRandomNum();
        userService.initialPassword(user.getId(), password);
        data.setResetPassword(password);
        return data;
    }

}
