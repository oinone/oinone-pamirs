package pro.shushi.pamirs.translate.manager.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.base.Splitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.util.JsonUtils;
import pro.shushi.pamirs.resource.api.model.ResourceTranslation;
import pro.shushi.pamirs.resource.api.model.ResourceTranslationItem;
import pro.shushi.pamirs.translate.pojo.TranslatePojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 维护translation缓存，以及api操作
 *
 * @author xzf 2022/12/21 22:22
 **/
@SuppressWarnings({"unchecked"})
@Component
public class TranslateRedisManager {

    @Autowired
    private RedisTemplate redisTemplate;

    public static final String translationRedisKey = "translation";

    public static final String itemExcelSnapshotKey = "translationItemExcelSnapshot";

    public static final String importKey = "translation:import.key";
    public static final String exportKey = "translation:export.key";

    private static final TypeReference<TranslatePojo> TRANSLATE_POJO_TR = new TypeReference<TranslatePojo>() {};

    public Boolean canLockImportKey() {
        boolean hasKey = Boolean.TRUE.equals(redisTemplate.hasKey(importKey));
        if (hasKey) {
            return false;
        }
        redisTemplate.expire(importKey, 3, TimeUnit.MINUTES);
        return true;
    }

    public void delLockImportKey() {
        redisTemplate.delete(importKey);
    }

    public Boolean canLockExportKey() {
        boolean hasKey = Boolean.TRUE.equals(redisTemplate.hasKey(exportKey));
        if (hasKey) {
            return false;
        }
        redisTemplate.expire(exportKey, 2, TimeUnit.MINUTES);
        return true;
    }

    public void delLockExportKey() {
        redisTemplate.delete(exportKey);
    }

    public void delTranslation(String translationUnique) {
        redisTemplate.opsForHash().delete(translationRedisKey, translationUnique);
    }

    public void delItem(String module, String originLang, String lang, String model, String origin) {
        String itemKey = TranslatePojo.itemKey(module, originLang, lang);
        String itemHashKey = TranslatePojo.itemHashKey(model, origin);
        redisTemplate.opsForHash().delete(itemKey, itemHashKey);
    }

    public void delAllItem() {
        redisTemplate.delete("translationItem#*");
    }

    public void delAllTranslation() {
        redisTemplate.delete(translationRedisKey);
    }

    public TranslatePojo getItem(String key) {
        List<String> list = Splitter.on("$").splitToList(key);
        String itemKey = list.get(0);
        String itemHashKey = list.get(1);
        Object obj = redisTemplate.opsForHash().get(itemKey, itemHashKey);
        return JSON.parseObject((String) obj, TranslatePojo.class);
    }

    public TranslatePojo getItem(String module, String originLang, String lang, String model, String origin) {
        String itemKey = TranslatePojo.itemKey(module, originLang, lang);
        String itemHashKey = TranslatePojo.itemHashKey(model, origin);
        Object obj = redisTemplate.opsForHash().get(itemKey, itemHashKey);
        return JSON.parseObject((String) obj, TranslatePojo.class);
    }

    public void putTranslation(String translationUnique, ResourceTranslation resourceTranslation) {
        redisTemplate.opsForHash().put(translationRedisKey, translationUnique, JsonUtils.toJSONString(resourceTranslation));
    }

    public void putAllTranslation(Map<String, ResourceTranslation> uniqueMap) {
        Map<String, String> map = new HashMap<>(uniqueMap.size());
        uniqueMap.forEach((key, value) -> map.put(key, JsonUtils.toJSONString(value)));
        redisTemplate.opsForHash().putAll(translationRedisKey, map);
    }

    public void putItem(TranslatePojo pojo) {
        String itemKey = TranslatePojo.itemKey(pojo.getModule(), pojo.getOriginLang(), pojo.getLang());
        String itemHashKey = TranslatePojo.itemHashKey(pojo.getModel(), pojo.getOrigin());
        redisTemplate.opsForHash().put(itemKey, itemHashKey, JSON.toJSONString(pojo));
    }

    public void putAllItem(Map<String, Map<String, String>> map) {
        for (Map.Entry<String, Map<String, String>> entry : map.entrySet()) {
            redisTemplate.opsForHash().putAll(entry.getKey(), entry.getValue());
        }
    }

    public String getItemExcelSnapshotKey(Long importTaskId) {
        return itemExcelSnapshotKey + ":" + importTaskId;
    }

    public void putExcelSnapshot(Long importTaskId, List<ResourceTranslationItem> list) {
        String key = getItemExcelSnapshotKey(importTaskId);
        String value = JsonUtils.toJSONString(list);
        redisTemplate.opsForValue().set(key, value);
    }

    public List<ResourceTranslationItem> getExcelSnapshot(Long importTaskId) {
        String key = getItemExcelSnapshotKey(importTaskId);
        Object value = redisTemplate.opsForValue().get(key);
        return JsonUtils.parseObjectList((String) value, ResourceTranslationItem.class);
    }

    public void delExcelSnapshot(Long importTaskId) {
        String key = getItemExcelSnapshotKey(importTaskId);
        redisTemplate.delete(key);
    }
}
