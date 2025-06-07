package pro.shushi.pamirs.framework.orm.json.serialize;


import com.alibaba.fastjson.serializer.NameFilter;
import pro.shushi.pamirs.framework.orm.json.utils.ModelJsonUtils;

/**
 * 名称预处理
 * <p>
 * 2022/4/27 11:29 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class PreNameSerializeFilter implements NameFilter {

    @Override
    public String process(Object object, String name, Object value) {
        ModelJsonUtils.preDealName(object, name);
        return name;
    }

}
