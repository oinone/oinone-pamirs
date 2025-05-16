package pro.shushi.pamirs.boot.web.loader;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.boot.base.model.WidgetDefinition;
import pro.shushi.pamirs.boot.web.extend.IWidgetLoader;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.Models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Fun(IWidgetLoader.FUN_NAMESPACE)
public class DefaultWidgetLoader implements IWidgetLoader {

    @Function
    public WidgetDefinition loadDefault() {
        WidgetDefinition result = new WidgetDefinition();
        List<WidgetDefinition> widgetDefinitionList = Models.origin().queryListByWrapper(
                Pops.<WidgetDefinition>lambdaQuery()
                        .from(WidgetDefinition.MODEL_MODEL)
                        .eq(WidgetDefinition::getSys, Boolean.FALSE)
                        .and(_wq -> {
                            _wq.isNotNull(WidgetDefinition::getJavascript)
                                    .or(_wq2 -> {
                                        _wq2.isNotNull(WidgetDefinition::getCss);
                                    });
                        })
        );
        if (CollectionUtils.isEmpty(widgetDefinitionList)) {
            return result;
        }

        Set<String> javascript = new HashSet<>();
        Set<String> css = new HashSet<>();
        for (WidgetDefinition widgetDefinition : widgetDefinitionList) {
            if (CollectionUtils.isNotEmpty(widgetDefinition.getJavascript())) {
                javascript.addAll(widgetDefinition.getJavascript());
            }
            if (CollectionUtils.isNotEmpty(widgetDefinition.getCss())) {
                css.addAll(widgetDefinition.getCss());
            }
        }
        result.setJavascript(new ArrayList<>(javascript));
        result.setCss(new ArrayList<>(css));
        return result;
    }
}
