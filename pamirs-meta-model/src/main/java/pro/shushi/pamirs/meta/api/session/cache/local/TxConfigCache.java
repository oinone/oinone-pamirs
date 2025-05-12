package pro.shushi.pamirs.meta.api.session.cache.local;

import pro.shushi.pamirs.meta.api.dto.config.TxConfig;
import pro.shushi.pamirs.meta.api.session.cache.api.TxConfigCacheApi;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.function.BiFunction;

/**
 * 事务配置缓存
 * <p>
 * 2021/8/19 12:37 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class TxConfigCache extends AbstractCacheWithDoubleKey<TxConfig> implements TxConfigCacheApi {

    @Override
    public String type() {
        return TxConfig.class.getSimpleName();
    }

    @Override
    public BiFunction<String, String, String> keyGenerator() {
        return (k1, k2) -> k1 + CharacterConstants.SEPARATOR_DOT + k2;
    }

}
