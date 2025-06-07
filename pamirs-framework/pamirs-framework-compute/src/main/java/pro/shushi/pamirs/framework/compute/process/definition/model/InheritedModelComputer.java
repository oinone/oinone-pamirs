package pro.shushi.pamirs.framework.compute.process.definition.model;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.compute.InheritedComputeTemplate;
import pro.shushi.pamirs.framework.compute.emnu.ComputeExpEnumerate;
import pro.shushi.pamirs.framework.compute.retry.RetryManager;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.compute.definition.ModelComputer;
import pro.shushi.pamirs.meta.api.core.compute.systems.inherit.InheritedExtendProcessor;
import pro.shushi.pamirs.meta.api.core.compute.systems.inherit.InheritedProcessor;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;
import pro.shushi.pamirs.meta.util.ModelUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 继承系统计算
 *
 * @author d@shushi.pro
 * @author cpc@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@Slf4j
@SPI.Service(InheritedModelComputer.SPI_NAME)
public class InheritedModelComputer implements ModelComputer<Meta, ModelDefinition> {

    public static final String SPI_NAME = "inherited";

    @Override
    public Result<Void> compute(ComputeContext context, Meta meta, String model, ModelDefinition data, Map<String, Object> computeContext) {
        InheritedProcessor inheritedProcessor = Spider.getDefaultExtension(InheritedProcessor.class);
        List<InheritedExtendProcessor> inheritedExtendProcessors = Spider.getLoader(InheritedExtendProcessor.class).getOrderedExtensions();
        return InheritedComputeTemplate.compute(meta, data, computeContext,
                this::validateInheritedList, this::validateInherited,
                true, (currentModel, superModel) -> superModel.getModule().equals(currentModel.getModule()),
                (currentModel, superModel) -> {
                    // 抽象基类
                    // 根据model添加抽象基类字段，排除非继承字段
                    inheritedProcessor.dealInheritedField(meta, currentModel, superModel, SystemSourceEnum.ABSTRACT_INHERITED);
                    // 添加函数
                    inheritedProcessor.dealInheritedMethod(meta, currentModel, superModel, SystemSourceEnum.ABSTRACT_INHERITED);
                    // 扩展逻辑
                    for (InheritedExtendProcessor processor : inheritedExtendProcessors) {
                        processor.abstractInherit(meta, currentModel, superModel);
                    }
                    // 模型元信息继承
                    inheritUnionModelMeta(currentModel, superModel);
                    inheritCommonModelMeta(meta, currentModel, superModel);
                    // 计算主键
                    computePks(context, meta, false, currentModel, superModel);
                }, (currentModel, superModel) -> {
                    // 传输继承
                    // 根据model添加抽象基类字段，排除非继承字段
                    inheritedProcessor.dealInheritedField(meta, currentModel, superModel, SystemSourceEnum.TRANSIENT_INHERITED);
                    // 添加函数
                    inheritedProcessor.dealInheritedMethod(meta, currentModel, superModel, SystemSourceEnum.TRANSIENT_INHERITED);
                    // 扩展逻辑
                    for (InheritedExtendProcessor processor : inheritedExtendProcessors) {
                        processor.transientInherit(meta, currentModel, superModel);
                    }
                    // 模型元信息继承
                    inheritCommonModelMeta(meta, currentModel, superModel);
                    // 计算主键
                    computePks(context, meta, false, currentModel, superModel);
                }, (currentModel, superModel) -> {
                    // 多表继承
                    // 处理多表继承类型字段
                    inheritedProcessor.dealInheritedTypeField(context, meta, currentModel, superModel, SystemSourceEnum.MULTI_TABLE_INHERITED);
                    // 根据model添加父模型字段，排除非继承字段
                    inheritedProcessor.dealInheritedField(meta, currentModel, superModel, SystemSourceEnum.MULTI_TABLE_INHERITED);
                    // 添加函数
                    inheritedProcessor.dealInheritedMethod(meta, currentModel, superModel, SystemSourceEnum.MULTI_TABLE_INHERITED);
                    // 扩展逻辑
                    for (InheritedExtendProcessor processor : inheritedExtendProcessors) {
                        processor.multiTableInherit(meta, currentModel, superModel);
                    }
                    // 模型元信息继承
                    inheritUnionModelMeta(currentModel, superModel);
                    inheritCommonModelMeta(meta, currentModel, superModel);
                    // 计算主键
                    computePks(context, meta, false, currentModel, superModel);
                }, (currentModel, superModel) -> {
                    // 代理继承
                    currentModel.setDataManager(true);
                    // 处理代理模型字段
                    inheritedProcessor.dealProxyField(currentModel);
                    // 根据model添加代理模型字段，排除非继承字段
                    inheritedProcessor.dealInheritedField(meta, currentModel, superModel, SystemSourceEnum.PROXY_INHERITED);
                    // 添加函数
                    inheritedProcessor.dealInheritedMethod(meta, currentModel, superModel, SystemSourceEnum.PROXY_INHERITED);
                    // 扩展逻辑
                    for (InheritedExtendProcessor processor : inheritedExtendProcessors) {
                        processor.proxyExtend(meta, currentModel, superModel);
                    }
                    // 模型元信息继承
                    inheritStoreModelMeta(currentModel, superModel);
                    inheritCommonModelMeta(meta, currentModel, superModel);
                    // 传递数据源模块
                    inheritDsModule(currentModel, superModel);
                    // 计算主键
                    computePks(context, meta, true, currentModel, superModel);
                }, (currentModel, superModel) -> {
                    // 扩展继承
                    // 根据model name添加继承字段，排除非继承字段
                    inheritedProcessor.dealInheritedField(meta, currentModel, superModel, SystemSourceEnum.EXTEND_INHERITED);
                    // 添加函数
                    inheritedProcessor.dealInheritedMethod(meta, currentModel, superModel, SystemSourceEnum.EXTEND_INHERITED);
                    // 扩展逻辑
                    for (InheritedExtendProcessor processor : inheritedExtendProcessors) {
                        processor.extend(meta, currentModel, superModel);
                    }
                    // 模型元信息继承
                    inheritStoreModelMeta(currentModel, superModel);
                    inheritCommonModelMeta(meta, currentModel, superModel);
                    // 传递数据源模块
                    inheritDsModule(currentModel, superModel);
                    // 计算主键
                    computePks(context, meta, true, currentModel, superModel);
                }, (currentModel) -> {
                    if (ModelTypeEnum.STORE.equals(currentModel.getType())) {
                        // 计算主键
                        computePks(context, meta, false, currentModel, null);
                    }
                });
    }

