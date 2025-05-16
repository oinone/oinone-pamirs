package pro.shushi.pamirs.auth.api.helper;

import pro.shushi.pamirs.auth.api.extend.authorization.AuthAuthorizationSceneApi;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.util.function.Consumer;

/**
 * 权限授权扩展执行器
 *
 * @author Adamancy Zhang at 18:40 on 2024-09-10
 */
public class AuthAuthorizationExtendExecutor {

    private AuthAuthorizationExtendExecutor() {
        // reject create object
    }

    public static <T extends AuthAuthorizationSceneApi> void execute(Class<T> api, String scene, Consumer<T> consumer) {
        for (T extendApi : Spider.getLoader(api).getOrderedExtensions()) {
            if (extendApi.isExecution(scene)) {
                consumer.accept(extendApi);
            }
        }
    }
}
