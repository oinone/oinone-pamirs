package pro.shushi.pamirs.meta.common.lambda;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.util.ConcurrentReferenceHashMap;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.lambda.ref.*;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * lambda帮助类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/19 1:39 下午
 */
@SuppressWarnings("unused")
public class LambdaUtil {

    private static final Map<Class<?>, SerializedLambda> CLASS_LAMBDA_CACHE = new ConcurrentReferenceHashMap<>(256);
    private static final String GET = "get";
    private static final String IS = "is";

    /***
     * 转换方法引用为属性名
     *
     * @param fn 函数接口
     * @return 属性名
     */
    public static <T, R> String fetchFieldName(Getter<T, R> fn) {
        SerializedLambda lambda = getSerializedLambda(fn);
        // 获取方法名
        String methodName = lambda.getImplMethodName();
        String prefix = null;
        if (methodName.startsWith(GET)) {
            prefix = GET;
        } else if (methodName.startsWith(IS)) {
            prefix = IS;
        }
        if (prefix == null) {
            throw new RuntimeException("无效的getter方法: " + methodName);
        }
        return toLowerCaseFirstOne(methodName.replaceFirst(prefix, CharacterConstants.SEPARATOR_EMPTY));
    }

    /***
     * 转换方法引用为类
     *
     * @param fn 函数接口
     * @return 类
     */
    public static <T, P, R> Class<?> fetchClazz(Func<T, P, R> fn) {
        SerializedLambda lambda = getSerializedLambda(fn);
        return fetchClazz(lambda);
    }

    public static <T, R> Class<?> fetchClazz(Func0<T, R> fn) {
        SerializedLambda lambda = getSerializedLambda(fn);
        return fetchClazz(lambda);
    }

    public static <T, A1, A2, R> Class<?> fetchClazz(Func2<? super T, A1, A2, R> fn) {
        SerializedLambda lambda = getSerializedLambda(fn);
        return fetchClazz(lambda);
    }

    public static <T, A1, A2, A3, R> Class<?> fetchClazz(Func3<? super T, A1, A2, A3, R> fn) {
        SerializedLambda lambda = getSerializedLambda(fn);
        return fetchClazz(lambda);
    }

    public static <T, A1, A2, A3, A4, R> Class<?> fetchClazz(Func4<? super T, A1, A2, A3, A4, R> fn) {
        SerializedLambda lambda = getSerializedLambda(fn);
        return fetchClazz(lambda);
    }

    public static <T, A1, A2, A3, A4, A5, R> Class<?> fetchClazz(Func5<? super T, A1, A2, A3, A4, A5, R> fn) {
        SerializedLambda lambda = getSerializedLambda(fn);
        return fetchClazz(lambda);
    }

    public static <T, A1, A2, A3, A4, A5, A6, R> Class<?> fetchClazz(Func6<? super T, A1, A2, A3, A4, A5, A6, R> fn) {
        SerializedLambda lambda = getSerializedLambda(fn);
        return fetchClazz(lambda);
    }

