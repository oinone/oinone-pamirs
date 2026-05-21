package pro.shushi.pamirs.framework.orm.converter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.framework.orm.converter.processor.PersistenceExtendProcessor;
import pro.shushi.pamirs.framework.orm.converter.processor.PersistenceSerializeProcessor;
import pro.shushi.pamirs.framework.orm.converter.processor.PersistenceTypeProcessor;
import pro.shushi.pamirs.framework.orm.converter.processor.RelatedConvertProcessor;
import pro.shushi.pamirs.framework.orm.named.ColumnToLnameProcessor;
import pro.shushi.pamirs.framework.orm.named.LnameToColumnProcessor;
import pro.shushi.pamirs.framework.orm.processor.OrmMappingProcessor;
import pro.shushi.pamirs.framework.orm.processor.OrmModelingProcessor;
import pro.shushi.pamirs.framework.orm.processor.OrmObjectingProcessor;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.convert.DataConverter;
import pro.shushi.pamirs.meta.api.core.orm.spi.PersistenceFieldExtendConverter;
import pro.shushi.pamirs.meta.api.core.orm.template.PersistenceDataComputeTemplate;
import pro.shushi.pamirs.meta.api.core.orm.template.context.FieldComputeContext;
import pro.shushi.pamirs.meta.api.core.orm.template.function.ModelBeforeComputeApi;
import pro.shushi.pamirs.meta.api.core.orm.template.function.PersistenceFieldComputeApi;
import pro.shushi.pamirs.meta.api.core.orm.template.function.PersistenceModelAfterComputeApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 持久化数据转换服务
 * <p>
 * 递归遍历
 *
 * @author d@shushi.pro
 * @author cpc@shushi.pro 去除闭包提升性能
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Component
public class PersistenceDataConverter implements DataConverter {

    /** 是否存在扩展转换器 */
    private static final boolean HAS_EXTEND_CONVERTERS;

    /** 是否启用批量优化 */
    private static final boolean ENABLE_BATCH_OPTIMIZATION = true;

    /** 复用计算上下文以减少 GC */
    private static final ThreadLocal<FieldComputeContext> CONTEXT_HOLDER = ThreadLocal.withInitial(FieldComputeContext::new);

    static {
        List<PersistenceFieldExtendConverter> converters = Spider.getLoader(PersistenceFieldExtendConverter.class).getOrderedExtensions();
        HAS_EXTEND_CONVERTERS = converters != null && !converters.isEmpty();
    }

    @Resource
    private OrmModelingProcessor ormModelingProcessor;

    @Resource
    private OrmMappingProcessor ormMappingProcessor;

    @Resource
    private OrmObjectingProcessor ormObjectingProcessor;

    @Resource
    private LnameToColumnProcessor lnameToColumnProcessor;

    @Resource
    private ColumnToLnameProcessor columnToLnameProcessor;

    @Resource
    private RelatedConvertProcessor relatedConvertProcessor;

    @Resource
    private PersistenceTypeProcessor persistenceTypeProcessor;

    @Resource
    private PersistenceSerializeProcessor persistenceSerializeProcessor;

    @Resource
    private PersistenceExtendProcessor persistenceExtendProcessor;

    private final ModelBeforeComputeApi modelBefore = (oModel, oObj) -> ormModelingProcessor.before(oModel, oObj);

    private final PersistenceModelAfterComputeApi inModelAfter = (oModel, oObj) -> ormMappingProcessor.after(oModel.getModel(), oObj);
    private final PersistenceFieldComputeApi inExtend = (context, modelConfig, fieldConfig, dMap) -> persistenceExtendProcessor.in(context, fieldConfig, dMap);
    private final PersistenceFieldComputeApi inType = (context, modelConfig, fieldConfig, dMap) -> persistenceTypeProcessor.in(modelConfig, fieldConfig, dMap);
    private final PersistenceFieldComputeApi inRelated = (context, modelConfig, fieldConfig, dMap) -> relatedConvertProcessor.in(fieldConfig, dMap);
    private final PersistenceFieldComputeApi inSerialize = (context, modelConfig, fieldConfig, dMap) -> persistenceSerializeProcessor.serialize(fieldConfig, dMap);
    private final PersistenceFieldComputeApi inLname = (context, modelConfig, fieldConfig, dMap) -> lnameToColumnProcessor.convert(fieldConfig, dMap);

