package pro.shushi.pamirs.framework.configure.db.service.impl;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.framework.common.utils.DataShardingHelper;
import pro.shushi.pamirs.framework.common.utils.ObjectUtils;
import pro.shushi.pamirs.framework.compare.DiffService;
import pro.shushi.pamirs.framework.compare.MetaDataLoadExtend;
import pro.shushi.pamirs.framework.configure.db.mapper.ModelDataMapper;
import pro.shushi.pamirs.framework.configure.db.model.ModelDataStatic;
import pro.shushi.pamirs.framework.configure.db.service.MetaService;
import pro.shushi.pamirs.framework.configure.inject.api.AutoInjectService;
import pro.shushi.pamirs.framework.configure.simulate.api.MetaSimulateService;
import pro.shushi.pamirs.framework.configure.simulate.service.MetaSimulator;
import pro.shushi.pamirs.framework.connectors.data.dialect.Dialects;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.TableMetaDialectService;
import pro.shushi.pamirs.framework.connectors.data.mapper.GenericMapper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.util.DataConfigurationHelper;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.configure.MetaModelFetcher;
import pro.shushi.pamirs.meta.api.core.data.DsApi;
import pro.shushi.pamirs.meta.api.core.faas.boot.ModulesApi;
import pro.shushi.pamirs.meta.api.core.orm.convert.DataConverter;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.api.dto.meta.MetaModel;
import pro.shushi.pamirs.meta.api.prefix.DataPrefixManager;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.constant.FieldConstants;
import pro.shushi.pamirs.meta.domain.ModelData;
import pro.shushi.pamirs.meta.domain.module.ModuleCategory;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;
import pro.shushi.pamirs.meta.util.ParallelStreamHelper;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static pro.shushi.pamirs.meta.annotation.sys.MetaSimulator.SIMULATE_PREFIX;

/**
 * 模块元数据服务实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/15 6:03 下午
 */
@Slf4j
@Order
@Component
@SPI.Service
public class DefaultMetaService implements MetaService {

    @Resource
    protected MetaModelFetcher metaModelFetcher;

    @Resource
    protected ModelDataMapper modelDataMapper;

    @Resource
    protected GenericMapper genericMapper;

    @Resource
    protected DataConverter persistenceDataConverter;

    @Resource
    protected MetaSimulateService metaSimulateService;

    @Resource
    protected AutoInjectService autoInjectService;

    @Override
    public Map<String, MetaData> loadMetaDataMap(Set<String> modules, Consumer<MetaBaseModel> directive, Supplier<Boolean> preAction) {
        return metaSimulateService.transientStaticExecute(MetaSimulator.simulate(), () -> {
            Map<String, MetaData> metaDataMap = new ConcurrentHashMap<>(modules.size());

            String dsKey = DsApi.get().baseDsKey(ModelData.MODEL_MODEL);
            String modelDataTableName = DataPrefixManager.tablePrefix(ModuleConstants.MODULE_BASE, ModelData.MODEL_MODEL, ModelData.TABLE_NAME);
            if (!Dialects.component(TableMetaDialectService.class, dsKey).existTable(dsKey, modelDataTableName)) {
                return metaDataMap;
            }

            // 执行前置动作
            preAction.get();

            // 装载元数据
            for (String module : modules) {
                long start = System.currentTimeMillis();
                MetaData metaData = loadMetaData(module, directive);
                if (null != metaData) {
                    metaDataMap.putIfAbsent(module, metaData);
                }
                long end = System.currentTimeMillis();
                log.info("[{}]模块,装载元数据,time:[{}]ms", module, end - start);
            }

            return metaDataMap;
        });
    }

