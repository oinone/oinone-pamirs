package pro.shushi.pamirs.boot.standard.spi;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.core.annotation.Order;
import org.springframework.data.util.CastUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.model.ServerAction;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.spi.api.meta.MetaDataSaverApi;
import pro.shushi.pamirs.boot.common.spi.api.meta.MetaUpgradeCheckApi;
import pro.shushi.pamirs.boot.common.supplier.MetaDataSupplier;
import pro.shushi.pamirs.boot.standard.utils.MetadataUpdateChecker;
import pro.shushi.pamirs.framework.common.utils.DataShardingHelper;
import pro.shushi.pamirs.framework.common.utils.ObjectUtils;
import pro.shushi.pamirs.framework.compare.DiffService;
import pro.shushi.pamirs.framework.compare.model.DiffModel;
import pro.shushi.pamirs.framework.configure.db.service.MetaService;
import pro.shushi.pamirs.framework.connectors.data.tx.transaction.Tx;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.compute.definition.MetaDataPreStoreComputer;
import pro.shushi.pamirs.meta.api.core.orm.WriteApi;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.api.session.cache.spi.SessionFillExtendApi;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.common.util.ListUtils;
import pro.shushi.pamirs.meta.common.util.TimeWatcher;
import pro.shushi.pamirs.meta.constant.FunctionAttributeConstants;
import pro.shushi.pamirs.meta.domain.ModelData;
import pro.shushi.pamirs.meta.domain.fun.ExtPoint;
import pro.shushi.pamirs.meta.domain.fun.ExtPointImplementation;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.domain.model.SequenceConfig;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

import java.util.*;
import java.util.function.Function;

