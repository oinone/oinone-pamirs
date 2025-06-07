package pro.shushi.pamirs.core.common.command;

import java.util.function.Function;

/**
 * 默认系统命令枚举
 *
 * @author Adamancy Zhang at 21:44 on 2021-06-21
 */
public enum DefaultSystemCommandEnum {

    CENTOS(CommandHelper.BASH, CommandHelper.EXIT_PREPARE),
    UBUNTU(CommandHelper.BASH, CommandHelper.EXIT_PREPARE),
    WINDOWS_SERVER(CommandHelper.POWERSHELL, CommandHelper.EXIT_PREPARE),
    MAC(CommandHelper.ZSH, CommandHelper.EXIT_PREPARE);

    private final String processPath;

    private final Function<String[], String[]> prepare;

    DefaultSystemCommandEnum(String processPath, Function<String[], String[]> prepare) {
        this.processPath = processPath;
        this.prepare = prepare;
    }

    public String getProcessPath() {
        return processPath;
    }

    public Function<String[], String[]> getPrepare() {
        return prepare;
    }
}
