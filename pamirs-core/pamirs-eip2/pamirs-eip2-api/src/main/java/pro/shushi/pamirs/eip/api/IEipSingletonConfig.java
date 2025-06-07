package pro.shushi.pamirs.eip.api;

import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.model.EipOpenInterface;
import pro.shushi.pamirs.eip.api.model.EipRouteDefinition;
import pro.shushi.pamirs.eip.api.service.EipService;
import pro.shushi.pamirs.meta.base.BaseModel;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.resource.api.model.SingletonModel;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

public interface IEipSingletonConfig<T extends BaseModel> extends SingletonModel<T> {

    default String eipSystem() {
        return this.getClass().getSimpleName();
    }

    String getSchema();

    default String schema() {
        return (String) ((T) this).get_d().get("schema");
    }

    ;

    default void initSystem() {
        T t = this.singletonModel();
        Map<String, IEipInitializationService> eipApiMap = BeanDefinitionUtils.getBeansOfType(IEipInitializationService.class);
        EipService eipService = BeanDefinitionUtils.getBean(EipService.class);
        for (IEipInitializationService eipApi : eipApiMap.values()) {
            if (!(eipApi instanceof Proxy) && eipApi.eipSystem().equals(eipSystem())) {
                List<IEipApi> iEipApiList = eipApi.initEip((IEipSingletonConfig) t);
                if (iEipApiList == null) continue;
                for (IEipApi iEipApi : iEipApiList) {
                    if (iEipApi instanceof EipOpenInterface) {
                        eipService.registerOpenInterface((EipOpenInterface) iEipApi);
                    } else if (iEipApi instanceof EipIntegrationInterface) {
                        eipService.registerInterface((EipIntegrationInterface) iEipApi);
                    } else if (iEipApi instanceof EipRouteDefinition) {
                        eipService.registerRouteDefinition((EipRouteDefinition) iEipApi);
                    } else {
                        throw new UnsupportedOperationException("不支持的接口类型");
                    }
                }
            }
        }

    }

}

