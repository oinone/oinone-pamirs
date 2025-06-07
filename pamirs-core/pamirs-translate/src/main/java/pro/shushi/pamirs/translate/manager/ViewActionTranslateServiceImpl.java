package pro.shushi.pamirs.translate.manager;

import com.google.common.collect.Sets;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.translate.manager.base.TranslateMetaBaseService;

import java.util.Set;

/**
 * @author xzf 2022/12/16 17:05
 **/
@Component
public class ViewActionTranslateServiceImpl implements TranslateMetaBaseService<ViewAction> {

    @Override
    public Set<String> modelType() {
        return Sets.newHashSet(
                ViewAction.MODEL_MODEL
        );
    }

    @Override
    public String initModelType() {
        return ViewAction.MODEL_MODEL;
    }
}