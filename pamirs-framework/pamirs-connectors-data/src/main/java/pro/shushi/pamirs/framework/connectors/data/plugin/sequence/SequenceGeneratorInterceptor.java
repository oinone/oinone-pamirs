package pro.shushi.pamirs.framework.connectors.data.plugin.sequence;

import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import pro.shushi.pamirs.framework.connectors.data.mapper.context.MapperContext;
import pro.shushi.pamirs.meta.api.core.compute.systems.type.gen.SequenceGenerator;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.common.CodeModel;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.constant.FieldConstants;
import pro.shushi.pamirs.meta.domain.model.SequenceConfig;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * 模型编码自动生成支持
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/14 2:11 上午
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
})
public class SequenceGeneratorInterceptor implements Interceptor {

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        if (SqlCommandType.INSERT != ms.getSqlCommandType()) {
            return invocation.proceed();
        }
        Object param = args[1];
        if (param instanceof Map) {
            Map map = (Map) param;
            // entity
            // 获取配置信息
            String model = MapperContext.model(map);
            if (StringUtils.isBlank(model)) {
                return invocation.proceed();
            }

            Object et = map.getOrDefault(Constants.ENTITY, null);
            if (et != null) {
                fillFieldCode(et, model);
            } else {
                List coll = (List) map.getOrDefault(Constants.COLLECTION, null);
                if (!CollectionUtils.isEmpty(coll)) {
                    for (Object e : coll) {
                        fillFieldCode(e, model);
                    }
                }
            }

            ModelConfig modelConfig = Optional.ofNullable(PamirsSession.getContext()).map(v -> v.getModelConfig(model)).orElse(null);
            SequenceConfig modelCodeConfig = Optional.ofNullable(modelConfig).map(ModelConfig::getSequenceConfig).orElse(null);
            if (null == modelCodeConfig || StringUtils.isBlank(modelCodeConfig.getCode())) {
                return invocation.proceed();
            }

            if (StringUtils.isBlank(modelCodeConfig.getSequence())) {
                modelCodeConfig = PamirsSession.getContext().getSequenceConfig(modelCodeConfig.getCode());
                modelConfig.setSequenceConfig(modelCodeConfig);
            }

            if (et != null) {
                fillModelCode(et, model, modelCodeConfig, modelConfig.isStaticConfig());
            } else {
                List coll = (List) map.getOrDefault(Constants.COLLECTION, null);
                if (!CollectionUtils.isEmpty(coll)) {
                    for (Object e : coll) {
                        fillModelCode(e, model, modelCodeConfig, modelConfig.isStaticConfig());
                    }
                }
            }
        }
        return invocation.proceed();
    }

    private void fillModelCode(Object entity, String model, SequenceConfig modelCodeConfig, boolean isStaticConfig) {
        if (!isStaticConfig && CodeModel.class.isAssignableFrom(entity.getClass())) {
            ((CodeModel) entity).generateCode(modelCodeConfig.getSequence(), modelCodeConfig.getCode());
        } else if (isStaticConfig || entity instanceof Map) {
            String code = TypeUtils.stringValueOf(FieldUtils.getFieldValue(entity, FieldConstants.CODE));
            if (StringUtils.isBlank(code)) {
                Object sequence = Spider.getDefaultExtension(SequenceGenerator.class)
                        .generate(modelCodeConfig.getSequence(), modelCodeConfig.getCode());
                if (null != sequence) {
                    FieldUtils.setFieldValue(entity, FieldConstants.CODE, TypeUtils.stringValueOf(sequence));
                }
            }
        }
    }

    private void fillFieldCode(Object entity, String model) {
        List<ModelFieldConfig> modelFieldConfigList = Optional.ofNullable(PamirsSession.getContext()).map(v -> v.getModelConfig(model))
                .map(ModelConfig::getModelFieldConfigList).orElse(null);
        if (CollectionUtils.isNotEmpty(modelFieldConfigList)) {
            for (ModelFieldConfig modelFieldConfig : modelFieldConfigList) {
                SequenceConfig sequenceConfig = modelFieldConfig.getSequenceConfig();
                if (null != sequenceConfig) {
                    String code = TypeUtils.stringValueOf(FieldUtils.getFieldValue(entity, modelFieldConfig.getLname()));
                    if (StringUtils.isBlank(code)) {
                        if (StringUtils.isBlank(sequenceConfig.getSequence())) {
                            sequenceConfig = PamirsSession.getContext().getSequenceConfig(sequenceConfig.getCode());
                        }
                        Object sequence = Spider.getDefaultExtension(SequenceGenerator.class)
                                .generate(sequenceConfig.getSequence(), sequenceConfig.getCode());
                        if (null != sequence) {
                            String seq = TypeUtils.stringValueOf(sequence);
                            FieldUtils.setFieldValue(entity, modelFieldConfig.getLname(), seq);
                            FieldUtils.setFieldValue(entity, modelFieldConfig.getColumn(), seq);
                        }
                    }
                }
            }
        }
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
        // to do nothing
    }

}
