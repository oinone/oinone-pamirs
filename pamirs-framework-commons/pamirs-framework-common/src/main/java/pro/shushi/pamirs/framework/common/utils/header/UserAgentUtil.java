package pro.shushi.pamirs.framework.common.utils.header;

import pro.shushi.pamirs.meta.enmu.ClientTypeEnum;

/**
 * @author drome
 */
public class UserAgentUtil {

    public static ClientTypeEnum parseClientType(String userAgentString) {
        Platform platform = parsePlatform(userAgentString);
        if (platform.isMobile()) {
            return ClientTypeEnum.MOBILE;
        }
        return ClientTypeEnum.PC;
    }

    private static Platform parsePlatform(String userAgentString) {
        for (Platform platform : Platform.platforms) {
            if (platform.isMatch(userAgentString)) {
                return platform;
            }
        }
        return Platform.Unknown;
    }
}
