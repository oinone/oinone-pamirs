package pro.shushi.pamirs.boot.web.manager;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.model.*;
import pro.shushi.pamirs.boot.base.ux.cache.api.ActionCacheApi;
import pro.shushi.pamirs.boot.base.ux.cache.api.ModelActionsCacheApi;
import pro.shushi.pamirs.boot.base.ux.cache.api.ModuleMenusCacheApi;
import pro.shushi.pamirs.boot.common.spi.api.boot.BootConditionApi;
import pro.shushi.pamirs.boot.common.util.MetaOnlineLocalUtil;
import pro.shushi.pamirs.boot.web.utils.ViewUtils;
import pro.shushi.pamirs.framework.common.emnu.BootModeEnum;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 元数据缓存管理器
 * <p>
 * 2022/5/14 12:15 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Component
public class MetaCacheManager {

    @Resource
    private BootConditionApi standardBootCondition;

    @Resource
    private UiIoManager uiioManager;

    public List<Menu> fetchCloneMenus(String module) {
        return uiioManager.cloneDataList(PamirsSession.getContext().getExtendCacheValue(ModuleMenusCacheApi.class, cache -> cache.get(module)));
    }

    public Action fetchCloneAction(String model, String name) {
        return uiioManager.cloneData(fetchAction(model, name));
    }

    public ViewAction fetchCloneViewAction(String model, String name) {
        if (BootModeEnum.dev.equals(standardBootCondition.mode())) {
            return new ViewAction().setModel(model).setName(name).queryOne();
        }
        Action cacheAction = fetchAction(model, name);
        if (cacheAction instanceof ViewAction) {
            ViewAction viewAction = (ViewAction) fetchAction(model, name);
            return uiioManager.cloneData(viewAction);
        }

        return null;
    }

    public List<Action> fetchActions(String model) {
        return PamirsSession.getContext().getExtendCacheValue(ModelActionsCacheApi.class, cache -> cache.get(model));
    }

    public Action fetchAction(String model, String name) {
        boolean metaOnline = MetaOnlineLocalUtil.metaOnline();
        if (metaOnline) {
            Map<String, List<Action>> actionMaps = new HashMap<>();
            List<Action> actions = PamirsSession.getContext().getExtendCacheValue(ModelActionsCacheApi.class,
                    cache -> cache.get(model));
            if (CollectionUtils.isNotEmpty(actions)) {
                actionMaps = actions.stream().collect(Collectors.groupingBy(Action::getName));
            }
            if (actionMaps.get(name) != null) {
                // 存在重复数据的情况
                return actionMaps.get(name).get(0);
            } else {
                return null;
            }
        } else {
            return PamirsSession.getContext().getExtendCacheValue(ActionCacheApi.class,
                    cache -> cache.get(model, name));
        }
    }

    public String fetchViewTemplate(String model, String resViewName, ViewTypeEnum viewTypeEnum) {
        return Optional.ofNullable(fetchView(model, resViewName, viewTypeEnum, false, true))
                .map(AbstractView::getTemplate).orElse(null);
    }

    public View fetchView(String model, String resViewName, ViewTypeEnum viewTypeEnum) {
        return fetchView(model, resViewName, viewTypeEnum, true, true);
    }

    public View fetchView(String model, String resViewName, ViewTypeEnum viewTypeEnum, Boolean needCheck) {
        return fetchView(model, resViewName, viewTypeEnum, true, needCheck);
    }

    private View fetchView(String model, String resViewName, ViewTypeEnum viewTypeEnum, boolean clone, Boolean needCheck) {
        View view = null;
        String viewType = Optional.ofNullable(viewTypeEnum).orElse(ViewTypeEnum.TABLE).value();
        if (BootModeEnum.dev.equals(standardBootCondition.mode())) {
            if (!StringUtils.isBlank(resViewName)) {
                view = new View().setModel(model).setName(resViewName).queryOne();
            }
            if (null == view) {
                List<View> views = new View().queryListByWrapper(new Pagination<View>().setSize(1L),
                        Pops.<View>lambdaQuery().from(View.MODEL_MODEL)
                                .eq(View::getModel, model)
                                .eq(View::getType, viewType)
                                .eq(View::getShow, true)
                                .eq(View::getActive, true)
                                .orderByAsc(View::getPriority));
                if (CollectionUtils.isNotEmpty(views)) {
                    view = views.get(0);
                }
            }
        } else {
            view = ViewUtils.fetchCacheView(model, resViewName, viewType);
            if (clone && null != view) {
                view = uiioManager.cloneData(view);
            }
        }
        if (needCheck) {
            ViewUtils.checkValidView(model, resViewName, view, viewType);
        }

        return view;
    }

}
