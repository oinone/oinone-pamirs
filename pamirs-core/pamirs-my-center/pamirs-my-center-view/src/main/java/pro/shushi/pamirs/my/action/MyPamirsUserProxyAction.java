package pro.shushi.pamirs.my.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.faas.utils.ArgUtils;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.my.pmodel.MyPamirsUserProxy;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.service.UserService;

@Base
@Component
@Model.model(MyPamirsUserProxy.MODEL_MODEL)
public class MyPamirsUserProxyAction {

    @Autowired
    private UserService userService;

    @Function(openLevel = FunctionOpenEnum.API)
    @Function.Advanced(displayName = "初始化数据", type = FunctionTypeEnum.QUERY)
    public MyPamirsUserProxy construct(MyPamirsUserProxy data) {
        Long userId = PamirsSession.getUserId();
        if (userId == null) {
            return null;
        }
        return new MyPamirsUserProxy().setId(userId).queryById();
    }

    @Action(displayName = "修改个人信息")
    public MyPamirsUserProxy modifyUserInfo(MyPamirsUserProxy data) {
        PamirsUser result = userService.modifyUserInfo(data);
        return ArgUtils.convert(PamirsUser.MODEL_MODEL, MyPamirsUserProxy.MODEL_MODEL, result);
    }
}
