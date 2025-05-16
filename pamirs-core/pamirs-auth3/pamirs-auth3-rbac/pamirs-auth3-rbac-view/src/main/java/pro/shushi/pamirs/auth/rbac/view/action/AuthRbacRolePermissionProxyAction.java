package pro.shushi.pamirs.auth.rbac.view.action;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.enumeration.AuthExpEnumerate;
import pro.shushi.pamirs.core.common.api.EditionService;
import pro.shushi.pamirs.auth.rbac.api.model.AuthRbacFieldPermissionModelSelect;
import pro.shushi.pamirs.auth.rbac.api.pmodel.AuthRbacRolePermissionProxy;
import pro.shushi.pamirs.auth.rbac.api.service.AuthRbacRolePermissionService;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.constant.ExpConstants;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.FunctionCategoryEnum;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.util.Optional;

/**
 * 角色权限动作
 *
 * @author Adamancy Zhang at 14:14 on 2024-08-09
 */
@Base
@Component
@Model.model(AuthRbacRolePermissionProxy.MODEL_MODEL)
public class AuthRbacRolePermissionProxyAction {

    @Autowired
    private AuthRbacRolePermissionService authRbacRolePermissionService;

    @Autowired
    private EditionService editionService;

    @Function(openLevel = FunctionOpenEnum.API, summary = "初始化")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public AuthRbacRolePermissionProxy construct(AuthRbacRolePermissionProxy data) {
        boolean edition = editionService.checkEdition();
        data.setEnterpriseEdition(edition);
        return data;
    }

    @Function.Advanced(displayName = "查询指定角色", type = FunctionTypeEnum.QUERY, category = FunctionCategoryEnum.QUERY_ONE, managed = true)
    @Function.fun(FunctionConstants.queryByEntity)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public AuthRbacRolePermissionProxy queryOne(AuthRbacRolePermissionProxy query) {
        AuthRbacRolePermissionProxy origin = authRbacRolePermissionService.queryOne(query);
        if (origin == null) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_ROLE_ERROR).errThrow();
        }
        origin.setResourcePermissions(null);
        origin.setManagementPermissions(null);
        origin.setCustomResourcePermissions(null);
        origin.setFieldPermissionModelSelect(null);
        origin.setFieldPermissions(null);
        boolean edition = editionService.checkEdition();
        if (edition) {
            origin.setRowPermissions(authRbacRolePermissionService.queryRowPermissions(origin));
        }
        origin.setEnterpriseEdition(edition);
        return origin;
    }

    @Action.Advanced(type = FunctionTypeEnum.UPDATE, managed = true, invisible = ExpConstants.idValueNotExist)
    @Action(displayName = "更新", label = "确定", summary = "修改", bindingType = ViewTypeEnum.FORM)
    public AuthRbacRolePermissionProxy update(AuthRbacRolePermissionProxy data) {
        return authRbacRolePermissionService.update(data);
    }

    @Function.Advanced(displayName = "查询字段权限", type = FunctionTypeEnum.QUERY)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.API})
    public AuthRbacRolePermissionProxy queryFieldPermissions(AuthRbacRolePermissionProxy query) {
        Long roleId = query.getId();
        String model = Optional.ofNullable(query.getFieldPermissionModelSelect())
                .map(AuthRbacFieldPermissionModelSelect::getModel)
                .orElse(null);
        if (roleId == null || StringUtils.isBlank(model)) {
            query.setFieldPermissions(null);
            return query;
        }
        AuthRbacRolePermissionProxy result = new AuthRbacRolePermissionProxy();
        result.setFieldPermissions(authRbacRolePermissionService.queryFieldPermissions(roleId, model));
        return result;
    }

}
