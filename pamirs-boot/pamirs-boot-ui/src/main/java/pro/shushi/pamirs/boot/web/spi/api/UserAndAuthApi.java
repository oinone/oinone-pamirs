package pro.shushi.pamirs.boot.web.spi.api;

import pro.shushi.pamirs.meta.annotation.Fun;

import java.util.Map;

/**
 * @author: xuxin
 * @createTime: 2024/05/23 16:35
 */
@Fun(UserAndAuthApi.FUN_NAMESPACE)
public interface UserAndAuthApi {
    String FUN_NAMESPACE = "base.UserAndAuthApi";

    /**
     * 获取当前用户和角色上下文信息
     *
     * @return
     */
    default Map<String, Object> generatorContext() {
        return null;
    }
}