    private final PersistenceModelAfterComputeApi outModelAfter = (oModel, oObj) -> ormObjectingProcessor.after(oModel, oObj);
    private final PersistenceFieldComputeApi outLname = (context, modelConfig, fieldConfig, dMap) -> columnToLnameProcessor.convert(fieldConfig, dMap);
    private final PersistenceFieldComputeApi outExtend = (context, modelConfig, fieldConfig, dMap) -> persistenceExtendProcessor.out(context, fieldConfig, dMap);
    private final PersistenceFieldComputeApi outSerialize = (context, modelConfig, fieldConfig, dMap) -> persistenceSerializeProcessor.deserialize(fieldConfig, dMap);
    private final PersistenceFieldComputeApi outType = (context, modelConfig, fieldConfig, dMap) -> persistenceTypeProcessor.out(modelConfig, fieldConfig, dMap);

    private final PersistenceFieldComputeApi[] inProcessors = new PersistenceFieldComputeApi[]{
            inExtend,// 扩展处理
            inType,// 字段类型处理
            inRelated,// 引用字段处理
            inSerialize,// 非String存储字段序列化
            inLname// 列名转化
    };

    private final PersistenceFieldComputeApi[] outProcessors = new PersistenceFieldComputeApi[]{
            outLname,// 列名转化
            outExtend,// 扩展处理
            outSerialize,// 非String存储字段反序列化
            outType// 字段类型处理
    };

    @Override
    public <T> T in(String model, Object obj) {
        if (ENABLE_BATCH_OPTIMIZATION && obj instanceof List) {
             return (T) processBatchIn(model, (List<?>) obj);
        }

        return processSingle(model, obj, inModelAfter, inProcessors);
    }

    @Override
    public <T> T out(String model, Object obj) {
        if (ENABLE_BATCH_OPTIMIZATION && obj instanceof List) {
             return (T) processBatchOut(model, (List<?>) obj);
        }

        return processSingle(model, obj, outModelAfter, outProcessors);
    }

