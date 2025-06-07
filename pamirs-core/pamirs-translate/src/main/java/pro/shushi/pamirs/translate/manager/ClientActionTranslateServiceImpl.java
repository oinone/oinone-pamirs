package pro.shushi.pamirs.translate.manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.model.ClientAction;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.common.lambda.Getter;
import pro.shushi.pamirs.translate.manager.base.TranslateMetaBaseService;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author xzf 2022/12/16 17:05
 **/
@Component
public class ClientActionTranslateServiceImpl implements TranslateMetaBaseService<ClientAction> {

    final List<Getter<ClientAction, String>> getters = Lists.newArrayList(ClientAction::getDisplayName, ClientAction::getLabel);

    Function<ClientAction, Set<String>> itemsFunction = (data) -> {
        Set<String> originSet = getters.stream().map(getter -> getter.apply(data)).collect(Collectors.toSet()); //去重
        Set<String> uniqueSet = originSet.stream().map(origin -> TranslateMetaBaseService.buildDefaultItemUnique(ModuleConstants.MODULE_BASE, ClientAction.MODEL_MODEL, RES_LANG_CODE, LANG_CODE, origin)).collect(Collectors.toSet());

        return uniqueSet;
    };

    @Override
    public Set<String> modelType() {
        return Sets.newHashSet(
                ClientAction.MODEL_MODEL
        );
    }

    @Override
    public String initModelType() {
        return ClientAction.MODEL_MODEL;
    }
}