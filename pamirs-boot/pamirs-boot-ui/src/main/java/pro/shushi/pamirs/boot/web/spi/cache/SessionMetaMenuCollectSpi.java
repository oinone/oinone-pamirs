package pro.shushi.pamirs.boot.web.spi.cache;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.boot.base.model.Menu;
import pro.shushi.pamirs.boot.base.ux.cache.api.ModuleMenusCacheApi;
import pro.shushi.pamirs.boot.base.ux.cache.util.UiSessionCacheUtil;
import pro.shushi.pamirs.meta.api.core.session.spi.SessionMetaCollectSpi;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.api.session.RequestContext;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.List;

/**
 * session菜单元数据收集扩展点SPI
 * <p>
 * 2022/4/27 5:33 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Component
@SPI.Service(Menu.MODEL_MODEL)
public class SessionMetaMenuCollectSpi implements SessionMetaCollectSpi {

    @Override
    public void collect(MetaData metaData, RequestContext context) {
        List<Menu> menuList = metaData.getDataList(Menu.MODEL_MODEL);
        if (!CollectionUtils.isEmpty(menuList)) {
            for (Menu menu : menuList) {
                // 收集模块菜单缓存
                context.putExtendCacheEntity(ModuleMenusCacheApi.class,
                        cacheApi -> UiSessionCacheUtil.putDataToListCache(cacheApi, menu.getModule(), menu));
            }
        }
    }

}
