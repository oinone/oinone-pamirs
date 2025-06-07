package pro.shushi.pamirs.core.common.json;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import pro.shushi.pamirs.core.common.StringHelper;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * ToString序列化
 *
 * @author Adamancy Zhang at 19:32 on 2021-08-30
 */
public class ToStringSerializer implements ObjectSerializer {

    public final static ToStringSerializer INSTANCE = new ToStringSerializer();

    private ToStringSerializer() {
        //reject create object
    }

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        serializer.write(StringHelper.valueOfNullable(object));
    }
}
