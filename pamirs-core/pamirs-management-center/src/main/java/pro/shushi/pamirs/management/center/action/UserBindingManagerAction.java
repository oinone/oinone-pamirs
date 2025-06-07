package pro.shushi.pamirs.management.center.action;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.management.center.service.RoleUserManagerService;
import pro.shushi.pamirs.management.center.tmodel.UserBindingManager;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;
import pro.shushi.pamirs.user.api.model.PamirsUser;

import java.util.List;

/**
 * @author WuXin at 10:09 on 2025/1/9
 */
@Slf4j
@Component
@Model.model(UserBindingManager.MODEL_MODEL)
public class UserBindingManagerAction {

    @Autowired
    private RoleUserManagerService roleUserManagerService;

    @Action.Advanced(type = FunctionTypeEnum.UPDATE)
    @Action(displayName = "更新", label = "确定", summary = "修改", bindingType = ViewTypeEnum.FORM)
    public UserBindingManager bindingUser(UserBindingManager data) {
        List<PamirsUser> bindingUsers = data.getBindingUsers();
        if (CollectionUtils.isEmpty(bindingUsers)) {
            return data;
        }
        return roleUserManagerService.bindingUser(data);
    }

    @Function.Advanced(displayName = "初始化", type = FunctionTypeEnum.QUERY)
    @Function(openLevel = FunctionOpenEnum.API)
    public UserBindingManager construct(UserBindingManager data, List<AuthRole> authRole) {
        if (CollectionUtils.isEmpty(authRole)) {
            return data;
        }
        data.setRoles(authRole);
        return roleUserManagerService.queryRoleById(data);
    }
}
