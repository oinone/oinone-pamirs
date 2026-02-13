package pro.shushi.pamirs.framework.orm.client.converter;

import javax.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.framework.orm.api.RecursionOrmApi;
import pro.shushi.pamirs.framework.orm.client.checker.ClientModelChecker;
import pro.shushi.pamirs.framework.orm.client.converter.processor.*;
import pro.shushi.pamirs.framework.orm.named.LnameToNameProcessor;
import pro.shushi.pamirs.framework.orm.named.NameToLnameProcessor;
import pro.shushi.pamirs.framework.orm.processor.OrmMappingProcessor;
import pro.shushi.pamirs.framework.orm.processor.OrmModelingProcessor;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.convert.ClientDataConverter;
import pro.shushi.pamirs.meta.api.core.orm.spi.PersistenceFieldExtendConverter;
import pro.shushi.pamirs.meta.api.core.orm.template.ClientDataComputeTemplate;
import pro.shushi.pamirs.meta.api.core.orm.template.context.FieldComputeContext;
import pro.shushi.pamirs.meta.api.core.orm.template.context.ModelComputeContext;
import pro.shushi.pamirs.meta.api.core.orm.template.function.ModelBeforeComputeApi;
import pro.shushi.pamirs.meta.api.core.orm.template.function.PersistenceFieldComputeApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.ClassUtils;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.lang.ref.SoftReference;
import java.util.*;

