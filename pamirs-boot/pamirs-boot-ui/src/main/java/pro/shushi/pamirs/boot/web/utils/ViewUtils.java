package pro.shushi.pamirs.boot.web.utils;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.base.model.View;
import pro.shushi.pamirs.boot.base.ux.cache.api.HighPriorityModelViewCacheApi;
import pro.shushi.pamirs.boot.base.ux.cache.api.ViewCacheApi;
import pro.shushi.pamirs.boot.web.enmu.BootUxdExpEnumerate;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.enmu.ActiveEnum;
import pro.shushi.pamirs.meta.enmu.DataContainerTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.text.MessageFormat;
import java.util.Optional;

/**
 * 视图工具类
 * <p>
 * 2022/2/28 2:42 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class ViewUtils {

    /**
     * 根据视图类型获取数据容器类型
     *
     * @param viewType 视图类型
     * @return 数据容器类型
     */
    public static DataContainerTypeEnum dataContainerType(ViewTypeEnum viewType) {
        switch (viewType) {
            case TABLE:
            case CALENDAR:
            case KANBAN:
            case GALLERY:
            case CHART:
                return DataContainerTypeEnum.LIST;
            case FORM:
            case DETAIL:
            case SEARCH:
            default:
                return DataContainerTypeEnum.OBJECT;
        }
    }

    public static View fetchCacheView(String model, String resViewName, String viewType) {
        View view = null;
        if (!StringUtils.isBlank(resViewName)) {
            view = PamirsSession.getContext().getExtendCacheValue(ViewCacheApi.class,
                    cache -> cache.get(model, resViewName));
        }
        if (null == view) {
            view = PamirsSession.getContext().getExtendCacheValue(HighPriorityModelViewCacheApi.class,
                    cache -> cache.get(model, viewType));
        }
        return view;
    }

    public static void checkValidView(String model, String resViewName, View view, String viewType) {
        if (null == view) {
            throw PamirsException.construct(BootUxdExpEnumerate.BASE_RES_VIEW_IS_NOT_EXIST_ERROR)
                    .appendMsg(MessageFormat.format("model:{0},viewType:{1},viewName:{2}", model, viewType, resViewName)).errThrow();
        }
        if (null != view.getActive() && ActiveEnum.INACTIVE.equals(view.getActive())) {
            throw PamirsException.construct(BootUxdExpEnumerate.BASE_RES_VIEW_IS_INACTIVE_ERROR)
                    .appendMsg(MessageFormat.format("模型:{0},视图类型:{1},视图api名称:{2}", model, viewType, resViewName)).errThrow();
        }
        if (null == view.getTemplate()) {
            throw PamirsException.construct(BootUxdExpEnumerate.BASE_RES_TEMPLATE_IS_NOT_EXIST_ERROR)
                    .appendMsg(MessageFormat.format("model:{0},viewType:{1},viewName:{2}", model, viewType, resViewName)).errThrow();
        }
    }

    public static boolean isUpdateHighPriorityView(View currentHighPriorityView, View view) {
        if (currentHighPriorityView == null) {
            return true;
        }
        int currentPriority = Optional.ofNullable(view.getPriority()).orElse(Integer.MAX_VALUE);
        int currentHighPriority = Optional.ofNullable(currentHighPriorityView.getPriority()).orElse(Integer.MAX_VALUE);
        return currentPriority < currentHighPriority || currentHighPriorityView.getName().equals(view.getName());
    }
}
