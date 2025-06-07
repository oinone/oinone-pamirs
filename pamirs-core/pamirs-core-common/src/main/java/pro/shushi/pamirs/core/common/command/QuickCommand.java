package pro.shushi.pamirs.core.common.command;

import pro.shushi.pamirs.core.common.StringHelper;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

/**
 * 快捷命令
 *
 * @author Adamancy Zhang at 12:05 on 2021-06-22
 */
public class QuickCommand {

    private static final String YES = "yes";

    private static final String NO = "no";

    private QuickCommand() {
        //reject create object
    }

    public static String cd(String path) {
        return StringHelper.concat(CharacterConstants.SEPARATOR_BLANK,
                "cd",
                path
        );
    }

    public static String mkdir(String path) {
        return StringHelper.concat(CharacterConstants.SEPARATOR_BLANK,
                "mkdir",
                "-vp",
                path
        );
    }

    public static String rmF(String path) {
        return StringHelper.concat(CharacterConstants.SEPARATOR_BLANK,
                "rm",
                "-f",
                path
        );
    }

    public static String rmRF(String path) {
        return StringHelper.concat(CharacterConstants.SEPARATOR_BLANK,
                "rm",
                "-rf",
                path
        );
    }

    public static String dirIsExist(String dirPath) {
        return String.format("[ -d %s ] && echo '%s' || echo '%s'", dirPath, YES, NO);
    }

    public static String fileIsExist(String filePath) {
        return String.format("[ -f %s ] && echo '%s' || echo '%s'", filePath, YES, NO);
    }

    public static boolean isYes(String response) {
        return YES.equals(response);
    }

    public static boolean isNo(String response) {
        return NO.equals(response);
    }
}
