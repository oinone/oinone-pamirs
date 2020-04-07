package pro.shushi.pamirs.meta.util;

import com.alibaba.fastjson.JSONException;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class MapUtils {

    public static void copy(Map<String, Object> source, Map<String, Object> target) throws JSONException {
        if (Objects.isNull(source)) {
            target = null;
            return;
        }
        if (Objects.isNull(target)) {
            target = new HashMap<>();
        }
        List<String> removeKey = Lists.newArrayList();
        for (Map.Entry<String, Object> m : target.entrySet()) {
            if (!source.containsKey(m.getKey())) {
                removeKey.add(m.getKey());
            }
        }
        if (CollectionUtils.isNotEmpty(removeKey)) {
            for (String key : removeKey) {
                target.put(key, null);
            }
        }
        for (Map.Entry<String, Object> m : source.entrySet()) {
            target.put(m.getKey(), m.getValue());
        }
    }


    /**
     * 移除map的空key
     * @param map
     * @return
     */
    public static void removeNullKey(Map map) {
        Set set = map.keySet();
        for (Iterator iterator = set.iterator(); iterator.hasNext(); ) {
            Object obj = (Object) iterator.next();
            if (null == obj) {
                iterator.remove();
            }
        }
    }

    /**
     * 移除map中的value空值
     * @param map
     * @return
     */
    public static void removeNullValue(Map map){
        Set set = map.keySet();
        for (Iterator iterator = set.iterator(); iterator.hasNext();) {
            Object obj = (Object) iterator.next();
            Object value =(Object)map.get(obj);
            if(null == value){
                iterator.remove();
            }
        }
    }

    public static Map<String, Object> listToMap(List<Map<String, Object>> mapList, String keyField){
        Map<String, Object> map = new HashMap<>();
        for(Map<String, Object> item : mapList){
            map.put((String)item.get(keyField), item);
        }
        return map;
    }

}

