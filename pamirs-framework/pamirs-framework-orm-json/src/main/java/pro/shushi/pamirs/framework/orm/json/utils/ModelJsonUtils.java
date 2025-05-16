package pro.shushi.pamirs.framework.orm.json.utils;


import pro.shushi.pamirs.framework.orm.json.contants.ModelJsonConstants;
import pro.shushi.pamirs.meta.base.D;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 模型json互转工具类
 * <p>
 * 2022/4/27 11:29 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class ModelJsonUtils {

    /**
     * 收集属性名
     *
     * @param object 对象
     * @param name   属性名
     */
    public static void preDealName(Object object, String name) {
        boolean isModelObject = object instanceof D;
        if (isModelObject) {
            Map<String, Object> dMap = ((D) object).get_d();
            if (null != dMap) {
                dMap.putIfAbsent(ModelJsonConstants.COMPLETED_FIELD_SET, new HashSet<>());
                @SuppressWarnings("unchecked")
                Set<String> completedFieldSet = (Set<String>) dMap.get(ModelJsonConstants.COMPLETED_FIELD_SET);
                completedFieldSet.add(name);
            }
        }
    }

}
