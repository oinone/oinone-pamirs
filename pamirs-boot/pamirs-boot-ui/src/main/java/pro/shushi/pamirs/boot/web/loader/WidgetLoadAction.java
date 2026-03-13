package pro.shushi.pamirs.boot.web.loader;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.model.WidgetDefinition;
import pro.shushi.pamirs.boot.web.extend.IWidgetLoader;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Base
@Component
@Model.model(WidgetDefinition.MODEL_MODEL)
public class WidgetLoadAction {

    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    @Function(openLevel = FunctionOpenEnum.API)
    public WidgetDefinition loadSDK(WidgetDefinition data) {
        // 通过namespace获取function列表,调用全部函数. 为了支持独立部署时,远程调用获取前端低代码文件
        List<FunctionDefinition> loadFunctions = Models.origin().queryListByWrapper(
                Pops.<FunctionDefinition>lambdaQuery().from(FunctionDefinition.MODEL_MODEL).setBatchSize(-1)
                        .eq(FunctionDefinition::getNamespace, IWidgetLoader.FUN_NAMESPACE)
        );
        if (CollectionUtils.isEmpty(loadFunctions)) {
            return data;
        }

        Set<String> css = new HashSet<>();
        Set<String> javascript = new HashSet<>();
        for (FunctionDefinition loadFunction : loadFunctions) {
            try {
                WidgetDefinition widgetDefinition = Fun.run(loadFunction.getNamespace(), loadFunction.getFun());
                if (widgetDefinition == null) {
                    continue;
                }
                if (CollectionUtils.isNotEmpty(widgetDefinition.getCss())) {
                    css.addAll(widgetDefinition.getCss());
                }
                if (CollectionUtils.isNotEmpty(widgetDefinition.getJavascript())) {
                    javascript.addAll(widgetDefinition.getJavascript());
                }
            } catch (Exception e) {
                // 支持远程调用,如果调用失败,不影响启动
                log.error("Warning! loadSDK load function error, can be ignored. namespace:" + loadFunction.getNamespace() + ", fun:" + loadFunction.getFun(), e);
            }
        }

        data.setCss(new ArrayList<>(css));
        data.setJavascript(new ArrayList<>(javascript));
        return data;
    }

}