    private void inheritDsModule(ModelDefinition currentModel, ModelDefinition superModel) {
        currentModel.setDsModule(superModel.getDsModule());
    }

    @SuppressWarnings("unchecked")
    private void computePks(ComputeContext context, Meta meta, boolean check, ModelDefinition data, ModelDefinition superModel) {
        Spider.getExtension(ModelComputer.class, PkModelComputer.SPI_NAME).compute(context, meta, data.getModel(), data, null);
        boolean isChangeTable = ModelUtils.isChangeTableInherited(data);
        if (check && !isChangeTable && !ListUtils.isEqualList(data.getPk(), superModel.getPk())) {
            try {
                if (!RetryManager.isRetry()) {
                    RetryManager.retry(superModel.getModel(), data);
                    return;
                }
            } catch (Exception e) {
                throw PamirsException.construct(ComputeExpEnumerate.BASE_INHERITED_RETRY_COMPUTE_PKS_ERROR, e)
                        .appendMsg("model:" + data.getModel() + ",super model:" + superModel.getModel()).errThrow();
            }
            throw PamirsException.construct(ComputeExpEnumerate.BASE_INHERITED_FORBIDDEN_MODIFY_PKS_ERROR)
                    .appendMsg("model:" + data.getModel() + ",super model:" + superModel.getModel()).errThrow();
        }
    }

