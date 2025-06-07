package pro.shushi.pamirs.framework.compute.serialize;

import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.orm.serialize.Serializer;
import pro.shushi.pamirs.meta.constant.FieldConstants;
import pro.shushi.pamirs.meta.enmu.SerializeEnum;
import pro.shushi.pamirs.meta.util.JsonUtils;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.util.List;

/**
 * JSON序列生成处理器实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/4 2:48 上午
 */
@Slf4j
@Component
public class JsonSerializeProcessor implements Serializer<Object, String> {

    @Override
    public String serialize(String ltype, Object value) {
        if (null == value) {
            return null;
        }
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
        filter.getExcludes().add(FieldConstants._d_modelFieldName);
        return JsonUtils.toJSONString(value, new SerializeFilter[]{
                filter
        }, SerializerFeature.MapSortField);
    }

    @Override
    public Object deserialize(String ltype, String ltypeT, String value, String format) {
        Class<?> clazz = TypeUtils.getClass(ltype);
        if (null == value) {
            return null;
        }
        if (List.class.getName().equals(ltype) || TypeUtils.isArrayType(ltype)) {
            Class<?> tClazz = TypeUtils.getClass(ltypeT);
            return JsonUtils.parseObjectList(value, tClazz);
        } else {
            return JsonUtils.parseObject(value, clazz);
        }
    }

    @Override
    public String type() {
        return SerializeEnum.JSON.value();
    }

}
