package pro.shushi.pamirs.meta.common.spi;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.common.constants.SpiNamespaceConstants;
import pro.shushi.pamirs.meta.common.spi.factory.JavaServiceLoaderFactory;
import pro.shushi.pamirs.meta.common.spi.factory.ServiceLoaderFactory;
import pro.shushi.pamirs.meta.common.util.SpiMapUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * SPI加载器
 *
 * @param <T>
 * @author d
 */
public class ExtensionServiceLoader<T> {

    private static final Logger logger = LoggerFactory.getLogger(ExtensionServiceLoader.class);

    private static final ConcurrentMap<Class<?>/*Factory class*/, ServiceLoaderFactory> LOADER_FACTORIES = new ConcurrentHashMap<>(64);

    private static final ConcurrentMap<Class<?>/*SPI*/, ExtensionServiceLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>(64);

    private static final ConcurrentMap<Class<?>/*Implementation class*/, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<>(64);

    private final Class<T>/*SPI*/ type;

    private String cachedDefaultName;

    private final Holder<ServiceLoaderFactory> loaderFactory = new Holder<>();

    private final ConcurrentMap<Class<?>/*Implementation class*/, String> cachedNames = new ConcurrentHashMap<>();

    private final Holder<Map<String, Class<?>/*Implementation class*/>> cachedClasses = new Holder<>();

    private final ConcurrentMap<String, Holder<T>/*Implementation instance*/> cachedInstances = new ConcurrentHashMap<>();

    private ExtensionServiceLoader(Class<T> type) {
        this.type = type;
        createFactory();
        loadExtensionInstances();
    }

