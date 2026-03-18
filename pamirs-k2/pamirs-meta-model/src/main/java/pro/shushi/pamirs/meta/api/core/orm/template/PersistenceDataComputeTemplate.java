package pro.shushi.pamirs.meta.api.core.orm.template;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.locale.utils.I18nUtils;
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
        FieldComputeContext context = new FieldComputeContext();
        context.setTotalContext(null);
        if (origin == null) {
            return null;
        } else if (StringUtils.isBlank(model)) {
            return (R) origin;
        } else if (Map.class.isAssignableFrom(origin.getClass()) || D.class.isAssignableFrom(origin.getClass())) {
            ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getSimpleModelConfig(model);
            if (null == modelConfig) {
                throw new RuntimeException(MessageFormat.format(I18nUtils.getMessage("DataComputeTemplate.modelConfigNotFound"), model));
            }
            return computeDMap(context, modelConfig, origin,
                    (ctx, oModel, oOrigin) -> modelBeforeComputeProcessor.before(oModel, oOrigin),
                    (ctx, oModel, oOrigin) -> modelAfterComputeProcessor.after(oModel, oOrigin),
                    fieldComputeProcessors);
        } else if (List.class.isAssignableFrom(origin.getClass())) {
            List list = ((List) origin);
            List result = new ArrayList(list.size());
            int i = 0;
            ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getSimpleModelConfig(model);
            if (null == modelConfig) {
                throw new RuntimeException(I18nUtils.getMessage("DataComputeTemplate.modelConfigNotFound", model));
            }
            FieldComputeContext subcontext = new FieldComputeContext();
            subcontext.setTotalContext(null);
            for (Object item : list) {
                subcontext.segment(i);
                result.add(computeDMap(subcontext, modelConfig, item,
                        (ctx, oModel, oOrigin) -> modelBeforeComputeProcessor.before(oModel, oOrigin),
                        (ctx, oModel, oOrigin) -> modelAfterComputeProcessor.after(oModel, oOrigin),
                        fieldComputeProcessors));
                subcontext.dropSegment();
                i++;
            }
            return (R) result;
        } else if (origin.getClass().isArray()) {
            Object[] objects = ((Object[]) origin);
            Object[] resultObjects = new Object[objects.length];
            ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getSimpleModelConfig(model);
            if (null == modelConfig) {
                throw new RuntimeException(I18nUtils.getMessage("DataComputeTemplate.modelConfigNotFound", model));
            }
            FieldComputeContext subcontext = new FieldComputeContext();
            subcontext.setTotalContext(null);
            int i = 0;
            for (Object item : objects) {
                subcontext.segment(i);
                resultObjects[i] = computeDMap(subcontext, modelConfig, item,
                        (ctx, oModel, oOrigin) -> modelBeforeComputeProcessor.before(oModel, oOrigin),
                        (ctx, oModel, oOrigin) -> modelAfterComputeProcessor.after(oModel, oOrigin),
                        fieldComputeProcessors);
                subcontext.dropSegment();
                i++;
            }
            return (R) resultObjects;
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
