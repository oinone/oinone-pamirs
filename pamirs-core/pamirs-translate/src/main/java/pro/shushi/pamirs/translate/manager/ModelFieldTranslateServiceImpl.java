package pro.shushi.pamirs.translate.manager;

import com.google.common.collect.Sets;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.translate.manager.base.TranslateMetaBaseService;

import java.util.Set;

/**
 * @author xzf 2022/12/16 21:49
 **/
@Component
public class ModelFieldTranslateServiceImpl implements TranslateMetaBaseService<ModelField> {

    @Override
    public Set<String> modelType() {
        return Sets.newHashSet(
                ModelField.MODEL_MODEL,
                "designer.DesignerModelField",
                "dataVisualization.DataChartModelField",
                "ui.designer.UiDesignerField",
                "workflow.WorkflowField"
        );
    }

    @Override
    public String initModelType() {
        return ModelField.MODEL_MODEL;
    }
}