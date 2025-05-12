package pro.shushi.pamirs.meta.api.core.orm.serialize.filter;

import com.alibaba.fastjson.serializer.ValueFilter;
import pro.shushi.pamirs.meta.api.core.orm.systems.directive.AbstractModelDirectiveApi;

/**
 * JSON序列化数据指令过滤器
 * <p>
 * 2021/9/27 12:02 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class IgnoreDirectiveSerializeFilter implements ValueFilter {

    public Object process(Object object, String name, Object value) {
        if (AbstractModelDirectiveApi.META_BIT.equals(name)) {
            return null;
        }
        return value;
    }

}
