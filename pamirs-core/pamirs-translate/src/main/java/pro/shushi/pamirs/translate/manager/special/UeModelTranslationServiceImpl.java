package pro.shushi.pamirs.translate.manager.special;

import com.google.common.collect.Sets;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.model.UeModel;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.translate.manager.base.TranslateMetaBaseService;

import java.util.Set;

/**
 * @author xzf 2022/12/28 10:23
 **/
@Component
public class UeModelTranslationServiceImpl implements TranslateMetaBaseService<MetaBaseModel> {

    @Override
    public Set<String> modelType() {
        return Sets.newHashSet(
                UeModel.MODEL_MODEL,
                "designer.DesignerModelDefinition"
        );
    }

    @Override
    public String initModelType() {
        return null;
    }
}
