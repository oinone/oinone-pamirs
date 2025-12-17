package pro.shushi.pamirs.core.common.serialize;

import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.orm.json.PamirsJsonUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.orm.serialize.Serializer;
import pro.shushi.pamirs.meta.constant.FieldConstants;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.util.List;

/**
 * ClientJSON 序列化处理器
 *
 * @author Adamancy Zhang at 19:08 on 2025-11-19
 */
@Slf4j
@Component
public class ClientJsonSerializeProcessor implements Serializer<Object, String> {

    public static final String TYPE = "CLIENT_JSON";

    @Override
    public String serialize(String ltype, Object value) {
        if (null == value) {
            return null;
        }
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
        filter.getExcludes().add(FieldConstants._d_modelFieldName);
        return PamirsJsonUtils.toJSONString(value, new SerializeFilter[]{
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
            return PamirsJsonUtils.parseObjectList(value, tClazz);
        } else {
            return PamirsJsonUtils.parseObject(value, clazz);
        }
    }

    @Override
    public String type() {
        return TYPE;
    }
}
