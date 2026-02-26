package pro.shushi.pamirs.translate.manager.cache;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.translate.manager.base.TranslateRedisManager;
import pro.shushi.pamirs.translate.pojo.TranslatePojo;

import java.util.concurrent.TimeUnit;

/**
 * 维护translation内存，以及api操作
 *
 * @author xzf 2022/12/21 22:22
 **/
@Component
public class TranslateL2CacheManager {

    @Autowired
    private TranslateRedisManager translateRedisManager;

    //model 是唯一键，所以根据model做key，
    private final LoadingCache<String, String> itemCache;

    public TranslateL2CacheManager() {
        itemCache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(60000L)
                .weakValues()
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(@Nonnull String itemUniqueKey) {
                        TranslatePojo item = translateRedisManager.getItem(itemUniqueKey);
                        if (item == null) {
                            return null;
                        }
                        return item.getTarget();
                    }
                });

    }

    public String getItemCache(String module, String originLang, String lang, String model, String origin) {
        String uniqueKey = TranslatePojo.uniqueKey(module, originLang, lang, model, origin);
        return this.itemCache.get(uniqueKey);
    }
}
