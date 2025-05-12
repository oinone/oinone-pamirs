package pro.shushi.pamirs.meta.common.util;

import java.lang.reflect.Field;

/**
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/13 2:23 下午
 */
@SuppressWarnings("unused")
public class AppClassLoader extends ClassLoader {

    private static class SingletonHolder {
        public final static AppClassLoader instance = new AppClassLoader();
    }

    public static AppClassLoader getInstance() {
        return SingletonHolder.instance;
    }


    private AppClassLoader() {

    }

    /**
     * 通过classBytes加载类
     *
     * @param className  全限定类名
     * @param classBytes 类定义
     * @return 类
     */
    @SuppressWarnings("unused")
    public Class<?> findClassByBytes(String className, byte[] classBytes) {
        return defineClass(className, classBytes, 0, classBytes.length);
    }

    public Class<?> findClass(String className) {
        return findLoadedClass(className);
    }

    /**
     * 复制对象所有属性值,并返回一个新对象
     *
     * @param srcObj 源对象
     * @return 目标对象
     */
    @SuppressWarnings("unused")
    public Object getObj(Class<?> clazz, Object srcObj) {
        try {
            Object newInstance = clazz.getDeclaredConstructor().newInstance();
            Field[] fields = srcObj.getClass().getDeclaredFields();
            for (Field oldInstanceField : fields) {
                String fieldName = oldInstanceField.getName();
                oldInstanceField.setAccessible(true);
                Field newInstanceField = newInstance.getClass().getDeclaredField(fieldName);
                newInstanceField.setAccessible(true);
                newInstanceField.set(newInstance, oldInstanceField.get(srcObj));
            }
            return newInstance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * get class loader
     *
     * @param clazz 类
     * @return class loader
     */
    @SuppressWarnings("unused")
    public static ClassLoader getClassLoader(Class<?> clazz) {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back to system class loader...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = clazz.getClassLoader();
            if (cl == null) {
                // getClassLoader() returning null indicates the bootstrap ClassLoader
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ex) {
                    // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
                }
            }
        }

        return cl;
    }

}