    private void createFactory() {
        ServiceLoaderFactory serviceLoaderFactory = loaderFactory.get();
        if (serviceLoaderFactory == null) {
            synchronized (loaderFactory) {
                serviceLoaderFactory = loaderFactory.get();
                if (serviceLoaderFactory == null) {
                    SPI spi = AnnotationUtils.getAnnotation(type, SPI.class);
                    if (null == spi) {
                        serviceLoaderFactory = LOADER_FACTORIES.get(JavaServiceLoaderFactory.class);
                        if (null == serviceLoaderFactory) {
                            serviceLoaderFactory = SpiMapUtils
                                    .concurrentComputeIfAbsent(LOADER_FACTORIES, JavaServiceLoaderFactory.class, k -> new JavaServiceLoaderFactory());
                        }
                    } else {
                        String defaultName = spi.value();
                        cachedDefaultName = StringUtils.isBlank(defaultName) ? SpiNamespaceConstants.PAMIRS : defaultName;
                        Class<? extends ServiceLoaderFactory> factoryClass = spi.factory();
                        serviceLoaderFactory = LOADER_FACTORIES.get(factoryClass);
                        if (null == serviceLoaderFactory) {
                            serviceLoaderFactory = SpiMapUtils.concurrentComputeIfAbsent(LOADER_FACTORIES, factoryClass, k -> {
                                try {
                                    return spi.factory().newInstance();
                                } catch (Throwable t) {
                                    throw new IllegalStateException("Loader factory (class: " +
                                            type + ") couldn't be instantiated: " + t.getMessage(), t);
                                }
                            });
                        }
                    }
                    loaderFactory.set(serviceLoaderFactory);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> ExtensionServiceLoader<T> getExtensionLoader(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Extension type == null");
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Extension type (" + type + ") is not an interface!");
        }

        ExtensionServiceLoader<T> loader = (ExtensionServiceLoader<T>) EXTENSION_LOADERS.get(type);
        if (loader == null) {
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (type) {
                loader = (ExtensionServiceLoader<T>) EXTENSION_LOADERS.get(type);
                if (loader == null) {
                    loader = new ExtensionServiceLoader<>(type);
                    EXTENSION_LOADERS.put(type, loader);
                }
            }
        }
        return loader;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void resetExtensionLoader(Class type) {
        ExtensionServiceLoader loader = EXTENSION_LOADERS.get(type);
        if (loader != null) {
            Map<String, Class<?>> classes = loader.getExtensionClasses();
            for (Map.Entry<String, Class<?>> entry : classes.entrySet()) {
                EXTENSION_INSTANCES.remove(entry.getValue());
            }
            classes.clear();
            EXTENSION_LOADERS.remove(type);
        }
    }

    private Map<String, Class<?>> getExtensionClasses() {
        Map<String, Class<?>> classes = cachedClasses.get();
        if (classes == null) {
            synchronized (cachedClasses) {
                classes = cachedClasses.get();
                if (classes == null) {
                    classes = loadExtensionClasses();
                    cachedClasses.set(classes);
                }
            }
        }
        return classes;
    }

    private Map<String, Class<?>> loadExtensionClasses() {
        Map<String, Class<?>> extensionClasses = new HashMap<>();
        loaderFactory.get().loadServiceInstances(type, (k, serviceInstanceSupplier) -> {
            String name = k.getLeft();
            Class<?> spiClass = k.getRight();
            extensionClasses.putIfAbsent(name, spiClass);
            cachedNames.putIfAbsent(spiClass, name);
        });
        return extensionClasses;
    }

    private void loadExtensionInstances() {
        getExtensionClasses();
        loaderFactory.get().loadServiceInstances(type, (k, serviceInstanceSupplier) -> {
            String name = k.getLeft();
            Class<?> spiClass = k.getRight();
            if (!EXTENSION_INSTANCES.containsKey(spiClass)) {
                T serviceInstance = serviceInstanceSupplier.get();
                EXTENSION_INSTANCES.putIfAbsent(spiClass, serviceInstance);
            }
            getExtension(name);
        });
    }

    public String getExtensionName(T extensionInstance) {
        return getExtensionName(extensionInstance.getClass());
    }

    public String getExtensionName(Class<?> extensionClass) {
        getExtensionClasses();// load class
        return cachedNames.get(extensionClass);
    }

    public static <T> T getExtension(Class<T> type, String name) {
        return ExtensionServiceLoader.getExtensionLoader(type).getExtension(name);
    }

    public T getExtension() {
        List<T> extensions = getOrderedExtensions();
        return CollectionUtils.isEmpty(extensions) ? null : extensions.get(0);
    }

    public T getExtension(String name) {
        final Holder<T> holder = getOrCreateHolder(name);
        T instance = holder.get();
        if (instance == null) {
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (holder) {
                instance = holder.get();
                if (instance == null) {
                    instance = createExtension(name);
                    holder.set(instance);
                }
            }
        }
        return instance;
    }

    /**
     * Return default extension, return <code>null</code> if it's not configured.
     */
    public T getDefaultExtension() {
        if (null == cachedDefaultName) {
            return null;
        }
        getExtensionClasses();
        return getExtension(cachedDefaultName);
    }

    public List<T> getExtensions() {
        return cachedInstances.values().stream().map(Holder::get).collect(Collectors.toList());
    }

    public boolean hasExtension(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Extension name == null");
        }
        Class<?> c = this.getExtensionClass(name);
        return c != null;
    }

    public List<T> getOrderedExtensions() {
        List<T> extensions = getExtensions();
        AnnotationAwareOrderComparator.sort(extensions);
        return Collections.unmodifiableList(extensions);
    }

    public Set<String> getSupportedExtensions() {
        Map<String, Class<?>> classes = getExtensionClasses();
        return Collections.unmodifiableSet(new TreeSet<>(classes.keySet()));
    }

    @SuppressWarnings("unchecked")
    private T createExtension(String name) {
        Class<?> clazz = getExtensionClasses().get(name);
        if (clazz == null) {
            throw new IllegalStateException("There is no implementation class: " + type + " for name: " + name);
        }
        T instance = (T) EXTENSION_INSTANCES.get(clazz);
        if (instance == null) {
            instance = (T) SpiMapUtils.concurrentComputeIfAbsent(EXTENSION_INSTANCES, clazz, k -> {
                try {
                    return clazz.newInstance();
                } catch (Throwable t) {
                    throw new IllegalStateException("Extension instance (name: " + name + ", class: " +
                            type + ") couldn't be instantiated: " + t.getMessage(), t);
                }
            });
        }
        return injectExtension(instance);
    }

    private T injectExtension(T instance) {
        try {
            for (Method method : instance.getClass().getMethods()) {
                AutoFill autoFill = method.getAnnotation(AutoFill.class);
                if (autoFill == null) {
                    continue;
                }
                try {
                    method.invoke(instance);
                } catch (Exception e) {
                    logger.error("Failed to inject via method " + method.getName()
                            + " of interface " + type.getName() + ": " + e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return instance;
    }

    private Class<?> getExtensionClass(String name) {
        if (type == null) {
            throw new IllegalArgumentException("Extension type == null");
        }
        if (name == null) {
            throw new IllegalArgumentException("Extension name == null");
        }
        return getExtensionClasses().get(name);
    }

    private Holder<T> getOrCreateHolder(String name) {
        Holder<T> holder = cachedInstances.get(name);
        if (holder == null) {
            holder = SpiMapUtils.computeIfAbsent(cachedInstances, name, k -> new Holder<>());
        }
        return holder;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "[" + type.getName() + "]";
    }

}
