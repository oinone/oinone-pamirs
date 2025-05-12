package pro.shushi.pamirs.meta.api.session;

import com.github.benmanes.caffeine.cache.Cache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.container.StaticModelConfigContainer;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.config.TxConfig;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.cache.SessionCacheFactory;
import pro.shushi.pamirs.meta.api.session.cache.api.*;
import pro.shushi.pamirs.meta.api.session.cache.fast.ModelFieldThreadCache;
import pro.shushi.pamirs.meta.api.session.cache.spi.SessionCacheFactoryApi;
import pro.shushi.pamirs.meta.base.BaseModel;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.domain.fun.*;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.domain.model.SequenceConfig;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.ComputeSceneEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.HookTypeEnum;
import pro.shushi.pamirs.meta.enmu.MetaExpEnumerate;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Consumer;

import static pro.shushi.pamirs.meta.enmu.MetaExpEnumerate.BASE_FUNCTION_IS_NOT_EXISTS_ERROR;
import static pro.shushi.pamirs.meta.enmu.MetaExpEnumerate.BASE_MODEL_CONFIG_IS_NOT_EXISTS_2_ERROR;

/**
 * 请求上下文
 *
 * @author d@shushi.pro
 * @author wx@shushi.pro
 * @version 1.0.0
 * date 2020/1/14 10:41 下午
 */
@Slf4j
@Data
public class RequestContext implements Serializable {

    private static final long serialVersionUID = 726467652569824666L;

    private Class<?> factoryApiClass;

    private ModuleCacheApi/*key:module*/ moduleCache;

    private ModelCacheApi/*key:model*/ modelCache;

    private SequenceConfigCacheApi/*key:code*/ sequenceConfigCache;

    private StandaloneFunCacheApi/*key:namespace#fun*/ standaloneFunCache;

    private TxConfigCacheApi/*key:namespace.fun*/ txConfigCache;

    private DataDictionaryCacheApi/*key:dictionary*/ dictCache;

    private InterfaceCacheApi/*key:namespace#fun*/ interfaceCache;

    private HooksCacheApi hookCache;

    private ExtPointImplementationCacheApi/*key:namespace#name*/ extPointImplementationCache;

    private ExpressionDefinitionCacheApi/*type#model#sign*/ expressionDefinitionCache;

    private ComputeDefinitionCacheApi/*type#model#sign*/ computeDefinitionCache;

    private Map<String, Object> extendCacheMap;

    private Cache<String, Function> functionCache;

    private Cache<String, Function>/*key:namespace#name*/ functionByNameCache;

    public boolean isEmpty() {
        return null == modelCache || modelCache.isEmpty();
    }

    public ModelConfig getModelConfig(String model) {
        if (StringUtils.isBlank(model)) {
            throw PamirsException.construct(BASE_MODEL_CONFIG_IS_NOT_EXISTS_2_ERROR).appendMsg("model:" + model).errThrow();
        }
        ModelConfig modelConfig = modelCache.get(model);
        if (null == modelConfig) {
            modelConfig = StaticModelConfigContainer.getModelConfig(model);
        }
        if (null == modelConfig) {
            modelConfig = StaticModelConfigContainer.getTransientModelConfig(model);
        }
        return modelConfig;
    }

    public ModelConfig getSimpleModelConfig(String model) {
        if (StringUtils.isBlank(model)) {
            throw PamirsException.construct(BASE_MODEL_CONFIG_IS_NOT_EXISTS_2_ERROR).appendMsg("model:" + model).errThrow();
        }
        ModelConfig modelConfig = modelCache.getSimpleModelConfig(model);
        if (modelConfig == null) {
            modelConfig = StaticModelConfigContainer.getModelConfig(model);
        }
        if (modelConfig == null) {
            modelConfig = StaticModelConfigContainer.getTransientModelConfig(model);
        }
        return modelConfig;
    }

    public ModelConfig getModelConfigByName(String name) {
        return modelCache.getByName(name);
    }

    public ModelConfig getSimpleModelConfigByName(String name) {
        return modelCache.getSimpleModelConfigByName(name);
    }

