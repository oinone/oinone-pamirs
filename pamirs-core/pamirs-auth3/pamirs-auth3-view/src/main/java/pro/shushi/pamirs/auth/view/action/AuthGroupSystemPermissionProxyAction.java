package pro.shushi.pamirs.auth.view.action;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.enmu.AuthGroupTypeEnum;
import pro.shushi.pamirs.auth.api.enumeration.AuthExpEnumerate;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.view.pmodel.AuthGroupSystemPermissionProxy;
import pro.shushi.pamirs.auth.view.service.AuthGroupService;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.constant.ExpConstants;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 系统权限权限组动作
 *
 * @author Adamancy Zhang at 22:07 on 2024-01-16
 */
@Component
@Model.model(AuthGroupSystemPermissionProxy.MODEL_MODEL)
public class AuthGroupSystemPermissionProxyAction {

    @Autowired
    private AuthGroupService authGroupService;

    @Action.Advanced(managed = true, invisible = ExpConstants.idValueExist)
    @Action(displayName = "创建权限组", label = "确定", summary = "添加", bindingType = ViewTypeEnum.FORM)
    public AuthGroupSystemPermissionProxy create(AuthGroupSystemPermissionProxy data) {
        verifyIsManagement(data);
        return authGroupService.createSystemPermissionGroup(data);
    }

    @Action.Advanced(managed = true, invisible = ExpConstants.idValueNotExist)
    @Action(displayName = "更新权限组", label = "确定", summary = "修改", bindingType = ViewTypeEnum.FORM)
    public AuthGroupSystemPermissionProxy update(AuthGroupSystemPermissionProxy data) {
        verifyIsManagement(data);
        return authGroupService.updateSystemPermissionGroup(data);
    }

    @Action(displayName = "删除权限组", bindingType = ViewTypeEnum.TABLE)
    @Function.Advanced(type = FunctionTypeEnum.DELETE)
    public AuthGroupSystemPermissionProxy deleteOne(AuthGroupSystemPermissionProxy data) {
        AuthGroupSystemPermissionProxy origin = fetchOne(data);
        verifyIsManagement(origin);
        Long groupId = origin.getId();
        authGroupService.delete(groupId);
        AuthGroupSystemPermissionProxy result = new AuthGroupSystemPermissionProxy();
        result.setId(groupId);
        return result;
    }

    @Action(displayName = "启用", bindingType = ViewTypeEnum.TABLE)
    @Action.Advanced(invisible = "activeRecord.active")
    public AuthGroupSystemPermissionProxy active(AuthGroupSystemPermissionProxy data) {
        AuthGroupSystemPermissionProxy origin = fetchOne(data);
        verifyIsManagement(origin);
        if (authGroupService.active(origin.getId())) {
            origin.setActive(Boolean.TRUE);
        }
        return origin;
    }

    @Action(displayName = "禁用", bindingType = ViewTypeEnum.TABLE)
    @Action.Advanced(invisible = "!activeRecord.active")
    public AuthGroupSystemPermissionProxy disable(AuthGroupSystemPermissionProxy data) {
        AuthGroupSystemPermissionProxy origin = fetchOne(data);
        verifyIsManagement(origin);
        if (authGroupService.disable(origin.getId())) {
            origin.setActive(Boolean.FALSE);
        }
        return origin;
    }

    @Action(displayName = "修改访问权限组的角色", bindingType = ViewTypeEnum.TABLE)
    public AuthGroupSystemPermissionProxy modifyRole(AuthGroupSystemPermissionProxy data) {
        AuthGroupSystemPermissionProxy origin = fetchOne(data);
        if (!AuthGroupTypeEnum.RUNTIME.equals(origin.getType())) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_GROUP_TYPE_ERROR).errThrow();
        }
        return modifyRoles(data, origin);
    }

    @Action(displayName = "修改管理权限组的角色", bindingType = ViewTypeEnum.TABLE)
    public AuthGroupSystemPermissionProxy modifyManagementRole(AuthGroupSystemPermissionProxy data) {
        AuthGroupSystemPermissionProxy origin = fetchOne(data);
        if (!AuthGroupTypeEnum.MANAGEMENT.equals(origin.getType())) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_GROUP_TYPE_ERROR).errThrow();
        }
        return modifyRoles(data, origin);
    }

    private AuthGroupSystemPermissionProxy modifyRoles(AuthGroupSystemPermissionProxy data, AuthGroupSystemPermissionProxy origin) {
        verifyIsManagement(origin);
        List<AuthRole> roles = data.getRoles();
        Set<Long> roleIds;
        if (CollectionUtils.isEmpty(roles)) {
            roleIds = new HashSet<>();
        } else {
            roleIds = roles.stream().map(AuthRole::getId).collect(Collectors.toSet());
        }
        List<AuthRole> saveRoles = authGroupService.modifyRoles(origin.getId(), roleIds);
        if (saveRoles == null) {
            saveRoles = new ArrayList<>();
        }
        origin.setRoles(saveRoles);
        return origin;
    }

    private AuthGroupSystemPermissionProxy fetchOne(AuthGroupSystemPermissionProxy query) {
        AuthGroupSystemPermissionProxy origin = FetchUtil.fetchOne(query);
        if (origin == null) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_GROUP_ERROR).errThrow();
        }
        return origin;
    }

    private void verifyIsManagement(AuthGroupSystemPermissionProxy data) {
        if (!authGroupService.verifyIsManagement(data)) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_GROUP_NOT_MANAGEMENT_ERROR).errThrow();
        }
    }
}
