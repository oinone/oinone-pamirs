package pro.shushi.pamirs.resource.api.loader;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.boot.base.model.WidgetDefinition;
import pro.shushi.pamirs.boot.web.extend.IWidgetLoader;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.resource.api.model.ResourceIconLib;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Fun(IWidgetLoader.FUN_NAMESPACE)
public class DefaultIconLoader implements IWidgetLoader {

    @Function
    public WidgetDefinition loadIcons() {
        WidgetDefinition result = new WidgetDefinition();
        List<ResourceIconLib> resourceIconLibList = new ResourceIconLib().queryList(
                Pops.<ResourceIconLib>lambdaQuery()
                        .from(ResourceIconLib.MODEL_MODEL)
                        .and(item -> {
                            item.isNotNull(ResourceIconLib::getJsUrls)
                                    .or(q -> {
                                        q.isNotNull(ResourceIconLib::getCssUrls);
                                    });
                        })
        );
        if (CollectionUtils.isEmpty(resourceIconLibList)) {
            return result;
        }

        Set<String> javascript = new HashSet<>();
        Set<String> css = new HashSet<>();
        for (ResourceIconLib ResourceIconLib : resourceIconLibList) {
            if (CollectionUtils.isNotEmpty(ResourceIconLib.getJsUrls())) {
                javascript.addAll(ResourceIconLib.getJsUrls());
            }
            if (CollectionUtils.isNotEmpty(ResourceIconLib.getCssUrls())) {
                css.addAll(ResourceIconLib.getCssUrls());
            }
        }
        result.setJavascript(new ArrayList<>(javascript));
        result.setCss(new ArrayList<>(css));
        return result;
    }
}
