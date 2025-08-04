package pro.shushi.pamirs.framework.compute;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.compute.ModelDefinitionComputer;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.compute.definition.FieldComputer;
import pro.shushi.pamirs.meta.api.core.compute.definition.ModelComputer;
import pro.shushi.pamirs.meta.api.core.compute.template.DefinitionComputer;
import pro.shushi.pamirs.meta.api.core.compute.template.LifeDataComputer;
import pro.shushi.pamirs.meta.api.core.session.Sessions;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.api.session.RequestContext;
import pro.shushi.pamirs.meta.api.session.cache.spi.SessionCacheFactoryApi;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.util.TimeWatcher;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.util.JsonUtils;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.function.Function;

import static pro.shushi.pamirs.framework.compute.constants.TipsConstants.*;
import static pro.shushi.pamirs.framework.compute.emnu.ComputeExpEnumerate.BASE_COMPUTE_ERROR;

/**
 * 模型配置计算
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@Slf4j
@Order
@Component
@SPI.Service
public class DefaultModelDefinitionComputer extends ComputeTemplate implements ModelDefinitionComputer {

    @Resource
    private SessionCacheFactoryApi defaultSessionCacheFactoryApi;

    @SuppressWarnings("unchecked")
    @Override
    public Result<Void> computeModel(ComputeContext context, Meta meta, List<ModelDefinition> modelDefinitions) {
        return (Result<Void>) Spider.getDefaultExtension(LifeDataComputer.class)
                .compute(ModelDefinition.MODEL_MODEL, context, meta, modelDefinitions,
                        CONSTRUCT_COMPUTER,
                        DEFAULT_FIELD_COMPUTER,
                        CHECK_FIELD_COMPUTER
                );
    }

    @SuppressWarnings("unchecked")
    @Override
    public Result<Void> computeField(ComputeContext context, Meta meta, List<ModelField> fieldList) {
        return (Result<Void>) Spider.getDefaultExtension(LifeDataComputer.class)
                .compute(ModelField.MODEL_MODEL, context, meta, fieldList,
                        CONSTRUCT_COMPUTER_FOR_FIELD,
                        DEFAULT_FIELD_COMPUTER_FOR_FIELD,
                        CHECK_FIELD_COMPUTER_FOR_FIELD
                );
    }

    @SuppressWarnings("unchecked")
    @Override
    public Result<Void> constructField(ComputeContext context, Meta meta, List<ModelField> fieldList) {
        return (Result<Void>) Spider.getDefaultExtension(LifeDataComputer.class)
                .compute(ModelField.MODEL_MODEL, context, meta, fieldList,
                        CONSTRUCT_COMPUTER_FOR_FIELD,
                        DEFAULT_FIELD_COMPUTER_FOR_FIELD
                );
    }

    @SuppressWarnings("unchecked")
    @Override
    public Result<Void> checkField(ComputeContext context, Meta meta, List<ModelField> fieldList) {
        return (Result<Void>) Spider.getDefaultExtension(LifeDataComputer.class)
                .compute(ModelField.MODEL_MODEL, context, meta, fieldList,
                        null,
                        CHECK_FIELD_COMPUTER_FOR_FIELD
                );
    }

    @Override
    public Result<Void> compute(ComputeContext context, List<Meta> metaList) {
        Result<Void> result = new Result<>();
        Set<String> completedModuleSet = new HashSet<>();
        for (Meta meta : metaList) {
            result = compute(context, meta, completedModuleSet);
            if (!result.isSuccess()) {
                throw PamirsException.construct(BASE_COMPUTE_ERROR)
                        .appendMsg(JsonUtils.toJSONString(result.getMessages())).errThrow();
            }
        }
        return result;
    }

    @Override
    public Result<Void> compute(ComputeContext context, Meta meta, Set<String/*module*/> completedModuleSet) {
        // 计算模型定义
        RequestContext requestContext = RequestContext.newContext(defaultSessionCacheFactoryApi);
        Result<Void> result = Sessions.fillSession(requestContext, Lists.newArrayList(meta.getData().values()));
        if (!result.isSuccess()) {
            return result;
        }
        return computeWithoutClearSession(context, meta, completedModuleSet);
    }

    @Override
    public Result<Void> computeWithoutClearSession(ComputeContext context, Meta meta, Set<String/*module*/> completedModuleSet) {
        Result<Void> result = new Result<>();

        String module = meta.getModule();

        // 计算模型定义
        log.info(START_COMPUTE_MODUlE, Thread.currentThread().getId(), module, meta.getData().keySet());

        TimeWatcher.watch(() -> {

            // 计算模型配置默认值
            TimeWatcher.watch(() -> computeModel(result, completedModuleSet, context, meta, CONSTRUCT_COMPUTER, DEFAULT_FIELD_COMPUTER), TIPS1, CharacterConstants.SEPARATOR_HYPHEN, module);

            // 低无一体计算
            TimeWatcher.watch(() -> computeModel(result, completedModuleSet, context, meta, FUSE_MODEL_COMPUTER), TIPS10, CharacterConstants.SEPARATOR_HYPHEN, module);

            // 计算字段配置
            TimeWatcher.watch(() -> constructField(result, completedModuleSet, context, meta), TIPS2, CharacterConstants.SEPARATOR_HYPHEN, module);

            // 计算继承，计算主键，计算多表继承自动生成字段
            TimeWatcher.watch(() -> computeModelDefinition(result, false, completedModuleSet, context, meta, INHERITED_MODEL_COMPUTER, INHERITED_FIELD_COMPUTER), TIPS3, CharacterConstants.SEPARATOR_HYPHEN, module);

            // 计算多对多关系并为新生成模型与字段自动生成关系字段、关联关系自动生成字段
            TimeWatcher.watch(() -> computeModelDefinition(result, false, completedModuleSet, context, meta, null, RELATION_FIELD_COMPUTER, MANY_TO_MANY_FIELD_COMPUTER), TIPS4, CharacterConstants.SEPARATOR_HYPHEN, module);

            // 计算乐观锁、引用字段、字段可选项
            TimeWatcher.watch(() -> computeModelDefinition(result, true, completedModuleSet, context, meta, OPTIMISTIC_LOCKER_MODEL_COMPUTER, RELATED_FIELD_COMPUTER, FIELD_OPTIONS_COMPUTER), TIPS5, CharacterConstants.SEPARATOR_HYPHEN, module);

            // 模型定义检查
            TimeWatcher.watch(() -> computeModel(result, completedModuleSet, context, meta, CHECK_MODEL_COMPUTER, CHECK_FIELD_COMPUTER), TIPS6, CharacterConstants.SEPARATOR_HYPHEN, module);

            // 字段定义检查
            TimeWatcher.watch(() -> checkField(result, completedModuleSet, context, meta), TIPS7, CharacterConstants.SEPARATOR_HYPHEN, module);

            // 计算模块
            TimeWatcher.watch(() -> computeModule(result, completedModuleSet, context, meta, CONSTRUCT_COMPUTER, DEFAULT_FIELD_COMPUTER, CHECK_FIELD_COMPUTER), TIPS8, CharacterConstants.SEPARATOR_HYPHEN, module);

            // 计算函数
            TimeWatcher.watch(() -> computeFunction(result, completedModuleSet, context, meta, CONSTRUCT_COMPUTER, DEFAULT_FIELD_COMPUTER, CHECK_FIELD_COMPUTER), TIPS9, CharacterConstants.SEPARATOR_HYPHEN, module);

        }, COMPLETE_ALL, module, TIME_ALL);

        // 设置已完成计算模块
        completedModuleSet.addAll(meta.getData().keySet());

        // 返回结果
        return result;
    }

    @SuppressWarnings({"rawtypes", "SameParameterValue", "unchecked"})
    protected void computeModule(Result<Void> result, Set<String/*model model + module*/> completedModuleSet,
                                 ComputeContext context, Meta meta,
                                 ModelComputer modelComputer, FieldComputer... fieldComputers) {
        if (null == meta.getCurrentModule()) {
            return;
        }
        if (completedModuleSet.contains(meta.getModule())) {
            return;
        }
        Result convertResult = Spider.getDefaultExtension(LifeDataComputer.class).compute(ModuleDefinition.MODEL_MODEL, context, meta,
                Lists.newArrayList(meta.getCurrentModule()), modelComputer, fieldComputers);
        result.fill(convertResult);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected void computeModel(Result<Void> result, Set<String/*model model + module*/> completedModuleSet,
                                ComputeContext context, Meta meta,
                                ModelComputer modelComputer, FieldComputer... fieldComputers) {
        List<ModelDefinition> dataList = collectionComputeMetaDataList(meta, completedModuleSet, MetaData::getModelList);
        if (!dataList.isEmpty()) {
            Result convertResult = Spider.getDefaultExtension(LifeDataComputer.class).compute(ModelDefinition.MODEL_MODEL, context, meta, dataList, modelComputer, fieldComputers);
            result.fill(convertResult);
        }
    }

    protected void constructField(Result<Void> result, Set<String> completedModuleSet, ComputeContext context, Meta meta) {
        computeField(result, completedModuleSet, context, meta, this::constructField);
    }

    @SuppressWarnings("unused")
    protected void checkField(Result<Void> result, Set<String> completedModuleSet, ComputeContext context, Meta meta) {
        if (!context.isCheckField()) {
            log.info(TIPS7_DISABLED);
            return;
        }
        computeField(result, completedModuleSet, context, meta, this::checkField);
    }

    @SuppressWarnings({"rawtypes"})
    protected void computeField(Result<Void> result, Set<String> completedModuleSet, ComputeContext context, Meta meta,
                                ComputeFieldFunction function) {
        List<ModelField> fieldList = collectionComputeMetaDataList(meta, completedModuleSet, MetaData::getModelFieldList);
        if (!fieldList.isEmpty()) {
            Result convertResult = function.apply(context, meta, fieldList);
            result.fill(convertResult);
        }
    }

    @SuppressWarnings({"rawtypes", "SameParameterValue", "unchecked"})
    protected void computeFunction(Result<Void> result, Set<String> completedModuleSet, ComputeContext context, Meta meta,
                                   ModelComputer modelComputer, FieldComputer... fieldComputers) {
        List<FunctionDefinition> functionList = collectionComputeFunctionList(meta, completedModuleSet);
        if (!functionList.isEmpty()) {
            Result convertResult = Spider.getDefaultExtension(LifeDataComputer.class).compute(FunctionDefinition.MODEL_MODEL, context, meta, functionList, modelComputer, fieldComputers);
            result.fill(convertResult);
        }
    }

    protected List<FunctionDefinition> collectionComputeFunctionList(Meta meta, Set<String> completedModuleSet) {
        return collectionComputeMetaDataList(meta, completedModuleSet, metaData -> {
            List<FunctionDefinition> list = new ArrayList<>();
            List<FunctionDefinition> functionDefinitionList = metaData.getModelFunctionList();
            if (CollectionUtils.isNotEmpty(functionDefinitionList)) {
                list.addAll(functionDefinitionList);
            }
            functionDefinitionList = metaData.getStandAloneFunctionList();
            if (CollectionUtils.isNotEmpty(functionDefinitionList)) {
                list.addAll(functionDefinitionList);
            }
            return list;
        });
    }

    @SuppressWarnings({"rawtypes"})
    protected void computeModelDefinition(Result<Void> result, boolean extendCompute, Set<String> completedModuleSet,
                                          ComputeContext context, Meta meta,
                                          ModelComputer modelComputer, FieldComputer... fieldComputers) {
        List<ModelDefinition> dataList = collectionComputeMetaDataList(meta, completedModuleSet, MetaData::getModelList);
        if (!dataList.isEmpty()) {
            Result convertResult = Spider.getDefaultExtension(DefinitionComputer.class).compute(context, meta, extendCompute, dataList, modelComputer, fieldComputers);
            result.fill(convertResult);
        }
    }

    protected <T> List<T> collectionComputeMetaDataList(Meta meta, Set<String> completedModuleSet, Function<MetaData, List<T>> getter) {
        List<T> dataList = new ArrayList<>();
        Map<String, MetaData> metaData = meta.getData();
        for (Map.Entry<String, MetaData> entry : metaData.entrySet()) {
            String module = entry.getKey();
            if (completedModuleSet.contains(module)) {
                continue;
            }
            List<T> modelList = getter.apply(entry.getValue());
            if (CollectionUtils.isNotEmpty(modelList)) {
                dataList.addAll(modelList);
            }
        }
        return dataList;
    }

    /**
     * 字段计算函数
     * <p>
     * 2021/3/12 12:45 下午
     *
     * @author d@shushi.pro
     * @version 1.0.0
     */
    @FunctionalInterface
    protected interface ComputeFieldFunction {

        Result<?> apply(ComputeContext context, Meta meta, List<ModelField> fieldList);

    }
}
