package pro.shushi.pamirs.boot.standard.spi;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.model.UeModel;
import pro.shushi.pamirs.boot.base.model.UeModule;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.spi.api.meta.MetaDataUpgraderApi;
import pro.shushi.pamirs.boot.common.supplier.MetaDataSupplier;
import pro.shushi.pamirs.boot.standard.enmu.BootStandardExpEnumerate;
import pro.shushi.pamirs.boot.standard.utils.MetadataUpdateChecker;
import pro.shushi.pamirs.framework.connectors.data.tx.transaction.Tx;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.WriteApi;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.api.session.cache.spi.SessionFillExtendApi;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.util.ListUtils;
import pro.shushi.pamirs.meta.common.util.TimeWatcher;
import pro.shushi.pamirs.meta.domain.ModelData;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 模块元数据升级加载API
 * <p>
 * 2020/8/27 5:53 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order(66)
@Component
@SPI.Service
public class StandardMetaDataUpgrader implements MetaDataUpgraderApi {

    @Override
    public void diffDelete(AppLifecycleCommand command, List<Meta> metaList,
                           Set<String> includeModuleModules, List<ModuleDefinition> excludeModules) {
        if (!command.getOptions().isDiffMeta()) {
            return;
        }
        boolean reloadMeta = command.getOptions().isReloadMeta();
        boolean updateMeta = command.getOptions().isUpdateMeta();
        boolean refreshSessionMeta = command.getOptions().isRefreshSessionMeta();
        if ((!reloadMeta || !updateMeta) && !refreshSessionMeta) {
            return;
        }

        if (CollectionUtils.isEmpty(metaList)) {
            return;
        }
        Set<String> includeModuleModuleSet = null != includeModuleModules ? new HashSet<>(includeModuleModules) : new HashSet<>();
        Set<String> excludeModuleModuleSet = excludeModules == null ? new HashSet<>() : excludeModules.stream().map(ModuleDefinition::getModule).collect(Collectors.toSet());
        Map<String/*model*/, Map<String/*module*/, List<MetaBaseModel>>> dataListMap = new HashMap<>();
        Map<String, MetaData> metaDataMap = new HashMap<>();
        for (Meta meta : metaList) {
            for (String module : meta.getData().keySet()) {
                if (!includeModuleModuleSet.contains(module)) {
                    continue;
                }
                if (metaDataMap.containsKey(module)) {
                    continue;
                }
                MetaData metaData = meta.getData().get(module);
                metaDataMap.putIfAbsent(module, metaData);
                Map<String/*meta model*/, Map<String/*model sign*/, MetaBaseModel>> data = metaData.getData();
                if (null != data) {
                    // 模型、字典、扩展点
                    for (String model : data.keySet()) {
                        List<MetaBaseModel> dataList = ListUtils.toList(data.get(model).values());
                        if (CollectionUtils.isNotEmpty(dataList)) {
                            List<MetaBaseModel> collectList = pro.shushi.pamirs.meta.common.util.MapUtils.computeIfAbsent(
                                    pro.shushi.pamirs.meta.common.util.MapUtils.computeIfAbsent(dataListMap, model,
                                            k -> new HashMap<>()), module, k -> new ArrayList<>());
                            collectComputeDataList(includeModuleModuleSet, excludeModuleModuleSet,
                                    metaData, model, dataList, collectList);
                        }
                    }
                    // 获取字段
                    List<ModelField> moduleModelFieldList = metaData.getModelFieldList();
                    if (CollectionUtils.isNotEmpty(moduleModelFieldList)) {
                        List<MetaBaseModel> collectList = pro.shushi.pamirs.meta.common.util.MapUtils.computeIfAbsent(
                                pro.shushi.pamirs.meta.common.util.MapUtils.computeIfAbsent(dataListMap, ModelField.MODEL_MODEL,
                                        k -> new HashMap<>()), module, k -> new ArrayList<>());
                        collectComputeDataList(includeModuleModuleSet, excludeModuleModuleSet,
                                metaData, ModelField.MODEL_MODEL, moduleModelFieldList, collectList);
                    }
                    // 获取函数
                    List<FunctionDefinition> moduleFunctionDefinitionList = metaData.getModelFunctionList();
                    if (CollectionUtils.isNotEmpty(moduleFunctionDefinitionList)) {
                        List<MetaBaseModel> collectList = pro.shushi.pamirs.meta.common.util.MapUtils.computeIfAbsent(
                                pro.shushi.pamirs.meta.common.util.MapUtils.computeIfAbsent(dataListMap, FunctionDefinition.MODEL_MODEL,
                                        k -> new HashMap<>()), module, k -> new ArrayList<>());
                        collectComputeDataList(includeModuleModuleSet, excludeModuleModuleSet,
                                metaData, FunctionDefinition.MODEL_MODEL, moduleFunctionDefinitionList, collectList);
                    }
                }
            }
        }
        TimeWatcher.watch(() -> {
            for (String model : dataListMap.keySet()) {
                Map<String, List<MetaBaseModel>> dataMap = dataListMap.get(model);
                removeMeta(includeModuleModuleSet, excludeModuleModuleSet, metaDataMap, model, updateMeta, dataMap);
            }
        }, I18nUtils.getMessage("StandardMetaDataUpgrader.diffComputeAllCompleted"));
    }

