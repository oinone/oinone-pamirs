package pro.shushi.pamirs.meta.util;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.Prop;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 属性工具类
 * <p>
 * 2022/3/2 11:08 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class PropUtils {

    /**
     * 从注解获取属性列表
     *
     * @param uxProps 注解
     * @return 属性列表
     */
    public static List<pro.shushi.pamirs.meta.domain.model.Prop> convertPropListFromAnnotation(Prop[] uxProps) {
        if (null == uxProps || 0 == uxProps.length) {
            return null;
        }
        List<pro.shushi.pamirs.meta.domain.model.Prop> propList = new ArrayList<>(uxProps.length);
        pro.shushi.pamirs.meta.domain.model.Prop prop;
        for (Prop uxKv : uxProps) {
            prop = new pro.shushi.pamirs.meta.domain.model.Prop();
            prop.setName(uxKv.name());
            prop.setValue(TypeUtils.valueOfPrimary(uxKv.type().getName(), uxKv.value(), null));
            propList.add(prop);
        }
        return propList;
    }

    /**
     * 从注解获取属性映射
     *
     * @param uxProps 注解
     * @return 属性映射
     */
    public static Map<String, Object> convertPropMapFromAnnotation(Prop[] uxProps) {
        if (null == uxProps || 0 == uxProps.length) {
            return null;
        }
        Map<String, Object> context = new LinkedHashMap<>(uxProps.length);
        for (Prop prop : uxProps) {
            if (StringUtils.isBlank(prop.name())){
                continue;
            }
            context.put(prop.name(), prop.value());
        }
        return context;
    }

}
