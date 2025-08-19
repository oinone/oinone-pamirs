package pro.shushi.pamirs.eip.view.init;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.extend.MetaDataEditor;
import pro.shushi.pamirs.core.common.InitializationUtil;
import pro.shushi.pamirs.eip.api.EipModule;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.model.EipInterfaceTestTransient;
import pro.shushi.pamirs.eip.api.model.scene.EipSceneInstance;
import pro.shushi.pamirs.eip.api.pmodel.EipSceneDefinitionProxy;
import pro.shushi.pamirs.eip.api.pmodel.EipSceneInstanceProxy;
import pro.shushi.pamirs.eip.api.tmodel.EipIntegrationInterfaceEdit;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.util.Map;

/**
 * @author Adamancy Zhang
 * @date 2021-01-05 13:55
 */
@Order(Ordered.LOWEST_PRECEDENCE - 70)
@Component
public class EipMetadataEditor implements MetaDataEditor {
    final boolean useModuleSuffix = false;

    @Override
    public void edit(AppLifecycleCommand command, Map<String, Meta> metaMap) {
        InitializationUtil util = InitializationUtil.get(metaMap, EipModule.MODULE_MODULE, EipModule.MODULE_NAME);
        if (util == null) {
            return;
        }

//        util.setHomepageByMenu("集成接口");

        createViewAction(util);
        modifyViewAction(util);

        editInterfaceViewAction(util);
    }

    private void createViewAction(InitializationUtil util) {
        util.createViewAction("eipSceneGenerateInstance", "生成实例", EipSceneDefinitionProxy.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), EipSceneDefinitionProxy.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.SINGLE, ActionTargetEnum.DIALOG, util.getViewNameByViewLoader("场景生成实例form", useModuleSuffix), null);
        util.createViewAction("eipSceneInstanceUpdateTask", "更新定时配置", EipSceneInstance.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), EipSceneInstanceProxy.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.SINGLE, ActionTargetEnum.DIALOG, util.getViewNameByViewLoader("实例更新定时任务form", useModuleSuffix), null);
        util.createViewAction("eipSceneInstanceUpdateIncUpdateLog", "更新增量配置", EipSceneInstance.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), EipSceneInstanceProxy.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.SINGLE, ActionTargetEnum.DIALOG, util.getViewNameByViewLoader("实例更新增量日志form", useModuleSuffix), null);
        util.createViewAction("eipSceneInstanceTestCall", "调用测试", EipSceneInstance.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), EipSceneInstanceProxy.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.SINGLE, ActionTargetEnum.DIALOG, util.getViewNameByViewLoader("实例调用测试form", useModuleSuffix), null);
    }

    private void modifyViewAction(InitializationUtil util) {

        util.modifyViewAction(EipSceneDefinitionProxy.MODEL_MODEL, InitializationUtil.DEFAULT_CREATE, viewAction ->
                viewAction.setViewType(ViewTypeEnum.FORM).setResView(util.getView(EipSceneDefinitionProxy.MODEL_MODEL, "场景form")));
        util.modifyViewAction(EipSceneDefinitionProxy.MODEL_MODEL, InitializationUtil.DEFAULT_UPDATE, viewAction ->
                viewAction.setViewType(ViewTypeEnum.FORM).setResView(util.getView(EipSceneDefinitionProxy.MODEL_MODEL, "场景form")));

    }

    private void editInterfaceViewAction(InitializationUtil util) {
        util.createViewAction("集成接口创建form", "创建", EipIntegrationInterface.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), EipIntegrationInterfaceEdit.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.CONTEXT_FREE, ActionTargetEnum.ROUTER, "集成接口创建form", null);
        util.createViewAction("集成接口修改form", "编辑", EipIntegrationInterface.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), EipIntegrationInterfaceEdit.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.SINGLE, ActionTargetEnum.ROUTER, "集成接口修改form", "context.activeRecord.dataStatus == 'NOT_ENABLED' || context.activeRecord.dataStatus == 'DISABLED'");
        util.createViewAction("集成接口详情detail", "详情", EipIntegrationInterface.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), EipIntegrationInterfaceEdit.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.SINGLE, ActionTargetEnum.ROUTER, "集成接口详情detail", null);
        util.createViewAction("集成接口复制form", "复制", EipIntegrationInterface.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), EipIntegrationInterfaceEdit.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.SINGLE, ActionTargetEnum.ROUTER, "集成接口复制form", null);

        util.createViewAction("redirectTestPage", "测试", EipIntegrationInterface.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.TABLE), EipInterfaceTestTransient.MODEL_MODEL, ViewTypeEnum.FORM, ActionContextTypeEnum.SINGLE, ActionTargetEnum.ROUTER, "integration_interface_test_form", null);
    }
}
