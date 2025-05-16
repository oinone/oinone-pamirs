package pro.shushi.pamirs.dev.tools.init;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.extend.MetaDataEditor;
import pro.shushi.pamirs.core.common.InitializationUtil;
import pro.shushi.pamirs.core.common.loader.BizInitLoader;
import pro.shushi.pamirs.dev.tools.DevToolsModule;
import pro.shushi.pamirs.dev.tools.model.DictionaryOverview;
import pro.shushi.pamirs.dev.tools.model.ExtPointOverview;
import pro.shushi.pamirs.dev.tools.model.FunctionOverview;
import pro.shushi.pamirs.dev.tools.model.ModelOverview;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xzf 2022/08/23 14:52
 **/
@Component
public class DevToolsMetaDataInit implements MetaDataEditor {


    @Override
    public void edit(AppLifecycleCommand command, Map<String, Meta> metaMap) {
        InitializationUtil util = InitializationUtil.get(metaMap, DevToolsModule.MODULE_MODULE, DevToolsModule.MODULE_NAME);
        if (util == null) {
            return;
        }

        final boolean useModuleSuffix = true;

        BizInitLoader.init(util, DevToolsModule.MODULE_MODULE, useModuleSuffix);

        initViewAction(util, useModuleSuffix);
        menuInit(util);
    }

    private void initViewAction(InitializationUtil util, boolean useModuleSuffix) {
        Map<String, Object> modelContext = new HashMap<>();
        modelContext.put("model", "rootRecord.model");
        util.createViewAction("searchByModel", "查询对比", ModelOverview.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.FORM), ModelOverview.MODEL_MODEL, ViewTypeEnum.DETAIL, ActionContextTypeEnum.SINGLE, ActionTargetEnum.ROUTER, util.getViewNameByViewLoader("model_overview_detail", false), null)
            .setContext(modelContext);

        Map<String, Object> funContext = new HashMap<>();
        funContext.put("namespace", "rootRecord.namespace");
        funContext.put("fun", "rootRecord.fun");
        util.createViewAction("searchByFun", "查询对比", FunctionOverview.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.FORM), FunctionOverview.MODEL_MODEL, ViewTypeEnum.DETAIL, ActionContextTypeEnum.SINGLE, ActionTargetEnum.ROUTER, util.getViewNameByViewLoader("function_overview_detail", false), null)
            .setContext(funContext);

        Map<String, Object> dictContext = new HashMap<>();
        dictContext.put("dictionary", "rootRecord.dictionary");
        util.createViewAction("searchByDict", "查询对比", DictionaryOverview.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.FORM), DictionaryOverview.MODEL_MODEL, ViewTypeEnum.DETAIL, ActionContextTypeEnum.SINGLE, ActionTargetEnum.ROUTER, util.getViewNameByViewLoader("dictionary_overview_detail", false), null)
            .setContext(dictContext);

        Map<String, Object> extContext = new HashMap<>();
        extContext.put("namespace", "rootRecord.namespace");
        funContext.put("name", "rootRecord.name");
        util.createViewAction("searchByExtPoint", "查询对比", ExtPointOverview.MODEL_MODEL, InitializationUtil.getOptions(ViewTypeEnum.FORM), ExtPointOverview.MODEL_MODEL, ViewTypeEnum.DETAIL, ActionContextTypeEnum.SINGLE, ActionTargetEnum.ROUTER, util.getViewNameByViewLoader("extpoint_overview_detail", false), null)
            .setContext(extContext);

    }

    private void menuInit(InitializationUtil util) {
        util.setHomepageByViewAction(ModelOverview.MODEL_MODEL,"searchByModel",null);
    }

}
