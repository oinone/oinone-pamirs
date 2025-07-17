package pro.shushi.pamirs.framework.connectors.data.dialect.factory;

import org.springframework.core.annotation.AnnotationUtils;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.Dialect;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.DialectPackageApi;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.DialectSelectorApi;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.ExtensionServiceLoader;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.util.ClassScanner;
import pro.shushi.pamirs.meta.common.util.ListUtils;
import pro.shushi.pamirs.meta.enmu.DataSourceEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static pro.shushi.pamirs.framework.connectors.data.dialect.enmu.DialectExpEnumerate.BASE_DIALECT_VERSION_ERROR;

/**
 * 方言服务工厂
 * <p>
 * 2020/7/16 1:48 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class DialectComponentFactory {

    private static volatile Map<Class<?>, Map<String, Object>> COMPONENTS_MAP;

    private static final HoldKeeper<DialectSelectorApi> DIALECT_SELECTOR_API_HOLDER = new HoldKeeper<>();

    private static Map<Class<?>, Map<String, Object>> init() {
        if (COMPONENTS_MAP == null) {
            synchronized (DialectComponentFactory.class) {
                if (COMPONENTS_MAP == null) {
                    Map<Class<?>, Map<String, Object>> componentsMap = new HashMap<>();
                    List<String> packages = ExtensionServiceLoader.getExtensionLoader(DialectPackageApi.class).getExtension().packages();
                    Set<Class<?>> classSet = ClassScanner.scan(ListUtils.toArray(packages), Dialect.class);
                    for (Class<?> clazz : classSet) {
                        Map<String, Object> componentMap = componentsMap.get(clazz);
                        if (null == componentMap) {
                            componentsMap.put(clazz, new ConcurrentHashMap<>());
                            componentMap = componentsMap.get(clazz);
                        }
                        List<?> componentList = Spider.getLoader(clazz).getOrderedExtensions();
                        for (Object dialectComponent : componentList) {
                            DialectVersion dialectVersion = getDialectVersion(dialectComponent);
                            componentMap.putIfAbsent(dialectVersion.getTypeAndVersion(), dialectComponent);
                            componentMap.putIfAbsent(dialectVersion.getTypeAndMajorVersion(), dialectComponent);
                        }
                    }
                    COMPONENTS_MAP = componentsMap;
                }
            }
        }
        return COMPONENTS_MAP;
    }

    private static DialectSelectorApi getDialectSelectorApi() {
        return DIALECT_SELECTOR_API_HOLDER.supply(() -> Spider.getLoader(DialectSelectorApi.class).getDefaultExtension());
    }

    private static DialectVersion getDialectVersion(Object component) {
        Dialect.component annotation = AnnotationUtils.getAnnotation(component.getClass(), Dialect.component.class);
        if (null == annotation) {
            throw PamirsException.construct(BASE_DIALECT_VERSION_ERROR).errThrow();
        }
        DataSourceEnum dataSourceEnum = annotation.type();
        String version = annotation.version();
        String majorVersion = annotation.majorVersion();
        DialectVersion dialectVersion = new DialectVersion();
        dialectVersion.setType(dataSourceEnum.value());
        dialectVersion.setVersion(version);
        dialectVersion.setMajorVersion(majorVersion);
        return dialectVersion;
    }

    @SuppressWarnings("unchecked")
    public static <T> T component(Class<T> componentClass, String dsKey) {
        Map<String, Object> concurrentMap = init().get(componentClass);
        if (concurrentMap == null) {
            return null;
        }
        T component = (T) concurrentMap.get(getDialectSelectorApi().type(dsKey));
        if (null == component) {
            return (T) concurrentMap.get(getDialectSelectorApi().major(dsKey));
        }
        return component;
    }
}