/**
 * RPC端数据转换服务
 * <p>
 * 递归遍历
 *
 * @author cpc@shushi.pro
 * @version 1.0.0
 * date 2024/2/27
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Component
public class RemoteClientDataConverter implements ClientDataConverter {

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
    private ClientModelChecker clientModelChecker;

    @Resource
    private ClientSerializeProcessor clientSerializeProcessor;

    @Resource
    private RemoteClientPageProcessor remoteClientPageProcessor;

    @Resource
    private ClientExtendProcessor clientExtendProcessor;

    @Resource
    private ClientComputeProcessor clientComputeProcessor;

    @Resource
    private RemoteClientTypeProcessor remoteClientTypeProcessor;

    @Resource
    private ClientArrayProcessor clientArrayProcessor;

    @Resource
    private OrmModelingProcessor ormModelingProcessor;

    @Resource
    private OrmMappingProcessor ormMappingProcessor;

    @Resource
    private NameToLnameProcessor nameToLnameProcessor;

    @Resource
    private LnameToNameProcessor lnameToNameProcessor;

    private final PersistenceFieldComputeApi inExtend = (context, modelConfig, fieldConfig, dMap) -> clientExtendProcessor.in(context, fieldConfig, dMap);
    private final PersistenceFieldComputeApi inType = (context, modelConfig, fieldConfig, dMap) -> remoteClientTypeProcessor.in(context, fieldConfig, dMap);
    private final PersistenceFieldComputeApi inArray = (context, modelConfig, fieldConfig, dMap) -> clientArrayProcessor.in(context, fieldConfig, dMap);
    private final PersistenceFieldComputeApi inSerialize = (context, modelConfig, fieldConfig, dMap) -> clientSerializeProcessor.in(context, fieldConfig, dMap);
    private final PersistenceFieldComputeApi inName = (context, modelConfig, fieldConfig, dMap) -> nameToLnameProcessor.convert(fieldConfig, dMap);

    private final PersistenceFieldComputeApi outExtend = (context, modelConfig, fieldConfig, dMap) -> clientExtendProcessor.out(context, fieldConfig, dMap);
    private final PersistenceFieldComputeApi outType = (context, modelConfig, fieldConfig, dMap) -> remoteClientTypeProcessor.out(context, fieldConfig, dMap);
    private final PersistenceFieldComputeApi outArray = (context, modelConfig, fieldConfig, dMap) -> clientArrayProcessor.out(context, fieldConfig, dMap);
    private final PersistenceFieldComputeApi outSerialize = (context, modelConfig, fieldConfig, dMap) -> clientSerializeProcessor.out(context, fieldConfig, dMap);
    private final PersistenceFieldComputeApi outName = (context, modelConfig, fieldConfig, dMap) -> lnameToNameProcessor.convert(fieldConfig, dMap);

    @Override
    public <T> T in(ModelComputeContext totalContext, String model, Object obj) {
        if (obj == null) return null;
        if (ENABLE_BATCH_OPTIMIZATION && obj instanceof List) {
            return (T) processBatchIn(totalContext, model, (List<?>) obj);
        }

        return processSingleIn(totalContext, model, obj);
    }

    private <T> T processSingleIn(ModelComputeContext totalContext, String model, Object obj) {
        int objId = getObjectId(obj);
        T reentry = getReentry(objId, obj);
        if (reentry != null) return reentry;

        return ClientDataComputeTemplate.getInstance().compute(totalContext, model, obj,
                this::in,
                (context, oModel, oObj) -> {
                    Models.modelDirective().enableOrmReentry(oObj);// 防重入
                    Object result = null;
                    ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(oModel);
                    String lname = modelConfig.getLname();
                    if (ClassUtils.isNoClass(lname)) {
                        result = new DataMap();
                    } else {
                        result = TypeUtils.getNewInstance(lname);
                    }
                    getReentryMap().put(objId, new SoftReference<Object>(Models.modelDirective().enableOrmReentry(result)));
                    return ormModelingProcessor.before(oModel, oObj);
                },// 模型化
                (context, modelConfig, oObj) -> {
                    String oModel = modelConfig.getModel();
                    oObj = RecursionOrmApi.getOrmObjectingProcessor().after(oModel, oObj);// 对象化
                    Object res = clientModelChecker.check(context, modelConfig.getModelDefinition(), oObj);// 模型约束校验
                    String lname = modelConfig.getLname();
                    SoftReference<Object> ref = getReentryMap(objId);
                    if (ref != null && ref.get() != null) {
                        Object target = ref.get();
                        if (ClassUtils.isNoClass(lname)) {
                            Map<String, Object> targetMap = (Map<String, Object>) target;
                            ((Map<String, Object>) res).forEach(targetMap::put);
                        } else {
                            if (D.class.isAssignableFrom(res.getClass())) {
                                D targetD = (D) target;
                                FieldUtils.setDValue(targetD, (Map<String, Object>) FieldUtils.getDValue(res));
                            } else {
                                Map<String, Object> targetMap = (Map<String, Object>) target;
                                ((Map<String, Object>) res).forEach(targetMap::put);
                            }
                        }
                    }
                    return res;
                },
                inName,
                inExtend,
                inSerialize,
                inType,
                inArray,
                (context, modelConfig, fieldConfig, dMap) -> clientComputeProcessor.in(context, fieldConfig, dMap)
        );
    }

    @Override
    public <T> T out(String model, Object obj) {
        if (obj == null) return null;
        if (ENABLE_BATCH_OPTIMIZATION && obj instanceof List) {
            return (T) processBatchOut(model, (List<?>) obj);
        }

        return processSingleOut(model, obj);
    }

    private <T> T processSingleOut(String model, Object obj) {
        int objId = getObjectId(obj);
        T reentry = getReentry(objId, obj);
        if (reentry != null) return reentry;

        return ClientDataComputeTemplate.getInstance().compute(null, model, obj,
                (context, oModel, oObj) -> this.out(oModel, oObj),
                (context, oModel, oObj) -> {
                    Models.modelDirective().enableOrmReentry(oObj);// 防重入
                    getReentryMap().put(objId, new SoftReference<Object>(Models.modelDirective().enableOrmReentry(new HashMap<String, Object>())));
                    return ormModelingProcessor.before(oModel, oObj);// 模型化
                },
                (context, modelConfig, oObj) -> {
                    String oModel = modelConfig.getModel();
                    remoteClientPageProcessor.out(oModel, oObj);// 分页数据处理
                    Map<String, Object> res = (Map<String, Object>) ormMappingProcessor.after(oModel, oObj);// map化
                    SoftReference<Object> ref = getReentryMap(objId);
                    if (ref != null && ref.get() != null) {
                        Map<String, Object> targetMap = (Map<String, Object>) ref.get();
                        res.forEach(targetMap::put);
                    }
                    return res;
                },
                outExtend,
                outType,
                outArray,
                outSerialize,
                outName
        );
    }

    private int getObjectId(Object obj) {
        if (obj == null) return 0;
        int objId = System.identityHashCode(obj);
        if (D.class.isAssignableFrom(obj.getClass())) {
            objId = System.identityHashCode(FieldUtils.getDValue(obj));
        }
        return objId;
    }

    private <T> T getReentry(int objId, Object obj) {
        if (Models.modelDirective().isOrmReentry(obj)) {
            SoftReference<Object> ref = getReentryMap(objId);
            if (ref != null) {
                return (T) ref.get();
            }
        }
        return null;
    }

    private <T> List<T> processBatchIn(ModelComputeContext totalContext, String model, List<?> objList) {
        if (CollectionUtils.isEmpty(objList)) {
            return (List<T>) objList;
        }

        ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getSimpleModelConfig(model);
        ModelAnalysis analysis = analyzeInModel(modelConfig);

        List<T> result = new ArrayList<>(objList.size());
        FieldComputeContext context = CONTEXT_HOLDER.get();

        try {
            context.setTotalContext(totalContext);
            context.setOp(null);

            for (Object obj : objList) {
                if (obj == null) {
                    result.add(null);
                    continue;
                }
                int objId = getObjectId(obj);
                T reentry = getReentry(objId, obj);
                if (reentry != null) {
                    result.add(reentry);
                    continue;
                }

                Models.modelDirective().enableOrmReentry(obj);// 防重入
                Object target = null;
                String lname = modelConfig.getLname();
                if (ClassUtils.isNoClass(lname)) {
                    target = new DataMap();
                } else {
                    target = TypeUtils.getNewInstance(lname);
                }
                getReentryMap().put(objId, new SoftReference<Object>(Models.modelDirective().enableOrmReentry(target)));

                Object processingObj = ormModelingProcessor.before(model, obj);

                Map<String, Object> dMap = null;
                if (processingObj instanceof Map) {
                    dMap = (Map<String, Object>) processingObj;
                } else if (processingObj instanceof D) {
                    dMap = ((D) processingObj).get_d();
                }

                if (dMap != null) {
                    for (ModelFieldConfig fc : analysis.allFields) {
                        inName.run(context, modelConfig, fc, dMap);
                    }
                    if (HAS_EXTEND_CONVERTERS) {
                        for (ModelFieldConfig fc : analysis.allFields) {
                            inExtend.run(context, modelConfig, fc, dMap);
                        }
                    }
                    for (ModelFieldConfig fc : analysis.serializeFields) {
                        inSerialize.run(context, modelConfig, fc, dMap);
                    }
                    for (ModelFieldConfig fc : analysis.typeFields) {
                        inType.run(context, modelConfig, fc, dMap);
                    }
                    for (ModelFieldConfig fc : analysis.arrayFields) {
                        inArray.run(context, modelConfig, fc, dMap);
                    }
                    for (ModelFieldConfig fc : analysis.allFields) {
                        clientComputeProcessor.in(context, fc, dMap);
                    }
                }

                Object res = RecursionOrmApi.getOrmObjectingProcessor().after(model, processingObj);// 对象化
                res = clientModelChecker.check(context.getTotalContext(), modelConfig.getModelDefinition(), res);// 模型约束校验
                
                SoftReference<Object> ref = getReentryMap(objId);
                if (ref != null && ref.get() != null) {
                    Object reentryTarget = ref.get();
                    if (ClassUtils.isNoClass(lname)) {
                        Map<String, Object> targetMap = (Map<String, Object>) reentryTarget;
                        ((Map<String, Object>) res).forEach(targetMap::put);
                    } else {
                        if (D.class.isAssignableFrom(res.getClass())) {
                            D targetD = (D) reentryTarget;
                            FieldUtils.setDValue(targetD, (Map<String, Object>) FieldUtils.getDValue(res));
                        } else {
                            Map<String, Object> targetMap = (Map<String, Object>) reentryTarget;
                            ((Map<String, Object>) res).forEach(targetMap::put);
                        }
                    }
                }
                result.add((T) res);
            }
        } finally {
            context.setTotalContext(null);
            context.setOp(null);
        }
        return result;
    }

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
                if (obj == null) {
                    result.add(null);
                    continue;
                }
                int objId = getObjectId(obj);
                T reentry = getReentry(objId, obj);
                if (reentry != null) {
                    result.add(reentry);
                    continue;
                }

                Models.modelDirective().enableOrmReentry(obj);// 防重入
                getReentryMap().put(objId, new SoftReference<Object>(Models.modelDirective().enableOrmReentry(new HashMap<String, Object>())));

                Object processingObj = ormModelingProcessor.before(model, obj);

                Map<String, Object> dMap = null;
                if (processingObj instanceof Map) {
                    dMap = (Map<String, Object>) processingObj;
                } else if (processingObj instanceof D) {
                    dMap = ((D) processingObj).get_d();
                }

                if (dMap != null) {
                    if (HAS_EXTEND_CONVERTERS) {
                        for (ModelFieldConfig fc : analysis.allFields) {
                            outExtend.run(context, modelConfig, fc, dMap);
                        }
                    }
                    for (ModelFieldConfig fc : analysis.typeFields) {
                        outType.run(context, modelConfig, fc, dMap);
                    }
                    for (ModelFieldConfig fc : analysis.arrayFields) {
                        outArray.run(context, modelConfig, fc, dMap);
                    }
                    for (ModelFieldConfig fc : analysis.serializeFields) {
                        outSerialize.run(context, modelConfig, fc, dMap);
                    }
                    for (ModelFieldConfig fc : analysis.allFields) {
                        outName.run(context, modelConfig, fc, dMap);
                    }
                }

                remoteClientPageProcessor.out(model, processingObj);// 分页数据处理
                Map<String, Object> res = (Map<String, Object>) ormMappingProcessor.after(model, processingObj);// map化
                SoftReference<Object> ref = getReentryMap(objId);
                if (ref != null && ref.get() != null) {
                    Map<String, Object> targetMap = (Map<String, Object>) ref.get();
                    res.forEach(targetMap::put);
                }
                result.add((T) res);
            }
        } finally {
            context.setTotalContext(null);
            context.setOp(null);
        }
        return result;
    }

    private static class ModelAnalysis {
        List<ModelFieldConfig> allFields;
        List<ModelFieldConfig> serializeFields = new ArrayList<>();
        List<ModelFieldConfig> typeFields = new ArrayList<>();
        List<ModelFieldConfig> arrayFields = new ArrayList<>();
    }

    private ModelAnalysis analyzeInModel(ModelConfig modelConfig) {
        ModelAnalysis analysis = new ModelAnalysis();
        analysis.allFields = modelConfig.getModelFieldConfigListSort();
        if (analysis.allFields == null) analysis.allFields = new ArrayList<>();
        for (ModelFieldConfig field : analysis.allFields) {
            if (needSerializeIn(field)) {
                analysis.serializeFields.add(field);
            }
            analysis.typeFields.add(field);
            if (needArrayIn(field)) {
                analysis.arrayFields.add(field);
            }
        }
        return analysis;
    }

    private ModelAnalysis analyzeOutModel(ModelConfig modelConfig) {
        ModelAnalysis analysis = new ModelAnalysis();
        analysis.allFields = modelConfig.getModelFieldConfigListSort();
        if (analysis.allFields == null) analysis.allFields = new ArrayList<>();
        for (ModelFieldConfig field : analysis.allFields) {
            analysis.typeFields.add(field);
            if (needArrayOut(field)) {
                analysis.arrayFields.add(field);
            }
            if (needSerializeOut(field)) {
                analysis.serializeFields.add(field);
            }
        }
        return analysis;
    }

    private boolean needArrayIn(ModelFieldConfig fieldConfig) {
        return TtypeEnum.isRelationMany(fieldConfig.getTtype());
    }

    private boolean needSerializeIn(ModelFieldConfig fieldConfig) {
        return fieldConfig.getStore() && !TypeUtils.isBaseType(fieldConfig.getLtype());
    }

    private boolean needArrayOut(ModelFieldConfig fieldConfig) {
        return TtypeEnum.isRelationMany(fieldConfig.getTtype());
    }

    private boolean needSerializeOut(ModelFieldConfig fieldConfig) {
        return fieldConfig.getStore() && !TypeUtils.isStringType(fieldConfig.getLtype());
    }

}
