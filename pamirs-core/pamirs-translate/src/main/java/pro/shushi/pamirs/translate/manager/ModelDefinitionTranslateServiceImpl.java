package pro.shushi.pamirs.translate.manager;

import com.google.common.collect.Sets;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.translate.manager.base.TranslateMetaBaseService;

import java.util.Set;

/**
 * @author xzf 2022/12/16 21:49
 **/
@Component
public class ModelDefinitionTranslateServiceImpl implements TranslateMetaBaseService<ModelDefinition> {

    @Override
    public Set<String> modelType() {
        return Sets.newHashSet(ModelDefinition.MODEL_MODEL);
    }


    @Override
    public String initModelType() {
        return ModelDefinition.MODEL_MODEL;
    }
}
