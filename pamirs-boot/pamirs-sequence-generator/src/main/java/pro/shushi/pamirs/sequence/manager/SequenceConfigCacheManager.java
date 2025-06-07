package pro.shushi.pamirs.sequence.manager;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import pro.shushi.pamirs.boot.common.util.MetaOnlineLocalUtil;
import pro.shushi.pamirs.framework.connectors.data.api.datasource.DsHintApi;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.domain.model.SequenceConfig;

import java.util.concurrent.TimeUnit;

/**
 * SequenceConfigCacheManager
 *
 * @author yakir on 2023/08/01 18:34.
 */
public class SequenceConfigCacheManager {

    private static final LoadingCache<String, SequenceConfig> cache = Caffeine.<String, SequenceConfig>newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .maximumSize(1000)
            .weakValues()
            .build(SequenceConfigCacheManager::querySequenceConfig);

    public static SequenceConfig get(String seq) {
        return cache.get(seq);
    }

    private static SequenceConfig querySequenceConfig(String code) {
        boolean metaOnline = MetaOnlineLocalUtil.metaOnline();
        if (metaOnline) {
            return PamirsSession.getContext().getSequenceConfig(code);
        } else {
            QueryWrapper<SequenceConfig> qw = new QueryWrapper<SequenceConfig>()
                    .select("`id`", "`code`", "`prefix`", "`suffix`", "`size`", "`format`", "`step`", "`initial`", "`sequence`",
                            "`is_random_step` as isRandomStep",
                            "`zeroing_period` as zeroingPeriod")
                    .eq("code", code);
            qw.setModel(SequenceConfig.MODEL_MODEL);
            try (DsHintApi ignored = DsHintApi.model(SequenceConfig.MODEL_MODEL)) {
                return Models.data().queryOneByWrapper(qw);
            }
        }
    }
}