    @Override
    public MetaData loadMetaData(String module, Consumer<MetaBaseModel> directive) {
        MetaData metaData = new MetaData();
        List<String> metaModels = metaModelFetcher.fetchMetaModels();
        List<ModelDataStatic> modelDataList;
        if (log.isDebugEnabled()) {
            long start = System.currentTimeMillis();
            modelDataList = modelDataMapper.selectListByEntity((ModelDataStatic) new ModelDataStatic().setLoadModule(module));
            log.debug("query {} metadata [{}] cost time: {}ms", module, modelDataList.size(), System.currentTimeMillis() - start);
        } else {
            modelDataList = modelDataMapper.selectListByEntity((ModelDataStatic) new ModelDataStatic().setLoadModule(module));
        }
        Map<String, List<Long>> resIdMap = new HashMap<>();
        Map<String, Map<Long, ModelData>> modelDataMap = new HashMap<>();
        for (ModelData modelData : modelDataList) {
            String model = modelData.getModel();
            resIdMap.putIfAbsent(model, new ArrayList<>());
            if (metaModels.contains(model)) {
                resIdMap.get(model).add(modelData.getResId());
                modelDataMap.putIfAbsent(model, new HashMap<>());
                modelDataMap.get(model).put(modelData.getResId(), modelData);
            }

            if (!modelData.getModule().equals(modelData.getLoadModule())) {
                String sign = StringUtils.substringAfter(modelData.getCode(), CharacterConstants.SEPARATOR_OCTOTHORPE);
                metaData.addCrossingExtendData(model, sign, modelData.getModule());
            }
        }
        // 装载元数据
        for (String model : sortModelSet(resIdMap.keySet())) {
            loadMetaDataForModel(metaData, module, model, resIdMap, modelDataMap.get(model), directive);
        }
        return metaData;
    }

    @Override
    public void crossingLoadMetaData(Map<String, MetaData> metaDataMap) {
        Set<String> runModuleSet = Spider.getDefaultExtension(ModulesApi.class).modules();
        // 跨模块挂载元数据
        for (String module : metaDataMap.keySet()) {
            long start = System.currentTimeMillis();
            crossingLoadMetaData(metaDataMap, runModuleSet, module);
            long end = System.currentTimeMillis();
            log.info("[{}]模块,跨模块挂载元数据,time:[{}]ms", module, end - start);
        }
    }

    protected void loadMetaDataForModel(MetaData metaData,
                                        String module,
                                        String model,
                                        Map<String, List<Long>> resIdMap,
                                        Map<Long, ModelData> modelDataMap,
                                        Consumer<MetaBaseModel> directive) {
        List<Long> ids = resIdMap.get(model);
        if (CollectionUtils.isEmpty(ids) || MapUtils.isEmpty(modelDataMap)) {
            return;
        }

        List<DataMap> dataMapList;
        if (log.isDebugEnabled()) {
            long start = System.currentTimeMillis();
            dataMapList = loadMetaDataMapList(model, ids);
            log.debug("[{}] load metadata model: {}, size: {}, cost time: {}ms", module, model, ids.size(), System.currentTimeMillis() - start);
        } else {
            dataMapList = loadMetaDataMapList(model, ids);
        }

        List<MetaBaseModel> dataList = persistenceDataConverter.out(model, dataMapList);
        MetaDataLoadExtend metaDataLoadExtend = Spider.getDefaultExtension(MetaDataLoadExtend.class);
        DiffService diffService = Spider.getDefaultExtension(DiffService.class);
        for (MetaBaseModel data : dataList) {
            Models.api().setModel(data, model);
            autoInjectService.autoInject(model, data);
            directive.accept(data);
            metaDataLoadExtend.handle(modelDataMap.get(data.getId()), data);
            diffService.hash(data);
            metaData.addData(data);
        }
    }

    protected List<DataMap> loadMetaDataMapList(String model, List<Long> ids) {
        int eachShardMax = DataConfigurationHelper.getDataConfiguration().getEachShardMax();
        if (ids.size() <= eachShardMax) {
            return genericMapper.selectList(Pops.<DataMap>query().from(model).in(FieldConstants.ID, ids));
        }
        List<List<Long>> idsGroups = DataShardingHelper.build(eachShardMax).sharding(ids);
        return ParallelStreamHelper.parallelStream(idsGroups)
                .flatMap(sublist -> genericMapper.selectList(Pops.<DataMap>query().from(model).in(FieldConstants.ID, sublist)).stream())
                .collect(Collectors.toList());
    }

