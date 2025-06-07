package pro.shushi.pamirs.framework.configure.annotation.core;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.framework.common.api.PlatformJarVersionCheckerApi;
import pro.shushi.pamirs.framework.configure.MetaConfiguration;
import pro.shushi.pamirs.framework.configure.annotation.configure.ConfigureConverterConfiguration;
import pro.shushi.pamirs.framework.configure.annotation.configure.ConfigureSignerConfiguration;
import pro.shushi.pamirs.framework.configure.annotation.core.cache.FieldMetaCache;
import pro.shushi.pamirs.framework.configure.annotation.core.check.MetaUniqueChecker;
import pro.shushi.pamirs.framework.configure.annotation.core.sign.clazz.ModelDefinitionReflectSigner;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.configure.DefinitionConfigurer;
import pro.shushi.pamirs.meta.api.core.configure.MetaModelFetcher;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ConverterType;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelReflectSigner;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelSigner;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.*;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.constants.VariableNameConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.common.util.TimeWatcher;
import pro.shushi.pamirs.meta.domain.ModelData;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;
import pro.shushi.pamirs.meta.util.ClassUtils;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.NamespaceAndFunUtils;
import pro.shushi.pamirs.meta.util.TypeUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.*;

import static pro.shushi.pamirs.framework.configure.annotation.contants.TipsConstants.*;
import static pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate.*;

/**
 * 安装元数据准备引擎
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/15 2:41 下午
 */
@Slf4j
@Component
@DependsOn(BeanDefinitionUtils.beanName)
public class AnnotationConfigurer implements DefinitionConfigurer {

    @SuppressWarnings("rawtypes")
    private final static Map<String/*T+D*/, List<ModelConverter>> metaModelConvertersMap = new HashMap<>();

    @SuppressWarnings("rawtypes")
    private final static Map<String/*T+D*/, ModelReflectSigner> metaModelReflectSignersMap = new HashMap<>();

    @SuppressWarnings("rawtypes")
    private final static Map<String/*T*/, ModelSigner> metaModelSignersMap = new HashMap<>();

    @SuppressWarnings({"unused"})
    @Resource
    private ConfigureConverterConfiguration modelConverterConfiguration;

    @SuppressWarnings({"unused"})
    @Resource
    private ConfigureSignerConfiguration configureSignerConfiguration;

    @SuppressWarnings({"unused"})
    @Resource
    private MetaModelFetcher metaModelFetcher;

    @SuppressWarnings({"unused"})
    @Resource
    private ModuleResolver moduleResolver;

    @Resource
    private MetaConfiguration metaConfiguration;

    @Resource
    private PlatformJarVersionCheckerApi platformJarVersionCheckerApi;

    @Order(0)
    @EventListener
    @SuppressWarnings({"rawtypes", "unused"})
    public void init(ApplicationStartedEvent event) {

        // init reflect signer of meta model
        List<ModelReflectSigner> modelReflectSignerBeanList = Spider.getLoader(ModelReflectSigner.class).getExtensions();
        Objects.requireNonNull(modelReflectSignerBeanList).forEach(v -> {
            if (!CollectionUtils.isEmpty(configureSignerConfiguration.getReflectSigners())
                    && !configureSignerConfiguration.getReflectSigners().contains(v.getClass().getName())) {
                return;
            }
            String modelClazz = fetchActualModelFromConverter(v.getClass());
            String source = fetchSourceClassFromConverter(v.getClass());
            String T_D = modelClazz + CharacterConstants.SEPARATOR_OCTOTHORPE + source;
            metaModelReflectSignersMap.put(T_D, v);
        });

        // init signer of meta model
        List<ModelSigner> modelSignerBeanList = Spider.getLoader(ModelSigner.class).getExtensions();
        Objects.requireNonNull(modelSignerBeanList).forEach(v -> {
            if (!CollectionUtils.isEmpty(configureSignerConfiguration.getSigners())
                    && !configureSignerConfiguration.getSigners().contains(v.getClass().getName())) {
                return;
            }
            String T_T = fetchActualModelFromConverter(v.getClass());
            metaModelSignersMap.put(T_T, v);
        });

        // init converter of meta model
        Map<String, ModelConverter> modelConverterBeanMap = BeanDefinitionUtils.getBeansOfType(ModelConverter.class);
        Objects.requireNonNull(modelConverterBeanMap).values().stream().sorted(Comparator.comparing(ModelConverter::priority))
                .forEach(v -> {
                    if (!CollectionUtils.isEmpty(modelConverterConfiguration.getAnnotation())
                            && !modelConverterConfiguration.getAnnotation().contains(v.getClass().getName())) {
                        return;
                    }
                    String group = v.group();
                    String source = fetchSourceClassFromConverter(v.getClass());
                    String T_D = group + CharacterConstants.SEPARATOR_OCTOTHORPE + source;
                    List<ModelConverter> metaModelConverters = metaModelConvertersMap.computeIfAbsent(T_D, k -> new ArrayList<>());
                    metaModelConverters.add(v);
                });

    }

