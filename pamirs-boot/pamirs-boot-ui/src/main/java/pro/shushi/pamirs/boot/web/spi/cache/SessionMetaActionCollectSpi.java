package pro.shushi.pamirs.boot.web.spi.cache;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.boot.base.model.*;
import pro.shushi.pamirs.boot.base.ux.cache.api.ActionCacheApi;
import pro.shushi.pamirs.boot.base.ux.cache.api.ModelActionsCacheApi;
import pro.shushi.pamirs.boot.base.ux.cache.util.UiSessionCacheUtil;
import pro.shushi.pamirs.meta.api.core.session.spi.SessionMetaCollectSpi;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.api.session.RequestContext;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.List;

/**
 * session动作元数据收集扩展点SPI
 * <p>
 * 2022/4/27 5:33 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Component
@SPI.Service(Action.MODEL_MODEL)
public class SessionMetaActionCollectSpi implements SessionMetaCollectSpi {

    private final static String[] actionGroups = new String[]{
            ServerAction.MODEL_MODEL,
            ViewAction.MODEL_MODEL,
            UrlAction.MODEL_MODEL,
            ClientAction.MODEL_MODEL
    };

    @Override
    public void collect(MetaData metaData, RequestContext context) {
        for (String actionGroup : actionGroups) {
            List<Action> actionList = metaData.getDataList(actionGroup);
            if (!CollectionUtils.isEmpty(actionList)) {
                for (Action action : actionList) {
                    // 收集动作缓存
                    context.putExtendCacheEntity(ActionCacheApi.class,
                            cacheApi -> cacheApi.put(action.getSign(), action));

                    // 收集模型动作缓存
                    context.putExtendCacheEntity(ModelActionsCacheApi.class,
                            cacheApi -> UiSessionCacheUtil.putDataToListCache(cacheApi, action.getModel(), action));
                }
            }
        }
    }

}
