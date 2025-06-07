package pro.shushi.pamirs.framework.orm.strategy;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.systems.strategy.StrategyApi;
import pro.shushi.pamirs.meta.api.session.PamirsSession;

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
public class DefaultStrategyApi implements StrategyApi {

    @Override
    public <T> void submit(T entity, Consumer<Object> insertConsumer, Consumer<Object> updateConsumer, Consumer<Object> insertOrUpdateConsumer) {
        if (!Models.modelDirective().isDirty(entity)) {
            return;
        }
        if (PamirsSession.directive().isUsePkStrategy()) {
            if (Models.compute().isPkValueValid(entity)) {
                updateConsumer.accept(entity);
            } else {
                insertConsumer.accept(entity);
            }
        } else {
            insertOrUpdateConsumer.accept(entity);
        }
    }

}
