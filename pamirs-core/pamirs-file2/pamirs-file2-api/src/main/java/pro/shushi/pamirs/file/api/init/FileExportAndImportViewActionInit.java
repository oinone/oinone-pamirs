package pro.shushi.pamirs.file.api.init;

import org.apache.commons.collections4.MapUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.constants.ViewActionConstants;
import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.extend.MetaDataEditor;
import pro.shushi.pamirs.boot.web.utils.UiActionUtils;
import pro.shushi.pamirs.boot.web.utils.ViewActionUtils;
import pro.shushi.pamirs.file.api.FileModule;
import pro.shushi.pamirs.file.api.model.ExcelExportTask;
import pro.shushi.pamirs.file.api.model.ExcelImportTask;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 2022/5/8 2:17 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@Component
public class FileExportAndImportViewActionInit implements MetaDataEditor {

    @Override
    public void edit(AppLifecycleCommand command, Map<String, Meta> metaMap) {
        if (MapUtils.isNotEmpty(metaMap)) {
            Set<String> dependencyFileModels = metaMap.values().stream()
                    .filter(v -> v.getData().containsKey(FileModule.MODULE_MODULE))
                    .map(Meta::getModule)
                    .collect(Collectors.toSet());
            for (String module : metaMap.keySet()) {
                if (!dependencyFileModels.contains(module)) {
                    // 不依赖文件模块,不生成导入导出动作
                    continue;
                }
                UiActionUtils.doSomethingForMenuModel(metaMap, module, (meta, data) ->
                        this.makeDefaultModelViewAction(meta, data, dependencyFileModels));
            }
        }
    }

    public static final String DEFAULT_IMPORT_VIEW_NAME = "import_dialog";

    public static final String DEFAULT_EXPORT_VIEW_NAME = "export_dialog";

    private void makeDefaultModelViewAction(Meta meta, ModelDefinition data, Set<String> dependencyFileModels) {
        if (!dependencyFileModels.contains(data.getModule())) {
            // 当前模块使用了其他模块的模型,对方模块未依赖文件,不生成导入导出动作
            return;
        }
        Map<String, Object> context = new HashMap<>();
        context.put("model", "'" + data.getModel() + "'");
        // 创建 跳转导入页 viewAction
        ViewActionUtils.makeDefaultViewAction(meta, data,
                ViewActionConstants.Import.name,
                ViewActionConstants.Import.displayName,
                null,
                ActionContextTypeEnum.CONTEXT_FREE,
                ViewTypeEnum.FORM, ViewActionConstants.Import.priority,
                ExcelImportTask.MODEL_MODEL, DEFAULT_IMPORT_VIEW_NAME, ActionTargetEnum.DIALOG,
                context);

        // 创建 跳转导出页 viewAction
        ViewActionUtils.makeDefaultViewAction(meta, data,
                ViewActionConstants.Export.name,
                ViewActionConstants.Export.displayName,
                null,
                ActionContextTypeEnum.CONTEXT_FREE,
                ViewTypeEnum.FORM, ViewActionConstants.Export.priority,
                ExcelExportTask.MODEL_MODEL, DEFAULT_EXPORT_VIEW_NAME, ActionTargetEnum.DIALOG,
                context);
    }
}
