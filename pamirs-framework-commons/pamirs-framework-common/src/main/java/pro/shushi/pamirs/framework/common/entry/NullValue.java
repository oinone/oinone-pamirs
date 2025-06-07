package pro.shushi.pamirs.framework.common.entry;

import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * Null Value
 *
 * @author Adamancy Zhang at 01:05 on 2024-05-04
 */
@JSONType(asm = false, serializer = NullValue.NullValueJSONSerializer.class)
public final class NullValue implements Serializable {

    private static final long serialVersionUID = 9064924458302074366L;

    public static final Object INSTANCE = new NullValue();

    private NullValue() {
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return this == obj;
    }

    @Override
    public int hashCode() {
        return NullValue.class.hashCode();
    }

    @Override
    public String toString() {
        return null;
    }

    /**
     * fast json serializer write null.
     */
    public static class NullValueJSONSerializer implements ObjectSerializer {

        @Override
        public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
            serializer.writeNull();
        }
    }
}
