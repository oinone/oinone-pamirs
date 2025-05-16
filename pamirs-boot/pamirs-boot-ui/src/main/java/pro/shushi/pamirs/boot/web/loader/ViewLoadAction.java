package pro.shushi.pamirs.boot.web.loader;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.boot.base.model.View;
import pro.shushi.pamirs.boot.base.ux.cache.api.ActionCacheApi;
import pro.shushi.pamirs.boot.base.ux.cache.api.HighPriorityModelViewCacheApi;
import pro.shushi.pamirs.boot.base.ux.cache.api.ViewCacheApi;
import pro.shushi.pamirs.boot.web.service.ViewService;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.util.JsonUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 视图管理器
 * <p>
 * 2021/5/26 12:07 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Base
@Service
@Fun(View.MODEL_MODEL)
public class ViewLoadAction {

    @Resource
    private ViewService viewService;

    /**
     * 加载视图
     * <p>
     * 加载布局、编译、权限处理、国际化
     * 通过View中的loadLayout、compiled、authed、translated控制是否加载布局、编译、权限处理、国际化
     *
     * @param viewList 视图列表
     * @return 处理后视图列表
     */
    @Function(summary = "加载", openLevel = FunctionOpenEnum.API)
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public List<View> load(List<View> viewList) {
        return viewService.load(viewList);
    }

    @Function(summary = "辉导测试", openLevel = FunctionOpenEnum.API)
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public View ddddddddd(View view) {
        if (!"ZfKQHDNlj96hFYYbbU4m7di3hvaTMgeU".equals(view.getName())) {
            return view;
        }
        String model = view.getModel();
        Map<String, Object> param = JsonUtils.parseMap(view.getSummary());
        if (null == param) {
            view.setSummary(JsonUtils.toJSONString(PamirsSession.getContext().getModelConfig(model)));
        } else {
            String field = (String) param.get("field");
            String fun = (String) param.get("fun");
            String action = (String) param.get("action");
            String viewName = (String) param.get("viewName");
            String viewType = (String) param.get("viewType");
            if (StringUtils.isNotBlank(field)) {
                view.setSummary(JsonUtils.toJSONString(PamirsSession.getContext().getModelField(model, field)));
            } else if (StringUtils.isNotBlank(fun)) {
                view.setSummary(JsonUtils.toJSONString(PamirsSession.getContext().getFunctionAllowNull(model, fun)));
            } else if (StringUtils.isNotBlank(action)) {
                view.setSummary(JsonUtils.toJSONString(PamirsSession.getContext().getExtendCacheValue(ActionCacheApi.class,
                        cache -> cache.get(model, action))));
            } else if (StringUtils.isNotBlank(viewName)) {
                view.setSummary(JsonUtils.toJSONString(PamirsSession.getContext().getExtendCacheValue(ViewCacheApi.class,
                        cache -> cache.get(model, viewName))));
            } else if (StringUtils.isNotBlank(viewType)) {
                view.setSummary(JsonUtils.toJSONString(PamirsSession.getContext().getExtendCacheValue(HighPriorityModelViewCacheApi.class,
                        cache -> cache.get(model, viewType))));
            } else {
                view.setSummary(JsonUtils.toJSONString(PamirsSession.getContext().getModelConfig(model)));
            }
        }
        return view;
    }

}
