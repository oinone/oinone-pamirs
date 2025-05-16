package pro.shushi.pamirs.translate.manager;

import com.google.common.collect.Sets;
import pro.shushi.pamirs.meta.domain.model.ErrorsDefinition;
import pro.shushi.pamirs.translate.manager.base.TranslateMetaBaseService;

import java.util.Set;

/**
 * ErrorsDefinitionTranslateServiceImpl
 *
 * @author yakir on 2023/10/10 19:43.
 */
public class ErrorsDefinitionTranslateServiceImpl implements TranslateMetaBaseService<ErrorsDefinition> {

    @Override
    public Set<String> modelType() {
        return Sets.newHashSet(
                ErrorsDefinition.MODEL_MODEL
        );
    }

    @Override
    public String initModelType() {
        return ErrorsDefinition.MODEL_MODEL;
    }
}
