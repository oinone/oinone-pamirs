package pro.shushi.pamirs.framework.orm.xml.bean;

import pro.shushi.pamirs.meta.common.util.ListUtils;

import java.beans.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Introspector with support Chain set-method feature
 * <p>
 * 2022/3/16 5:48 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class ChainIntrospector {

    public static final String GET_PREFIX = "get";
    public static final String IS_PREFIX = "is";
    public static final String SET_PREFIX = "set";

    public static PropertyDescriptor[] getTargetPropertyInfo(Class<?> beanClass, MethodDescriptor[] methodDescriptors) {

        Map<String, PropertyDescriptor> pdMap = new HashMap<>();

        for (MethodDescriptor methodDescriptor : methodDescriptors) {
            Method method = methodDescriptor.getMethod();
            if (method == null) {
                continue;
            }
            // skip static methods.
            int mods = method.getModifiers();
            if (Modifier.isStatic(mods)) {
                continue;
            }
            String name = method.getName();
            Class<?>[] argTypes = method.getParameterTypes();
            Class<?> resultType = method.getReturnType();
            int argCount = argTypes.length;
            PropertyDescriptor pd = null;

            if (name.length() <= 3 && !name.startsWith(IS_PREFIX)) {
                // Optimization. Don't bother with invalid propertyNames.
                continue;
            }

            if (argCount == 0) {
                if (name.startsWith(GET_PREFIX)) {
                    // Simple getter
                    String pdName = name.substring(3);
                    pd = fillPropertyDescriptor(pdName, pdMap, () -> newReadMethod(pdName, method), v -> setReadMethod(v, method));
                } else if (resultType == boolean.class && name.startsWith(IS_PREFIX)) {
                    // Boolean getter
                    String pdName = name.substring(2);
                    pd = fillPropertyDescriptor(pdName, pdMap, () -> newReadMethod(pdName, method), v -> setReadMethod(v, method));
                }
            } else {
                boolean b = void.class.equals(resultType) || resultType.isAssignableFrom(beanClass);
                if (argCount == 1) {
                    if (int.class.equals(argTypes[0]) && name.startsWith(GET_PREFIX)) {
                        String pdName = name.substring(3);
                        pd = fillPropertyDescriptor(pdName, pdMap, () -> newIndexedReadMethod(pdName, method),
                                v -> setIndexedReadMethod((IndexedPropertyDescriptor) v, method));
                    } else if (b && name.startsWith(SET_PREFIX)) {
                        // Simple setter
                        String pdName = name.substring(3);
                        pd = fillPropertyDescriptor(pdName, pdMap, () -> newWriteMethod(pdName, method), v -> setWriteMethod(v, method));
                        if (throwsException(method)) {
                            pd.setConstrained(true);
                        }
                    }
                } else if (argCount == 2) {
                    if (b && int.class.equals(argTypes[0]) && name.startsWith(SET_PREFIX)) {
                        String pdName = name.substring(3);
                        pd = fillPropertyDescriptor(pdName, pdMap, () -> newIndexedWriteMethod(pdName, method),
                                v -> setIndexedWriteMethod((IndexedPropertyDescriptor) v, method));
                        if (throwsException(method)) {
                            pd.setConstrained(true);
                        }
                    }
                }
            }

            if (pd != null) {
                // If this class or one of its base classes is a PropertyChange
                // source, then we assume that any properties we discover are "bound".
                pdMap.put(pd.getName(), pd);
            }
        }

        return ListUtils.toArray(PropertyDescriptor.class, pdMap.values());
    }

    private static PropertyDescriptor fillPropertyDescriptor(String pdName,
                                                             Map<String, PropertyDescriptor> pdMap,
                                                             Supplier<PropertyDescriptor> supplier,
                                                             Consumer<PropertyDescriptor> consumer) {
        PropertyDescriptor pd = pdMap.get(pdName);
        if (null == pd) {
            pd = pdMap.put(pdName, supplier.get());
        } else {
            consumer.accept(pd);
        }
        return pd;
    }

    private static PropertyDescriptor newReadMethod(String pdName, Method method) {
        try {
            return new PropertyDescriptor(pdName, method, null);
        } catch (IntrospectionException e) {
            // This happens if a PropertyDescriptor or IndexedPropertyDescriptor
            // constructor fins that the method violates details of the deisgn
            // pattern, e.g. by having an empty name, or a getter returning
            // void , or whatever.
            return null;
        }
    }

    private static void setReadMethod(PropertyDescriptor pd, Method method) {
        try {
            pd.setReadMethod(method);
        } catch (IntrospectionException e) {
            // This happens if a PropertyDescriptor or IndexedPropertyDescriptor
            // constructor fins that the method violates details of the deisgn
            // pattern, e.g. by having an empty name, or a getter returning
            // void , or whatever.
        }
    }

    private static PropertyDescriptor newIndexedReadMethod(String pdName, Method method) {
        try {
            return new IndexedPropertyDescriptor(pdName, null, null, method, null);
        } catch (IntrospectionException e) {
            // This happens if a PropertyDescriptor or IndexedPropertyDescriptor
            // constructor fins that the method violates details of the deisgn
            // pattern, e.g. by having an empty name, or a getter returning
            // void , or whatever.
            return null;
        }
    }

    private static void setIndexedReadMethod(IndexedPropertyDescriptor pd, Method method) {
        try {
            pd.setIndexedReadMethod(method);
        } catch (IntrospectionException e) {
            // This happens if a PropertyDescriptor or IndexedPropertyDescriptor
            // constructor fins that the method violates details of the deisgn
            // pattern, e.g. by having an empty name, or a getter returning
            // void , or whatever.
        }
    }

    private static PropertyDescriptor newWriteMethod(String pdName, Method method) {
        try {
            return new PropertyDescriptor(pdName, null, method);
        } catch (IntrospectionException e) {
            // This happens if a PropertyDescriptor or IndexedPropertyDescriptor
            // constructor fins that the method violates details of the deisgn
            // pattern, e.g. by having an empty name, or a getter returning
            // void , or whatever.
            return null;
        }
    }

    private static void setWriteMethod(PropertyDescriptor pd, Method method) {
        try {
            pd.setWriteMethod(method);
        } catch (IntrospectionException e) {
            // This happens if a PropertyDescriptor or IndexedPropertyDescriptor
            // constructor fins that the method violates details of the deisgn
            // pattern, e.g. by having an empty name, or a getter returning
            // void , or whatever.
        }
    }

    private static PropertyDescriptor newIndexedWriteMethod(String pdName, Method method) {
        try {
            return new IndexedPropertyDescriptor(pdName, null, null, null, method);
        } catch (IntrospectionException e) {
            // This happens if a PropertyDescriptor or IndexedPropertyDescriptor
            // constructor fins that the method violates details of the deisgn
            // pattern, e.g. by having an empty name, or a getter returning
            // void , or whatever.
            return null;
        }
    }

    private static void setIndexedWriteMethod(IndexedPropertyDescriptor pd, Method method) {
        try {
            pd.setIndexedWriteMethod(method);
        } catch (IntrospectionException e) {
            // This happens if a PropertyDescriptor or IndexedPropertyDescriptor
            // constructor fins that the method violates details of the deisgn
            // pattern, e.g. by having an empty name, or a getter returning
            // void , or whatever.
        }
    }

    /**
     * Return true iff the given method throws the given exception.
     */
    private static boolean throwsException(Method method) {
        Class<?>[] exs = method.getExceptionTypes();
        for (Class<?> ex : exs) {
            if (ex == PropertyVetoException.class) {
                return true;
            }
        }
        return false;
    }

}
