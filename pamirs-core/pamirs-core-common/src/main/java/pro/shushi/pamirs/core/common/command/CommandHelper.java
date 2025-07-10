package pro.shushi.pamirs.core.common.command;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 命令行帮助类
 *
 * @author Adamancy Zhang on 2021-04-27 09:48
 */
@Slf4j
public final class CommandHelper {

    private static final String SUCCESS_CODE = "0";

    public static final String EXIT = "exit";

    public static final String BASH = "/bin/bash";

    public static final String ZSH = "/bin/zsh";

    public static final String CMD = "cmd";

    public static final String POWERSHELL = "powershell";

    public static final String SERIAL_SPLIT = "; ";

    public static final String AND_SPLIT = " && ";

    private static final List<String> WINDOWS_PROCESS_PATHS = Arrays.asList(CMD, POWERSHELL);

    public static final Function<String[], String[]> EXIT_PREPARE = commands -> {
        if (CommandHelper.EXIT.equals(commands[commands.length - 1])) {
            return commands;
        }
        String[] commandList = Arrays.copyOf(commands, commands.length + 1);
        commandList[commands.length] = CommandHelper.EXIT;
        return commandList;
    };

    private CommandHelper() {
        //reject create object
    }

    /**
     * 执行命令行命令
     *
     * @param processPath 执行程序所在路径
     * @param commands    命令集
     * @return 响应结果集
     * @throws IOException          任何I/O异常
     * @throws InterruptedException 线程中断异常
     */
    public static List<String> executeCommands(String processPath, String... commands) throws IOException, InterruptedException {
        if (WINDOWS_PROCESS_PATHS.contains(processPath)) {
            return executeWindowsCommands(commands);
        }
        List<String> results = new ArrayList<>();
        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        try {
            process = runtime.exec(processPath, null, null);
            try (PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(process.getOutputStream())), true)) {
                for (String command : commands) {
                    if (log.isInfoEnabled()) {
                        log.info("execute: {}", command);
                    }
                    out.println(command);
                }
            }
            process.waitFor();
            String responseLine, errorLine;
            try (BufferedReader isr = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                while ((responseLine = isr.readLine()) != null) {
                    if (log.isInfoEnabled()) {
                        log.info("response: {}", responseLine);
                    }
                    results.add(responseLine);
                }
            }
            try (BufferedReader errorIsr = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                while ((errorLine = errorIsr.readLine()) != null) {
                    if (log.isErrorEnabled()) {
                        log.error("response error: {}", errorLine);
                    }
                    results.add(errorLine);
                }
            }
            String exitValue = Integer.toString(process.exitValue());
            results.add(exitValue);
            if (isSuccess(exitValue)) {
                if (log.isInfoEnabled()) {
                    log.info("exit: {}", exitValue);
                }
            } else {
                if (log.isErrorEnabled()) {
                    log.error("exit error: {}", exitValue);
                }
            }
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return results;
    }

    private static List<String> executeWindowsCommands(String... commands) throws IOException {
        Process process = Runtime.getRuntime().exec(commands);
        process.getOutputStream().close();
        Scanner scanner = new Scanner(process.getInputStream(), StandardCharsets.UTF_8.name());
        List<String> result = new ArrayList<>();
        while (scanner.hasNext()) {
            result.add(scanner.next());
        }
        result.add(Integer.toString(process.exitValue()));
        return result;
    }

    /**
     * 执行命令行命令并自动退出
     *
     * @param processPath 执行程序所在路径
     * @param commands    命令集
     * @return 响应结果集
     * @throws IOException          任何I/O异常
     * @throws InterruptedException 线程中断异常
     */
    public static List<String> executeCommandsAndAutoExit(String processPath, String... commands) throws IOException, InterruptedException {
        if (WINDOWS_PROCESS_PATHS.contains(processPath)) {
            return executeCommands(processPath, commands);
        }
        return executeCommands(processPath, EXIT_PREPARE.apply(commands));
    }

    /**
     * 判断退出值是否为成功值
     *
     * @param existValue 退出值
     * @return 是否成功退出
     */
    public static boolean isSuccess(String existValue) {
        return SUCCESS_CODE.equals(existValue);
    }

    /**
     * 判断响应结果集的最后一个值是否为成功值
     *
     * @param responses 响应结果集
     * @return 是否成功退出
     */
    public static boolean isSuccess(List<String> responses) {
        if (CollectionUtils.isEmpty(responses)) {
            return false;
        }
        return isSuccess(responses.get(responses.size() - 1));
    }
}
