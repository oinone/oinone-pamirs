package pro.shushi.pamirs.auth.view.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.enumeration.AuthExpEnumerate;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.model.AuthRoleType;
import pro.shushi.pamirs.auth.api.service.AuthRoleTypeService;
import pro.shushi.pamirs.core.common.WrapperHelper;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.constant.ExpConstants;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.*;

import java.util.List;

/**
 * 角色类型动作
 *
 * @author Adamancy Zhang at 21:36 on 2024-03-20
 */
@Component
@Model.model(AuthRoleType.MODEL_MODEL)
public class AuthRoleTypeAction {

    @Autowired
    private AuthRoleTypeService authRoleTypeService;

    @Function.Advanced(displayName = "查询角色类型列表", type = FunctionTypeEnum.QUERY, category = FunctionCategoryEnum.QUERY_PAGE, managed = true)
    @Function.fun(FunctionConstants.queryPage)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public Pagination<AuthRoleType> queryPage(Pagination<AuthRoleType> page, IWrapper<AuthRoleType> queryWrapper) {
        return authRoleTypeService.queryPage(page, WrapperHelper.lambda(queryWrapper)
                .ne(AuthRoleType::getSource, AuthorizationSourceEnum.BUILD_IN.value()));
    }

    @Function.Advanced(displayName = "查询指定角色类型", type = FunctionTypeEnum.QUERY, category = FunctionCategoryEnum.QUERY_ONE, managed = true)
    @Function.fun(FunctionConstants.queryByEntity)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public AuthRoleType queryOne(AuthRoleType query) {
        AuthRoleType origin = authRoleTypeService.queryOne(query);
        if (origin == null) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_ROLE_TYPE_ERROR).errThrow();
        }
        return origin;
    }

    @Action.Advanced(name = FunctionConstants.create, managed = true, invisible = ExpConstants.idValueExist)
    @Action(displayName = "创建", label = "确定", summary = "添加", bindingType = ViewTypeEnum.FORM)
    public AuthRoleType create(AuthRoleType data) {
        return authRoleTypeService.create(data);
    }

    @Action.Advanced(name = FunctionConstants.update, managed = true, invisible = ExpConstants.idValueNotExist)
    @Action(displayName = "更新", label = "确定", summary = "修改", bindingType = ViewTypeEnum.FORM)
    public AuthRoleType update(AuthRoleType data) {
        return authRoleTypeService.update(data);
    }

    @Action.Advanced(managed = true)
    @Action(displayName = "删除", label = "删除", contextType = ActionContextTypeEnum.SINGLE_AND_BATCH)
    @Function.fun(FunctionConstants.deleteWithFieldBatch)
    public List<AuthRoleType> delete(List<AuthRoleType> dataList) {
        return authRoleTypeService.delete(dataList);
    }

}
