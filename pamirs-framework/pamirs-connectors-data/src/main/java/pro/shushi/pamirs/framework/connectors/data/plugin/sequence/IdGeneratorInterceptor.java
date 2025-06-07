package pro.shushi.pamirs.framework.connectors.data.plugin.sequence;

import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import pro.shushi.pamirs.framework.connectors.data.mapper.context.MapperContext;
import pro.shushi.pamirs.meta.api.core.compute.systems.type.gen.IdGenerator;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.constant.SqlConstants;
import pro.shushi.pamirs.meta.util.FieldUtils;

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
public class IdGeneratorInterceptor implements Interceptor {

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
            String keyGenerator = PamirsTableInfo.fetchKeyGenerator(model);
            if (PamirsTableInfo.isAutoIncrement(keyGenerator)) {
                return invocation.proceed();
            }
            Object et = map.getOrDefault(Constants.ENTITY, null);
            ModelConfig modelConfig = PamirsSession.getContext().getSimpleModelConfig(model);
            if (et != null) {
                fillId(et, keyGenerator, modelConfig.isStaticConfig());
            } else {
                List coll = (List) map.getOrDefault(Constants.COLLECTION, null);
                if (CollectionUtils.isEmpty(coll)) {
                    return invocation.proceed();
                }
                for (Object e : coll) {
                    fillId(e, keyGenerator, modelConfig.isStaticConfig());
                }
            }
        }
        return invocation.proceed();
    }

    public static Object fillAndFetchId(String model, Object entity) {
        Object id = FieldUtils.getFieldValue(entity, SqlConstants.ID);
        if (null == id) {
            String keyGenerator = PamirsTableInfo.fetchKeyGenerator(model);
            if (!PamirsTableInfo.isAutoIncrement(keyGenerator)) {
                fillId(entity, keyGenerator, false);
            }
            return FieldUtils.getFieldValue(entity, SqlConstants.ID);
        }
        return id;
    }

    private static void fillId(Object entity, String keyGenerator, boolean isStaticModelConfig) {
        if (!isStaticModelConfig && IdModel.class.isAssignableFrom(entity.getClass())) {
            ((IdModel) entity).generateId(keyGenerator);
        } else if (isStaticModelConfig || entity instanceof Map) {
            Object id = FieldUtils.getFieldValue(entity, SqlConstants.ID);
            if (null == id) {
                Optional.ofNullable(Spider.getDefaultExtension(IdGenerator.class)).map(v -> v.generate(keyGenerator))
                        .ifPresent(sequence -> FieldUtils.setFieldValue(entity, SqlConstants.ID, Long.valueOf(sequence.toString())));
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