    @Override
    public Result<List<Meta>> extractDefinition(final Set<String> includeModules, final Set<String> excludeModules) {
        Map<String, ModuleDefinition> allModules = moduleResolver.resolve();
        return extractDefinition(true, includeModules, excludeModules, allModules, new HashMap<>(), new HashMap<>());
    }

    @Override
    @SuppressWarnings({"rawtypes"})
    public Result<List<Meta>> extractDefinition(final boolean installMeta,
                                                final Set<String> includeModules,
                                                final Set<String> excludeModules,
                                                final Map<String, ModuleDefinition> moduleInfoMap,
                                                final Map<String, MetaData> upgradeModuleMap,
                                                final Map<String, MetaData> reloadModuleMap) {
        Result<List<Meta>> result = new Result<>();
        List<Meta> metaDataList = new ArrayList<>();
        result.setData(metaDataList);

        List<ModelConverter> moduleConverters = metaModelConvertersMap.get(ModuleDefinition.MODEL_MODEL
                + CharacterConstants.SEPARATOR_OCTOTHORPE + Class.class.getName());
        if (CollectionUtils.isEmpty(moduleConverters)) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                    .error(BASE_MODULE_NO_CONVERTER_ERROR));
            result.error();
        }
        List<ModelConverter> modelConverters = metaModelConvertersMap.get(ModelDefinition.MODEL_MODEL
                + CharacterConstants.SEPARATOR_OCTOTHORPE + Class.class.getName());
        if (CollectionUtils.isEmpty(modelConverters)) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                    .error(BASE_MODEL_NO_CONVERTER_ERROR));
            result.error();
        }
        List<ModelConverter> modelFieldConverters = metaModelConvertersMap.get(ModelField.MODEL_MODEL
                + CharacterConstants.SEPARATOR_OCTOTHORPE + Field.class.getName());
        if (CollectionUtils.isEmpty(modelFieldConverters)) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                    .error(BASE_FIELD_NO_CONVERTER_ERROR));
            result.error();
        }
        List<ModelConverter> functionConverters = metaModelConvertersMap.get(FunctionDefinition.MODEL_MODEL
                + CharacterConstants.SEPARATOR_OCTOTHORPE + Method.class.getName());
        if (CollectionUtils.isEmpty(functionConverters)) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                    .error(BASE_FUNCTION_NO_CONVERTER_ERROR));
            result.error();
        }
        if (!result.isSuccess()) {
            return result;
        }

        // 获取元模型配置
        List<MetaModel> metaModels = metaModelFetcher.fetchMetaModelList();

        // 准备模块信息
        Set<String/*module*/> scanModuleSet = new HashSet<>();
        for (String module : moduleInfoMap.keySet()) {
            if (!CollectionUtils.isEmpty(includeModules) && !includeModules.contains(module)) {
                continue;
            }
            if (!CollectionUtils.isEmpty(excludeModules) && excludeModules.contains(module)) {
                continue;
            }
            scanModuleSet.add(module);
        }
        scanModuleSet.addAll(upgradeModuleMap.keySet());
        scanModuleSet.addAll(reloadModuleMap.keySet());
        List<String> scanModules = ModuleDependencyResolver.sortModulesByDependent(moduleInfoMap, scanModuleSet);
        // 将Class转化为模型
        // 扫描模块
        Map<String, MetaData> completedModuleMap = new HashMap<>();
        for (String moduleModule : scanModules) {
            log.info(START_SCAN_MODUlE + moduleModule);
            TimeWatcher.watch(() -> {
                ModuleDefinition pamirsModule = moduleInfoMap.get(moduleModule);
                if (null == pamirsModule) {
                    throw PamirsException.construct(BASE_MODULE_IS_NOT_EXISTS_ERROR)
                            .appendMsg("该模块不存在:" + moduleModule).errThrow();
                }

                Meta meta = new Meta();
                meta.setModule(moduleModule);
                meta.setBootModuleSet(includeModules);
                MetaCrossing metaCrossing = new MetaCrossing();
                metaCrossing.setBootModuleSet(includeModules);

                // 扫描依赖模块
                Map<String, String[]> dependentPackagePrefix = new HashMap<>();
                Set<String> dependencySortedModules = new LinkedHashSet<>();
                ModuleDependencyResolver.fetchModuleDependencyPackage(dependencySortedModules, dependentPackagePrefix,
                        moduleInfoMap, pamirsModule);
                for (String dependentModuleModule : dependencySortedModules) {
                    log.info(START_SCAN_DEPENDENT_MODUlE + dependentModuleModule);
                    TimeWatcher.watch(() -> {
                        String[] dependentPackagePrefixArray = dependentPackagePrefix.get(dependentModuleModule);
                        checkConflict(pamirsModule, moduleModule, dependentModuleModule, dependentPackagePrefixArray);
                        if (completedModuleMap.containsKey(dependentModuleModule)) {
                            addMetaDataToMeta(meta, metaCrossing, dependentModuleModule, completedModuleMap.get(dependentModuleModule));
                        } else {
                            MetaData dependentMetaData = getMetaData(upgradeModuleMap, reloadModuleMap, dependentModuleModule);
                            if (installMeta) {
                                ModuleDefinition dependentModule = moduleInfoMap.get(dependentModuleModule);
                                scanPackages(moduleConverters, modelConverters, modelFieldConverters, functionConverters, metaModels, result,
                                        dependentPackagePrefixArray, dependentModule, metaCrossing, dependentMetaData);
                            }
                            completedModuleMap.put(dependentModuleModule, dependentMetaData);
                            addMetaDataToMeta(meta, metaCrossing, dependentModuleModule, dependentMetaData);
                        }
                    });
                }

                // 扫描本模块
                log.info(START_SCAN_CURRENT_MODUlE + moduleModule);
                TimeWatcher.watch(() -> {
                    if (completedModuleMap.containsKey(moduleModule)) {
                        addMetaDataToMeta(meta, metaCrossing, moduleModule, completedModuleMap.get(moduleModule));
                    } else {
                        MetaData metaData = getMetaData(upgradeModuleMap, reloadModuleMap, moduleModule);
                        if (installMeta) {
                            scanPackages(moduleConverters, modelConverters, modelFieldConverters, functionConverters, metaModels, result,
                                    pamirsModule.getPackagePrefix(), pamirsModule, metaCrossing, metaData);
                        }
                        completedModuleMap.put(moduleModule, metaData);
                        addMetaDataToMeta(meta, metaCrossing, moduleModule, metaData);
                    }
                });
                result.getData().add(meta);
                log.info(TIME_ALL);
            });
            log.info(COMPLETE_ALL + moduleModule);
        }
        MetaUniqueChecker.clear();
        result.logMessages(metaConfiguration.getLogLevel());
        return result;
    }

    private MetaData getMetaData(Map<String, MetaData> upgradeModuleMap, Map<String, MetaData> reloadModuleMap, String moduleModule) {
        MetaData metaData = upgradeModuleMap.get(moduleModule);
        if (metaData == null) {
            metaData = reloadModuleMap.get(moduleModule);
            if (metaData == null) {
                metaData = new MetaData();
                upgradeModuleMap.put(moduleModule, metaData);
            }
        }
        return metaData;
    }

    private void addMetaDataToMeta(Meta meta, MetaCrossing metaCrossing, String dependentModuleModule, MetaData dependentMetaData) {
        metaCrossing.put(dependentMetaData.getExtendMap());
        meta.getData().putIfAbsent(dependentModuleModule, dependentMetaData);
    }

    private void checkConflict(ModuleDefinition pamirsModule, String moduleModule, String dependentModuleModule, String[] dependentPackagePrefixArray) {
        if (null != dependentPackagePrefixArray) {
            for (String dependentPackagePrefix : dependentPackagePrefixArray) {
                if (null == pamirsModule.getPackagePrefix()) {
                    continue;
                }
                for (String packagePrefix : pamirsModule.getPackagePrefix()) {
                    String temp = packagePrefix.replace(dependentPackagePrefix, CharacterConstants.SEPARATOR_EMPTY);
                    if (StringUtils.isBlank(temp) || temp.startsWith(CharacterConstants.SEPARATOR_DOT)) {
                        throw PamirsException.construct(BASE_MODULE_PACKAGE_CONFLICT_ERROR)
                                .appendMsg(MessageFormat.format("模块中包含了依赖模块的扫描包路径，模块：{0}:{1}，依赖模块：{2}:{3}",
                                        moduleModule, packagePrefix, dependentModuleModule, dependentPackagePrefix)).errThrow();
                    }
                    temp = dependentPackagePrefix.replace(packagePrefix, CharacterConstants.SEPARATOR_EMPTY);
                    if (StringUtils.isBlank(temp) || temp.startsWith(CharacterConstants.SEPARATOR_DOT)) {
                        throw PamirsException.construct(BASE_MODULE_PACKAGE_CONFLICT_ERROR)
                                .appendMsg(MessageFormat.format("依赖模块中包含了模块的扫描包路径，模块：{0}:{1}，依赖模块：{2}:{3}",
                                        moduleModule, packagePrefix, dependentModuleModule, dependentPackagePrefix)).errThrow();
                    }
                }
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void scanPackages(List<ModelConverter> moduleConverters,
                              List<ModelConverter> modelConverters,
                              List<ModelConverter> modelFieldConverters,
                              List<ModelConverter> functionConverters,
                              List<MetaModel> metaModels,
                              Result result,
                              String[] packages,
                              ModuleDefinition pamirsModule,
                              MetaCrossing metaCrossing,
                              MetaData metaData) {
        if (ArrayUtils.isEmpty(packages) || null == pamirsModule.getModuleClazz()) {
            return;
        }
        MetaNames moduleNames = new MetaNames();
        // 模块转换
        Result<ModuleDefinition> moduleConvertResult = convert0(moduleNames, moduleConverters, pamirsModule.getModuleClazz(), metaCrossing, metaData);
        result.addMessages(moduleConvertResult.getMessages());
        if (!moduleConvertResult.isSuccess()) {
            result.error();
            return;
        }
        // 类扫描
        Set<Class<?>> classes = ClassUtils.getClassesByPacks(packages);

        TimeWatcher.watch(() -> {
            platformJarVersionCheckerApi.jarVersion(classes);
        }, "获取Jar版本");

        List<Method> methods = new ArrayList<>();
        List<Field> fields = new ArrayList<>();
        TimeWatcher.watch(() -> {
            for (Class clazz : classes) {
                if (clazz.isMemberClass()) {
                    continue;
                }
                if (ArrayUtils.isEmpty(clazz.getDeclaredAnnotations())) {
                    continue;
                }
                // 收集方法
                methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
                // 模型定义转换
                MetaNames names = new MetaNames();
                names.setModule(moduleNames.getModule());
                names.setModuleName(moduleNames.getModuleName());
                names.setModuleAbbr(moduleNames.getModuleAbbr());
                names.setDsKey(moduleNames.getDsKey());
                Result<ModelDefinition> modelConvertResult = convert0(names, modelConverters, clazz, metaCrossing, metaData);
                result.addMessages(modelConvertResult.getMessages());
                if (modelConvertResult.isSuccess()) {
                    ModelDefinition modelDefinition = modelConvertResult.getData();
                    if (null != modelDefinition) {
                        // 收集字段
                        int i = 0;
                        for (Field field : clazz.getDeclaredFields()) {
                            FieldMetaCache.setFieldSlot(field, i);
                            fields.add(field);
                            i++;
                        }
                    }
                } else {
                    result.error();
                    continue;
                }
                // 非核心元模型Class转换
                convert(result, names, metaModels, clazz, metaCrossing, metaData);
            }
        }, SCAN_MODEL);
        TimeWatcher.watch(() -> {
            MetaNames names;
            ModelDefinitionReflectSigner modelDefinitionSigner = (ModelDefinitionReflectSigner) Spider
                    .getExtension(ModelReflectSigner.class, ModelDefinition.MODEL_MODEL);
            // 字段转换
            for (Field field : fields) {
                if (ArrayUtils.isEmpty(field.getDeclaredAnnotations())) {
                    continue;
                }
                names = new MetaNames();
                names.setModule(moduleNames.getModule());
                names.setModel(modelDefinitionSigner.sign(names, field.getDeclaringClass()));
                Result convertResult = convert0(names, modelFieldConverters, field, metaCrossing, metaData);
                result.fill(convertResult);
                // 非核心元模型Field转换
                convert(result, names, metaModels, field, metaCrossing, metaData);
            }
        }, SCAN_FIELD);
        TimeWatcher.watch(() -> {
            MetaNames names;
            // 函数转换
            for (Method method : methods) {
                if (method.isBridge() || Modifier.isPrivate(method.getModifiers())) {
                    continue;
                }
                if (ArrayUtils.isEmpty(method.getDeclaredAnnotations())) {
                    continue;
                }
                names = new MetaNames();
                names.setModule(moduleNames.getModule());
                names.setModel(NamespaceAndFunUtils.namespace(method));
                Result convertResult = convert0(names, functionConverters, method, metaCrossing, metaData);
                result.fill(convertResult);
                // 非核心元模型Method转换
                convert(result, names, metaModels, method, metaCrossing, metaData);
            }
        }, SCAN_FUNCTION);
    }

    @SuppressWarnings({"rawtypes"})
    private <D> void convert(Result<?> result, MetaNames names, List<MetaModel> metaModels, D source,
                             MetaCrossing metaCrossing, MetaData metaData) {
        for (MetaModel metaModel : metaModels) {
            if (metaModel.getCore().contains(source.getClass())) {
                continue;
            }
            String key = fetchConverterKey(metaModel.getGroup(), source);
            List<ModelConverter> converters = metaModelConvertersMap.get(key);
            if (CollectionUtils.isEmpty(converters)) {
                continue;
            }
            Result<?> convertResult = convert0(names, converters, source, metaCrossing, metaData);
            result.fill(convertResult);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private <D> Result convert0(MetaNames names, List<ModelConverter> modelConverters, D source, MetaCrossing metaCrossing, MetaData metaData) {
        Result result = new Result();
        ExecuteContext validateContext = new ExecuteContext();
        boolean signSuccess = true;
        for (ModelConverter converter : modelConverters) {
            Result validateResult = converter.validate(validateContext, names, source);
            result.addMessages(validateResult.getMessages());
            if (validateContext.isBroken()) {
                break;
            }
            if (!validateResult.isSuccess()) {
                continue;
            }

            Pair<Result<Object>, String> pairResult = fetchMetaObject(names, source, converter, metaData);
            Result metaResult = pairResult.getKey();
            result.fill(metaResult);
            if (!metaResult.isSuccess()) {
                if (!CollectionUtils.isEmpty(metaResult.getMessages())) {
                    signSuccess = false;
                }
                continue;
            }
            String sign = pairResult.getValue();
            if (null != sign && metaCrossing.isCrossingFromIgnoreModule(converter.group(), sign)) {
                continue;
            }

            Object meta = metaResult.getData();
            meta = converter.convert(names, source, meta);

            if (null == meta) {
                continue;
            }
            ConverterType converterType = converterType(converter);
            if (!ConverterType.object.equals(converterType)) {
                ModelSigner modelSigner = null;
                Collection<MetaBaseModel> dataList = ConverterType.map.equals(converterType)
                        ? ((Map<String, MetaBaseModel>) meta).values() : (Collection<MetaBaseModel>) meta;
                for (MetaBaseModel o : dataList) {
                    if (null == modelSigner) {
                        String metaModelClazz = Optional.ofNullable(converter.metaModelClazz()).map(Class::getName).orElse(null);
                        if (StringUtils.isBlank(metaModelClazz)) {
                            metaModelClazz = fetchActualModelFromConverter(converter.getClass());
                        }
                        Result<ModelSigner> modelSignerResult = fetchModelSigner(metaModelClazz);
                        result.fill(modelSignerResult);
                        if (!modelSignerResult.isSuccess()) {
                            return result;
                        }
                        modelSigner = modelSignerResult.getData();
                    }
                    o.setSign(modelSigner.sign(o));
                    resolveDiff(names, metaData, o);
                }
            } else {
                resolveDiff(names, metaData, (MetaBaseModel) meta);
            }
            result.setData(meta);
            metaData.addData(meta);
        }
        result.setSuccess(signSuccess && validateContext.isSuccess());
        return result;
    }

    private void resolveDiff(MetaNames names, MetaData metaData, MetaBaseModel o) {
        Models.modelDirective().disableMetaCompleted(o);
        String oldModule = metaData.removeCrossingExtendData(o.getSignModel(), o.getSign());
        if (null != oldModule && !oldModule.equals(names.getModule())) {
            metaData.addDiffModelData(o.getSignModel(), o.getSign(),
                    new ModelData().setModule(oldModule).setLoadModule(names.getModule())
                            .code(o.getSignModel(), o.getSign()));
        }
    }

    private <T> String fetchConverterKey(String metaModelClassName, T source) {
        return metaModelClassName + CharacterConstants.SEPARATOR_OCTOTHORPE + source.getClass().getName();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private <D, T> Pair<Result<T>, String> fetchMetaObject(MetaNames names, D source, ModelConverter modelConverter, MetaData metaData) {
        Result<T> result = new Result<>();
        Pair<Result<T>, String> pairResult = new MutablePair<>(result, null);
        String metaModelClazz = Optional.ofNullable(modelConverter.metaModelClazz()).map(Class::getName).orElse(null);
        if (StringUtils.isBlank(metaModelClazz)) {
            metaModelClazz = fetchActualModelFromConverter(modelConverter.getClass());
        }
        T meta;
        ConverterType converterType = converterType(modelConverter);
        if (ConverterType.list.equals(converterType)) {
            meta = (T) fetchMetaModelMapWithSigns(names, source, modelConverter, metaData).values();
        } else if (ConverterType.map.equals(converterType)) {
            meta = (T) fetchMetaModelMapWithSigns(names, source, modelConverter, metaData);
        } else {
            String sign = modelConverter.sign(names, source);
            if (StringUtils.isBlank(sign)) {
                Result<ModelReflectSigner> modelSignerResult = fetchModelReflectSigner(metaModelClazz, source);
                result.fill(modelSignerResult);
                if (!modelSignerResult.isSuccess()) {
                    return pairResult;
                }
                sign = modelSignerResult.getData().sign(names, source);
            }
            if (StringUtils.isBlank(sign)) {
                result.error();
                return pairResult;
            }
            pairResult.setValue(sign);
            meta = (T) metaData.getDataItem(modelConverter.group(), sign);
            if (null == meta) {
                try {
                    Class<?> clazz = modelConverter.metaModelClazz();
                    if (null == clazz) {
                        Type type = TypeUtils.getInterfaceGenericType(modelConverter.getClass());
                        assert type != null;
                        String modelClazz = StringUtils.substringBefore(type.getTypeName(), CharacterConstants.SEPARATOR_LT);
                        clazz = Class.forName(modelClazz);
                    }
                    meta = (T) clazz.newInstance();
                    ((MetaBaseModel) meta).setSign(sign);
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                    throw PamirsException.construct(BASE_CLASS_NOT_FOUNT_ERROR, e).errThrow();
                }
            }
            FieldUtils.setFieldValue(meta, VariableNameConstants.entityModel, modelConverter.group());
        }
        result.setData(meta);
        return pairResult;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private <D> Map fetchMetaModelMapWithSigns(MetaNames names, D source, ModelConverter modelConverter, MetaData metaData) {
        Map meta;
        List<String> signs = modelConverter.signs(names, source);
        if (CollectionUtils.isEmpty(signs)) {
            meta = new LinkedHashMap();
        } else {
            meta = new LinkedHashMap(signs.size());
            for (String sign : signs) {
                Object data = metaData.getDataItem(modelConverter.group(), sign);
                if (null != data) {
                    meta.put(sign, data);
                }
            }
        }
        return meta;
    }

    @SuppressWarnings({"rawtypes"})
    private ConverterType converterType(ModelConverter modelConverter) {
        if (null != modelConverter.type()) {
            return modelConverter.type();
        } else {
            return converterType(modelConverter.getClass());
        }
    }

    @SuppressWarnings({"rawtypes"})
    private ConverterType converterType(Class converterClass) {
        Type type = TypeUtils.getInterfaceGenericType(converterClass);
        assert type != null;
        String modelClazz = StringUtils.substringBefore(type.getTypeName(), CharacterConstants.SEPARATOR_LT);
        try {
            Class clazz = Class.forName(modelClazz);
            if (TypeUtils.isCollection(clazz)) {
                return ConverterType.list;
            } else if (TypeUtils.isMap(clazz)) {
                return ConverterType.map;
            }
            return ConverterType.object;
        } catch (ClassNotFoundException e) {
            throw PamirsException.construct(BASE_CLASS_NOT_FOUNT_ERROR, e).errThrow();
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private <D> Result<ModelReflectSigner> fetchModelReflectSigner(String metaModelClazzName, D source) {
        Result result = new Result();
        String converterKey = fetchConverterKey(metaModelClazzName, source);
        ModelReflectSigner modelSigner = metaModelReflectSignersMap.get(converterKey);
        if (null == modelSigner) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                    .error(BASE_MODEL_NO_REFLECT_SIGNER_ERROR)
                    .append(MessageFormat.format("请配置元模型{0}的签名器ModelReflectSigner，source:{1}",
                            metaModelClazzName, source.getClass().getName())));
            return result.error();
        }
        result.setData(modelSigner);
        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Result<ModelSigner> fetchModelSigner(String metaModelClazzName) {
        Result result = new Result();
        ModelSigner modelSigner = metaModelSignersMap.get(metaModelClazzName);
        if (null == modelSigner) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                    .error(BASE_MODEL_NO_REFLECT_SIGNER_ERROR)
                    .append(MessageFormat.format("请配置元模型{0}的签名器ModelSigner",
                            metaModelClazzName)));
            return result.error();
        }
        result.setData(modelSigner);
        return result;
    }

    @SuppressWarnings({"rawtypes"})
    private String fetchActualModelFromConverter(Class converterClass) {
        Type type = TypeUtils.getInterfaceGenericType(converterClass);
        ConverterType converterType = converterType(converterClass);
        if (ConverterType.list.equals(converterType)) {
            return StringUtils.substringBefore(StringUtils.substringAfter(Objects.requireNonNull(type).getTypeName(),
                    CharacterConstants.SEPARATOR_LT), CharacterConstants.SEPARATOR_GT).trim();
        } else if (ConverterType.map.equals(converterType)) {
            return StringUtils.substringBefore(StringUtils.substringAfter(Objects.requireNonNull(type).getTypeName(),
                    CharacterConstants.SEPARATOR_COMMA), CharacterConstants.SEPARATOR_GT).trim();
        } else {
            return Objects.requireNonNull(type).getTypeName();
        }
    }

    @SuppressWarnings({"rawtypes"})
    private String fetchSourceClassFromConverter(Class converterClass) {
        Type type = Objects.requireNonNull(TypeUtils.getInterfaceGenericTypes(converterClass))[1];
        return type.getTypeName();
    }

}
