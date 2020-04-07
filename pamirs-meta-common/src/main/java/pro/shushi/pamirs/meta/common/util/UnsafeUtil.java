package pro.shushi.pamirs.meta.common.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@SuppressWarnings("restriction")
public class UnsafeUtil {

	private static Unsafe unsafe;
	private static Map<Class<?>, Map<String, FieldInfo>> clazzFieldOffsetMap = new HashMap<Class<?>, Map<String, FieldInfo>>();
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
		if(object == null) return null;
		FieldInfo fieldInfo = fieldInfo(object, fieldName);
		if(fieldInfo.field.getType().getName().equals("long")){
			return unsafe.getLong(object, fieldInfo.fieldOffset);
		}else if(fieldInfo.field.getType().getName().equals("int")){
			return unsafe.getInt(object, fieldInfo.fieldOffset);
		}else if(fieldInfo.field.getType().getName().equals("short")){
			return unsafe.getShort(object, fieldInfo.fieldOffset);
		}else if(fieldInfo.field.getType().getName().equals("boolean")){
			return unsafe.getBoolean(object, fieldInfo.fieldOffset);
		}else if(fieldInfo.field.getType().getName().equals("double")){
			return unsafe.getDouble(object, fieldInfo.fieldOffset);
		}else if(fieldInfo.field.getType().getName().equals("float")){
			return unsafe.getFloat(object, fieldInfo.fieldOffset);
		}else if(fieldInfo.field.getType().getName().equals("byte")){
			return unsafe.getByte(object, fieldInfo.fieldOffset);
		}else if(fieldInfo.field.getType().getName().equals("char")){
			return unsafe.getChar(object, fieldInfo.fieldOffset);
		}
		return unsafe.getObject(object, fieldInfo.fieldOffset);
	}

	public static Object setValue(Object object, String fieldName,Object value) throws SecurityException {
		if(object == null) return null;
		FieldInfo fieldInfo = fieldInfo(object, fieldName);
		if(fieldInfo.field.getType().getName().equals("long")){
			return unsafe.getAndSetLong(object, fieldInfo.fieldOffset,(Long)value);
		}else if(fieldInfo.field.getType().getName().equals("int")){
			return unsafe.getAndSetInt(object, fieldInfo.fieldOffset,(Integer)value);
		}
		return unsafe.getAndSetObject(object, fieldInfo.fieldOffset,value);
	}

	private static FieldInfo fieldInfo(Object object, String fieldName){
		Map<String, FieldInfo> someClazzFieldOffsetMap = clazzFieldOffsetMap.get(object.getClass());
		if (someClazzFieldOffsetMap == null) {
			someClazzFieldOffsetMap = new HashMap<>();
			clazzFieldOffsetMap.put(object.getClass(), someClazzFieldOffsetMap);
		}
		FieldInfo fieldInfo = someClazzFieldOffsetMap.get(fieldName);
		if (fieldInfo == null) {
			fieldInfo = new FieldInfo();
			Field field = getDeclaredField(object.getClass(), fieldName);
			long fieldOffset = unsafe.objectFieldOffset(field);
			fieldInfo.field= field;
			fieldInfo.fieldOffset = fieldOffset;
			someClazzFieldOffsetMap.put(fieldName, fieldInfo);
		}
		return fieldInfo;
	}

	static class FieldInfo {
		long fieldOffset;
		Field field;
	}

	private static Field getDeclaredField(Class clazz, String fieldName) {

		List<Field> fields = new ArrayList<>();
		Class tmpClazz = clazz;
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

}
