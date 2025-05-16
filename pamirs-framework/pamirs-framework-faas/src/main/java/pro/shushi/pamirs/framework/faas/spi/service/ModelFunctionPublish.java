package pro.shushi.pamirs.framework.faas.spi.service;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.faas.spi.api.remote.IRemoteServiceInit;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.constant.ModuleFunctionConstants;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 模型函数动态发布成远程服务
 * @author cpc@shushi.pro
 * @version 1.0.0
 * date 2024/1/18
 */
@Component
public class ModelFunctionPublish{

    public Boolean publishServiceWrap(List<ModelDefinition> modelDefinitions) {
        if (CollectionUtils.isEmpty(modelDefinitions)) {
            return Boolean.TRUE;
        }
        Function function = PamirsSession.getContext().getFunctionAllowNull(modelDefinitions.get(0).getModule(), ModuleFunctionConstants.FUN_PUBLISH_SERVICE);
        if (function != null) {
            Fun.run(modelDefinitions.get(0).getModule(), ModuleFunctionConstants.FUN_PUBLISH_SERVICE, modelDefinitions);
        } else {
            publishService(modelDefinitions);
        }
        return Boolean.TRUE;
    }

    public Boolean publishService(List<ModelDefinition> modelDefinitions) {
        IRemoteServiceInit remoteServiceInit = Spider.getDefaultExtension(IRemoteServiceInit.class);
        remoteServiceInit.init(modelDefinitions.stream().map(ModelDefinition::getModel).collect(Collectors.toList()));
        return Boolean.TRUE;
    }
}
