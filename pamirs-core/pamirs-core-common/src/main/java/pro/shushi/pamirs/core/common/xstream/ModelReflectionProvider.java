package pro.shushi.pamirs.core.common.xstream;

import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.core.util.Fields;
import pro.shushi.pamirs.meta.base.D;

import java.lang.reflect.Field;
import java.util.Iterator;

/**
 * Model 反射提供者
 *
 * @author Adamancy Zhang at 18:20 on 2021-08-18
 */
public class ModelReflectionProvider extends PureJavaReflectionProvider {

    public ModelReflectionProvider() {
        super();
    }

    public ModelReflectionProvider(FieldDictionary fieldDictionary) {
        super(fieldDictionary);
    }

    @Override
    public final void writeField(Object object, String fieldName, Object value, Class definedIn) {
        if (object instanceof D) {
            ((D) object).get_d().put(fieldName, value);
        } else {
            super.writeField(object, fieldName, value, definedIn);
        }
    }

    @Override
    public void visitSerializableFields(Object object, Visitor visitor) {
        for (Iterator<?> iterator = fieldDictionary.fieldsFor(object.getClass()); iterator.hasNext(); ) {
            Field field = (Field) iterator.next();
            if (!fieldModifiersSupported(field)) {
                continue;
            }
            validateFieldAccess(field);
            Object value;
            if (object instanceof D) {
                value = ((D) object).get_d().get(field.getName());
            } else {
                value = Fields.read(field, object);
            }
            visitor.visit(field.getName(), field.getType(), field.getDeclaringClass(), value);
        }
    }
}
