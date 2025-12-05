package pro.shushi.pamirs.auth.view.action;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.enumeration.AuthExpEnumerate;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.api.service.AuthRoleService;
import pro.shushi.pamirs.auth.api.service.manager.AuthCacheManager;
import pro.shushi.pamirs.auth.api.service.manager.AuthRoleManager;
import pro.shushi.pamirs.core.common.DataShardingHelper;
import pro.shushi.pamirs.core.common.function.FunctionConstant;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.constant.ExpConstants;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.*;
import pro.shushi.pamirs.ux.common.utils.WrapperHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 角色动作
 *
 * @author Adamancy Zhang at 13:57 on 2024-01-09
 */
@Component
@Model.model(AuthRole.MODEL_MODEL)
public class AuthRoleAction {

    @Autowired
    private AuthRoleService authRoleService;

    @Autowired
    private AuthRoleManager authRoleManager;

    @Autowired
    private AuthCacheManager authCacheManager;

    @Function.Advanced(displayName = "查询角色列表", type = FunctionTypeEnum.QUERY, category = FunctionCategoryEnum.QUERY_PAGE, managed = true)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public Pagination<AuthRole> queryPage(Pagination<AuthRole> page, IWrapper<AuthRole> queryWrapper) {
        return authRoleService.queryPage(page, WrapperHelper.lambda(queryWrapper)
                .ne(AuthRole::getSource, AuthorizationSourceEnum.BUILD_IN.value()));
    }

    @Function.Advanced(displayName = "根据条件查询记录列表", type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(FunctionConstants.queryListByWrapper)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public List<AuthRole> queryListByWrapper(IWrapper<AuthRole> queryWrapper) {
        return authRoleService.queryListByWrapper(WrapperHelper.lambda(queryWrapper)
                .ne(AuthRole::getSource, AuthorizationSourceEnum.BUILD_IN.value()));
    }

    @Function.Advanced(displayName = "查询指定角色", type = FunctionTypeEnum.QUERY, category = FunctionCategoryEnum.QUERY_ONE, managed = true)
    @Function.fun(FunctionConstants.queryByEntity)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public AuthRole queryOne(AuthRole query) {
        AuthRole origin = authRoleService.queryOne(query);
        if (origin == null) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_ROLE_ERROR).errThrow();
        }
        return origin;
    }

    @Action.Advanced(type = FunctionTypeEnum.CREATE, managed = true, invisible = ExpConstants.idValueExist)
    @Action(displayName = "创建", label = "确定", summary = "添加", bindingType = ViewTypeEnum.FORM)
    public AuthRole create(AuthRole data) {
        return authRoleService.create(data);
    }

    @Action.Advanced(type = FunctionTypeEnum.UPDATE, managed = true, invisible = ExpConstants.idValueNotExist)
    @Action(displayName = "更新", label = "确定", summary = "修改", bindingType = ViewTypeEnum.FORM)
    public AuthRole update(AuthRole data) {
        return authRoleService.update(data);
    }

    @Action.Advanced(type = FunctionTypeEnum.DELETE, managed = true, priority = 66)
    @Action(displayName = "删除", label = "删除", contextType = ActionContextTypeEnum.SINGLE_AND_BATCH)
    @Function.fun(FunctionConstant.deleteWithFieldBatch)
    public List<AuthRole> delete(List<AuthRole> dataList) {
        List<AuthRole> existRoles = queryExistRoles(dataList);
        if (CollectionUtils.isEmpty(existRoles)) {
            return new ArrayList<>();
        }
        authRoleManager.delete(existRoles);
        return existRoles;
    }

    @Action.Advanced(invisible = "context.activeRecord.active")
    @Action(displayName = "启用", bindingType = ViewTypeEnum.TABLE)
    public AuthRole active(AuthRole data) {
        AuthRole origin = queryOne(data);
        authRoleManager.active(origin);
        return origin;
    }

    @Action.Advanced(invisible = "!context.activeRecord.active")
    @Action(displayName = "禁用", bindingType = ViewTypeEnum.TABLE)
    public AuthRole disable(AuthRole data) {
        AuthRole origin = queryOne(data);
        authRoleManager.disable(origin);
        return origin;
    }

    @Action(displayName = "权限生效", bindingType = ViewTypeEnum.TABLE, contextType = ActionContextTypeEnum.SINGLE_AND_BATCH)
    public List<AuthRole> refreshCache(List<AuthRole> roles) {
        List<AuthRole> existRoles = queryExistRoles(roles);
        if (CollectionUtils.isEmpty(existRoles)) {
            return new ArrayList<>();
        }
        authCacheManager.refresh(existRoles);
        return existRoles;
    }

    @Action(displayName = "全部权限生效", bindingType = ViewTypeEnum.TABLE, contextType = ActionContextTypeEnum.CONTEXT_FREE)
    public AuthRole refreshAllCache(AuthRole data) {
        authCacheManager.refreshAll();
        return data;
    }

    private List<AuthRole> queryExistRoles(List<AuthRole> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return new ArrayList<>();
        }
        Set<Long> ids = new HashSet<>();
        for (AuthRole role : roles) {
            ids.add(assertId(role.getId()));
        }
        return DataShardingHelper.build().collectionSharding(ids, (sublist) -> authRoleService.queryListByWrapper(Pops.<AuthRole>lambdaQuery()
                .from(AuthRole.MODEL_MODEL)
                .in(AuthRole::getId, sublist)));
    }

    private Long assertId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Invalid id value.");
        }
        return id;
    }
}
