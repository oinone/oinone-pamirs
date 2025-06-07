package pro.shushi.pamirs.meta.api.session.cache.local;

import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.cache.api.StandaloneFunCacheApi;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;

import java.util.function.BiFunction;

/**
 * 孤立函数缓存
 * <p>
 * 2021/8/19 12:37 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class StandaloneFunCache extends AbstractCacheWithDoubleKey<Function> implements StandaloneFunCacheApi {

    @Override
    public String type() {
        return Function.class.getSimpleName();
    }

    @Override
    public BiFunction<String, String, String> keyGenerator() {
        return FunctionDefinition::sign;
    }

}
