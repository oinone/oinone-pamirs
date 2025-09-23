package pro.shushi.pamirs.meta.common.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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
    public static ClassLoader getClassLoader(Class<?> clazz) {
        List<ClassLoader> candidates = new ArrayList<>();
        candidates.add(AppClassLoader.class.getClassLoader());
        candidates.add(Thread.currentThread().getContextClassLoader());
        candidates.add(ClassLoader.getPlatformClassLoader());
        candidates.add(ClassLoader.getSystemClassLoader());
        for (ClassLoader cl : candidates) {
            if (cl == null) continue;
            try {
                if (cl.getResource(clazz.getCanonicalName().replaceAll("\\.", "/") + ".class") != null) {
                    return cl;
                }
            } catch (Throwable ignored) {}
        }
        return null != Thread.currentThread().getContextClassLoader()
                ? Thread.currentThread().getContextClassLoader()
                : ClassLoader.getSystemClassLoader();
    }
}
