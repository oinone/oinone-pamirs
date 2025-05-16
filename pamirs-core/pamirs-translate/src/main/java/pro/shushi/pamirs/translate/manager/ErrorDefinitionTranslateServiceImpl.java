package pro.shushi.pamirs.translate.manager;

import com.google.common.collect.Sets;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import pro.shushi.pamirs.meta.domain.model.ErrorDefinition;
import pro.shushi.pamirs.translate.manager.base.TranslateMetaBaseService;

import java.util.Set;

/**
 * @author xzf 2022/12/16 21:49
 **/
@Component
@ControllerAdvice
public class ErrorDefinitionTranslateServiceImpl implements TranslateMetaBaseService<ErrorDefinition> {

    @Override
    public Set<String> modelType() {
        return Sets.newHashSet(
                ErrorDefinition.MODEL_MODEL
        );
    }

    @Override
    public String initModelType() {
        return ErrorDefinition.MODEL_MODEL;
    }
}