    public List<String> getModelsByTable(String table) {
        return modelCache.getModelsByTable(table);
    }

    public void addModelConfig(ModelConfig modelConfig) {
        modelCache.putIfAbsent(modelConfig.getModel(), modelConfig);
    }

    public ModuleDefinition getModule(String module) {
        return moduleCache.get(module);
    }

    public <M extends ModuleDefinition> void addModule(M moduleDefinition) {
        moduleCache.put(moduleDefinition.getModule(), moduleDefinition);
    }

    public SequenceConfig getSequenceConfig(String code) {
        return sequenceConfigCache.get(code);
    }

    @SuppressWarnings("unused")
    public void addSequenceConfig(SequenceConfig sequenceConfig) {
        sequenceConfigCache.put(sequenceConfig.getCode(), sequenceConfig);
    }

    public ModelFieldConfig getModelField(String model, String field) {
        return ModelFieldThreadCache.get(model, field, (m, f) -> {
            ModelConfig modelConfig = getSimpleModelConfig(m);
            if (null == modelConfig) {
                return null;
            }
            List<String> tableModels = modelConfig.getModels();
            for (String tableModel : tableModels) {
                ModelFieldConfig modelFieldConfig = getModelField0(tableModel, f);
                if (null != modelFieldConfig) {
                    return modelFieldConfig;
                }
            }
            return null;
        });
    }

    private ModelFieldConfig getModelField0(String model, String field) {
        ModelConfig modelConfig = getSimpleModelConfig(model);
        if (null == modelConfig) {
            return null;
        }
        List<ModelFieldConfig> modelFieldConfigList = modelConfig.getModelFieldConfigList();
        for (ModelFieldConfig modelFieldConfig : modelFieldConfigList) {
            if (field.equals(modelFieldConfig.getField())) {
                return modelFieldConfig;
            }
        }
        return null;
    }

    public ModelFieldConfig getModelFieldByFieldName(String model, String fieldName) {
        return ModelFieldThreadCache.getByName(model, fieldName, (m, f) -> {
            ModelConfig modelConfig = getSimpleModelConfig(m);
            if (null == modelConfig) {
                return null;
            }
            List<String> tableModels = modelConfig.getModels();
            for (String tableModel : tableModels) {
                ModelFieldConfig modelFieldConfig = getModelFieldByFieldName0(tableModel, f);
                if (null != modelFieldConfig) {
                    return modelFieldConfig;
                }
            }
            return null;
        });
    }

    private ModelFieldConfig getModelFieldByFieldName0(String model, String fieldName) {
        ModelConfig modelConfig = getSimpleModelConfig(model);
        if (null == modelConfig) {
            return null;
        }
        List<ModelField> modelFieldList = modelConfig.getModelDefinition().getModelFields();
        for (ModelField modelField : modelFieldList) {
            if (fieldName.equals(modelField.getLname())) {
                return new ModelFieldConfig(modelField);
            }
        }
        return null;
    }

    @SuppressWarnings("unused")
    public void disableFieldCache(String model, String field) {
    }

    @SuppressWarnings("unused")
    public void disableFieldCacheByFieldName(String model, String fieldName) {
    }

    public Function getFunction(String fun) {
        return getFunction(NamespaceConstants.pamirs, fun);
    }

    public Function getFunction(String namespace, String fun) {
        Function function = getFunctionOrInterfaceAllowNull(namespace, fun);
        if (null != function) {
            return function;
        }
        throw PamirsException.construct(BASE_FUNCTION_IS_NOT_EXISTS_ERROR)
                .appendMsg(MessageFormat.format("namespace:{0},fun:{1}", namespace, fun)).errThrow();
    }

    public Function getFunctionOrInterfaceAllowNull(String namespace, String fun) {
        Function function = getFunctionAllowNull(namespace, fun);
        if (null == function) {
            String key = FunctionDefinition.sign(namespace, fun);
            return functionCache.get(key, k -> interfaceCache.get(namespace, fun));
        }
        return function;
    }

