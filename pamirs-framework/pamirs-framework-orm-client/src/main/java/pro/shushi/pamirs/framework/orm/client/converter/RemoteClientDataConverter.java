package pro.shushi.pamirs.framework.orm.client.converter;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.orm.api.RecursionOrmApi;
import pro.shushi.pamirs.framework.orm.client.checker.ClientModelChecker;
import pro.shushi.pamirs.framework.orm.client.converter.processor.*;
import pro.shushi.pamirs.framework.orm.named.LnameToNameProcessor;
import pro.shushi.pamirs.framework.orm.named.NameToLnameProcessor;
import pro.shushi.pamirs.framework.orm.processor.OrmMappingProcessor;
import pro.shushi.pamirs.framework.orm.processor.OrmModelingProcessor;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.convert.ClientDataConverter;
import pro.shushi.pamirs.meta.api.core.orm.template.ClientDataComputeTemplate;
import pro.shushi.pamirs.meta.api.core.orm.template.context.FieldComputeContext;
import pro.shushi.pamirs.meta.api.core.orm.template.context.FieldComputeOp;
import pro.shushi.pamirs.meta.api.core.orm.template.context.ModelComputeContext;
import pro.shushi.pamirs.meta.api.core.orm.template.function.PersistenceFieldComputeApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.util.ClassUtils;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.TypeUtils;

