package pro.shushi.pamirs.user.view.init;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.extend.MetaDataEditor;
import pro.shushi.pamirs.core.common.InitializationUtil;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;
import pro.shushi.pamirs.user.api.UserModule;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserModifyPwdTran;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserRoleTransient;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserTransient;

import java.util.Map;

/**
 * {@link UserModule}元数据编辑
 *
 * @author Adamancy Zhang at 17:21 on 2021-09-06
 */
@Order(100)
@Component
public class UserMetadataEdit implements MetaDataEditor {

    @Override
    public void edit(AppLifecycleCommand command, Map<String, Meta> metaMap) {
        InitializationUtil util = InitializationUtil.get(metaMap, UserModule.MODULE_MODULE, UserModule.MODULE_NAME);
        if (util == null) {
            return;
        }
        initViewAction(util);
    }

    private void initViewAction(InitializationUtil util) {
        util.createViewAction("create_user_view_action", "创建", PamirsUser.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), PamirsUser.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.ROUTER, "create_user_view");
        util.createViewAction("initial_password_view_action", "设置初始化密码", PamirsUser.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), PamirsUser.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.SINGLE, ActionTargetEnum.DIALOG, "修改用户初始化密码");
        util.createViewAction("reset_password_view_action", "重置密码", PamirsUser.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), PamirsUserModifyPwdTran.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.SINGLE, ActionTargetEnum.DIALOG, "modify_pwd_dialog_view");
        util.createViewAction("modify_initial_pwd_view_action", "修改初始密码", PamirsUserTransient.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.FORM), PamirsUserTransient.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.ROUTER, "modify_initial_pwd_view");
        util.createViewAction("modify_pwd_view_action", "修改密码", PamirsUserTransient.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.FORM), PamirsUserTransient.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.DIALOG, "modify_pwd_view");
        util.createViewAction("modify_user_info_action", "修改用户基础信息", PamirsUser.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), PamirsUser.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.SINGLE, ActionTargetEnum.ROUTER, "modify_user_info");
        util.createViewAction("modify_user_role_action", "修改用户角色", PamirsUser.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), PamirsUserRoleTransient.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.SINGLE_AND_BATCH, ActionTargetEnum.DIALOG, "modify_user_role");
    }
}
