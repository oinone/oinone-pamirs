package pro.shushi.pamirs.channel.core.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.orm.converter.processor.PersistenceExtendProcessor;
import pro.shushi.pamirs.framework.orm.converter.processor.PersistenceSerializeProcessor;
import pro.shushi.pamirs.framework.orm.converter.processor.PersistenceTypeProcessor;
import pro.shushi.pamirs.framework.orm.converter.processor.RelatedConvertProcessor;
import pro.shushi.pamirs.framework.orm.named.ColumnToLnameProcessor;
import pro.shushi.pamirs.framework.orm.processor.OrmMappingProcessor;
import pro.shushi.pamirs.framework.orm.processor.OrmModelingProcessor;
import pro.shushi.pamirs.framework.orm.processor.OrmObjectingProcessor;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * ElasticDataConverter
 *
 * @author yakir on 2023/04/08 17:05.
 * @author wx on 2026/02/12
 */
@Component
public class ElasticDataConverter {

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

    @Autowired
    private OrmModelingProcessor ormModelingProcessor;

    @Autowired
    private OrmMappingProcessor ormMappingProcessor;

    @Autowired
    private OrmObjectingProcessor ormObjectingProcessor;

    @Autowired
    private ColumnToLnameProcessor columnToLnameProcessor;

    @Autowired
    private RelatedConvertProcessor relatedConvertProcessor;

    @Autowired
    private PersistenceTypeProcessor persistenceTypeProcessor;

    @Autowired
    private PersistenceSerializeProcessor persistenceSerializeProcessor;

    @Autowired
    private PersistenceExtendProcessor persistenceExtendProcessor;

    private final ModelBeforeComputeApi<Object> modelBefore = (oModel, oObj) -> ormModelingProcessor.before(oModel, oObj);

    private final PersistenceModelAfterComputeApi<Object> inModelAfter = (oModel, oObj) -> ormMappingProcessor.after(oModel.getModel(), oObj);
    private final PersistenceFieldComputeApi inExtend = (context, modelConfig, fieldConfig, dMap) -> persistenceExtendProcessor.in(context, fieldConfig, dMap);
    private final PersistenceFieldComputeApi inRelated = (context, modelConfig, fieldConfig, dMap) -> relatedConvertProcessor.in(fieldConfig, dMap);
    private final PersistenceFieldComputeApi inType = (context, modelConfig, fieldConfig, dMap) -> persistenceTypeProcessor.in(modelConfig, fieldConfig, dMap);
    private final PersistenceFieldComputeApi inSerialize = (context, modelConfig, fieldConfig, dMap) -> persistenceSerializeProcessor.serialize(fieldConfig, dMap);

    private final PersistenceModelAfterComputeApi<Object> outModelAfter = (oModel, oObj) -> ormObjectingProcessor.after(oModel, oObj);
    private final PersistenceFieldComputeApi outLname = (context, modelConfig, fieldConfig, dMap) -> columnToLnameProcessor.convert(fieldConfig, dMap);
    private final PersistenceFieldComputeApi outExtend = (context, modelConfig, fieldConfig, dMap) -> persistenceExtendProcessor.out(context, fieldConfig, dMap);
    private final PersistenceFieldComputeApi outSerialize = (context, modelConfig, fieldConfig, dMap) -> persistenceSerializeProcessor.deserialize(fieldConfig, dMap);
    private final PersistenceFieldComputeApi outType = (context, modelConfig, fieldConfig, dMap) -> persistenceTypeProcessor.out(modelConfig, fieldConfig, dMap);
    private final PersistenceFieldComputeApi outRelated = (context, modelConfig, fieldConfig, dMap) -> relatedConvertProcessor.out(fieldConfig, dMap);

    @SuppressWarnings("unchecked")
    public <T> T in(String model, Object obj) {
        if (ENABLE_BATCH_OPTIMIZATION && obj instanceof List) {
            return (T) processBatchIn(model, (List<?>) obj);
        }

        return PersistenceDataComputeTemplate.getInstance().compute(model, obj,
                modelBefore,
                inModelAfter,
                getInProcessors(model)
        );
    }

    @SuppressWarnings("unchecked")
    public <T> T out(String model, Object obj) {
        if (ENABLE_BATCH_OPTIMIZATION && obj instanceof List) {
            return (T) processBatchOut(model, (List<?>) obj);
        }

        return PersistenceDataComputeTemplate.getInstance().compute(model, obj,
                modelBefore,
                outModelAfter,
                getOutProcessors(model)
        );
    }

