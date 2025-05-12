package pro.shushi.pamirs.meta.api.session.cache.holder;

import pro.shushi.pamirs.meta.api.session.cache.spi.CommonMetaDataCacheApi;
import pro.shushi.pamirs.meta.api.session.cache.spi.RequestMetaDataCacheApi;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.Spider;

/**
 * 请求元数据缓存预加载API持有者
 *
 * @author Adamancy Zhang at 19:57 on 2024-10-24
 */
public class RequestMetaDataCacheApiHolder {

    private static final HoldKeeper<RequestMetaDataCacheApi> holder = new HoldKeeper<>();

    public static RequestMetaDataCacheApi get() {
        return holder.supply(() -> Spider.getDefaultExtension(RequestMetaDataCacheApi.class));
    }

    private static final HoldKeeper<CommonMetaDataCacheApi> commonCacheApiHolder = new HoldKeeper<>();

    public static CommonMetaDataCacheApi getCommonCacheApi() {
        return commonCacheApiHolder.supply(() -> Spider.getDefaultExtension(CommonMetaDataCacheApi.class));
    }
}