    public Function getFunctionAllowNull(String namespace, String fun) {
        String key = FunctionDefinition.sign(namespace, fun);
        return functionCache.get(key, k -> {
            ModelConfig modelConfig = modelCache.get(namespace);
            if (null != modelConfig) {
                List<FunctionDefinition> functionDefinitionList = modelConfig.getModelDefinition().getFunctions();
                for (FunctionDefinition functionDefinition : functionDefinitionList) {
                    if (functionDefinition.getFun().equals(fun)) {
                        return new Function(functionDefinition);
                    }
                }
            }
            return standaloneFunCache.get(namespace, fun);
        });
    }

    public Function getFunctionByName(String namespace, String name) {
        String key = FunctionDefinition.sign(namespace, name);
        return functionByNameCache.get(key, k -> {
            ModelConfig modelConfig = modelCache.get(namespace);
            if (null != modelConfig) {
                List<FunctionDefinition> functionDefinitionList = modelConfig.getModelDefinition().getFunctions();
                for (FunctionDefinition functionDefinition : functionDefinitionList) {
                    if (functionDefinition.getName().equals(name)) {
                        return new Function(functionDefinition);
                    }
                }
            }
            return null;
        });
    }

    public Function findFunction(String namespace, String fun) {
        Function function = getFunction(namespace, fun);
        if (null == function) {
            function = getFunction(BaseModel.MODEL_MODEL, fun);
        }
        if (null == function) {
            function = getFunction(NamespaceConstants.pamirs, fun);
        }
        return function;
    }

    public void disableFunctionCache(String namespace, String fun) {
        String key = FunctionDefinition.sign(namespace, fun);
        functionCache.invalidate(key);
    }

    public DataDictionary getDictionary(String dictionary) {
        return dictCache.get(dictionary);
    }

    public void addDictionary(DataDictionary dataDictionary) {
        dictCache.putIfAbsent(dataDictionary.getDictionary(), dataDictionary);
    }

    public TxConfig getTxConfig(String namespace, String fun) {
        if (null == txConfigCache) {
            return null;
        }
        return txConfigCache.get(namespace, fun);
    }

    public void addTxConfig(TxConfig txConfig) {
        txConfigCache.putIfAbsent(txConfig.getNamespace(), txConfig.getFun(), txConfig);
    }

    public List<ExtPointImplementation> getExtPointImplementationList(String namespace, String extPointName) {
        return extPointImplementationCache.get(namespace, extPointName);
    }

    public List<Hook> getExecuteHooks(HookTypeEnum type, String model, String fun,
                                      List<FunctionTypeEnum> functionType, Set<String> excludeHooks) {
        List<Hook> executeHooks = new ArrayList<>();
        List<Hook> allHooks = hookCache.get();
        if (CollectionUtils.isEmpty(allHooks)) {
            return null;
        }
        String module = PamirsSession.getServApp();
        boolean hasCurrentModule = StringUtils.isNotBlank(module);
        List<String> excludeHooksForModule = null;
        if (hasCurrentModule) {
            ModuleDefinition moduleDefinition = PamirsSession.getContext().getModule(module);
            if (null != moduleDefinition) {
                excludeHooksForModule = moduleDefinition.getExcludeHooks();
            }
        }
        for (Hook hook : allHooks) {
            if (null != excludeHooks && excludeHooks.contains(hook.getExecuteNamespace())) {
                continue;
            }
            if (!CollectionUtils.isEmpty(functionType) && !CollectionUtils.isEmpty(hook.getFunctionTypes())) {
                Set<FunctionTypeEnum> typeSet = new HashSet<>(hook.getFunctionTypes());
                typeSet.retainAll(functionType);
                if (typeSet.size() == hook.getFunctionTypes().size()) {
                    continue;
                }
            }
            if (null != excludeHooksForModule && excludeHooksForModule.contains(hook.getExecuteNamespace())) {
                continue;
            }
            if (hasCurrentModule && !CollectionUtils.isEmpty(hook.getModule()) && !hook.getModule().contains(module)) {
                continue;
            }
            if (null != type && !hook.getHookType().equals(type)) {
                continue;
            }
            if (StringUtils.isNotBlank(model) && !CollectionUtils.isEmpty(hook.getModel()) && !hook.getModel().contains(model)) {
                continue;
            }
            if (StringUtils.isNotBlank(fun) && !CollectionUtils.isEmpty(hook.getFun()) && !hook.getFun().contains(fun)) {
                continue;
            }
            executeHooks.add(hook);
        }
        return executeHooks;
    }

