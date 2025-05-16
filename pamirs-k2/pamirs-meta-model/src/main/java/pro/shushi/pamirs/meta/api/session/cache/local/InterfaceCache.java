package pro.shushi.pamirs.meta.api.session.cache.local;

import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.cache.api.InterfaceCacheApi;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.fun.InterfaceDefinition;

import java.util.function.BiFunction;

/**
 * 事务配置缓存
 * <p>
 * 2021/8/19 12:37 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class InterfaceCache extends AbstractCacheWithDoubleKey<Function> implements InterfaceCacheApi {

    @Override
    public String type() {
        return InterfaceDefinition.class.getSimpleName();
    }

    @Override
    public BiFunction<String, String, String> keyGenerator() {
        return FunctionDefinition::sign;
    }

}
