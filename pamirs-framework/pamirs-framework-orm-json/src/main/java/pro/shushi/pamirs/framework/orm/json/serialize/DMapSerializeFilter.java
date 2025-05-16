package pro.shushi.pamirs.framework.orm.json.serialize;

import com.alibaba.fastjson.serializer.AfterFilter;
import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.framework.orm.json.contants.ModelJsonConstants;
import pro.shushi.pamirs.meta.base.D;

import java.util.Map;
import java.util.Set;

/**
 * 2022/4/27 11:29 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class DMapSerializeFilter extends AfterFilter {

    @Override
    public void writeAfter(Object object) {
        boolean isModelObject = object instanceof D;
        if (isModelObject) {
            Map<String, Object> dMap = ((D) object).get_d();
            if (null != dMap) {
                @SuppressWarnings("unchecked")
                Set<String> completedFieldSet = (Set<String>) dMap.get(ModelJsonConstants.COMPLETED_FIELD_SET);
                boolean nonCompletedFieldSet = CollectionUtils.isEmpty(completedFieldSet);
                for (String fieldName : dMap.keySet()) {
                    if (!nonCompletedFieldSet && completedFieldSet.contains(fieldName)
                            || ModelJsonConstants.COMPLETED_FIELD_SET.equals(fieldName)) {
                        continue;
                    }
                    Object value = dMap.get(fieldName);
                    writeKeyValue(fieldName, value);
                }
                dMap.remove(ModelJsonConstants.COMPLETED_FIELD_SET);
            }
        }
    }

}
