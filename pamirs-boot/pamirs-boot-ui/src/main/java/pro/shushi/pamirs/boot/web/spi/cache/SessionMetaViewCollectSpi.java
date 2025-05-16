package pro.shushi.pamirs.boot.web.spi.cache;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.boot.base.model.AbstractView;
import pro.shushi.pamirs.boot.base.model.View;
import pro.shushi.pamirs.boot.base.ux.cache.api.HighPriorityModelViewCacheApi;
import pro.shushi.pamirs.boot.base.ux.cache.api.ViewCacheApi;
import pro.shushi.pamirs.meta.api.core.session.spi.SessionMetaCollectSpi;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.api.session.RequestContext;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.util.List;
import java.util.Optional;

/**
 * session视图元数据收集扩展点SPI
 * <p>
 * 2022/4/27 5:33 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Component
@SPI.Service(View.MODEL_MODEL)
public class SessionMetaViewCollectSpi implements SessionMetaCollectSpi {

    @Override
    public void collect(MetaData metaData, RequestContext context) {
        List<View> viewList = metaData.getDataList(View.MODEL_MODEL);
        if (!CollectionUtils.isEmpty(viewList)) {
            for (View view : viewList) {
                // 收集视图缓存
                context.putExtendCacheEntity(ViewCacheApi.class,
                        cache -> cache.put(view.getSign(), view));

                // 收集高优先级模型视图缓存
                String viewType = Optional.ofNullable(view.getType()).orElse(ViewTypeEnum.TABLE).value();
                View currentHighPriorityModelView = context.getExtendCacheValue(HighPriorityModelViewCacheApi.class,
                        cache -> cache.get(view.getModel(), viewType));
                int currentHighPriority = Optional.ofNullable(currentHighPriorityModelView)
                        .map(AbstractView::getPriority).orElse(Integer.MAX_VALUE);
                if (null == currentHighPriorityModelView || null != view.getPriority() && view.getPriority() < currentHighPriority) {
                    context.putExtendCacheEntity(HighPriorityModelViewCacheApi.class,
                            cache -> cache.put(view.getModel(), viewType, view));
                }
            }
        }
    }

}
