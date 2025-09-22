package pro.shushi.pamirs.boot.standard.printer;

import org.slf4j.Logger;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Slf4j日志输出
 *
 * @author Adamancy Zhang at 14:33 on 2024-10-16
 */
public class Slf4jPrinter implements Printer {

    private final boolean isEnabled;

    private final Logger log;

    private final PrinterMethod<Logger> method;

    public Slf4jPrinter(Logger log, PrinterMethod<Logger> method) {
        this.isEnabled = isEnabled(log, method);
        this.log = log;
        this.method = method;
    }

    private static boolean isEnabled(Logger log, PrinterMethod<Logger> method) {
        String methodName = LambdaUtil.getSerializedLambda(method).getImplMethodName();
        String isEnabledMethodName = "is" + Character.toUpperCase(methodName.charAt(0)) + methodName.substring(1) + "Enabled";
        try {
            Method isEnabledMethod = log.getClass().getMethod(isEnabledMethodName);
            return (boolean) isEnabledMethod.invoke(log);
        } catch (NoClassDefFoundError | NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
        }
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void println(String format, Object... args) {
        if (isEnabled()) {
            method.invoke(log, format, args);
        }
    }
}