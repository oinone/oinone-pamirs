package pro.shushi.pamirs.framework.orm.strategy;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.systems.strategy.RelationStrategyApi;
import pro.shushi.pamirs.meta.api.core.orm.systems.strategy.StrategyApi;
import pro.shushi.pamirs.meta.constant.FieldConstants;
import pro.shushi.pamirs.meta.util.FieldUtils;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.function.Consumer;

/**
 * 数据提交策略默认实现
 * <p>
 * 2020/7/21 1:49 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Component
public class DefaultRelationStrategyApi implements RelationStrategyApi {

    @Resource
    private StrategyApi strategyApi;

    @Override
    public void submit(String references, List<String> referenceFields, Object entity,
                       Consumer<Object> insertConsumer, Consumer<Object> updateConsumer,
                       Consumer<Object> insertOrUpdateConsumer) {
        if (entity instanceof List) {
            //noinspection unchecked
            for (Object item : (List<Object>) entity) {
                submit(references, referenceFields, item, insertConsumer, updateConsumer, insertOrUpdateConsumer);
            }
        } else {
            if (Models.compute().isOnlyNonEmptyFields(references, referenceFields, null, entity)) {
                return;
            }
            FieldUtils.setFieldValue(entity, FieldConstants._d_modelFieldName, references);
            strategyApi.submit(entity, insertConsumer, updateConsumer, insertOrUpdateConsumer);
        }
    }

}
