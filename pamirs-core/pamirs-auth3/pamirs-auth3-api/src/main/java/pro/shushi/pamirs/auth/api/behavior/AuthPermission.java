package pro.shushi.pamirs.auth.api.behavior;

/**
 * 权限项
 *
 * @author Adamancy Zhang at 13:32 on 2024-04-26
 */
public interface AuthPermission extends AuthAuthorizationSource {

    /**
     * 获取权限ID
     *
     * @return 权限ID
     */
    Long getId();

    /**
     * 获取权限项编码
     *
     * @return 权限项编码
     */
    String getCode();

    /**
     * 刷新权限项编码
     *
     * @return 权限项编码
     */
    default String refreshCode() {
        return getCode();
    }

    /**
     * 权限项签名
     *
     * @return 获取权限项签名
     */
    default String sign() {
        return getCode();
    }
}
