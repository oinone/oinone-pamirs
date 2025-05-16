package pro.shushi.pamirs.boot.base.ux.cache.local;

import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.base.ux.cache.api.ActionCacheApi;
import pro.shushi.pamirs.meta.api.session.cache.local.AbstractCacheWithDoubleKey;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;

import java.util.function.BiFunction;

/**
 * 动作缓存
 * <p>
 * 2021/8/19 12:37 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class ActionCache extends AbstractCacheWithDoubleKey<Action> implements ActionCacheApi {

    @Override
    public String type() {
        return ActionCacheApi.class.getSimpleName();
    }

    @Override
    public BiFunction<String, String, String> keyGenerator() {
        return FunctionDefinition::sign;
    }

}
