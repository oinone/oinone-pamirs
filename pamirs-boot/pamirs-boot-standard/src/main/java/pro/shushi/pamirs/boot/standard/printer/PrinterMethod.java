package pro.shushi.pamirs.boot.standard.printer;

import java.io.Serializable;

/**
 * 日志打印函数
 *
 * @author Adamancy Zhang at 14:33 on 2024-10-16
 */
@FunctionalInterface
public interface PrinterMethod<LOGGER> extends Serializable {

    /**
     * 执行打印函数
     *
     * @param log    日志打印对象
     * @param format 日志文本，可使用{}作为参数占位符
     * @param args   任意数量参数
     */
    void invoke(LOGGER log, String format, Object... args);
}