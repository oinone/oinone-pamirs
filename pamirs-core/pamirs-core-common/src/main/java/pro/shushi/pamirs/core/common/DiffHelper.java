package pro.shushi.pamirs.core.common;

import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.core.common.cache.MemoryListSearchCache;
import pro.shushi.pamirs.core.common.cache.UniqueKeyGenerator;
import pro.shushi.pamirs.core.common.diff.DiffList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 差量帮助类
 *
 * @author Adamancy Zhang at 20:13 on 2025-04-27
 */
public class DiffHelper {

    private DiffHelper() {
        // reject create object
    }

    public static <K, V> DiffList<V> diff(List<V> dataList, List<V> existList, UniqueKeyGenerator<V, K> keyGetter) {
        MemoryListSearchCache<K, V> widgetCache = new MemoryListSearchCache<>(existList, keyGetter);
        List<V> allList = new ArrayList<>();
        List<V> createList = new ArrayList<>();
        List<V> updateList = new ArrayList<>();
        for (V data : dataList) {
            V exist = widgetCache.compute(keyGetter.generator(data), (k, v) -> v);
            if (exist == null) {
                createList.add(data);
            } else {
                updateList.add(data);
            }
            allList.add(data);
        }
        widgetCache.fill();
        List<V> deleteList = new ArrayList<>(widgetCache.getNotComputedCache().values());
        return DiffList.list(allList, createList, updateList, deleteList);
    }

    public static <K, V> DiffList<V> diff(Map<String, List<V>> dataGroupMap, Map<String, List<V>> existGroupMap, UniqueKeyGenerator<V, K> keyGetter) {
        existGroupMap = new HashMap<>(existGroupMap);
        List<V> createList = new ArrayList<>();
        List<V> updateList = new ArrayList<>();
        List<V> deleteList = new ArrayList<>();
        for (Map.Entry<String, List<V>> entry : dataGroupMap.entrySet()) {
            String group = entry.getKey();
            List<V> dataList = entry.getValue();
            List<V> existList = existGroupMap.remove(group);
            if (CollectionUtils.isEmpty(existList)) {
                createList.addAll(dataList);
            } else {
                DiffList<V> diffList = DiffHelper.diff(dataList, existList, keyGetter);
                createList.addAll(diffList.getCreate());
                updateList.addAll(diffList.getUpdate());
                deleteList.addAll(diffList.getDelete());
            }
        }
        for (List<V> deleteWidgetInstances : existGroupMap.values()) {
            deleteList.addAll(deleteWidgetInstances);
        }
        return DiffList.list(createList, updateList, deleteList);
    }
}