    private Result<Void> validateInheritedList(ModelDefinition currentModel, List<String> superModels) {
        Result<Void> result = new Result<>();
        if (ModelTypeEnum.STORE.equals(currentModel.getType())) {
            int extendInherited = 0;
            int multiTableInherited = 0;
            for (String superModel : superModels) {
                if (Models.inherited().isExtendInherited(currentModel.getModel(), superModel)) {
                    extendInherited++;
                }
                if (Models.inherited().isMultiTableInherited(currentModel.getModel(), superModel)) {
                    multiTableInherited++;
                }
                if (extendInherited > 1 || multiTableInherited > 1) {
                    result.error();
                    result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR).append("存储模型不可扩展继承多个存储模型或者多表继承多个存储模型, model:" + currentModel.getModel()));
                    break;
                }
            }
        }
        return result;
    }

    private Result<Void> validateInherited(ModelDefinition currentModel, ModelDefinition superModel) {
        Result<Void> result = new Result<>();
        if (ModelTypeEnum.STORE.equals(currentModel.getType())) {
            switch (ModelTypeEnum.valueOf(superModel.getType().name())) {
                case STORE:
                case ABSTRACT:
                    break;
                case PROXY:
                case TRANSIENT:
                    result.error();
                    result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR).append("存储模型的父模型只能是抽象基类或存储模型, model:" + currentModel.getModel()));
                    break;
            }
        } else if (ModelTypeEnum.PROXY.equals(currentModel.getType())) {
            switch (ModelTypeEnum.valueOf(superModel.getType().name())) {
                case PROXY:
                    // 代理继承父子模型都是代理模型时，代理的模型必需一致，且代理的模型一致的情况下是允许多重继承的
                    if (!currentModel.getProxy().equals(superModel.getProxy())) {
                        result.error();
                        result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR).append("代理继承父子模型的代理模型不一致, model:" + currentModel.getModel()));
                    }
                    break;
                case STORE:
                    if (!currentModel.getProxy().equals(superModel.getModel())
                            && !superModel.getSuperModels().contains(currentModel.getProxy())) {
                        result.error();
                        result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR).append("代理继承子模型的代理模型与父模型不一致, model:" + currentModel.getModel()));
                    }
                    break;
                case ABSTRACT:
                    break;
                case TRANSIENT:
                    result.error();
                    result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR).append("代理继承的父模型只能是存储模型或代理模型, model:" + currentModel.getModel()));
                    break;
            }
        } else if (ModelTypeEnum.ABSTRACT.equals(currentModel.getType())) {
            switch (ModelTypeEnum.valueOf(superModel.getType().name())) {
                case ABSTRACT:
                    break;
                case STORE:
                case PROXY:
                case TRANSIENT:
                    result.error();
                    result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR).append("抽象基类的父模型只能是抽象基类, model:" + currentModel.getModel()));
                    break;
            }
        } else if (ModelTypeEnum.TRANSIENT.equals(currentModel.getType())) {
            switch (ModelTypeEnum.valueOf(superModel.getType().name())) {
                case ABSTRACT:
                case TRANSIENT:
                    break;
                case STORE:
                case PROXY:
                    result.error();
                    result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR).append("传输模型的父模型只能是抽象基类或者传输模型, model:" + currentModel.getModel()));
                    break;
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private void inheritCommonModelMeta(Meta meta, ModelDefinition currentModel, ModelDefinition superModel) {
        // 处理继承
        // 排序
        if (null == currentModel.getOrdering() && null != superModel.getOrdering()) {
            currentModel.setOrdering(superModel.getOrdering());
        }

        // 数据标题
        List<String> labelFields = currentModel.getLabelFields();
        if (CollectionUtils.isEmpty(labelFields)) {
            List<String> superLabelFields = superModel.getLabelFields();
            if (CollectionUtils.isNotEmpty(superLabelFields)) {
                currentModel.setLabelFields(superLabelFields);
            }
        }
        String label = currentModel.getLabel();
        if (StringUtils.isBlank(label)) {
            String superLabel = superModel.getLabel();
            if (!StringUtils.isBlank(superLabel)) {
                currentModel.setLabel(superLabel);
            }
        }
    }

    private void inheritStoreModelMeta(ModelDefinition currentModel, ModelDefinition superModel) {
        // 同表继承须设置数据源和表名与父类一致
        if (!ModelUtils.isChangeTableInherited(currentModel)) {
            // 继承父类数据源
            currentModel.setDsKey(superModel.getDsKey());
            // 继承父类表名
            currentModel.setTable(superModel.getTable());
            // 继承父类表备注
            currentModel.setRemark(superModel.getRemark());
            // 继承父类索引与子类索引合并
            inheritIndexMeta(currentModel, superModel);
            // 继承ORM设置
            currentModel.setLogicDelete(superModel.getLogicDelete());
            currentModel.setLogicDeleteColumn(superModel.getLogicDeleteColumn());
            currentModel.setLogicDeleteValue(superModel.getLogicDeleteValue());
            currentModel.setLogicNotDeleteValue(superModel.getLogicNotDeleteValue());
            currentModel.setUnderCamel(superModel.getUnderCamel());
            currentModel.setCapitalMode(superModel.getCapitalMode());
            currentModel.setCharset(superModel.getCharset());
            currentModel.setCollate(superModel.getCollate());
        } else {
            inheritUnionModelMeta(currentModel, superModel);
        }
    }

    private void inheritUnionModelMeta(ModelDefinition currentModel, ModelDefinition superModel) {
        // 继承父类索引与子类索引合并
        inheritIndexMeta(currentModel, superModel);
        // 继承ORM设置
        doSetter(() -> null == currentModel.getLogicDelete() && null != superModel.getLogicDelete(),
                currentModel::setLogicDelete, superModel::getLogicDelete);
        doSetter(() -> StringUtils.isBlank(currentModel.getLogicDeleteColumn()) && StringUtils.isNotBlank(superModel.getLogicDeleteColumn()),
                currentModel::setLogicDeleteColumn, superModel::getLogicDeleteColumn);
        doSetter(() -> StringUtils.isBlank(currentModel.getLogicDeleteValue()) && StringUtils.isNotBlank(superModel.getLogicDeleteValue()),
                currentModel::setLogicDeleteValue, superModel::getLogicDeleteValue);
        doSetter(() -> StringUtils.isBlank(currentModel.getLogicNotDeleteValue()) && StringUtils.isNotBlank(superModel.getLogicNotDeleteValue()),
                currentModel::setLogicNotDeleteValue, superModel::getLogicNotDeleteValue);
        doSetter(() -> null == currentModel.getUnderCamel() && null != superModel.getUnderCamel(),
                currentModel::setUnderCamel, superModel::getUnderCamel);
        doSetter(() -> null == currentModel.getCapitalMode() && null != superModel.getCapitalMode(),
                currentModel::setCapitalMode, superModel::getCapitalMode);
        doSetter(() -> null == currentModel.getCharset() && null != superModel.getCharset(),
                currentModel::setCharset, superModel::getCharset);
        doSetter(() -> null == currentModel.getCollate() && null != superModel.getCollate(),
                currentModel::setCollate, superModel::getCollate);
    }

    private void inheritIndexMeta(ModelDefinition currentModel, ModelDefinition superModel) {
        // 继承父类索引与子类索引合并
        currentModel.setUniques(pro.shushi.pamirs.meta.common.util.ListUtils.uniqueStringListUnion(currentModel.getUniques(), superModel.getUniques()));
        currentModel.setIndexes(pro.shushi.pamirs.meta.common.util.ListUtils.uniqueStringListUnion(currentModel.getIndexes(), superModel.getIndexes()));
    }

    private <T> void doSetter(Supplier<Boolean> condition, Consumer<T> setter, Supplier<T> getter) {
        if (condition.get()) {
            setter.accept(getter.get());
        }
    }

}
