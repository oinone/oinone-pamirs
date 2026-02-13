package pro.shushi.pamirs.meta.api.core.orm.template;

import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.api.core.compute.template.OrmComputer;
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
 * 客户端数据计算模板
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ClientDataComputeTemplate {

    private final static OrmComputer ormComputer = new DefaultOrmComputer();

    private static ClientDataComputeTemplate INSTANCE;

    // 使用 ThreadLocal 复用 Context，减少对象创建开销，提高性能
    private static final ThreadLocal<FieldComputeContext> CONTEXT_HOLDER = ThreadLocal.withInitial(FieldComputeContext::new);

    public static ClientDataComputeTemplate getInstance() {
        if (null == INSTANCE) {
            synchronized (ClientDataComputeTemplate.class) {
                if (null == INSTANCE) {
                    ClientDataComputeTemplate.INSTANCE = new ClientDataComputeTemplate();
                }
            }
        }
        return ClientDataComputeTemplate.INSTANCE;
    }

    private FieldComputeContext getCleanContext(ModelComputeContext totalContext) {
        FieldComputeContext context = CONTEXT_HOLDER.get();
        context.setTotalContext(totalContext);
        context.setOp(null);
        return context;
    }

    public <T, R> R compute(String model, T origin, ModelIteratorComputeApi cycleComputeApi,
                            ModelBeforeComputeApi modelBeforeComputeProcessor,
                            PersistenceModelAfterComputeWithContextApi modelAfterComputeProcessor,
                            PersistenceFieldComputeApi... fieldComputeProcessors) {
        return compute(null, model, origin,
                (context, oModel, oOrigin) -> cycleComputeApi.run(model, oOrigin),
                (context, oModel, oOrigin) -> modelBeforeComputeProcessor.before(model, oOrigin),
                modelAfterComputeProcessor,
                fieldComputeProcessors);
    }

    public <T, R> R compute(ModelComputeContext totalContext, String model, T origin,
                            ModelIteratorComputeWithContextApi cycleComputeApi,
                            ModelBeforeComputeWithContextApi modelBeforeComputeProcessor,
                            PersistenceModelAfterComputeWithContextApi modelAfterComputeProcessor,
                            PersistenceFieldComputeApi... fieldComputeProcessors) {
        FieldComputeContext context = getCleanContext(totalContext);
        try {
            return ((OrmComputer<T, R>) ormComputer).compute(model, origin,
                    (oModel, oOrigin) -> {// model & map
                        Map<String, Object> dMap;
                        if (Map.class.isAssignableFrom(oOrigin.getClass())) {
                            dMap = (Map<String, Object>) oOrigin;
                        } else {
                            dMap = ((D) oOrigin).get_d();
                        }
                        oOrigin = (R) modelBeforeComputeProcessor.before(totalContext, oModel, oOrigin);
                        ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(oModel);
                        if (null == modelConfig) {
                            throw new RuntimeException(MessageFormat.format("未找到对应的模型配置，model:{0}", oModel));
                        }
                        List<ModelFieldConfig> modelFieldConfigList = modelConfig.getModelFieldConfigList();
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
                        oOrigin = (R) modelAfterComputeProcessor.after(totalContext, modelConfig, oOrigin);
                        return oOrigin;
                    },
                    (oModel, oMap) -> {// list
                        List list = ((List) oMap);
                        List result = new ArrayList(list.size());
                        int i = 0;
                        for (Object item : list) {
                            context.segment(i);
                            result.add(cycleComputeApi.run(totalContext, oModel, item));
                            context.dropSegment();
                            i++;
                        }
                        return (R) result;
                    },
                    (oModel, oMap) -> {// array
                        Object[] objects = ((Object[]) oMap);
                        Object[] resultObjects = new Object[objects.length];
                        int i = 0;
                        for (Object item : objects) {
                            context.segment(i);
                            resultObjects[i] = cycleComputeApi.run(totalContext, oModel, item);
                            context.dropSegment();
                            i++;
                        }
                        return (R) resultObjects;
                    }
            );
        } finally {
            context.setTotalContext(null);
            context.setOp(null);
        }
    }

}
