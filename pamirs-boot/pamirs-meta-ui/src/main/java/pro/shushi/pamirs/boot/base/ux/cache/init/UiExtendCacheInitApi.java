package pro.shushi.pamirs.boot.base.ux.cache.init;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.ux.cache.api.*;
import pro.shushi.pamirs.boot.base.ux.cache.local.*;
import pro.shushi.pamirs.meta.api.session.cache.spi.ExtendCacheInitApi;
import pro.shushi.pamirs.meta.api.session.cache.util.SessionCacheUtil;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.Map;

/**
 * 视图层缓存初始化程序
 * <p>
 * 2022/5/5 4:21 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@SPI.Service(UiExtendCacheInitApi.API_NAME)
@Component
public class UiExtendCacheInitApi implements ExtendCacheInitApi {

    public final static String API_NAME = "UiExtendCacheInitApi";

    @Override
    public void init(Map<String, Object> extendCacheMap) {

        // 初始化模块菜单列表缓存
        SessionCacheUtil.initCache(extendCacheMap, ModuleMenusCacheApi.class, ModuleMenusCache.class);

        // 初始化动作缓存
        SessionCacheUtil.initCache(extendCacheMap, ActionCacheApi.class, ActionCache.class);

        // 初始化模型动作列表缓存
        SessionCacheUtil.initCache(extendCacheMap, ModelActionsCacheApi.class, ModelActionsCache.class);

        // 初始化视图缓存
        SessionCacheUtil.initCache(extendCacheMap, ViewCacheApi.class, ViewCache.class);

        // 初始化高优先级模型视图缓存缓存
        SessionCacheUtil.initCache(extendCacheMap, HighPriorityModelViewCacheApi.class, HighPriorityModelViewCache.class);

    }

}