    private static Class<?> fetchClazz(SerializedLambda lambda) {
        String instantiatedTypeName = normalizedName(lambda.getInstantiatedMethodType().substring(2, lambda.getInstantiatedMethodType().indexOf(59)));
        try {
            return Class.forName(instantiatedTypeName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /***
     * 转换方法引用为方法名
     *
     * @param fn 函数接口
     * @return 方法名
     */
    public static <T, P, R> String fetchMethodName(Func<T, P, R> fn) {
        SerializedLambda lambda = getSerializedLambda(fn);
        // 获取方法名
        return lambda.getImplMethodName();
    }

    public static <T, R> String fetchMethodName(Func0<T, R> fn) {
        SerializedLambda lambda = getSerializedLambda(fn);
        // 获取方法名
        return lambda.getImplMethodName();
    }

    public static <T, A1, A2, R> String fetchMethodName(Func2<? super T, A1, A2, R> fn) {
        SerializedLambda lambda = getSerializedLambda(fn);
        return lambda.getImplMethodName();
    }

    public static <T, A1, A2, A3, R> String fetchMethodName(Func3<? super T, A1, A2, A3, R> fn) {
        SerializedLambda lambda = getSerializedLambda(fn);
        return lambda.getImplMethodName();
    }

    public static <T, A1, A2, A3, A4, R> String fetchMethodName(Func4<? super T, A1, A2, A3, A4, R> fn) {
        SerializedLambda lambda = getSerializedLambda(fn);
        return lambda.getImplMethodName();
    }

    public static <T, A1, A2, A3, A4, A5, R> String fetchMethodName(Func5<? super T, A1, A2, A3, A4, A5, R> fn) {
        SerializedLambda lambda = getSerializedLambda(fn);
        return lambda.getImplMethodName();
    }

    public static <T, A1, A2, A3, A4, A5, A6, R> String fetchMethodName(Func6<? super T, A1, A2, A3, A4, A5, A6, R> fn) {
        SerializedLambda lambda = getSerializedLambda(fn);
        return lambda.getImplMethodName();
    }

    /**
     * 获取方法
     *
     * @param fn  函数接口
     * @param <T> 函数所在类
     * @param <R> 函数返回类型
     * @return 方法
     */
    public static <T, P, R> Method fetchMethod(Func<? super T, P, R> fn) {
        @SuppressWarnings("unchecked") Class<T> functionClazz = (Class<T>) fetchClazz(fn);
        return getReferencedMethod(functionClazz, fn);
    }

    public static <T, R> Method fetchMethod(Func0<? super T, R> fn) {
        @SuppressWarnings("unchecked") Class<T> functionClazz = (Class<T>) fetchClazz(fn);
        return getReferencedMethod(functionClazz, fn);
    }

    public static <T, A1, A2, R> Method fetchMethod(Func2<? super T, A1, A2, R> fn) {
        @SuppressWarnings("unchecked") Class<T> functionClazz = (Class<T>) fetchClazz(fn);
        return getReferencedMethod(functionClazz, fn);
    }

    public static <T, A1, A2, A3, R> Method fetchMethod(Func3<? super T, A1, A2, A3, R> fn) {
        @SuppressWarnings("unchecked") Class<T> functionClazz = (Class<T>) fetchClazz(fn);
        return getReferencedMethod(functionClazz, fn);
    }

    public static <T, A1, A2, A3, A4, R> Method fetchMethod(Func4<? super T, A1, A2, A3, A4, R> fn) {
        @SuppressWarnings("unchecked") Class<T> functionClazz = (Class<T>) fetchClazz(fn);
        return getReferencedMethod(functionClazz, fn);
    }

    public static <T, A1, A2, A3, A4, A5, R> Method fetchMethod(Func5<? super T, A1, A2, A3, A4, A5, R> fn) {
        @SuppressWarnings("unchecked") Class<T> functionClazz = (Class<T>) fetchClazz(fn);
        return getReferencedMethod(functionClazz, fn);
    }

    public static <T, A1, A2, A3, A4, A5, A6, R> Method fetchMethod(Func6<? super T, A1, A2, A3, A4, A5, A6, R> fn) {
        @SuppressWarnings("unchecked") Class<T> functionClazz = (Class<T>) fetchClazz(fn);
        return getReferencedMethod(functionClazz, fn);
    }

    private static String normalizedName(String name) {
        return name.replace('/', '.');
    }

    /**
     * 首字母转小写
     *
     * @param s 字符串
     * @return 结果
     */
    public static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    public static SerializedLambda getSerializedLambda(Serializable fn) {
        SerializedLambda lambda = CLASS_LAMBDA_CACHE.get(fn.getClass());
        // 先检查缓存中是否已存在
        if (lambda == null) {
            try {
                // 提取SerializedLambda并缓存
                Method method = fn.getClass().getDeclaredMethod("writeReplace");
                method.setAccessible(Boolean.TRUE);
                lambda = (SerializedLambda) method.invoke(fn);
                CLASS_LAMBDA_CACHE.put(fn.getClass(), lambda);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return lambda;
    }

    public static <T, R> Method getReferencedMethod(
            Class<T> clazz,
            Func0<? super T, R> methodRef) {
        return getReferencedMethod(clazz, phantom -> {
            try {
                methodRef.apply(phantom);
            } catch (Exception e) {
                // 正常情况下，不会跑到这里来
            }
        });
    }

    public static <T, P, R> Method getReferencedMethod(
            Class<T> clazz,
            Func<? super T, P, R> methodRef) {
        return getReferencedMethod(clazz, phantom -> {
            try {
                methodRef.apply(phantom, null);
            } catch (Exception e) {
                // 正常情况下，不会跑到这里来
            }
        });
    }

    public static <T, A1, A2, R> Method getReferencedMethod(
            Class<T> clazz,
            Func2<? super T, A1, A2, R> methodRef) {
        return getReferencedMethod(clazz, phantom -> {
            try {
                // 注意第三个参数，必须是一个非 null 的值
                // 否则在 unboxing 的时候会抛出 NullPointerException
                methodRef.apply(phantom, null, null);
            } catch (Exception e) {
                // 正常情况下，不会跑到这里来
            }
        });
    }

    public static <T, A1, A2, A3, R> Method getReferencedMethod(
            Class<T> clazz,
            Func3<? super T, A1, A2, A3, R> methodRef) {
        return getReferencedMethod(clazz, phantom -> {
            try {
                methodRef.apply(phantom, null, null, null);
            } catch (Exception e) {
                // 正常情况下，不会跑到这里来
            }
        });
    }

    public static <T, A1, A2, A3, A4, R> Method getReferencedMethod(
            Class<T> clazz,
            Func4<? super T, A1, A2, A3, A4, R> methodRef) {
        return getReferencedMethod(clazz, phantom -> {
            try {
                methodRef.apply(phantom, null, null, null, null);
            } catch (Exception e) {
                // 正常情况下，不会跑到这里来
            }
        });
    }

    public static <T, A1, A2, A3, A4, A5, R> Method getReferencedMethod(
            Class<T> clazz,
            Func5<? super T, A1, A2, A3, A4, A5, R> methodRef) {
        return getReferencedMethod(clazz, phantom -> {
            try {
                methodRef.apply(phantom, null, null, null, null, null);
            } catch (Exception e) {
                // 正常情况下，不会跑到这里来
            }
        });
    }

    public static <T, A1, A2, A3, A4, A5, A6, R> Method getReferencedMethod(
            Class<T> clazz,
            Func6<? super T, A1, A2, A3, A4, A5, A6, R> methodRef) {
        return getReferencedMethod(clazz, phantom -> {
            try {
                // 注意第三个参数，必须是一个非 null 的值
                // 否则在 unboxing 的时候会抛出 NullPointerException
                methodRef.apply(phantom, null, null, null, null, null, null);
            } catch (Exception e) {
                // 正常情况下，不会跑到这里来
            }
        });
    }

    public static <T> Method getReferencedMethod(Class<T> clazz, Consumer<? super T> invoker) {
        // 创建一个 Enhancer，并配置拦截器
        AtomicReference<Method> ref = new AtomicReference<>();
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
            ref.set(method);
            return null;
        });

        // 创建一个实例
        //noinspection unchecked
        T phantom = (T) enhancer.create();

        // invoker 需要在实例上调用 MethodReference
        invoker.accept(phantom);

        Method method = ref.get();
        if (method == null) {
            // 如果传入的不是方法引用，而是直接 new 出来的 Function 实例，那么 method 就会是 null
            throw new IllegalArgumentException(String.format("Invalid method reference on class [%s]", clazz));
        }
        return method;
    }

}