    private void crossingLoadMetaData(Map<String, MetaData> metaDataMap, Set<String> runModuleSet, String module) {
        MetaData metaData = metaDataMap.get(module);
        for (String model : metaData.getData().keySet()) {
            List<MetaBaseModel> dataList = metaData.getDataList(model);
            for (MetaBaseModel data : dataList) {
                tagCrossing(metaDataMap, metaData, runModuleSet, model, data);
            }
        }
    }

    private void tagCrossing(Map<String, MetaData> metaDataMap, MetaData metaData, Set<String> runModuleSet, String model, MetaBaseModel data) {
        String sign = data.getSign();
        if (metaData.isCrossingExtendData(model, sign)) {
            String sourceModule = metaData.getCrossingModule(model, sign);
            MetaData sourceMetaData = metaDataMap.get(sourceModule);
            if (sourceMetaData == null) {
                // 未加载的源模块非继承元数据，仅参与元数据计算，不做差量删除
                // 继承元数据保持isMetaCompleted不变
                if (!SystemSourceEnum.isInherited(data.getSystemSource())) {
                    data.disableMetaCompleted();
                }
            } else {
                // 继承元数据向源模块添加元数据
                // 继承计算会将isMetaCompleted置为false
                // 如未经过继承计算的元数据，将被删除
                // 主要用于处理跨模块继承的函数在删除元数据时无法正确删除的问题
                if (SystemSourceEnum.isInherited(data.getSystemSource())) {
                    data.enableMetaCompleted();
                    MetaBaseModel cloneData = ObjectUtils.clone(data);
                    sourceMetaData.addData(cloneData);
                    metaData.addCrossingMetaData(sourceModule, sourceMetaData);
                } else {
                    // 非继承元数据仅在运行模块中处理跨模块元数据
                    // 如未参与元数据计算的元数据，将被源模块删除
                    // 主要用于处理跨模块添加的函数在删除元数据时无法正确删除的问题
                    if (runModuleSet.contains(sourceModule)) {
                        data.enableMetaCompleted();
                        MetaBaseModel cloneData = ObjectUtils.clone(data);
                        sourceMetaData.addData(cloneData);
                        metaData.addCrossingMetaData(sourceModule, sourceMetaData);
                    } else {
                        // 参与元数据计算，不做差量删除
                        data.disableMetaCompleted();
                    }
                }
            }
        }
    }

    @Override
    public void prepareModels(Map<String/*model*/, String/*simulate model*/> modelMap) {
        modelMap.put(ModuleDefinition.MODEL_MODEL, SIMULATE_PREFIX + ModuleDefinition.MODEL_MODEL);
        modelMap.put(ModuleCategory.MODEL_MODEL, SIMULATE_PREFIX + ModuleCategory.MODEL_MODEL);
    }

    @Override
    public Set<String> sortModelSet(Set<String> modelSet) {
        Map<String, Integer> metaModelPriorityMap = metaModelFetcher.fetchMetaModelPriorityMap();
        return sortModelSet(modelSet, metaModelPriorityMap);
    }

    @Override
    public Set<String> sortModelSet(Set<String> modelSet, Map<String, Integer> metaModelPriorityMap) {
        Set<String> sortedModelSet = new LinkedHashSet<>();
        List<MetaModel> models = new ArrayList<>();
        for (String model : modelSet) {
            Integer priority = Optional.ofNullable(metaModelPriorityMap.get(model)).orElse(50);
            models.add(new MetaModel().setGroup(model).setPriority(priority));
        }
        models.sort(Comparator.comparing(MetaModel::getPriority));
        models.stream().map(MetaModel::getGroup).forEachOrdered(sortedModelSet::add);
        return sortedModelSet;
    }

}
