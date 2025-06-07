package pro.shushi.pamirs.user.view.action;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.api.service.AuthRoleService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserRoleTransient;
import pro.shushi.pamirs.user.api.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author xzf 2023/1/9 14:57
 */
@Slf4j
@Component
@Model.model(PamirsUserRoleTransient.MODEL_MODEL)
public class PamirsUserRoleAction {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthRoleService authRoleService;

    @Function(openLevel = FunctionOpenEnum.API)
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public PamirsUserRoleTransient construct(PamirsUserRoleTransient data, List<PamirsUser> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return data;
        }
        List<Long> userIds = userList.stream().map(PamirsUser::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(userIds)) {
            return data;
        }

        userList = Models.origin().queryListByWrapper(
                Pops.<PamirsUser>lambdaQuery().from(PamirsUser.MODEL_MODEL).in(PamirsUser::getId, userIds)
        );
        data.setUserList(userList);
        return data;
    }

    @Action(displayName = "确定", summary = "修改用户角色")
    public PamirsUserRoleTransient modifyUserRole(PamirsUserRoleTransient data) {
        List<PamirsUser> users = data.getUserList();
        if (CollectionUtils.isEmpty(users)) {
            return data;
        }
        List<AuthRole> roles = data.getRoleList();
        if (CollectionUtils.isEmpty(roles)) {
            return data;
        }

        Set<Long> userIds = users.stream().map(PamirsUser::getId).collect(Collectors.toSet());
        List<PamirsUser> originUsers = userService.queryListByIds(new ArrayList<>(userIds));
        if (CollectionUtils.isEmpty(originUsers)) {
            return data;
        }

        Set<Long> roleIds = roles.stream().map(AuthRole::getId).collect(Collectors.toSet());
        List<AuthRole> originRoles = authRoleService.fetchRoles(roleIds);
        if (CollectionUtils.isEmpty(originRoles)) {
            return data;
        }

        userService.bindUserRole(originUsers, originRoles);
        return data;
    }
}

