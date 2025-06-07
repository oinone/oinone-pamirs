package pro.shushi.pamirs;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import pro.shushi.pamirs.framework.connectors.data.dialect.mysql.MysqlTableInfoDialectService;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.cache.DefaultLocalCachePrefixApi;
import pro.shushi.pamirs.meta.api.cache.LocalCachePrefixApi;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.DynamicDsKeyComputer;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.PamirsMapperConfigurationProxy;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.TableNameComputer;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.BatchOperation;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsDataConfiguration;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableConfig;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.api.core.session.RequestSessionApi;
import pro.shushi.pamirs.meta.api.dto.config.api.DefaultModelConfigApi;
import pro.shushi.pamirs.meta.api.dto.config.api.ModelConfigApi;
import pro.shushi.pamirs.meta.api.enmu.BatchCommitTypeEnum;
import pro.shushi.pamirs.meta.api.session.RequestContext;
import pro.shushi.pamirs.meta.api.session.cache.spi.DefaultSessionCacheFactoryApi;
import pro.shushi.pamirs.meta.common.constants.SpiNamespaceConstants;
import pro.shushi.pamirs.meta.common.spi.ExtensionServiceLoader;
import pro.shushi.pamirs.meta.common.spi.Holder;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;

/**
 * @author Adamancy Zhang at 12:21 on 2025-04-11
 */
public abstract class AbstractInitSessionTest {

    @BeforeAll
    @Order(0)
    public static void beforeAll0() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
        initRequestSession();
    }

    protected static void initRequestSession() throws NoSuchFieldException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        initSPIService(ModelConfigApi.class, new DefaultModelConfigApi());
        initSPIService(LocalCachePrefixApi.class, new DefaultLocalCachePrefixApi());
        initCommonApi(PamirsMapperConfigurationProxy.class, new PamirsMapperConfigurationProxy() {

            @Override
            public PamirsDataConfiguration fetchPamirsDataConfiguration(String dsKey) {
                return null;
            }

            @Override
            public PamirsTableConfig fetchPamirsTableConfig(String dsKey) {
                return null;
            }

            @Override
            public TableNameComputer fetchTableNameComputer() {
                return null;
            }

            @Override
            public DynamicDsKeyComputer fetchDynamicDsKeyComputer() {
                return null;
            }

            @Override
            public BatchCommitTypeEnum batch() {
                return null;
            }

            @Override
            public Map<String, BatchOperation> batchConfig() {
                return Collections.emptyMap();
            }

            @Override
            public BatchOperation batchOperationForModel(String model) {
                return null;
            }

            @Override
            public void fillDefaultConfig(String dsKey, PamirsTableInfo pamirsTableInfo) {
                new MysqlTableInfoDialectService().fillDefaultConfig(dsKey, pamirsTableInfo);
            }
        });
        initSPIService(RequestSessionApi.class, new RequestSessionApi() {

            private final RequestContext context = RequestContext.newContext(new DefaultSessionCacheFactoryApi());

            @Override
            public RequestContext getContext() {
                return context;
            }

            @Override
            public void setContext(RequestContext context) {
                // do nothing.
            }
        });
    }

    protected static <T> void initSPIService(Class<T> clazz, T instance) throws NoSuchFieldException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        initSPIService(clazz, instance, SpiNamespaceConstants.PAMIRS);
    }

    @SuppressWarnings("unchecked")
    protected static <T> void initSPIService(Class<T> clazz, T instance, String spiName) throws NoSuchFieldException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ExtensionServiceLoader<T> loader = generatorExtensionServiceLoader(clazz);
        Map<String, Holder<T>> cachedInstances = (Map<String, Holder<T>>) FieldUtils.getFieldValue(loader, "cachedInstances");
        Holder<T> holder = new Holder<>();
        holder.set(instance);
        cachedInstances.put(spiName, holder);
    }

    @SuppressWarnings({"unchecked"})
    protected static <T> ExtensionServiceLoader<T> generatorExtensionServiceLoader(Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        Field field = ExtensionServiceLoader.class.getDeclaredField("EXTENSION_LOADERS");
        field.setAccessible(true);
        Map<Class<?>, ExtensionServiceLoader<T>> extensionInstances = (Map<Class<?>, ExtensionServiceLoader<T>>) field.get(ExtensionServiceLoader.class);
        ExtensionServiceLoader<T> loader = extensionInstances.get(clazz);
        if (loader == null) {
            Constructor<ExtensionServiceLoader<T>> constructor = (Constructor<ExtensionServiceLoader<T>>) (Object) ExtensionServiceLoader.class.getDeclaredConstructor(Class.class);
            constructor.setAccessible(true);
            loader = constructor.newInstance(clazz);
            extensionInstances.put(clazz, loader);
        }
        return loader;
    }

    @SuppressWarnings("unchecked")
    protected static <T> void initCommonApi(Class<T> clazz, T instance) throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        Field field = CommonApiFactory.class.getDeclaredField("CACHE");
        field.setAccessible(true);
        Map<Object, Object> cache = (Map<Object, Object>) field.get(CommonApiFactory.class);
        Constructor<?> constructor = Class.forName("pro.shushi.pamirs.meta.api.CommonApiFactory$ApiCacheKey").getDeclaredConstructor(Class.class);
        constructor.setAccessible(true);
        Object key = constructor.newInstance(clazz);
        cache.put(key, instance);
    }
}
