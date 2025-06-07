package pro.shushi.pamirs.auth.api.runtime.session;

import pro.shushi.pamirs.meta.api.session.PamirsSession;

/**
 * 函数权限Session
 *
 * @author Adamancy Zhang at 12:10 on 2024-03-11
 */
public class AuthFunctionPermissionSession {

    private static final String PERMISSION_VERIFIED = "PERMISSION_VERIFIED";

    private static final String SUCCESS = Boolean.TRUE.toString();

    private static final String FAILURE = Boolean.FALSE.toString();

    /**
     * 是否已验证函数权限
     *
     * @return true: 不需要验证; false: 需要验证
     */
    public static boolean isVerified() {
        String isValid = PamirsSession.getTransmittableExtend().get(PERMISSION_VERIFIED);
        if (isValid == null) {
            isValid = FAILURE;
        }
        return SUCCESS.equals(isValid);
    }

    /**
     * 验证通过
     */
    public static void passed() {
        PamirsSession.getTransmittableExtend().put(PERMISSION_VERIFIED, SUCCESS);
    }
}
