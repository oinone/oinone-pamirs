package pro.shushi.pamirs.meta.api.core.orm.clone;

import org.apache.commons.collections.CollectionUtils;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.base.D;

import java.util.List;
import java.util.Map;

public class ReferenceUtils {

    @SuppressWarnings({"unchecked"})
    public static <T> void deal(T origin, T target) {
        Map<String, Object> originDMap = Models.d(origin);
        Map<String, Object> targetDMap = Models.d(target);
        for (Map.Entry<String, Object> entry : originDMap.entrySet()) {
            String entryKey = entry.getKey();
            Object entryValue = entry.getValue();
            if (entryValue instanceof List && instanceOfD((List<Object>) entryValue)) {
                for (int j = 0; j < ((List<Object>) entryValue).size(); j++) {
                    Models.setD(((List<Object>) targetDMap.get(entryKey)).get(j), Models.d(((List<Object>) entryValue).get(j)));
                }
            } else {
                targetDMap.put(entryKey, entryValue);
            }
        }
    }

    public static <T> void dealList(List<T> originList, List<T> targetList) {
        //序列化不改变list的顺序
        for (int i = 0; i < targetList.size(); i++) {
            deal(originList.get(i), targetList.get(i));
        }
    }

    private static boolean instanceOfD(List<Object> objects) {
        if (CollectionUtils.isNotEmpty(objects)) {
            for (Object object : objects) {
                if (object != null) {
                    return object instanceof D;
                }
            }
        }
        return Boolean.FALSE;
    }

}
