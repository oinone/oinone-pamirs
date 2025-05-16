package pro.shushi.pamirs.translate.manager;

import com.google.common.collect.Sets;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.model.ServerAction;
import pro.shushi.pamirs.translate.manager.base.TranslateMetaBaseService;

import java.util.Set;

/**
 * @author xzf 2022/12/16 17:05
 **/
@Component
public class ServerActionTranslateServiceImpl implements TranslateMetaBaseService<ServerAction> {

    @Override
    public Set<String> modelType() {
        return Sets.newHashSet(
                ServerAction.MODEL_MODEL
        );
    }

    @Override
    public String initModelType() {
        return ServerAction.MODEL_MODEL;
    }
}