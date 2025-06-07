package pro.shushi.pamirs.management.center.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.management.center.service.RoleUserManagerService;
import pro.shushi.pamirs.management.center.tmodel.RoleUserManager;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

/**
 * @author WuXin at 16:23 on 2025/1/8
 */
@Slf4j
@Component
@Model.model(RoleUserManager.MODEL_MODEL)
public class RoleUserManagerAction {

    @Autowired
    private RoleUserManagerService roleUserManagerService;

    @Action.Advanced(type = FunctionTypeEnum.UPDATE)
    @Action(displayName = "更新", label = "确定", summary = "修改", bindingType = ViewTypeEnum.FORM)
    public RoleUserManager updateRoleForUser(RoleUserManager data) {
        return roleUserManagerService.updateRoleForUser(data);
    }

    @Function.Advanced(displayName = "初始化", type = FunctionTypeEnum.QUERY)
    @Function(openLevel = FunctionOpenEnum.API)
    public RoleUserManager construct(RoleUserManager data, AuthRole authRole) {
        Long roleId = authRole.getId();
        if (roleId == null) {
            return data;
        }
        data.setRoleId(roleId);
        return roleUserManagerService.queryRoleUserById(data);
    }
}
