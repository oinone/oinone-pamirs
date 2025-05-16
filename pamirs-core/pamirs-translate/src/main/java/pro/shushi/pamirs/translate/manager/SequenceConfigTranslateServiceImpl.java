package pro.shushi.pamirs.translate.manager;

import com.google.common.collect.Sets;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.domain.model.SequenceConfig;
import pro.shushi.pamirs.translate.manager.base.TranslateMetaBaseService;

import java.util.Set;

/**
 * SequenceConfigTranslateServiceImpl
 *
 * @author yakir on 2023/10/11 11:12.
 */
@Component
public class SequenceConfigTranslateServiceImpl implements TranslateMetaBaseService<SequenceConfig> {

    @Override
    public Set<String> modelType() {
        return Sets.newHashSet(
                SequenceConfig.MODEL_MODEL,
                "designer.DesignerSequenceConfig"
        );
    }

    @Override
    public String initModelType() {
        return SequenceConfig.MODEL_MODEL;
    }
}