import javax.annotation.Resource;
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

    // 批量优化开关
    private static final boolean ENABLE_BATCH_OPTIMIZATION = true;

    // 使用 ThreadLocal 复用 FieldComputeContext，减少对象创建
    private static final ThreadLocal<FieldComputeContext> CONTEXT_HOLDER = ThreadLocal.withInitial(FieldComputeContext::new);

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

    // ========== Lambda 表达式提取为 final 字段（in 方向）==========
    private final PersistenceFieldComputeApi inName = (context, modelConfig, fieldConfig, dMap) -> nameToLnameProcessor.convert(fieldConfig, dMap);
    private final PersistenceFieldComputeApi inExtend = (context, modelConfig, fieldConfig, dMap) -> clientExtendProcessor.in(context, fieldConfig, dMap);
    private final PersistenceFieldComputeApi inSerialize = (context, modelConfig, fieldConfig, dMap) -> clientSerializeProcessor.in(context, fieldConfig, dMap);
    private final PersistenceFieldComputeApi inType = (context, modelConfig, fieldConfig, dMap) -> remoteClientTypeProcessor.in(context, fieldConfig, dMap);
    private final PersistenceFieldComputeApi inArray = (context, modelConfig, fieldConfig, dMap) -> clientArrayProcessor.in(context, fieldConfig, dMap);

    // in 方向的处理器数组
    private final PersistenceFieldComputeApi[] inProcessors = new PersistenceFieldComputeApi[]{
            inName, inExtend, inSerialize, inType, inArray
    };

    // ========== Lambda 表达式提取为 final 字段（out 方向）==========
    private final PersistenceFieldComputeApi outExtend = (context, modelConfig, fieldConfig, dMap) -> clientExtendProcessor.out(context, fieldConfig, dMap);
    private final PersistenceFieldComputeApi outType = (context, modelConfig, fieldConfig, dMap) -> remoteClientTypeProcessor.out(context, fieldConfig, dMap);
    private final PersistenceFieldComputeApi outArray = (context, modelConfig, fieldConfig, dMap) -> clientArrayProcessor.out(context, fieldConfig, dMap);
    private final PersistenceFieldComputeApi outSerialize = (context, modelConfig, fieldConfig, dMap) -> clientSerializeProcessor.out(context, fieldConfig, dMap);
    private final PersistenceFieldComputeApi outName = (context, modelConfig, fieldConfig, dMap) -> lnameToNameProcessor.convert(fieldConfig, dMap);

    // out 方向的处理器数组
    private final PersistenceFieldComputeApi[] outProcessors = new PersistenceFieldComputeApi[]{
            outExtend, outType, outArray, outSerialize, outName
    };

    @Override
    public <T> T in(ModelComputeContext totalContext, String model, Object obj) {
        if (obj == null) return null;

        // 批量优化：如果是 List 类型，走批量处理路径
        if (ENABLE_BATCH_OPTIMIZATION && obj instanceof List) {
            return (T) processBatchIn(totalContext, model, (List<?>) obj);
        }

        int objIdTemp = System.identityHashCode(obj);
        if (D.class.isAssignableFrom(obj.getClass())) {
            objIdTemp = System.identityHashCode(FieldUtils.getDValue(obj));
        }
        int objId = objIdTemp;
        if (Models.modelDirective().isOrmReentry(obj)) {// 判断是否重入
            if (getReentryMap(objId) != null) {
                T res = (T) (getReentryMap(objId).get());
                return res;
            }
        }
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
                    if (ClassUtils.isNoClass(lname)) {
                        Map obj1 = (Map) getReentryMap().get(objId).get();
                        ((Map) res).forEach((k, v) -> {
                            obj1.put(k, v);
                        });
                    } else {
                        if (D.class.isAssignableFrom(res.getClass())) {
                            D obj1 = (D) getReentryMap().get(objId).get();
                            FieldUtils.setDValue(obj1, (Map) FieldUtils.getDValue(res));
                        } else {
                            Map obj1 = (Map) getReentryMap().get(objId).get();
                            ((Map) res).forEach((k, v) -> {
                                obj1.put(k, v);
                            });
                        }
                    }
                    return res;
                },
                inProcessors
        );
    }

    @Override
    public <T> T out(String model, Object obj) {
        if (obj == null) return null;

        // 批量优化：如果是 List 类型，走批量处理路径
        if (ENABLE_BATCH_OPTIMIZATION && obj instanceof List) {
            return (T) processBatchOut(model, (List<?>) obj);
        }

        int objIdTemp = System.identityHashCode(obj);
        if (D.class.isAssignableFrom(obj.getClass())) {
            objIdTemp = System.identityHashCode(FieldUtils.getDValue(obj));
        }
        int objId = objIdTemp;

        if (Models.modelDirective().isOrmReentry(obj)) {// 判断是否重入
            if (getReentryMap(objId) != null) {
                T res = (T) (getReentryMap(objId).get());
                return res;
            }
        }
        return ClientDataComputeTemplate.getInstance().compute(model, obj,
                this::out,
                (oModel, oObj) -> {
                    Models.modelDirective().enableOrmReentry(oObj);// 防重入
                    getReentryMap().put(objId, new SoftReference<Object>(Models.modelDirective().enableOrmReentry(new HashMap())));
                    return ormModelingProcessor.before(oModel, oObj);// 模型化
                },
                (context, modelConfig, oObj) -> {
                    String oModel = modelConfig.getModel();
                    remoteClientPageProcessor.out(oModel, oObj);// 分页数据处理
                    Map<String, Object> res = (Map<String, Object>) ormMappingProcessor.after(oModel, oObj);// map化
                    Map obj1 = (HashMap) getReentryMap().get(objId).get();
                    res.forEach((k, v) -> {
                        obj1.put(k, v);
                    });
                    return res;
                },
                outProcessors
        );
    }

    /**
     * 批量处理 in 方向（List）
     * 扁平化递归调用，完整支持 FieldComputeOp 控制流
     */
    private <T> List<T> processBatchIn(ModelComputeContext totalContext, String model, List<?> list) {
        if (list.isEmpty()) {
            return new ArrayList<>();
        }

        FieldComputeContext context = CONTEXT_HOLDER.get();
        context.setTotalContext(totalContext);
        context.setOp(null);

        try {
            List<T> result = new ArrayList<>(list.size());
            ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(model);
            List<ModelFieldConfig> modelFieldConfigList = modelConfig.getModelFieldConfigList();

            int i = 0;
            for (Object item : list) {
                context.segment(i);
                try {
                    if (item == null) {
                        result.add(null);
                        continue;
                    }

                    // 如果嵌套的元素还是 List，递归调用
                    if (item instanceof List) {
                        result.add((T) processBatchIn(totalContext, model, (List<?>) item));
                        continue;
                    }

                    // 如果是 Array，递归处理
                    if (item.getClass().isArray()) {
                        Object[] arr = (Object[]) item;
                        Object[] resultArr = new Object[arr.length];
                        for (int j = 0; j < arr.length; j++) {
                            resultArr[j] = in(totalContext, model, arr[j]);
                        }
                        result.add((T) resultArr);
                        continue;
                    }

                    // 获取对象 ID（用于防重入）
                    int objIdTemp = System.identityHashCode(item);
                    if (D.class.isAssignableFrom(item.getClass())) {
                        objIdTemp = System.identityHashCode(FieldUtils.getDValue(item));
                    }
                    int objId = objIdTemp;

                    // 判断是否重入
                    if (Models.modelDirective().isOrmReentry(item)) {
                        if (getReentryMap(objId) != null) {
                            T res = (T) (getReentryMap(objId).get());
                            result.add(res);
                            continue;
                        }
                    }

                    // 模型化前处理
                    Models.modelDirective().enableOrmReentry(item);
                    String lname = modelConfig.getLname();
                    Object resultObj = null;
                    if (ClassUtils.isNoClass(lname)) {
                        resultObj = new DataMap();
                    } else {
                        resultObj = TypeUtils.getNewInstance(lname);
                    }
                    getReentryMap().put(objId, new SoftReference<Object>(Models.modelDirective().enableOrmReentry(resultObj)));

                    Object processingObj = ormModelingProcessor.before(model, item);

                    // 获取 dMap
                    Map<String, Object> dMap;
                    if (Map.class.isAssignableFrom(processingObj.getClass())) {
                        dMap = (Map<String, Object>) processingObj;
                    } else {
                        dMap = ((D) processingObj).get_d();
                    }

                    // 完整的字段处理循环，支持 FieldComputeOp
                    if (modelFieldConfigList != null && !modelFieldConfigList.isEmpty()) {
                        XXX:
                        for (ModelFieldConfig fieldConfig : modelFieldConfigList) {
                            context.segment(fieldConfig.getName());
                            context.setOp(null);
                            try {
                                for (PersistenceFieldComputeApi processor : inProcessors) {
                                    if (FieldComputeOp.skipNextProcessor.equals(context.getOp())) {
                                        continue;
                                    }
                                    processor.run(context, modelConfig, fieldConfig, dMap);
                                    if (FieldComputeOp.continueNextField.equals(context.getOp())) {
                                        continue XXX;
                                    }
                                    if (FieldComputeOp.skipAllField.equals(context.getOp())) {
                                        break XXX;
                                    }
                                }
                            } finally {
                                context.dropSegment();
                            }
                        }
                    }

                    // 对象化 + 模型约束校验
                    processingObj = RecursionOrmApi.getOrmObjectingProcessor().after(model, processingObj);
                    Object res = clientModelChecker.check(totalContext, modelConfig.getModelDefinition(), processingObj);

                    // 将结果填充到预创建的对象中
                    if (ClassUtils.isNoClass(lname)) {
                        Map obj1 = (Map) getReentryMap().get(objId).get();
                        ((Map) res).forEach((k, v) -> {
                            obj1.put(k, v);
                        });
                    } else {
                        if (D.class.isAssignableFrom(res.getClass())) {
                            D obj1 = (D) getReentryMap().get(objId).get();
                            FieldUtils.setDValue(obj1, (Map) FieldUtils.getDValue(res));
                        } else {
                            Map obj1 = (Map) getReentryMap().get(objId).get();
                            ((Map) res).forEach((k, v) -> {
                                obj1.put(k, v);
                            });
                        }
                    }

                    result.add((T) res);
                } finally {
                    context.dropSegment();
                }
                i++;
            }

            return result;
        } finally {
            context.setTotalContext(null);
            context.setOp(null);
        }
    }

    /**
     * 批量处理 out 方向（List）
     * 扁平化递归调用，完整支持 FieldComputeOp 控制流
     */
    private <T> List<T> processBatchOut(String model, List<?> list) {
        if (list.isEmpty()) {
            return new ArrayList<>();
        }

        FieldComputeContext context = CONTEXT_HOLDER.get();
        context.setTotalContext(null);
        context.setOp(null);

        try {
            List<T> result = new ArrayList<>(list.size());
            ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(model);
            List<ModelFieldConfig> modelFieldConfigList = modelConfig.getModelFieldConfigList();

            int i = 0;
            for (Object item : list) {
                context.segment(i);
                try {
                    if (item == null) {
                        result.add(null);
                        continue;
                    }

                    // 如果嵌套的元素还是 List，递归调用
                    if (item instanceof List) {
                        result.add((T) processBatchOut(model, (List<?>) item));
                        continue;
                    }

                    // 如果是 Array，递归处理
                    if (item.getClass().isArray()) {
                        Object[] arr = (Object[]) item;
                        Object[] resultArr = new Object[arr.length];
                        for (int j = 0; j < arr.length; j++) {
                            resultArr[j] = out(model, arr[j]);
                        }
                        result.add((T) resultArr);
                        continue;
                    }

                    // 获取对象 ID（用于防重入）
                    int objIdTemp = System.identityHashCode(item);
                    if (D.class.isAssignableFrom(item.getClass())) {
                        objIdTemp = System.identityHashCode(FieldUtils.getDValue(item));
                    }
                    int objId = objIdTemp;

                    // 判断是否重入
                    if (Models.modelDirective().isOrmReentry(item)) {
                        if (getReentryMap(objId) != null) {
                            T res = (T) (getReentryMap(objId).get());
                            result.add(res);
                            continue;
                        }
                    }

                    // 模型化前处理
                    Models.modelDirective().enableOrmReentry(item);
                    getReentryMap().put(objId, new SoftReference<Object>(Models.modelDirective().enableOrmReentry(new HashMap())));

                    Object processingObj = ormModelingProcessor.before(model, item);

                    // 获取 dMap
                    Map<String, Object> dMap;
                    if (Map.class.isAssignableFrom(processingObj.getClass())) {
                        dMap = (Map<String, Object>) processingObj;
                    } else {
                        dMap = ((D) processingObj).get_d();
                    }

                    // 完整的字段处理循环，支持 FieldComputeOp
                    if (modelFieldConfigList != null && !modelFieldConfigList.isEmpty()) {
                        XXX:
                        for (ModelFieldConfig fieldConfig : modelFieldConfigList) {
                            context.segment(fieldConfig.getName());
                            context.setOp(null);
                            try {
                                for (PersistenceFieldComputeApi processor : outProcessors) {
                                    if (FieldComputeOp.skipNextProcessor.equals(context.getOp())) {
                                        continue;
                                    }
                                    processor.run(context, modelConfig, fieldConfig, dMap);
                                    if (FieldComputeOp.continueNextField.equals(context.getOp())) {
                                        continue XXX;
                                    }
                                    if (FieldComputeOp.skipAllField.equals(context.getOp())) {
                                        break XXX;
                                    }
                                }
                            } finally {
                                context.dropSegment();
                            }
                        }
                    }

                    // 分页数据处理 + map化
                    remoteClientPageProcessor.out(model, processingObj);
                    Map<String, Object> res = (Map<String, Object>) ormMappingProcessor.after(model, processingObj);

                    // 将结果填充到预创建的对象中
                    Map obj1 = (HashMap) getReentryMap().get(objId).get();
                    res.forEach((k, v) -> {
                        obj1.put(k, v);
                    });

                    result.add((T) res);
                } finally {
                    context.dropSegment();
                }
                i++;
            }

            return result;
        } finally {
            context.setTotalContext(null);
            context.setOp(null);
        }
    }

}