    private <T> T processSingle(String model, Object obj, PersistenceModelAfterComputeApi after, PersistenceFieldComputeApi[] processors) {
        return PersistenceDataComputeTemplate.getInstance().compute(model, obj,
                modelBefore,
                after,
                processors
        );
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> processBatchIn(String model, List<?> objList) {
        if (CollectionUtils.isEmpty(objList)) {
            return (List<T>) objList;
        }

        ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getSimpleModelConfig(model);
        ModelAnalysis analysis = analyzeInModel(modelConfig);
        List<T> result = new ArrayList<>(objList.size());
        FieldComputeContext context = CONTEXT_HOLDER.get();
        try {
            context.setTotalContext(null);
            context.setOp(null);
            for (Object obj : objList) {
                Object processingObj = modelBefore.before(model, obj);
                
                Map<String, Object> dMap = null;
                if (processingObj instanceof Map) {
                    dMap = (Map<String, Object>) processingObj;
                } else if (processingObj instanceof D) {
                    dMap = ((D) processingObj).get_d();
                }

                if (dMap != null) {
                    if (HAS_EXTEND_CONVERTERS) {
                        for (ModelFieldConfig fc : analysis.allFields) {
                            inExtend.run(context, modelConfig, fc, dMap);
                        }
                    }
                    for (ModelFieldConfig fc : analysis.typeFields) {
                        inType.run(context, modelConfig, fc, dMap);
                    }
                    for (ModelFieldConfig fc : analysis.relatedFields) {
                        inRelated.run(context, modelConfig, fc, dMap);
                    }
                    for (ModelFieldConfig fc : analysis.serializeFields) {
                        inSerialize.run(context, modelConfig, fc, dMap);
                    }
                    if (Models.modelDirective().isDoColumn(dMap)) {
                        for (ModelFieldConfig fc : analysis.lnameFields) {
                            inLname.run(context, modelConfig, fc, dMap);
                        }
                    }
                }
                result.add((T) inModelAfter.after(modelConfig, processingObj));
            }
        } finally {
            context.setTotalContext(null);
            context.setOp(null);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> processBatchOut(String model, List<?> objList) {
        if (CollectionUtils.isEmpty(objList)) {
            return (List<T>) objList;
        }

        ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getSimpleModelConfig(model);
        ModelAnalysis analysis = analyzeOutModel(modelConfig);
        
        List<T> result = new ArrayList<>(objList.size());
        FieldComputeContext context = CONTEXT_HOLDER.get();
        
        try {
            context.setTotalContext(null);
            context.setOp(null);

            for (Object obj : objList) {
                Object processingObj = modelBefore.before(model, obj);
                
                Map<String, Object> dMap = null;
                if (processingObj instanceof Map) {
                    dMap = (Map<String, Object>) processingObj;
                } else if (processingObj instanceof D) {
                    dMap = ((D) processingObj).get_d();
                }

                if (dMap != null) {
                    if (Models.modelDirective().isDoColumn(dMap)) {
                        for (ModelFieldConfig fc : analysis.lnameFields) {
                            outLname.run(context, modelConfig, fc, dMap);
                        }
                    }
                    if (HAS_EXTEND_CONVERTERS) {
                        for (ModelFieldConfig fc : analysis.allFields) {
                            outExtend.run(context, modelConfig, fc, dMap);
                        }
                    }
                    for (ModelFieldConfig fc : analysis.serializeFields) {
                        outSerialize.run(context, modelConfig, fc, dMap);
                    }
                    for (ModelFieldConfig fc : analysis.typeFields) {
                        outType.run(context, modelConfig, fc, dMap);
                    }
                }
                result.add((T) outModelAfter.after(modelConfig, processingObj));
            }
        } finally {
            context.setTotalContext(null);
            context.setOp(null);
        }
        return result;
    }

    private static class ModelAnalysis {
        List<ModelFieldConfig> allFields;
        List<ModelFieldConfig> typeFields = new ArrayList<>();
        List<ModelFieldConfig> relatedFields = new ArrayList<>();
        List<ModelFieldConfig> serializeFields = new ArrayList<>();
        List<ModelFieldConfig> lnameFields = new ArrayList<>();
    }

    private ModelAnalysis analyzeInModel(ModelConfig modelConfig) {
        ModelAnalysis analysis = new ModelAnalysis();
        analysis.allFields = modelConfig.getModelFieldConfigListSort();
        if (analysis.allFields == null) analysis.allFields = new ArrayList<>();

        for (ModelFieldConfig fc : analysis.allFields) {
            analysis.typeFields.add(fc);
            if (TtypeEnum.RELATED.value().equals(fc.getTtype())) {
                analysis.relatedFields.add(fc);
            }
            if (Boolean.TRUE.equals(fc.getStore()) && !TypeUtils.isBaseType(fc.getLtype())) {
                analysis.serializeFields.add(fc);
            }
            if (StringUtils.isNotBlank(fc.getColumn())) {
                analysis.lnameFields.add(fc);
            }
        }
        return analysis;
    }

    private ModelAnalysis analyzeOutModel(ModelConfig modelConfig) {
        ModelAnalysis analysis = new ModelAnalysis();
        analysis.allFields = modelConfig.getModelFieldConfigListSort();
        if (analysis.allFields == null) analysis.allFields = new ArrayList<>();

        for (ModelFieldConfig fc : analysis.allFields) {
            if (StringUtils.isNotBlank(fc.getColumn())) {
                analysis.lnameFields.add(fc);
            }
            if (Boolean.TRUE.equals(fc.getStore()) && !TypeUtils.isBaseType(fc.getLtype())) {
                analysis.serializeFields.add(fc);
            }
            analysis.typeFields.add(fc);
        }
        return analysis;
    }

}