    public List<ExpressionDefinition> getExpressionDefinitionList(ComputeSceneEnum computeScene, String model, String sign) {
        return expressionDefinitionCache.get(computeScene.value(), model, sign);
    }

    public List<ComputeDefinition> getComputeDefinitionList(ComputeSceneEnum computeScene, String model, String sign) {
        return computeDefinitionCache.get(computeScene.value(), model, sign);
    }

    @SuppressWarnings({"unchecked", "unused"})
    public <T> T getExtendCache(Class<T> cacheApi) {
        String cacheName = cacheApi.getSimpleName();
        T cache = (T) extendCacheMap.get(cacheName);
        if (null == cache) {
            throw PamirsException.construct(MetaExpEnumerate.BASE_CACHE_ERROR)
                    .appendMsg(MessageFormat.format("cache:{0}", cacheName)).errThrow();
        }
        return cache;
    }

    @SuppressWarnings({"unused", "unchecked"})
    public <T> T putExtendCache(Class<T> cacheApi, T cache) {
        return (T) extendCacheMap.putIfAbsent(cacheApi.getSimpleName(), cache);
    }

    public <T, R> R getExtendCacheValue(Class<T> cacheApi, java.util.function.Function<T, R> cacheSupplier) {
        T cache = getExtendCache(cacheApi);
        return cacheSupplier.apply(cache);
    }

    public <T> void putExtendCacheEntity(Class<T> cacheApi, Consumer<T> cacheConsumer) {
        T cache = getExtendCache(cacheApi);
        cacheConsumer.accept(cache);
    }

    public static RequestContext newContext(SessionCacheFactoryApi sessionCacheFactoryApi) {
        return new RequestContext().init(sessionCacheFactoryApi);
    }

    public RequestContext init(SessionCacheFactoryApi sessionCacheFactoryApi) {
        factoryApiClass = sessionCacheFactoryApi.getClass();

        moduleCache = SessionCacheFactory.fetchModuleCache(sessionCacheFactoryApi);
        modelCache = SessionCacheFactory.fetchModelCache(sessionCacheFactoryApi);
        sequenceConfigCache = SessionCacheFactory.fetchSequenceConfigCache(sessionCacheFactoryApi);
        standaloneFunCache = SessionCacheFactory.fetchStandaloneFunCache(sessionCacheFactoryApi);
        txConfigCache = SessionCacheFactory.fetchTxConfigCache(sessionCacheFactoryApi);
        dictCache = SessionCacheFactory.fetchDataDictionaryCache(sessionCacheFactoryApi);
        interfaceCache = SessionCacheFactory.fetchInterfaceCache(sessionCacheFactoryApi);
        hookCache = SessionCacheFactory.fetchHookCache(sessionCacheFactoryApi);
        extPointImplementationCache = SessionCacheFactory.fetchExtPointImplementationCache(sessionCacheFactoryApi);
        expressionDefinitionCache = SessionCacheFactory.fetchExpressionDefinitionCache(sessionCacheFactoryApi);
        computeDefinitionCache = SessionCacheFactory.fetchComputeDefinitionCache(sessionCacheFactoryApi);

        extendCacheMap = SessionCacheFactory.fetchExtendCache(sessionCacheFactoryApi);

        functionCache = SessionCacheFactory.fetchFunctionCache(sessionCacheFactoryApi);
        functionByNameCache = SessionCacheFactory.fetchFunctionByNameCache(sessionCacheFactoryApi);

        return this;
    }

    public void clear() {
        moduleCache.clear();
        modelCache.clear();
        sequenceConfigCache.clear();
        standaloneFunCache.clear();
        txConfigCache.clear();
        dictCache.clear();
        interfaceCache.clear();
        hookCache.clear();
        extPointImplementationCache.clear();
        expressionDefinitionCache.clear();
        computeDefinitionCache.clear();
        functionCache.invalidateAll();

        extendCacheMap.clear();
    }

}
