package pro.shushi.pamirs.translate.manager;

import com.google.common.collect.Sets;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.translate.manager.base.TranslateMetaBaseService;

import java.util.Set;

/**
 * @author xzf 2022/12/16 21:49
 **/
@Component
public class ModuleDefinitionTranslateServiceImpl implements TranslateMetaBaseService<ModuleDefinition> {

    @Override
    public Set<String> modelType() {
        return Sets.newHashSet(
                ModuleDefinition.MODEL_MODEL,
                "base.AppSwitcherModuleProxy",
                "workflow.WorkflowAppModule",
                "designer.DesignerModuleDefinition",
                "ui.designer.UiDesignerModuleProxy",
                "dataVisualization.DataChartModule"
        );
    }

    @Override
    public String initModelType() {
        return ModuleDefinition.MODEL_MODEL;
    }
}
