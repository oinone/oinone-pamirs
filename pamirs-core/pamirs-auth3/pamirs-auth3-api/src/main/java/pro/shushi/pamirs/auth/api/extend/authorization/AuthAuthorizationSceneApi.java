package pro.shushi.pamirs.auth.api.extend.authorization;

/**
 * 权限授权场景API
 *
 * @author Adamancy Zhang at 18:29 on 2024-09-10
 */
public interface AuthAuthorizationSceneApi {

    String RBAC_SCENE = "rbac";

    String GROUP_SCENE = "group";

    /**
     * 适用场景
     *
     * @return 适用场景
     * @see AuthAuthorizationSceneApi#RBAC_SCENE 权限配置场景
     * @see AuthAuthorizationSceneApi#GROUP_SCENE 系统权限场景
     */
    String scene();

    /**
     * 是否执行当前扩展
     *
     * @param scene 正在执行的授权场景
     * @return 是否执行当前扩展
     */
    default boolean isExecution(String scene) {
        return !scene().equals(scene);
    }

}
