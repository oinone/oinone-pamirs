package pro.shushi.pamirs.boot.standard.printer;

import pro.shushi.pamirs.meta.common.util.PStringUtils;

/**
 * 标准日志输出（std）
 *
 * @author Adamancy Zhang at 14:32 on 2024-10-16
 */
public class StdPrinter implements Printer {

    public static final Printer INSTANCE = new StdPrinter();

    @Override
    public void println(String format, Object... args) {
        System.out.println(PStringUtils.parse1(format, args));
    }
}