package pro.shushi.pamirs.framework.orm.xml.provider;

import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import org.apache.commons.lang.StringUtils;
import pro.shushi.pamirs.framework.orm.xml.contants.ModelXmlConstants;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.constant.FieldConstants;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * PureJavaReflectionProvider扩展类
 * <p>
 * 2022/3/21 4:47 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class PamirsReflectionProvider extends PureJavaReflectionProvider {

    public PamirsReflectionProvider() {
        super();
    }

    public PamirsReflectionProvider(FieldDictionary fieldDictionary) {
        super(fieldDictionary);
    }

    @SuppressWarnings("unchecked")
    public void visitSerializableFields(Object object, Visitor visitor) {
        Set<String> completedFieldSet = null;
        boolean isModelObject = object instanceof D;
        if (isModelObject) {
            completedFieldSet = new HashSet<>();
        }

        for (Iterator<Object> iterator = fieldDictionary.fieldsFor(object.getClass()); iterator.hasNext(); ) {
            Field field = (Field) iterator.next();
            if (!fieldModifiersSupported(field)) {
                continue;
            }

            if (isModelObject) {
                completedFieldSet.add(field.getName());
            }

            if (FieldConstants._dFieldName.equals(field.getName())) {
                continue;
            }

            validateFieldAccess(field);
            Object value = null;// FieldUtils.getFieldValue(object, field.getName());

            try {
//                PropertyDescriptor pd = new PropertyDescriptor(field.getName(), field.getDeclaringClass());
//                Method rM = pd.getReadMethod();
                // 获得读方法
                String getMethodName = "get" + StringUtils.capitalize(field.getName());
                Method[] methods = field.getDeclaringClass().getMethods();
                for (Method method : methods) {
                    if (getMethodName.equals(method.getName())) {
                        value = method.invoke(object);
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }

            visitor.visit(field.getName(), field.getType(), field.getDeclaringClass(), value);
        }

        if (isModelObject) {
            ((D) object).get_d().put(ModelXmlConstants.COMPLETED_FIELD_SET, completedFieldSet);
        }
    }

    public void writeField(Object object, String fieldName, Object value, Class definedIn) {
        Field field = this.fieldDictionary.field(object.getClass(), fieldName, definedIn);
        this.validateFieldAccess(field);
        // FieldUtils.setFieldValue(object, field.getName(), value);

        try {
//            PropertyDescriptor pd = new PropertyDescriptor(field.getName(), definedIn);
//            Method wM = pd.getWriteMethod();
            //获得写方法
            String setMethodName = "set" + StringUtils.capitalize(fieldName);
            Method[] methods = definedIn.getMethods();
            for (Method method : methods) {
                if (setMethodName.equals(method.getName())) {
                    method.invoke(object, value);
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