    private <T extends R, R extends MetaBaseModel> void collectComputeDataList(Set<String> includeModules,
                                                                               Set<String> excludeModules,
                                                                               MetaData metaData, String model,
                                                                               List<T> dataList, List<R> collectList) {
        for (T data : dataList) {
            if (!Models.modelDirective().isMetaCompleted(data)) {
                continue;
            }
            if (MetadataUpdateChecker.shouldUpdateMetadata(model)) {
                continue;
            }
            collectList.add(data);
        }
    }

    private <T extends MetaBaseModel> void removeMeta(Set<String> includeModules, Set<String> excludeModules,
                                                      Map<String/*module*/, MetaData> metaDataMap,
                                                      String model, Boolean updateMeta,
                                                      Map<String/*module*/, List<T>> dataMap) {
        if (MapUtils.isNotEmpty(dataMap)) {
            List<T> dataList = new ArrayList<>();
            List<ModelData> modelDataList = new ArrayList<>();
            for (String module : dataMap.keySet()) {
                if (!includeModules.contains(module) || excludeModules.contains(module)) {
                    continue;
                }
                List<T> moduleDataList = dataMap.get(module);
                if (CollectionUtils.isNotEmpty(moduleDataList)) {
                    MetaData metaData = metaDataMap.get(module);
                    for (T dataItem : moduleDataList) {
                        if (!UeModel.MODEL_MODEL.equals(model) && !UeModule.MODEL_MODEL.equals(model)) {
                            boolean result = metaData.removeDataItem(dataItem);
                            if (!result) {
                                throw PamirsException.construct(BootStandardExpEnumerate.BASE_UPGRADE_META_DATA_IS_NOT_EXIST_ERROR)
                                        .appendMsg("module:" + module + ",model:" + model + ",sign:" + dataItem.getSign()).errThrow();
                            }
                            dataList.add(dataItem);
                        }
                        modelDataList.add(new ModelData().setLoadModule(module).setCode(model + CharacterConstants.SEPARATOR_OCTOTHORPE + dataItem.getSign()));
                    }
                }
            }
            // 分布式Session删除缓存操作
            handleDistributionSession(model, dataList);

            // 元数据变更新到DB
            if (updateMeta) {
                Tx.build().executeWithoutResult((status) -> {
                    // 删除DB元数据
                    deleteMetaRecord(model, dataList, modelDataList);
                });
            }
        }
    }

    private static <T extends MetaBaseModel> void handleDistributionSession(String model, List<T> dataList) {
        //1、 分布式Session删除缓存操作
        Spider.getDefaultExtension(SessionFillExtendApi.class).deleteMetaData(model, dataList);

        //2、如果字段删除了，则对应的模型也需做对应的更新
        dataList.forEach(data -> {
            if (data instanceof ModelField) {
                MetaDataSupplier.addDiffModel(((ModelField) data).getModel());
            }
        });
    }

    private <T extends MetaBaseModel> void deleteMetaRecord(String model, List<T> dataList, List<ModelData> modelDataList) {
        if (CollectionUtils.isNotEmpty(dataList)) {
            TimeWatcher.watch(() -> CommonApiFactory.getApi(WriteApi.class).deleteByPks(dataList),
                    model + ",Diff delete records size:" + dataList.size());
        }
        if (CollectionUtils.isNotEmpty(modelDataList)) {
            TimeWatcher.watch(() -> CommonApiFactory.getApi(WriteApi.class).deleteByUniques(modelDataList),
                    ",Diff delete modelData size:" + modelDataList.size());
        }
    }

}
