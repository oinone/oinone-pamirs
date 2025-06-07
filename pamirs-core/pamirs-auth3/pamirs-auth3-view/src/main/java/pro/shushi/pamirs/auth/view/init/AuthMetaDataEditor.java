package pro.shushi.pamirs.auth.view.init;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.AuthModule;
import pro.shushi.pamirs.auth.api.model.AuthCustomGroup;
import pro.shushi.pamirs.auth.api.model.AuthGroup;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.api.model.AuthRoleType;
import pro.shushi.pamirs.auth.view.model.AuthCustomResourcePermissionItem;
import pro.shushi.pamirs.auth.view.model.AuthResourcePermissionItem;
import pro.shushi.pamirs.auth.view.model.AuthRowPermissionItem;
import pro.shushi.pamirs.auth.view.pmodel.AuthGroupResourcePermissionProxy;
import pro.shushi.pamirs.auth.view.pmodel.AuthGroupSystemPermissionProxy;
import pro.shushi.pamirs.auth.view.pmodel.AuthRoleProxy;
import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.extend.MetaDataEditor;
import pro.shushi.pamirs.core.common.InitializationUtil;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.util.Map;

/**
 * 权限模块元数据编辑
 *
 * @author Adamancy Zhang at 11:28 on 2024-01-09
 */
@Component
public class AuthMetaDataEditor implements MetaDataEditor {

    @Override
    public void edit(AppLifecycleCommand command, Map<String, Meta> metaMap) {
        InitializationUtil util = InitializationUtil.get(metaMap, AuthModule.MODULE_MODULE, AuthModule.MODULE_NAME);
        if (util == null) {
            return;
        }
        initAuth(util);
    }

    private void initAuth(InitializationUtil util) {
        viewActionInit(util);
        viewActionModify(util);
    }

    private void viewActionInit(InitializationUtil util) {
        util.createViewAction("permissionCreate", "新增权限项", AuthResourcePermissionItem.MODEL_MODEL, Lists.newArrayList(ViewTypeEnum.TABLE), AuthResourcePermissionItem.MODEL_MODEL, ViewTypeEnum.FORM,
                ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.ROUTER, "权限项自定义form");
        util.createViewAction("permissionUpdate", "编辑权限项", AuthResourcePermissionItem.MODEL_MODEL, Lists.newArrayList(ViewTypeEnum.TABLE), AuthResourcePermissionItem.MODEL_MODEL, ViewTypeEnum.FORM,
                ActionContextTypeEnum.SINGLE, ActionTargetEnum.ROUTER, "权限项自定义form");

        util.createViewAction("authGroupManagement", "基础管理模式", AuthRole.MODEL_MODEL, Lists.newArrayList(ViewTypeEnum.TABLE), AuthGroup.MODEL_MODEL, ViewTypeEnum.FORM,
                ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.ROUTER, "权限组系统设置Custom");

        util.createViewAction("authGroupPermissionDialog", "基础管理模式", AuthResourcePermissionItem.MODEL_MODEL, Lists.newArrayList(ViewTypeEnum.TABLE), AuthResourcePermissionItem.MODEL_MODEL, ViewTypeEnum.FORM,
                ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, "权限组系统设置Custom");

        util.createViewAction("authRoleDetail", "角色详情", AuthRoleProxy.MODEL_MODEL, Lists.newArrayList(ViewTypeEnum.TABLE), AuthRoleProxy.MODEL_MODEL, ViewTypeEnum.DETAIL,
                ActionContextTypeEnum.SINGLE, ActionTargetEnum.DRAWER, "系统权限-角色详情detail");

        // 系统权限
        util.createViewAction("authGroupSystemPermissionLookup", "查看", AuthGroupSystemPermissionProxy.MODEL_MODEL, Lists.newArrayList(ViewTypeEnum.TABLE), AuthGroupSystemPermissionProxy.MODEL_MODEL, ViewTypeEnum.DETAIL,
                ActionContextTypeEnum.SINGLE, ActionTargetEnum.ROUTER, "SystemAuthGroupSettingDetail");

        // 资源权限
        util.createViewAction("authGroupResourcePermissionLookup", "详情", AuthGroupResourcePermissionProxy.MODEL_MODEL, Lists.newArrayList(ViewTypeEnum.TABLE), AuthGroupResourcePermissionProxy.MODEL_MODEL, ViewTypeEnum.FORM,
                ActionContextTypeEnum.SINGLE, ActionTargetEnum.ROUTER, "AuthGroupResourcePermissionDetail");

        // 数据权限 - 数据权限项
        util.createViewAction("authGroupDataPermissionAddPermission", "新增权限项", AuthRowPermissionItem.MODEL_MODEL, Lists.newArrayList(ViewTypeEnum.TABLE), AuthRowPermissionItem.MODEL_MODEL, ViewTypeEnum.TABLE,
                        ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, "AuthGroupDataPermissionForm_PermissionTable")
                .setFilter("source == 'MANUAL'");
        util.createViewAction("authGroupDataPermissionCreatePermission", "创建权限项", AuthRowPermissionItem.MODEL_MODEL, Lists.newArrayList(ViewTypeEnum.TABLE), AuthRowPermissionItem.MODEL_MODEL, ViewTypeEnum.FORM,
                ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, "AuthGroupDataPermissionForm_PermissionForm");
        util.createViewAction("authGroupDataPermissionEditPermission", "编辑权限项", AuthRowPermissionItem.MODEL_MODEL, Lists.newArrayList(ViewTypeEnum.TABLE), AuthRowPermissionItem.MODEL_MODEL, ViewTypeEnum.FORM,
                ActionContextTypeEnum.SINGLE, ActionTargetEnum.DIALOG, "AuthGroupDataPermissionForm_PermissionForm");

        // 数据权限 - 角色
        util.createViewAction("authGroupDataPermissionAddRole", "新增角色", AuthRole.MODEL_MODEL, Lists.newArrayList(ViewTypeEnum.TABLE), AuthRole.MODEL_MODEL, ViewTypeEnum.TABLE,
                ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, "AuthGroupDataPermissionForm_RoleTable");
    }

