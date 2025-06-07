package pro.shushi.pamirs.meta.common.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.*;

@SuppressWarnings("restriction")
public class UnsafeUtil {

    private static final Unsafe unsafe;
    private static Map<Class<?>, Map<String, FieldInfo>> clazzFieldOffsetMap = new HashMap<>();

    static {
        Field field;
        try {
            field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public static Object getValue(Object object, String fieldName) throws SecurityException {
        if (object == null) return null;
        FieldInfo fieldInfo = fieldInfo(object, fieldName);
        switch (fieldInfo.field.getType().getName()) {
            case "long":
                return unsafe.getLong(object, fieldInfo.fieldOffset);
            case "int":
                return unsafe.getInt(object, fieldInfo.fieldOffset);
            case "short":
                return unsafe.getShort(object, fieldInfo.fieldOffset);
            case "boolean":
                return unsafe.getBoolean(object, fieldInfo.fieldOffset);
            case "double":
                return unsafe.getDouble(object, fieldInfo.fieldOffset);
            case "float":
                return unsafe.getFloat(object, fieldInfo.fieldOffset);
            case "byte":
                return unsafe.getByte(object, fieldInfo.fieldOffset);
            case "char":
                return unsafe.getChar(object, fieldInfo.fieldOffset);
        }
        return unsafe.getObject(object, fieldInfo.fieldOffset);
    }

    public static Object setValue(Object object, String fieldName, Object value) throws SecurityException {
        if (object == null) return null;
        FieldInfo fieldInfo = fieldInfo(object, fieldName);
        if (fieldInfo.field.getType().getName().equals("long")) {
            return unsafe.getAndSetLong(object, fieldInfo.fieldOffset, (Long) value);
        } else if (fieldInfo.field.getType().getName().equals("int")) {
            return unsafe.getAndSetInt(object, fieldInfo.fieldOffset, (Integer) value);
        }
        return unsafe.getAndSetObject(object, fieldInfo.fieldOffset, value);
    }

    private static FieldInfo fieldInfo(Object object, String fieldName) {
        Map<String, FieldInfo> someClazzFieldOffsetMap = clazzFieldOffsetMap.computeIfAbsent(object.getClass(), k -> new HashMap<>());
        FieldInfo fieldInfo = someClazzFieldOffsetMap.get(fieldName);
        if (fieldInfo == null) {
            fieldInfo = new FieldInfo();
            Field field = getDeclaredField(object.getClass(), fieldName);
            long fieldOffset = unsafe.objectFieldOffset(field);
            fieldInfo.field = field;
            fieldInfo.fieldOffset = fieldOffset;
            someClazzFieldOffsetMap.put(fieldName, fieldInfo);
        }
        return fieldInfo;
    }

    static class FieldInfo {
        long fieldOffset;
        Field field;
    }

    private static Field getDeclaredField(Class<?> clazz, String fieldName) {
        List<Field> fields = new ArrayList<>();
        Class<?> tmpClazz = clazz;
        while (tmpClazz != null) {
            fields.addAll(Arrays.asList(tmpClazz.getDeclaredFields()));
            tmpClazz = tmpClazz.getSuperclass();
        }

        for (Field f : fields) {
            if (f.getName().equalsIgnoreCase(fieldName)) {
                return f;
            }
        }

        return null;
    }

    public static Object allocateInstance(Class<?> type) throws InstantiationException {
        return unsafe.allocateInstance(type);
    }

    /**
     * 界面设计器添加按钮引NullMark的问题可能会报NPE,暂时这个地方处理掉
     *
     * @param object
     * @param fieldName
     * @param value
     * @return
     * @throws SecurityException
     */
    @Deprecated
    public static Object setValueAllowFieldNull(Object object, String fieldName, Object value) throws SecurityException {
        if (object == null) {
            return null;
        }
        FieldInfo fieldInfo = fieldInfoAllowFieldNull(object, fieldName);
        if (Objects.isNull(fieldInfo)) {
            return object;
        }
        if (fieldInfo.field.getType().getName().equals("long")) {
            return unsafe.getAndSetLong(object, fieldInfo.fieldOffset, (Long) value);
        } else if (fieldInfo.field.getType().getName().equals("int")) {
            return unsafe.getAndSetInt(object, fieldInfo.fieldOffset, (Integer) value);
        }
        return unsafe.getAndSetObject(object, fieldInfo.fieldOffset, value);
    }

    @Deprecated
    private static FieldInfo fieldInfoAllowFieldNull(Object object, String fieldName) {
        Map<String, FieldInfo> someClazzFieldOffsetMap = clazzFieldOffsetMap.computeIfAbsent(object.getClass(), k -> new HashMap<>());
        FieldInfo fieldInfo = someClazzFieldOffsetMap.get(fieldName);
        if (fieldInfo == null) {
            fieldInfo = new FieldInfo();
            Field field = getDeclaredField(object.getClass(), fieldName);
            if (Objects.isNull(field)) {
                return null;
            }
            long fieldOffset = unsafe.objectFieldOffset(field);
            fieldInfo.field = field;
            fieldInfo.fieldOffset = fieldOffset;
            someClazzFieldOffsetMap.put(fieldName, fieldInfo);
        }
        return fieldInfo;
    }

}
