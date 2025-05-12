package pro.shushi.pamirs.meta.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ConcurrentReferenceHashMap;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.enmu.MetaExpEnumerate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Java方法工具类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/4 2:20 下午
 */
@Slf4j
public class MethodUtils {

    private static final Map<Class<?>, Set<Method>> annotatedBaseTypeCache =
            new ConcurrentReferenceHashMap<>(256);

    @SuppressWarnings("unused")
    public static <A extends Annotation> List<Class<?>> getClasses4MethodWithAnnotationType(String packageName, Class<A> annotationType) {
        List<Class<?>> result = new ArrayList<>();
        Set<Class<?>> clazzs = ClassUtils.getClasses(packageName);
        xxx:
        for (Class<?> clazz : clazzs) {

            Set<Method> methods = getAnnotatedMethodsInBaseType(clazz);
            for (Method method : methods) {
                Annotation annotation = AnnotationUtils.getAnnotation(method, annotationType);
                if (annotation != null) {
                    result.add(clazz);
                    continue xxx;
                }
            }
        }
        return result;
    }

    @SuppressWarnings("unused")
    public static <A extends Annotation> List<Class<?>> getClasses4MethodWithAnnotationTypeForFun(String packageName, Class<A> annotationType) {
        List<Class<?>> result = new ArrayList<>();
        Set<Class<?>> clazzs = ClassUtils.getClasses(packageName);
        xxx:
        for (Class<?> clazz : clazzs) {

            Set<Method> methods = getAnnotatedMethodsInBaseTypeForFun(clazz);
            for (Method method : methods) {
                Annotation annotation = AnnotationUtils.getAnnotation(method, annotationType);
                if (annotation != null) {
                    result.add(clazz);
                    continue xxx;
                }
            }
        }
        return result;
    }

    public static Set<Method> getAnnotatedMethodsInBaseType(Class<?> baseType) {
        boolean ifcCheck = baseType.isInterface();
        Method[] methods = (ifcCheck ? baseType.getMethods() : baseType.getDeclaredMethods());
        return getAnnotatedMethodsInBaseType(baseType, methods);
    }

    public static Set<Method> getAnnotatedMethodsInBaseTypeForFun(Class<?> baseType) {
        Method[] methods = baseType.getMethods();
        return getAnnotatedMethodsInBaseType(baseType, methods);
    }

    private static Set<Method> getAnnotatedMethodsInBaseType(Class<?> baseType, Method[] methods) {
        boolean ifcCheck = baseType.isInterface();
        if (ifcCheck && org.springframework.util.ClassUtils.isJavaLanguageInterface(baseType)) {
            return Collections.emptySet();
        }

        Set<Method> annotatedMethods = annotatedBaseTypeCache.get(baseType);
        if (annotatedMethods != null) {
            return annotatedMethods;
        }
        for (Method baseMethod : methods) {
            try {
                // Public methods on interfaces (including interface hierarchy),
                // non-private (and therefore overridable) methods on base classes
                if ((ifcCheck || !Modifier.isPrivate(baseMethod.getModifiers())) &&
                        hasSearchableAnnotations(baseMethod)) {
                    if (annotatedMethods == null) {
                        annotatedMethods = new HashSet<>();
                    }
                    annotatedMethods.add(baseMethod);
                }
            } catch (Throwable ex) {
                log.error("{}", MetaExpEnumerate.SYSTEM_ERROR.msg(), ex);
            }
        }
        if (annotatedMethods == null) {
            annotatedMethods = Collections.emptySet();
        }
        annotatedBaseTypeCache.put(baseType, annotatedMethods);
        return annotatedMethods;
    }

    private static boolean hasSearchableAnnotations(Method ifcMethod) {
        Annotation[] anns = ifcMethod.getAnnotations();
        if (anns.length == 0) {
            return false;
        }
        if (anns.length == 1) {
            Class<?> annType = anns[0].annotationType();
            return (annType != Nullable.class && annType != Deprecated.class);
        }
        return true;
    }

    public static Class<?>[] getClasses(Object[] params) {
        Class<?>[] paramClass = null;
        if (params != null) {
            int paramsLength = params.length;
            paramClass = new Class[paramsLength];
            for (int i = 0; i < paramsLength; i++) {
                if (null == params[i]) {
                    paramClass[i] = Object.class;
                } else if (!(params[i] instanceof String)) {
                    paramClass[i] = params[i].getClass();
                } else {
                    try {
                        paramClass[i] = Class.forName(StringUtils.trim((String) params[i]));
                    } catch (Exception e) {
                        paramClass[i] = String.class;
                    }
                }
            }
        }
        return paramClass;
    }

    public static String[] getClasses(Class<?>[] paramTypes) {
        String[] paramTypeStrings = null;
        if (paramTypes != null) {
            int paramsLength = paramTypes.length;
            paramTypeStrings = new String[paramsLength];
            for (int i = 0; i < paramsLength; i++) {
                paramTypeStrings[i] = paramTypes[i].getName();
            }
        }
        return paramTypeStrings;
    }

    public static String getArgNamesString(Method method) {
        Parameter[] parameters = method.getParameters();
        if (null == parameters || 0 == parameters.length) {
            return "";
        }
        return StringUtils.join(Arrays.stream(parameters).map(Parameter::getName).collect(Collectors.toList()), ",");
    }

    public static String[] getArgNames(Method method) {
        Parameter[] parameters = method.getParameters();
        if (null == parameters || 0 == parameters.length) {
            return null;
        }
        String[] argNames = new String[parameters.length];
        return Arrays.stream(parameters).map(Parameter::getName).collect(Collectors.toList()).toArray(argNames);
    }

    public static boolean isStatic(Method method) {
        int modifiers = method.getModifiers();
        return Modifier.isStatic(modifiers);
    }

    public static boolean isInterface(Method method) {
        int modifiers = method.getDeclaringClass().getModifiers();
        return Modifier.isInterface(modifiers);
    }

    public static Object dealSingleDynamicParameterMethod(Object obj) {
        if (null == obj) {
            return obj;
        } else if (obj.getClass().isArray()) {
            return ((Object[]) obj)[0];
        }
        return obj;
    }

}