    private void viewActionModify(InitializationUtil util) {
        // 角色类型
        util.modifyViewAction(AuthRoleType.MODEL_MODEL, InitializationUtil.DEFAULT_CREATE, viewAction -> viewAction.setResViewName("AuthRoleTypeForm"));
        util.modifyViewAction(AuthRoleType.MODEL_MODEL, InitializationUtil.DEFAULT_UPDATE, viewAction -> viewAction.setResViewName("AuthRoleTypeForm"));
        util.modifyViewAction(AuthRoleType.MODEL_MODEL, InitializationUtil.DEFAULT_DETAIL, viewAction -> viewAction.setResViewName("AuthRoleTypeDetail"));

        // 角色管理
        util.modifyViewAction(AuthRole.MODEL_MODEL, InitializationUtil.DEFAULT_CREATE, viewAction -> viewAction.setResViewName("AuthRoleForm"));
        util.modifyViewAction(AuthRole.MODEL_MODEL, InitializationUtil.DEFAULT_UPDATE, viewAction -> viewAction.setResViewName("AuthRoleForm"));
        util.modifyViewAction(AuthRole.MODEL_MODEL, InitializationUtil.DEFAULT_DETAIL, viewAction -> viewAction.setResViewName("AuthRoleDetail"));

        // 数据权限项
        util.modifyViewAction(AuthRowPermissionItem.MODEL_MODEL, InitializationUtil.DEFAULT_CREATE, viewAction -> viewAction.setResViewName("AuthRowPermissionItemForm"));
        util.modifyViewAction(AuthRowPermissionItem.MODEL_MODEL, InitializationUtil.DEFAULT_UPDATE, viewAction -> viewAction.setResViewName("AuthRowPermissionItemForm"));
        util.modifyViewAction(AuthRowPermissionItem.MODEL_MODEL, InitializationUtil.DEFAULT_DETAIL, viewAction -> viewAction.setResViewName("AuthRowPermissionItemDetail"));

        // 自定义权限组
        util.modifyViewAction(AuthCustomGroup.MODEL_MODEL, InitializationUtil.DEFAULT_CREATE, viewAction -> viewAction.setResViewName("AuthCustomGroupForm"));
        util.modifyViewAction(AuthCustomGroup.MODEL_MODEL, InitializationUtil.DEFAULT_UPDATE, viewAction -> viewAction.setResViewName("AuthCustomGroupForm"));
        util.modifyViewAction(AuthCustomGroup.MODEL_MODEL, InitializationUtil.DEFAULT_DETAIL, viewAction -> viewAction.setResViewName("AuthCustomGroupDetail"));

        // 自定义资源权限项
        util.modifyViewAction(AuthCustomResourcePermissionItem.MODEL_MODEL, InitializationUtil.DEFAULT_CREATE, viewAction -> viewAction.setResViewName("AuthCustomResourcePermissionItemForm"));
        util.modifyViewAction(AuthCustomResourcePermissionItem.MODEL_MODEL, InitializationUtil.DEFAULT_UPDATE, viewAction -> viewAction.setResViewName("AuthCustomResourcePermissionItemForm"));
        util.modifyViewAction(AuthCustomResourcePermissionItem.MODEL_MODEL, InitializationUtil.DEFAULT_DETAIL, viewAction -> viewAction.setResViewName("AuthCustomResourcePermissionItemDetail"));
    }
}
