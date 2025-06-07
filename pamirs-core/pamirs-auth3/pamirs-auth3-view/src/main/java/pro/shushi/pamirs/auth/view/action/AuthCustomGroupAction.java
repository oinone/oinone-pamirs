package pro.shushi.pamirs.auth.view.action;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.enumeration.AuthExpEnumerate;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.model.AuthCustomGroup;
import pro.shushi.pamirs.auth.api.service.group.AuthCustomGroupService;
import pro.shushi.pamirs.core.common.DataShardingHelper;
import pro.shushi.pamirs.core.common.WrapperHelper;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 自定义权限组动作
 *
 * @author Adamancy Zhang at 14:34 on 2024-08-21
 */
@Component
@Model.model(AuthCustomGroup.MODEL_MODEL)
public class AuthCustomGroupAction {

    @Autowired
    private AuthCustomGroupService authCustomGroupService;

    @Function.Advanced(displayName = "查询自定义权限组列表", type = FunctionTypeEnum.QUERY, category = FunctionCategoryEnum.QUERY_PAGE, managed = true)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public Pagination<AuthCustomGroup> queryPage(Pagination<AuthCustomGroup> page, IWrapper<AuthCustomGroup> queryWrapper) {
        return authCustomGroupService.queryPage(page, WrapperHelper.lambda(queryWrapper)
                .ne(AuthCustomGroup::getSource, AuthorizationSourceEnum.BUILD_IN.value()));
    }

    @Function.Advanced(displayName = "查询指定自定义权限组", type = FunctionTypeEnum.QUERY, category = FunctionCategoryEnum.QUERY_ONE, managed = true)
    @Function.fun(FunctionConstants.queryByEntity)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public AuthCustomGroup queryOne(AuthCustomGroup query) {
        AuthCustomGroup origin = authCustomGroupService.queryOne(query);
        if (origin == null) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_ROLE_ERROR).errThrow();
        }
        return origin;
    }

    @Action.Advanced(type = FunctionTypeEnum.CREATE, managed = true, invisible = ExpConstants.idValueExist)
    @Action(displayName = "创建", label = "确定", summary = "添加", bindingType = ViewTypeEnum.FORM)
    public AuthCustomGroup create(AuthCustomGroup data) {
        return authCustomGroupService.create(data);
    }

    @Action.Advanced(type = FunctionTypeEnum.UPDATE, managed = true, invisible = ExpConstants.idValueNotExist)
    @Action(displayName = "更新", label = "确定", summary = "修改", bindingType = ViewTypeEnum.FORM)
    public AuthCustomGroup update(AuthCustomGroup data) {
        return authCustomGroupService.update(data);
    }

    @Action.Advanced(type = FunctionTypeEnum.DELETE, managed = true, priority = 66)
    @Action(displayName = "删除", label = "删除", contextType = ActionContextTypeEnum.SINGLE_AND_BATCH)
    @Function.fun(FunctionConstant.deleteWithFieldBatch)
    public List<AuthCustomGroup> delete(List<AuthCustomGroup> dataList) {
        List<AuthCustomGroup> existGroups = queryExistGroups(dataList);
        if (CollectionUtils.isEmpty(existGroups)) {
            return new ArrayList<>();
        }
        authCustomGroupService.delete(existGroups);
        return existGroups;
    }

    private List<AuthCustomGroup> queryExistGroups(List<AuthCustomGroup> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return new ArrayList<>();
        }
        Set<Long> ids = new HashSet<>();
        for (AuthCustomGroup role : roles) {
            ids.add(assertId(role.getId()));
        }
        return DataShardingHelper.build().collectionSharding(ids, (sublist) -> authCustomGroupService.queryListByWrapper(Pops.<AuthCustomGroup>lambdaQuery()
                .from(AuthCustomGroup.MODEL_MODEL)
                .in(AuthCustomGroup::getId, sublist)));
    }

    private Long assertId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Invalid id value.");
        }
        return id;
    }
}
