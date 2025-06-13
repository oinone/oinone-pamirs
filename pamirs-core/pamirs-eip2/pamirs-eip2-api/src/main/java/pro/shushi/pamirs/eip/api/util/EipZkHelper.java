package pro.shushi.pamirs.eip.api.util;

import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.Arrays;

/**
 * @author yeshenyue on 2025/6/10 19:08.
 */
public class EipZkHelper {

    public static String processorListenerPath(String rootPath, String path) {
        String dataPath = path.substring(rootPath.length());
        if (dataPath.isEmpty()) {
            return dataPath;
        }

        // 非租户
        if (dataPath.startsWith(CharacterConstants.SEPARATOR_SLASH)) {
            return dataPath.substring(1);
        }

        // 租户
        if (dataPath.startsWith(CharacterConstants.SEPARATOR_OCTOTHORPE)) {
            String[] parts = dataPath.substring(1).split(CharacterConstants.SEPARATOR_SLASH);
            PamirsTenantSession.setTenant(parts[0]);
            return parts.length >= 2 ? String.join(CharacterConstants.SEPARATOR_SLASH,
                    Arrays.copyOfRange(parts, 1, parts.length)) : "";
        }
        return dataPath;
    }
}
