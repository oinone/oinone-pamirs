package pro.shushi.k2.fun.processor;

import javax.annotation.processing.AbstractProcessor;
import java.lang.reflect.Method;

/**
 * JetBrainsWarpProcessor
 *
 * @author yakir on 2023/11/01 11:18.
 */
abstract
public class JetBrainsWarpProcessor extends AbstractProcessor {

    private static boolean isJb = true;

    protected static <T> T jbUnwrap(Class<? extends T> iface, T wrapper) {
        if (!isJb) {
            return wrapper;
        }
        T unwrapped = null;
        try {
            final Class<?> apiWrappers = wrapper.getClass().getClassLoader().loadClass("org.jetbrains.jps.javac.APIWrappers");
            final Method unwrapMethod = apiWrappers.getDeclaredMethod("unwrap", Class.class, Object.class);
            unwrapped = iface.cast(unwrapMethod.invoke(null, iface, wrapper));
        } catch (Throwable ignored) {
            isJb = false;
        }
        return unwrapped != null ? unwrapped : wrapper;
    }
}
