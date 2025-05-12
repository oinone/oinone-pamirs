package pro.shushi.pamirs.meta.api.session.cache.local;

import pro.shushi.pamirs.meta.api.session.cache.api.ExtPointImplementationCacheApi;
import pro.shushi.pamirs.meta.domain.fun.ExtPointImplementation;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;

import java.util.List;
import java.util.function.BiFunction;

/**
 * 扩展点实现缓存
 * <p>
 * 2021/8/19 12:37 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class ExtPointImplementationCache extends AbstractCacheWithDoubleKey<List<ExtPointImplementation>>
        implements ExtPointImplementationCacheApi {

    @Override
    public String type() {
        return ExtPointImplementation.class.getSimpleName();
    }

    @Override
    public BiFunction<String, String, String> keyGenerator() {
        return FunctionDefinition::sign;
    }

}
