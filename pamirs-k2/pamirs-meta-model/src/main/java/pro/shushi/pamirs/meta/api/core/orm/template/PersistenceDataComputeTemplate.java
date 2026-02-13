package pro.shushi.pamirs.meta.api.core.orm.template;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.api.core.orm.template.context.FieldComputeContext;
import pro.shushi.pamirs.meta.api.core.orm.template.context.FieldComputeOp;
import pro.shushi.pamirs.meta.api.core.orm.template.context.ModelComputeContext;
import pro.shushi.pamirs.meta.api.core.orm.template.function.*;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.D;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 持久层数据计算模板
 *
 * @author d@shushi.pro
 * @author cpc@shushi.pro 去除闭包提升性能
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class PersistenceDataComputeTemplate {

    private static PersistenceDataComputeTemplate INSTANCE;

    /** 复用计算上下文以减少 GC */
    private static final ThreadLocal<FieldComputeContext> CONTEXT_HOLDER = ThreadLocal.withInitial(FieldComputeContext::new);

    public static PersistenceDataComputeTemplate getInstance() {
        if (null == INSTANCE) {
            synchronized (PersistenceDataComputeTemplate.class) {
                if (null == INSTANCE) {
                    PersistenceDataComputeTemplate.INSTANCE = new PersistenceDataComputeTemplate();
                }
            }
        }
        return PersistenceDataComputeTemplate.INSTANCE;
    }

    public <T, R> R compute(String model, T origin,
                            ModelBeforeComputeApi modelBeforeComputeProcessor,
                            PersistenceModelAfterComputeApi modelAfterComputeProcessor,
                            PersistenceFieldComputeApi... fieldComputeProcessors) {
        if (origin == null) {
            return null;
        } else if (StringUtils.isBlank(model)) {
            return (R) origin;
        }

        FieldComputeContext context = CONTEXT_HOLDER.get();
        context.setTotalContext(null);

        try {
            if (Map.class.isAssignableFrom(origin.getClass()) || D.class.isAssignableFrom(origin.getClass())) {
                ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getSimpleModelConfig(model);
                if (null == modelConfig) {
                    throw new RuntimeException(MessageFormat.format("未找到对应的模型配置，model:{0}", model));
                }
                return computeDMap(context, modelConfig, origin,
                        (ctx, oModel, oOrigin) -> modelBeforeComputeProcessor.before(oModel, oOrigin),
                        (ctx, oModel, oOrigin) -> modelAfterComputeProcessor.after(oModel, oOrigin),
                        fieldComputeProcessors);
            } else if (List.class.isAssignableFrom(origin.getClass())) {
                List list = ((List) origin);
                List result = new ArrayList(list.size());
                if (list.isEmpty()) {
                    return (R) result;
                }

                ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getSimpleModelConfig(model);
                if (null == modelConfig) {
                    throw new RuntimeException(MessageFormat.format("未找到对应的模型配置，model:{0}", model));
                }

                int i = 0;
                for (Object item : list) {
                    context.segment(i);
                    try {
                        result.add(computeDMap(context, modelConfig, item,
                                (ctx, oModel, oOrigin) -> modelBeforeComputeProcessor.before(oModel, oOrigin),
                                (ctx, oModel, oOrigin) -> modelAfterComputeProcessor.after(oModel, oOrigin),
                                fieldComputeProcessors));
                    } finally {
                        context.dropSegment();
                    }
                    i++;
                }
                return (R) result;
            } else if (origin.getClass().isArray()) {
                Object[] objects = ((Object[]) origin);
                Object[] resultObjects = new Object[objects.length];
                if (objects.length == 0) {
                    return (R) resultObjects;
                }

                ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getSimpleModelConfig(model);
                if (null == modelConfig) {
                    throw new RuntimeException(MessageFormat.format("未找到对应的模型配置，model:{0}", model));
                }

                int i = 0;
                for (Object item : objects) {
                    context.segment(i);
                    try {
                        resultObjects[i] = computeDMap(context, modelConfig, item,
                                (ctx, oModel, oOrigin) -> modelBeforeComputeProcessor.before(oModel, oOrigin),
                                (ctx, oModel, oOrigin) -> modelAfterComputeProcessor.after(oModel, oOrigin),
                                fieldComputeProcessors);
                    } finally {
                        context.dropSegment();
                    }
                    i++;
                }
                return (R) resultObjects;
            }
        } finally {
            context.setTotalContext(null);
            context.setOp(null);
        }

        return (R) origin;
    }

    private <T, R> R computeDMap(FieldComputeContext context,
                                 ModelConfig modelConfig,
                                 T origin,
                                 ModelBeforeComputeWithContextApi modelBeforeComputeProcessor,
                                 PersistenceModelAfterComputeWithContextApi modelAfterComputeProcessor,
                                 PersistenceFieldComputeApi[] fieldComputeProcessors) {
        Map<String, Object> dMap;
        if (origin == null) {
            return null;
        } else if (Map.class.isAssignableFrom(origin.getClass())) {
            dMap = (Map<String, Object>) origin;
        } else {
            dMap = ((D) origin).get_d();
        }
        ModelComputeContext totalContext = context.getTotalContext();
        String model = modelConfig.getModel();
        R result = (R) modelBeforeComputeProcessor.before(totalContext, model, origin);
        List<ModelFieldConfig> modelFieldConfigList = modelConfig.getModelFieldConfigListSort();
        if (!CollectionUtils.isEmpty(modelFieldConfigList)) {
            if (null != fieldComputeProcessors) {
                XXX:
                for (ModelFieldConfig fieldConfig : modelFieldConfigList) {
                    context.segment(fieldConfig.getName());
                    context.setOp(null);
                    try {
                        for (PersistenceFieldComputeApi fieldComputeProcessor : fieldComputeProcessors) {
                            if (FieldComputeOp.skipNextProcessor.equals(context.getOp())) {
                                continue;
                            }
                            fieldComputeProcessor.run(context, modelConfig, fieldConfig, dMap);
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
        }

        return (R) modelAfterComputeProcessor.after(totalContext, modelConfig, result);
    }
}