/**
 * 元数据加载API
 * <p>
 * 2020/8/27 5:53 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
@Order(66)
@Component
@SPI.Service
public class StandardMetaDataSaver implements MetaDataSaverApi {

    @Override
    public void save(AppLifecycleCommand command, Map<String/*module*/, Meta> metaMap, Set<String> excludeModules) {
        boolean updateMeta = command.getOptions().isUpdateMeta();
        boolean refreshSessionMeta = command.getOptions().isRefreshSessionMeta();
        if (!updateMeta && !refreshSessionMeta) {
            return;
        }

        Map<String, ModelData> diffLoadMap = new HashMap<>();
        // step1、收集数据
        Map<String/*model*/, Map<String/*sign*/, DiffModel>> dataListMap = storeMeta(metaMap, excludeModules, diffLoadMap);
        // step2、保存数据
        long start = System.currentTimeMillis();
        storeMetaData(metaMap, dataListMap, diffLoadMap, updateMeta);
        log.info("Metadata saving completed {}ms", System.currentTimeMillis() - start);
    }

    private void storeMetaData(Map<String/*module*/, Meta> metaMap,
                               Map<String/*model*/, Map<String/*sign*/, DiffModel>> dataListMap,
                               Map<String, ModelData> diffLoadMap, boolean updateMeta) {
        List<MetaDataPreStoreComputer<MetaBaseModel>> preComputers = CastUtils.cast(BeanDefinitionUtils.getBeansOfTypeByOrdered(MetaDataPreStoreComputer.class));
        for (String model : MetaService.get().sortModelSet(dataListMap.keySet())) {
            if (ModelField.MODEL_MODEL.equals(model) || FunctionDefinition.MODEL_MODEL.equals(model)) {
                continue;
            }
            Map<String, DiffModel> dataMap = dataListMap.get(model);
            if (MapUtils.isNotEmpty(dataMap)) {
                compute(metaMap, preComputers, model, dataMap);
                storeMetaData(model, dataMap, updateMeta);
            }
        }
        // 字段处理
        Map<String, DiffModel> modelFieldMap = dataListMap.get(ModelField.MODEL_MODEL);
        if (MapUtils.isNotEmpty(modelFieldMap)) {
            compute(metaMap, preComputers, ModelField.MODEL_MODEL, modelFieldMap);
            storeMetaData(ModelField.MODEL_MODEL, modelFieldMap, updateMeta);
        }
        // 函数处理
        Map<String, DiffModel> functionDefinitionMap = dataListMap.get(FunctionDefinition.MODEL_MODEL);
        if (MapUtils.isNotEmpty(functionDefinitionMap)) {
            compute(metaMap, preComputers, FunctionDefinition.MODEL_MODEL, functionDefinitionMap);
            storeMetaData(FunctionDefinition.MODEL_MODEL, functionDefinitionMap, updateMeta);
        }

        // 字段处理--字段变更后处理对用模型的缓存
        storeFieldModelSession(dataListMap);

        if (updateMeta) {
            // 注册安装表变更处理（元数据未变更）
            Tx.build().executeWithoutResult((status) -> {
                List<ModelData> modelDataList = ListUtils.toList(diffLoadMap.values());
                List<List<ModelData>> modelDataListGroup = ListUtils.fixedGrouping(modelDataList, 200);
                if (CollectionUtils.isNotEmpty(modelDataListGroup)) {
                    for (List<ModelData> oneModelDataList : modelDataListGroup) {
                        CommonApiFactory.getApi(WriteApi.class).createOrUpdateBatch(oneModelDataList);
                    }
                }
            });
        }
    }

    private Map<String/*model*/, Map<String/*sign*/, DiffModel>> storeMeta(Map<String/*module*/, Meta> metaMap, Set<String> excludeModules, Map<String, ModelData> diffLoadMap) {
        Map<String/*model*/, Map<String/*sign*/, DiffModel>> dataListMap = new LinkedHashMap<>(16);
        for (Meta meta : metaMap.values()) {
            String module = meta.getModule();
            if (null != excludeModules && excludeModules.contains(module)) {
                continue;
            }
            MetaData metaData = meta.getCurrentModuleData();
            Map<String/*meta model*/, Map<String/*model sign*/, MetaBaseModel>> data = metaData.getData();
            if (null != data) {
                // 模型、字典、服务器动作、扩展点、扩展点实现、拦截器
                for (String model : data.keySet()) {
                    List<MetaBaseModel> dataList = ListUtils.toList(data.get(model).values());
                    if (CollectionUtils.isNotEmpty(dataList)) {
                        collectStoreData(meta, metaData, model, pro.shushi.pamirs.meta.common.util.MapUtils.computeIfAbsent(dataListMap, model,
                                        k -> new HashMap<>(dataList.size())), dataList,
                                v -> fetchLoadModule(meta, module, model, v),
                                v -> fetchSourceModule(metaData, module, model, v));
                    }
                }
                // 获取字段
                List<ModelField> moduleModelFieldList = metaData.getModelFieldList();
                if (CollectionUtils.isNotEmpty(moduleModelFieldList)) {
                    collectStoreData(meta, metaData, ModelField.MODEL_MODEL, pro.shushi.pamirs.meta.common.util.MapUtils.computeIfAbsent(dataListMap, ModelField.MODEL_MODEL,
                                    k -> new HashMap<>(moduleModelFieldList.size())), moduleModelFieldList,
                            v -> fetchLoadModule(meta, module, ModelField.MODEL_MODEL, v),
                            v -> fetchSourceModule(metaData, module, ModelField.MODEL_MODEL, v));
                }
                // 获取函数
                List<FunctionDefinition> moduleFunctionDefinitionList = metaData.getModelFunctionList();
                if (CollectionUtils.isNotEmpty(moduleFunctionDefinitionList)) {
                    collectStoreData(meta, metaData, FunctionDefinition.MODEL_MODEL, pro.shushi.pamirs.meta.common.util.MapUtils.computeIfAbsent(dataListMap, FunctionDefinition.MODEL_MODEL,
                                    k -> new HashMap<>(moduleFunctionDefinitionList.size())), moduleFunctionDefinitionList,
                            v -> fetchLoadModule(meta, module, FunctionDefinition.MODEL_MODEL, v),
                            v -> fetchSourceModule(metaData, module, FunctionDefinition.MODEL_MODEL, v));
                }
            }
            // 获取安装注册信息变更表
            diffLoadMap.putAll(metaData.getDiffLoadMap());
        }

        return dataListMap;
    }

    private <T extends MetaBaseModel> void collectStoreData(Meta meta, MetaData metaData, String group,
                                                            Map<String/*sign*/, DiffModel> collectMap, List<T> dataList,
                                                            Function<MetaBaseModel, String> loadModuleFetcher,
                                                            Function<MetaBaseModel, String> sourceModuleFetcher) {
        for (MetaBaseModel metaBaseModel : dataList) {
            String sign = metaBaseModel.getSign();
            String module = sourceModuleFetcher.apply(metaBaseModel);
            String loadModule = null;
            SystemSourceEnum systemSource = metaBaseModel.getSystemSource();
            if (collectMap.containsKey(sign)) {
                // 元数据已经存在，
                // 如果传入元数据为跨模块扩展，则覆盖原元数据
                // 如果传入元数据不是跨模块扩展而是自定义元数据，则覆盖原元数据
                // 否则丢弃
                DiffModel originDiffModel = collectMap.get(sign);
                String originModule = originDiffModel.getModule();
                if (module.equals(originModule)) {// 非跨模块
                    if (SystemSourceEnum.isInherited(systemSource)
                            || SystemSourceEnum.RELATION.equals(systemSource)) {// 非自定义数据
                        continue;
                    }
                }
                // 只存储启动模块加载的元数据
                loadModule = loadModuleFetcher.apply(metaBaseModel);
                if (!module.equals(loadModule)) {
                    continue;
                }
            }
            if (null == loadModule) {
                loadModule = loadModuleFetcher.apply(metaBaseModel);
            }
            // 只存储启动模块未跨模块重载的元数据
            ModelData crossModelData = metaData.removeDiffModelData(group, sign);
            if (crossModelData == null || (module.equals(loadModule) && meta.isInBootModules(crossModelData.getModule()))) {
                collectMap.put(sign, new DiffModel().setModelObject(metaBaseModel).setModule(module).setLoadModule(loadModule).setSign(sign));
            }
        }
    }

    private void storeMetaData(String model, Map<String/*sign*/, DiffModel> dataMap, Boolean updateMeta) {
        int size = dataMap.size();
        if (size == 0) {
            log.info("No metadata change: {}", model);
            return;
        }

        Map<String/*module*/, List<DiffModel>> cloneDataMap = new HashMap<>(32);
        List<MetaBaseModel> dataList = Collections.synchronizedList(new ArrayList<>(size));
        Map<String, MetaBaseModel> fillIdDataMap = new HashMap<>();
        TimeWatcher.watch(() -> {
            for (DiffModel diffModel : dataMap.values()) {
                if (!Spider.getDefaultExtension(DiffService.class).diff(diffModel.getModelObject())) {
                    continue;
                }
                String module = diffModel.getModule();
                MetaBaseModel originData = diffModel.getModelObject();
                fillIdDataMap.put(diffModel.getSign() + CharacterConstants.SEPARATOR_OCTOTHORPE + originData.getSign(), originData);
                MetaBaseModel targetData = ObjectUtils.clone(originData);
                pro.shushi.pamirs.meta.common.util.MapUtils.computeIfAbsent(cloneDataMap, module,
                        k -> new ArrayList<>()).add(diffModel.setModelObject(targetData));

                // 处理元数据基础信息
                if (null == targetData.getCreateDate()) {
                    Date writeDate = new Date();
                    targetData.setCreateDate(writeDate);
                    targetData.setWriteDate(writeDate);
                }
                targetData.unsetId();
                targetData.disableMetaDiffing().disableMetaCompleted().disableMetaCrossing();
                dataList.add(targetData);

                //2、如果字段更新了，则对应的模型也需做对应的更新
                if (ModelField.MODEL_MODEL.equals(model)) {
                    MetaDataSupplier.addDiffModel(((ModelField) targetData).getModel());
                }

                //3、如果模型的函数更新了，则对应的模型也需做对应的更新
                if (FunctionDefinition.MODEL_MODEL.equals(model)) {
                    MetaDataSupplier.addDiffModel(((FunctionDefinition) targetData).getNamespace());
                }
            }
        }, "差量安装计算: " + model, "total: " + size);

        if ((updateMeta || MetadataUpdateChecker.shouldUpdateMetadata(model)) && !dataList.isEmpty()) {
            TimeWatcher.watch(() -> Tx.build().executeWithoutResult((status) -> {

                // 保存元数据
                if (log.isDebugEnabled()) {
                    DataShardingHelper.build().collectionSharding(dataList, sublist -> {
                        long start = System.currentTimeMillis();
                        log.debug("{} createOrUpdateBatch Metadata save count: {}", model, sublist.size());
                        CommonApiFactory.getApi(WriteApi.class).createOrUpdateBatch(sublist);
                        log.debug("{} createOrUpdateBatch Metadata cost time: {}ms", model, System.currentTimeMillis() - start);
                        return sublist;
                    });
                } else {
                    DataShardingHelper.build().collectionSharding(dataList, sublist -> {
                        CommonApiFactory.getApi(WriteApi.class).createOrUpdateBatch(sublist);
                        return sublist;
                    });
                }

                // 生成安装注册表
                List<ModelData> modelDataList = new ArrayList<>(dataList.size());
                for (String module : cloneDataMap.keySet()) {
                    List<DiffModel> moduleDataList = cloneDataMap.get(module);
                    for (DiffModel diffModel : moduleDataList) {
                        MetaBaseModel data = diffModel.getModelObject();

                        // 回填内存元数据ID
                        Long id = data.getId();
                        String fillIdDataKey = diffModel.getSign() + CharacterConstants.SEPARATOR_OCTOTHORPE + data.getSign();
                        MetaBaseModel originData = fillIdDataMap.get(fillIdDataKey);
                        if (originData == null) {
                            log.error("origin metadata not found. key: {}", fillIdDataKey);
                        } else {
                            originData.setId(id);
                        }

                        // 添加注册表信息
                        modelDataList.add(new ModelData()
                                .setModule(module).setLoadModule(diffModel.getLoadModule())
                                .code(model, data.getSign())
                                .setModel(model).setResId(id)
                                .setLowCode(false).setSource(data.getSystemSource())
                                .setDateInit(data.getCreateDate()).setDateUpdate(new Date())
                        );
                    }
                }

                if (!modelDataList.isEmpty()) {
                    // 保存安装注册表
                    if (log.isDebugEnabled()) {
                        DataShardingHelper.build().collectionSharding(modelDataList, sublist -> {
                            long start = System.currentTimeMillis();
                            log.debug("{} createOrUpdateBatch ModelData save count: {}", model, sublist.size());
                            CommonApiFactory.getApi(WriteApi.class).createOrUpdateBatch(sublist);
                            log.debug("{} createOrUpdateBatch ModelData cost time: {}ms", model, System.currentTimeMillis() - start);
                            return sublist;
                        });
                    } else {
                        DataShardingHelper.build().collectionSharding(modelDataList, sublist -> {
                            CommonApiFactory.getApi(WriteApi.class).createOrUpdateBatch(sublist);
                            return sublist;
                        });
                    }
                }

                MetaUpgradeCheckApi.get().saveMetadataToDB(model);
            }), "保存元数据: " + model, "install count: " + dataList.size());
        }

        // Session填充Redis操作, 更新缓存中的元数据; 更新元数据的操作需要放置到更新DB后面，更新DB存在修改数据的情况。
        // 重要提示：元数据更新必须放在DB更新完成后面，否则会有并发问题。
        Spider.getDefaultExtension(SessionFillExtendApi.class).updateMetaData(model, dataList);
    }

    private void storeFieldModelSession(Map<String/*model*/, Map<String/*sign*/, DiffModel>> dataListMap) {
        Set<String/*Model*/> diffModelSigns = MetaDataSupplier.getDiffModel();
        if (CollectionUtils.isEmpty(diffModelSigns)) {
            return;
        }

        List<MetaBaseModel> dataList = Collections.synchronizedList(new ArrayList<>());
        TimeWatcher.watch(() -> {
            Map<String/*module*/, List<DiffModel>> cloneDataMap = new HashMap<>(32);
            Map<String, DiffModel> modelDataMap = dataListMap.get(ModelDefinition.MODEL_MODEL);
            for (String fieldModel : diffModelSigns) {
                DiffModel diffModel = modelDataMap.get(fieldModel);
                if (diffModel != null) {
                    String module = diffModel.getModule();
                    MetaBaseModel data = ObjectUtils.clone(diffModel.getModelObject());
                    pro.shushi.pamirs.meta.common.util.MapUtils.computeIfAbsent(cloneDataMap, module, k -> new ArrayList<>())
                            .add(diffModel.setModelObject(data));
                    dataList.add(data);
                }
            }
        }, "差量计算-模型变更的Model计算结果total: " + dataList.size());

        Spider.getDefaultExtension(SessionFillExtendApi.class).updateMetaData(ModelDefinition.MODEL_MODEL, dataList);
    }

    private void compute(Map<String/*module*/, Meta> metaMap, List<MetaDataPreStoreComputer<MetaBaseModel>> preComputers, String model, Map<String/*sign*/, DiffModel> dataMap) {
        for (MetaDataPreStoreComputer<MetaBaseModel> computer : preComputers) {
            if (computer.canCompute(model)) {
                for (String sign : dataMap.keySet()) {
                    DiffModel diffModel = dataMap.get(sign);
                    if (null == diffModel) {
                        continue;
                    }
                    if (Models.modelDirective().isMetaCompleted(diffModel.getModelObject())) {
                        continue;
                    }
                    computer.compute(metaMap, model, diffModel.getModule(), diffModel.getModelObject());
                }
            }
        }
    }

    private String fetchLoadModule(Meta meta, String currentModule, String model, MetaBaseModel data) {
        ModelDefinition modelDefinition;
        switch (model) {
            case ModelField.MODEL_MODEL:
                modelDefinition = meta.getModel(((ModelField) data).getModel());
                return fetchCrossingLoadModule(meta, currentModule, model, modelDefinition, data);
            case FunctionDefinition.MODEL_MODEL:
                modelDefinition = meta.getModel(((FunctionDefinition) data).getNamespace());
                String loadModule = fetchCrossingLoadModule(meta, currentModule, model, modelDefinition, data);
                data.addAttribute(FunctionAttributeConstants.LOAD_MODULE, loadModule);
                return loadModule;
            case ServerAction.MODEL_MODEL:
                modelDefinition = meta.getModel(((ServerAction) data).getModel());
                return fetchCrossingLoadModule(meta, currentModule, model, modelDefinition, data);
            case ExtPoint.MODEL_MODEL:
                modelDefinition = meta.getModel(((ExtPoint) data).getNamespace());
                return fetchCrossingLoadModule(meta, currentModule, model, modelDefinition, data);
            case ExtPointImplementation.MODEL_MODEL:
                modelDefinition = meta.getModel(((ExtPointImplementation) data).getNamespace());
                return fetchCrossingLoadModule(meta, currentModule, model, modelDefinition, data);
            case SequenceConfig.MODEL_MODEL:
                return ((SequenceConfig) data).getModule();
            default:
                return fetchCrossingLoadModule(meta, currentModule, model, null, data);
        }
    }

    private String fetchCrossingLoadModule(Meta meta, String currentModule, String model, ModelDefinition modelDefinition, MetaBaseModel data) {
        String loadModule = null;
        if (null != modelDefinition) {
            loadModule = modelDefinition.getModule();
        } else {
            String sign = data.getSign();
            for (String module : meta.getData().keySet()) {
                MetaData metaData = meta.getData().get(module);
                if (metaData.isCrossingExtendData(model, sign)) {
                    loadModule = module;
                    break;
                }
            }
        }
        if (null != loadModule) {
            return loadModule;
        }
        if (ModelDefinition.MODEL_MODEL.equals(model)) {
            loadModule = ((ModelDefinition) data).getModule();
        }
        if (null == loadModule) {
            loadModule = currentModule;
        }
        return loadModule;
    }

    private String fetchSourceModule(MetaData metaData, String currentModule, String model, MetaBaseModel data) {
        String sign = data.getSign();
        String sourceModule = metaData.getCrossingModule(model, sign);
        if (null != sourceModule) {
            return sourceModule;
        }
        if (ModelDefinition.MODEL_MODEL.equals(model)) {
            sourceModule = ((ModelDefinition) data).getModule();
        }
        if (sourceModule == null) {
            sourceModule = currentModule;
        }
        return sourceModule;
    }

}
