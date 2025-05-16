package pro.shushi.pamirs.auth.view.action;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.enumeration.AuthExpEnumerate;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.model.permission.AuthResourcePermission;
import pro.shushi.pamirs.auth.api.service.permission.AuthResourcePermissionService;
import pro.shushi.pamirs.auth.view.model.AuthCustomResourcePermissionItem;
import pro.shushi.pamirs.core.common.DataShardingHelper;
import pro.shushi.pamirs.core.common.WrapperHelper;
import pro.shushi.pamirs.core.common.function.FunctionConstant;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.constant.ExpConstants;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 自定义资源权限项动作
 *
 * @author Adamancy Zhang at 14:23 on 2024-08-21
 */
@Component
@Model.model(AuthCustomResourcePermissionItem.MODEL_MODEL)
public class AuthCustomResourcePermissionItemAction {

    @Autowired
    private AuthResourcePermissionService authResourcePermissionService;

    @Function.Advanced(displayName = "查询自定义资源权限项列表", type = FunctionTypeEnum.QUERY, category = FunctionCategoryEnum.QUERY_PAGE, managed = true)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public Pagination<AuthCustomResourcePermissionItem> queryPage(Pagination<AuthCustomResourcePermissionItem> page, IWrapper<AuthCustomResourcePermissionItem> queryWrapper) {
        return Models.origin().queryPage(page, WrapperHelper.lambda(queryWrapper)
                .eq(AuthCustomResourcePermissionItem::getSource, AuthorizationSourceEnum.MANUAL.value()));
    }

    @Function.Advanced(displayName = "查询指定自定义资源权限项", type = FunctionTypeEnum.QUERY, category = FunctionCategoryEnum.QUERY_ONE, managed = true)
    @Function.fun(FunctionConstants.queryByEntity)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public AuthCustomResourcePermissionItem queryOne(AuthCustomResourcePermissionItem query) {
        AuthCustomResourcePermissionItem origin = Optional.ofNullable(authResourcePermissionService.queryOne(AuthResourcePermission.transfer(query, new AuthResourcePermission())))
                .map(v -> AuthResourcePermission.transfer(v, new AuthCustomResourcePermissionItem()))
                .orElse(null);
        if (origin == null) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_ROLE_ERROR).errThrow();
        }
        return origin;
    }

    @Action.Advanced(type = FunctionTypeEnum.CREATE, managed = true, invisible = ExpConstants.idValueExist)
    @Action(displayName = "创建", label = "确定", summary = "添加", bindingType = ViewTypeEnum.FORM)
    public AuthCustomResourcePermissionItem create(AuthCustomResourcePermissionItem data) {
        AuthResourcePermission result = authResourcePermissionService.create(AuthResourcePermission.transfer(data, new AuthResourcePermission()));
        if (result != null) {
            data = AuthResourcePermission.transfer(result, new AuthCustomResourcePermissionItem());
        }
        return data;
    }

    @Action.Advanced(type = FunctionTypeEnum.UPDATE, managed = true, invisible = ExpConstants.idValueNotExist)
    @Action(displayName = "更新", label = "确定", summary = "修改", bindingType = ViewTypeEnum.FORM)
    public AuthCustomResourcePermissionItem update(AuthCustomResourcePermissionItem data) {
        AuthResourcePermission result = authResourcePermissionService.update(AuthResourcePermission.transfer(data, new AuthResourcePermission()));
        if (result != null) {
            data = AuthResourcePermission.transfer(result, new AuthCustomResourcePermissionItem());
        }
        return data;
    }

    @Action.Advanced(type = FunctionTypeEnum.DELETE, managed = true, priority = 66)
    @Action(displayName = "删除", label = "删除", contextType = ActionContextTypeEnum.SINGLE_AND_BATCH)
    @Function.fun(FunctionConstant.deleteWithFieldBatch)
    public List<AuthCustomResourcePermissionItem> delete(List<AuthCustomResourcePermissionItem> dataList) {
        List<AuthResourcePermission> existResourcePermissions = queryExistResourcePermissions(dataList);
        if (CollectionUtils.isEmpty(existResourcePermissions)) {
            return new ArrayList<>();
        }
        authResourcePermissionService.delete(existResourcePermissions);
        return existResourcePermissions.stream().map(v -> AuthResourcePermission.transfer(v, new AuthCustomResourcePermissionItem())).collect(Collectors.toList());
    }

    private List<AuthResourcePermission> queryExistResourcePermissions(List<AuthCustomResourcePermissionItem> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return new ArrayList<>();
        }
        Set<Long> ids = new HashSet<>();
        for (AuthCustomResourcePermissionItem role : roles) {
            ids.add(assertId(role.getId()));
        }
        return DataShardingHelper.build().collectionSharding(ids, (sublist) -> authResourcePermissionService.queryListByWrapper(Pops.<AuthResourcePermission>lambdaQuery()
                .from(AuthResourcePermission.MODEL_MODEL)
                .in(AuthResourcePermission::getId, sublist)));
    }

    private Long assertId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Invalid id value.");
        }
        return id;
    }
}
