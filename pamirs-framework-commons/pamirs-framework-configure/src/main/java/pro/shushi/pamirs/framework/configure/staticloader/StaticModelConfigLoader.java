package pro.shushi.pamirs.framework.configure.staticloader;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.configure.simulate.api.MetaSimulateService;
import pro.shushi.pamirs.framework.configure.simulate.service.MetaSimulator;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.container.StaticModelConfigContainer;
import pro.shushi.pamirs.meta.api.core.configure.MetaModelFetcher;
import pro.shushi.pamirs.meta.common.util.ClassScanner;
import pro.shushi.pamirs.meta.configure.PamirsFrameworkSystemConfiguration;

import jakarta.annotation.Resource;
import java.util.Set;

/**
 * 静态模型配置装载器
 * <p>
 * 2020/6/19 6:17 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Component
public class StaticModelConfigLoader {

    public static final String BEAN_NAME = "staticModelConfigLoader";

    @Resource
    private MetaSimulateService metaSimulateService;

    @Resource
    private PamirsFrameworkSystemConfiguration pamirsFrameworkSystemConfiguration;

    @Resource
    private MetaModelFetcher defaultMetaModelFetcher;

    @EventListener
    @Order(10)
    void init(ApplicationStartedEvent event) {
        String[] modelConfigLocations = pamirsFrameworkSystemConfiguration.getStaticModelConfigLocations();
        if (ArrayUtils.isNotEmpty(modelConfigLocations)) {
            // 模型静态配置扫描
            Set<Class<?>> modelClasses = ClassScanner.scan(modelConfigLocations, Model.Static.class);
            for (Class<?> clazz : modelClasses) {
                TableInfoFetcher.initStaticModelConfig(clazz);
            }
        }
        // 模型模拟配置扫描
        Set<Class<?>> metaClasses = defaultMetaModelFetcher.fetchMetaClasses();
        for (Class<?> metaClazz : metaClasses) {
            pro.shushi.pamirs.meta.annotation.sys.MetaSimulator metaSimulator =
                    AnnotationUtils.getAnnotation(metaClazz, pro.shushi.pamirs.meta.annotation.sys.MetaSimulator.class);
            if (null != metaSimulator) {
                TableInfoFetcher.initStaticModelConfig(metaClazz);
            }
        }
        // 静态模型和模拟模型计算
        metaSimulateService.transientStaticExecuteWithoutResult(MetaSimulator.simulate(),
                StaticModelConfigContainer::completeCompute);
    }

}
