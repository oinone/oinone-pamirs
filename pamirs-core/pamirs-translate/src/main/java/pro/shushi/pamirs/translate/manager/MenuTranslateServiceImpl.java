package pro.shushi.pamirs.translate.manager;

import com.google.common.collect.Sets;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.model.Menu;
import pro.shushi.pamirs.translate.manager.base.TranslateMetaBaseService;

import java.util.Set;

/**
 * @author xzf 2022/12/16 21:49
 **/
@Component
public class MenuTranslateServiceImpl implements TranslateMetaBaseService<Menu> {

    @Override
    public Set<String> modelType() {
        return Sets.newHashSet(
                Menu.MODEL_MODEL,
                "apps.AppMenu",
                "apps.AppsModuleMenuProxy"
        );
    }

    @Override
    public String initModelType() {
        return Menu.MODEL_MODEL;
    }
}
