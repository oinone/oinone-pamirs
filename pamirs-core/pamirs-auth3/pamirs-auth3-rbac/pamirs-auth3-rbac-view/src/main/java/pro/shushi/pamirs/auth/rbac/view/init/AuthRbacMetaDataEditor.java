package pro.shushi.pamirs.auth.rbac.view.init;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.AuthModule;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.rbac.api.model.AuthRbacRowPermissionItem;
import pro.shushi.pamirs.auth.rbac.api.pmodel.AuthRbacRolePermissionProxy;
import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.extend.MetaDataEditor;
import pro.shushi.pamirs.core.common.InitializationUtil;
import pro.shushi.pamirs.core.common.MapHelper;
import pro.shushi.pamirs.core.common.function.FunctionConstant;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.util.Map;

/**
 * RBAC权限模块元数据编辑
 *
 * @author Adamancy Zhang at 12:34 on 2024-08-09
 */
@Component
public class AuthRbacMetaDataEditor implements MetaDataEditor {

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
        util.createViewAction("authRolePermissionEdit", "权限配置",
                AuthRole.MODEL_MODEL,
                Lists.newArrayList(ViewTypeEnum.TABLE),
                AuthRbacRolePermissionProxy.MODEL_MODEL,
                ViewTypeEnum.FORM,
                ActionContextTypeEnum.SINGLE,
                ActionTargetEnum.ROUTER,
                "AuthRbacRolePermissionForm", null,
                (v) -> v.setLoad(FunctionConstant.queryByEntity)
                        .setInvisible("activeRecord.source == BUILD_IN")
                        .setContext(MapHelper.<String, Object>newInstance()
                                .put("id", "activeRecord.id")
                                .build()));

        // 权限配置 - 数据权限
        util.createViewAction("authRolePermissionAddRowPermission", "新增权限项", AuthRbacRowPermissionItem.MODEL_MODEL, Lists.newArrayList(ViewTypeEnum.TABLE), AuthRbacRowPermissionItem.MODEL_MODEL, ViewTypeEnum.TABLE,
                        ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, "AuthRbacRolePermissionForm_RowPermissionTable")
                .setFilter("source == 'MANUAL'");
    }

    private void viewActionModify(InitializationUtil util) {
    }
}
