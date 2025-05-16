package pro.shushi.pamirs.boot.web.service;

import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.base.model.View;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.base.ux.model.view.UIAction;
import pro.shushi.pamirs.boot.base.ux.model.view.UIField;
import pro.shushi.pamirs.meta.domain.model.ModelField;

import java.util.List;

/**
 * 视图处理服务
 * <p>
 * 2022/2/23 9:55 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface ViewService {

    /**
     * 加载视图
     *
     * @param viewList 视图列表
     * @return 加载后视图列表
     */
    List<View> load(List<View> viewList);

    /**
     * 加载视图
     *
     * @param view 视图
     * @return 加载后视图
     */
    View load(View view);

    /**
     * 编译视图
     *
     * @param viewList 视图列表
     * @return 编译后视图列表
     */
    List<View> compile(List<View> viewList);

    /**
     * 编译视图
     *
     * @param view 视图
     * @return 编译后视图
     */
    View compile(View view);

    /**
     * 编译字段
     *
     * @param modelField 字段
     * @param uiField    视图字段
     * @return 编译后字段
     */
    UIField compile(ModelField modelField, UIField uiField);

    /**
     * 编译动作
     *
     * @param action   动作
     * @param uiAction 视图动作
     * @return 编译后动作
     */
    UIAction compile(Action action, UIAction uiAction);

    /**
     * 加载视图布局
     *
     * @param viewList 视图列表
     * @return 加载布局后视图列表
     */
    List<View> layout(List<View> viewList);

    /**
     * 加载视图布局
     *
     * @param view 视图
     * @return 加载布局后视图
     */
    View layout(View view);

    /**
     * 处理视图权限
     *
     * @param viewList 视图列表
     * @return 处理权限后视图列表
     */
    List<View> auth(List<View> viewList);

    /**
     * 处理视图权限
     *
     * @param view 视图
     * @return 处理权限后视图
     */
    View auth(View view);

    /**
     * 国际化视图
     *
     * @param viewList 视图列表
     * @return 国际化后视图列表
     */
    List<View> internationalization(List<View> viewList);

    /**
     * 国际化视图
     *
     * @param view 视图
     * @return 国际化后视图
     */
    View internationalization(View view);

    /**
     * 视图个性化配置
     *
     * @param viewList 视图列表
     * @return 个性化后视图列表
     */
    List<View> userPreference(List<View> viewList, ViewAction viewAction);

    /**
     * 国际化视图
     *
     * @param view 视图
     * @return 个性化后视图
     */
    View userPreference(View view, ViewAction viewAction);

}