    private List<Object> processBatchIn(String model, List<?> list) {
        ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getSimpleModelConfig(model);
        ModelAnalysis analysis = new ModelAnalysis(modelConfig);
        FieldComputeContext context = CONTEXT_HOLDER.get();
        try {
            context.setTotalContext(null);
            context.setOp(null);
            List<Object> results = new ArrayList<>(list.size());
            for (Object item : list) {
                Object processingObj = modelBefore.before(model, item);
                if (processingObj == null) {
                    results.add(null);
                    continue;
                }
                Map<String, Object> dMap = null;
                if (processingObj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> map = (Map<String, Object>) processingObj;
                    dMap = map;
                } else if (processingObj instanceof D) {
                    dMap = ((D) processingObj).get_d();
                }

                if (dMap != null) {
                    // 1. Extend
                    if (HAS_EXTEND_CONVERTERS) {
                        for (ModelFieldConfig field : analysis.allFields) {
                            inExtend.run(context, modelConfig, field, dMap);
                        }
                    }
                    // 2. Related
                    for (ModelFieldConfig field : analysis.relatedFields) {
                        inRelated.run(context, modelConfig, field, dMap);
                    }
                    // 3. Type
                    for (ModelFieldConfig field : analysis.typeFields) {
                        inType.run(context, modelConfig, field, dMap);
                    }
                    // 4. Serialize
                    for (ModelFieldConfig field : analysis.serializeFields) {
                        inSerialize.run(context, modelConfig, field, dMap);
                    }
                }
                results.add(inModelAfter.after(modelConfig, processingObj));
            }
            return results;
        } finally {
            context.setTotalContext(null);
            context.setOp(null);
        }
    }

    private List<Object> processBatchOut(String model, List<?> list) {
        ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getSimpleModelConfig(model);
        ModelAnalysis analysis = new ModelAnalysis(modelConfig);
        FieldComputeContext context = CONTEXT_HOLDER.get();
        try {
            context.setTotalContext(null);
            context.setOp(null);
            List<Object> results = new ArrayList<>(list.size());
            for (Object item : list) {
                Object processingObj = modelBefore.before(model, item);
                if (processingObj == null) {
                    results.add(null);
                    continue;
                }
                Map<String, Object> dMap = null;
                if (processingObj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> map = (Map<String, Object>) processingObj;
                    dMap = map;
                } else if (processingObj instanceof D) {
                    dMap = ((D) processingObj).get_d();
                }

                if (dMap != null) {
                    // 1. Lname
                    for (ModelFieldConfig field : analysis.allFields) {
                        outLname.run(context, modelConfig, field, dMap);
                    }
                    // 2. Extend
                    if (HAS_EXTEND_CONVERTERS) {
                        for (ModelFieldConfig field : analysis.allFields) {
                            outExtend.run(context, modelConfig, field, dMap);
                        }
                    }
                    // 3. Serialize
                    for (ModelFieldConfig field : analysis.serializeFields) {
                        outSerialize.run(context, modelConfig, field, dMap);
                    }
                    // 4. Type
                    for (ModelFieldConfig field : analysis.typeFields) {
                        outType.run(context, modelConfig, field, dMap);
                    }
                    // 5. Related
                    for (ModelFieldConfig field : analysis.relatedFields) {
                        outRelated.run(context, modelConfig, field, dMap);
                    }
                }
                results.add(outModelAfter.after(modelConfig, processingObj));
            }
            return results;
        } finally {
            context.setTotalContext(null);
            context.setOp(null);
        }
    }

    private PersistenceFieldComputeApi[] getInProcessors(String model) {
        return new PersistenceFieldComputeApi[]{inExtend, inRelated, inType, inSerialize};
    }

    private PersistenceFieldComputeApi[] getOutProcessors(String model) {
        return new PersistenceFieldComputeApi[]{outLname, outExtend, outSerialize, outType, outRelated};
    }

    private static class ModelAnalysis {
        final List<ModelFieldConfig> allFields;
        final List<ModelFieldConfig> typeFields;
        final List<ModelFieldConfig> relatedFields;
        final List<ModelFieldConfig> serializeFields;

        ModelAnalysis(ModelConfig modelConfig) {
            this.allFields = modelConfig.getModelFieldConfigListSort();
            this.typeFields = new ArrayList<>();
            this.relatedFields = new ArrayList<>();
            this.serializeFields = new ArrayList<>();

            for (ModelFieldConfig field : allFields) {
                if (TtypeEnum.isRelatedType(field.getTtype())) {
                    relatedFields.add(field);
                }
                // ElasticDataConverter Specific logic for Type and Serialize
                if (field.getStore()) {
                    if (!TypeUtils.isBaseType(field.getLtype())) {
                        serializeFields.add(field);
                    }
                    // All stored fields might need type conversion
                    typeFields.add(field);
                }
            }
        }
    }
}
