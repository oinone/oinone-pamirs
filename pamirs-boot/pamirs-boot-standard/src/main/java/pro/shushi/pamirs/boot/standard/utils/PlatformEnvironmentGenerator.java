package pro.shushi.pamirs.boot.standard.utils;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.domain.PlatformEnvironment;

/**
 * PlatformEnvironment生成器
 *
 * @author Adamancy Zhang at 21:49 on 2024-10-23
 */
public class PlatformEnvironmentGenerator {

    private PlatformEnvironmentGenerator() {
        // reject create object
    }

    /**
     * 构造一个环境信息
     *
     * @param type  类型
     * @param key   环境变量Key
     * @param value 值
     * @return 环境信息
     */
    public static PlatformEnvironment newInstance(String type, String key, String value) {
        PlatformEnvironment environment = new PlatformEnvironment();
        environment.setCode(ShortCodeHelper.encode(type + CharacterConstants.SEPARATOR_OCTOTHORPE + key));
        environment.setType(type);
        environment.setKey(key);
        if (StringUtils.isBlank(value)) {
            value = null;
        }
        environment.setValue(value);
        return environment;
    }
}
