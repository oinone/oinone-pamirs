package pro.shushi.pamirs.file.api.auth;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.entity.node.ActionPermissionNode;
import pro.shushi.pamirs.auth.api.extend.load.PermissionNodeConvertExtendApi;
import pro.shushi.pamirs.auth.api.helper.AuthExtendHelper;
import pro.shushi.pamirs.auth.api.loader.visitor.AuthCompileContext;
import pro.shushi.pamirs.boot.base.constants.ClientActionConstants;
import pro.shushi.pamirs.boot.base.constants.ViewActionConstants;
import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.base.ux.model.view.UIAction;
import pro.shushi.pamirs.boot.base.ux.model.view.UIField;
import pro.shushi.pamirs.file.api.action.ExcelExportTaskAction;
import pro.shushi.pamirs.file.api.action.ExcelImportTaskAction;
import pro.shushi.pamirs.file.api.model.ExcelExportTask;
import pro.shushi.pamirs.file.api.model.ExcelImportTask;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.util.Optional;

/**
 * 导入/导出权限节点转换扩展
 *
 * @author Adamancy Zhang at 09:22 on 2024-03-08
 */
@Component
@Order(88)
public class FilePermissionNodeConvertExtend implements PermissionNodeConvertExtendApi {

    private static final String IMPORT_TASK_ACTION_NAME = LambdaUtil.fetchMethodName(ExcelImportTaskAction::createImportTask);

    private static final String O2M_IMPORT_TASK_ACTION_NAME = LambdaUtil.fetchMethodName(ExcelImportTaskAction::o2mImport);

    private static final String M2M_IMPORT_TASK_ACTION_NAME = LambdaUtil.fetchMethodName(ExcelImportTaskAction::m2mImport);

    private static final String EXPORT_TASK_ACTION_NAME = LambdaUtil.fetchMethodName(ExcelExportTaskAction::createExportTask);

    @Override
    public void convertActionNode(ActionPermissionNode node, AuthCompileContext context, UIAction actionNode, Action action) {
        ActionTypeEnum actionType = action.getActionType();
        if (ActionTypeEnum.CLIENT.equals(actionType)) {
            String actionName = action.getName();
            if (ClientActionConstants.Import.name.equals(actionName)) {
                createImportTaskActionNode(node, context);
            } else if (ClientActionConstants.Export.name.equals(actionName)) {
                createExportTaskActionNode(node, context);
            }
        } else if (ActionTypeEnum.VIEW.equals(actionType)) {
            TtypeEnum ttype = Optional.ofNullable(context.getCurrentField())
                    .map(UIField::getTtype)
                    .orElse(null);
            String actionName = action.getName();
            if (ttype == null) {
                if (ViewActionConstants.Import.name.equals(actionName)) {
                    createImportTaskActionNode(node, context);
                } else if (ViewActionConstants.Export.name.equals(actionName)) {
                    createExportTaskActionNode(node, context);
                }
            } else if (TtypeEnum.O2M.equals(ttype)) {
                if (ViewActionConstants.Import.name.equals(actionName)) {
                    createO2MImportTaskActionNode(node, context);
                } else if (ViewActionConstants.Export.name.equals(actionName)) {
                    createExportTaskActionNode(node, context);
                }
            } else if (TtypeEnum.M2M.equals(ttype)) {
                if (ViewActionConstants.Import.name.equals(actionName)) {
                    createM2MImportTaskActionNode(node, context);
                } else if (ViewActionConstants.Export.name.equals(actionName)) {
                    createExportTaskActionNode(node, context);
                }
            }
        }
    }

    protected void createImportTaskActionNode(ActionPermissionNode node, AuthCompileContext context) {
        AuthExtendHelper.addExtendActionNode(node, AuthExtendHelper.createExtendActionNode(context, ExcelImportTask.MODEL_MODEL, IMPORT_TASK_ACTION_NAME));
    }

    protected void createO2MImportTaskActionNode(ActionPermissionNode node, AuthCompileContext context) {
        AuthExtendHelper.addExtendActionNode(node, AuthExtendHelper.createExtendActionNode(context, ExcelImportTask.MODEL_MODEL, O2M_IMPORT_TASK_ACTION_NAME));
    }

    protected void createM2MImportTaskActionNode(ActionPermissionNode node, AuthCompileContext context) {
        AuthExtendHelper.addExtendActionNode(node, AuthExtendHelper.createExtendActionNode(context, ExcelImportTask.MODEL_MODEL, M2M_IMPORT_TASK_ACTION_NAME));
    }

    protected void createExportTaskActionNode(ActionPermissionNode node, AuthCompileContext context) {
        AuthExtendHelper.addExtendActionNode(node, AuthExtendHelper.createExtendActionNode(context, ExcelExportTask.MODEL_MODEL, EXPORT_TASK_ACTION_NAME));
    }
}
