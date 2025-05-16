package pro.shushi.pamirs.boot.web.init;

import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.boot.base.model.View;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.base.ux.constants.DslNodeConstants;
import pro.shushi.pamirs.boot.base.ux.model.UIView;
import pro.shushi.pamirs.boot.base.ux.model.UIWidget;
import pro.shushi.pamirs.boot.base.ux.model.view.UIAction;
import pro.shushi.pamirs.boot.web.utils.UiActionUtils;
import pro.shushi.pamirs.framework.configure.MetaConfiguration;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

import java.util.List;
import java.util.Optional;
import java.util.Stack;

/**
 * 视图初始化工具类
 * <p>
 * 2022/8/10 1:10 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class ViewInitHelper {

    private static final String VIEWS_PACKAGE = MetaConfiguration.DEFAULT_VIEWS_PACKAGE;

    private static final String LOAD_PATH_PREFIX = "classpath*:";

    public static boolean isDefaultViewsPackage(String viewsPackage) {
        return VIEWS_PACKAGE.equals(viewsPackage);
    }

    public static String getMaskLoadPath(String viewsPackage, String module) {
        return LOAD_PATH_PREFIX + viewsPackage + "/" + module + "/mask/**/*.xml";
    }

    public static String getDefaultMaskLoadPath(String module) {
        return getMaskLoadPath(ViewInitHelper.VIEWS_PACKAGE, module);
    }

    public static String getLayoutLoadPath(String viewsPackage, String module) {
        return LOAD_PATH_PREFIX + viewsPackage + "/" + module + "/layout/**/*.xml";
    }

    public static String getDefaultLayoutLoadPath(String module) {
        return getLayoutLoadPath(ViewInitHelper.VIEWS_PACKAGE, module);
    }

    public static String getTemplateLoadPath(String viewsPackage, String module) {
        return LOAD_PATH_PREFIX + viewsPackage + "/" + module + "/template/**/*.xml";
    }

    public static String getDefaultTemplateLoadPath(String module) {
        return getTemplateLoadPath(ViewInitHelper.VIEWS_PACKAGE, module);
    }

    public static void autoCreateMetaData(MetaData metaData, UIView uiView) {
        List<UIWidget> widgets = uiView.getWidgets();
        Stack<String> viewStack = new Stack<>();
        viewStack.push(uiView.getModel());
        autoCreateMetaData(metaData, viewStack, uiView, widgets);
    }

    private static void autoCreateMetaData(MetaData metaData, Stack<String> viewStack, UIView uiView, List<UIWidget> widgets) {
        if (CollectionUtils.isNotEmpty(widgets)) {
            for (UIWidget uiWidget : widgets) {
                boolean isView = DslNodeConstants.NODE_VIEW.equals(uiWidget.getDslNodeType()) || uiWidget instanceof UIView;
                if (DslNodeConstants.NODE_ACTION.equals(uiWidget.getDslNodeType()) || uiWidget instanceof UIAction) {
                    UIAction uiAction = (UIAction) uiWidget;
                    uiAction.setModel(Optional.ofNullable(uiAction.getModel()).orElse(viewStack.peek()));
                    UiActionUtils.refreshActionMetaData(metaData, uiView, uiAction, SystemSourceEnum.MANUAL);
                } else if (isView) {
                    UIView innerUiView = (UIView) uiWidget;
                    String model = innerUiView.getModel();
                    if (null == model) {
                        model = viewStack.peek();
                    }
                    viewStack.push(model);
                }

                List<UIWidget> childWidgets = uiWidget.getWidgets();
                autoCreateMetaData(metaData, viewStack, uiView, childWidgets);

                if (isView) {
                    viewStack.pop();
                }
            }
        }
    }

    public static void fixViewActionViewType(MetaData metaData, View view) {
        List<ViewAction> viewActionList = metaData.getDataList(ViewAction.MODEL_MODEL);
        for (ViewAction viewAction : viewActionList) {
            String resModel = Optional.ofNullable(viewAction.getResModel()).orElse(viewAction.getModel());
            String resView = viewAction.getResViewName();
            if (null != view.getType() && view.getName().equals(resView) && view.getModel().equals(resModel)) {
                viewAction.setViewType(view.getType());
            }
        }
    }

}
