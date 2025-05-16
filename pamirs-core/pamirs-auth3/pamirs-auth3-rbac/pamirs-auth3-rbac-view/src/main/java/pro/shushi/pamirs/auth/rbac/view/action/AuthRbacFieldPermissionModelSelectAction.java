package pro.shushi.pamirs.auth.rbac.view.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.rbac.api.model.AuthRbacFieldPermissionModelSelect;
import pro.shushi.pamirs.auth.rbac.api.service.AuthRbacFieldPermissionModelSelectService;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.enmu.FunctionCategoryEnum;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

/**
 * 字段权限模型选择动作
 *
 * @author Adamancy Zhang at 10:14 on 2024-08-23
 */
@Base
@Component
@Model.model(AuthRbacFieldPermissionModelSelect.MODEL_MODEL)
public class AuthRbacFieldPermissionModelSelectAction {

    @Autowired
    private AuthRbacFieldPermissionModelSelectService authRbacFieldPermissionModelSelectService;

    @Function.Advanced(displayName = "查询字段权限模型列表", type = FunctionTypeEnum.QUERY, category = FunctionCategoryEnum.QUERY_PAGE, managed = true)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public Pagination<AuthRbacFieldPermissionModelSelect> queryPage(Pagination<AuthRbacFieldPermissionModelSelect> page, IWrapper<AuthRbacFieldPermissionModelSelect> queryWrapper) {
        return authRbacFieldPermissionModelSelectService.queryPage(page, queryWrapper);
    }
}
