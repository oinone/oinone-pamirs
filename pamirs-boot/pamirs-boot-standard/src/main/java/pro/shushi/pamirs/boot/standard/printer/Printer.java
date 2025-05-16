package pro.shushi.pamirs.boot.standard.printer;

/**
 * 日志打印输出
 *
 * @author Adamancy Zhang at 14:30 on 2024-10-16
 */
@FunctionalInterface
public interface Printer {

    /**
     * 是否启用当前日志打印
     *
     * @return 是否启用
     */
    default boolean isEnabled() {
        return true;
    }

    /**
     * 打印一行日志
     *
     * @param format 日志文本，可使用{}作为参数占位符
     * @param args   任意数量参数
     */
    void println(String format, Object... args);
}