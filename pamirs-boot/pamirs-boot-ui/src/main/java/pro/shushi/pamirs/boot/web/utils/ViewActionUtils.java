package pro.shushi.pamirs.boot.web.utils;

import com.google.common.collect.Lists;
import pro.shushi.pamirs.boot.base.constants.ViewActionConstants;
import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.base.ux.constants.ActionPropConstants;
import pro.shushi.pamirs.boot.base.ux.model.view.UIAction;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.constant.MetaDefaultConstants;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.Prop;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.DataContainerTypeEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.util.Map;
import java.util.Optional;

/**
 * 跳转动作工具类
 * <p>
 * 2022/5/4 3:14 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
public class ViewActionUtils {

    public static String viewActionTitle(DataContainerTypeEnum dataType, String modelDisplayName, String actionDisplayName) {
        if (DataContainerTypeEnum.LIST.equals(dataType)) {
            modelDisplayName = modelDisplayName + I18nUtils.getMessage(ViewActionConstants.redirectTablePage.title);
        } else {
            if (actionDisplayName != null && !actionDisplayName.contains("internalGoto")) {
                modelDisplayName = modelDisplayName + actionDisplayName;
            }
        }

        return modelDisplayName;
    }

    public static void makeDefaultViewAction(Meta meta, ModelDefinition data,
                                             String viewActionName,
                                             String displayName,
                                             String title,
                                             ActionContextTypeEnum contextType,
                                             ViewTypeEnum viewType,
                                             int priority) {
        makeDefaultViewAction(meta, data, viewActionName, displayName, title, contextType, viewType, priority, data.getModel(), null, null, null);
    }

    public static void makeDefaultViewAction(Meta meta, ModelDefinition data,
                                             String viewActionName,
                                             String displayName,
                                             String title,
                                             ActionContextTypeEnum contextType,
                                             ViewTypeEnum viewType,
                                             int priority,
                                             String resModel,
                                             String resViewName,
                                             ActionTargetEnum target,
                                             Map<String, Object> context) {
        String sign = ViewAction.sign(data.getModel(), viewActionName);
        ViewAction defaultViewAction = meta.getData().get(data.getModule())
                .getDataItem(ViewAction.MODEL_MODEL, sign);
        boolean newAction = false;
        if (null == defaultViewAction) {
            defaultViewAction = new ViewAction();
            newAction = true;
        }
        if (newAction || SystemSourceEnum.SYSTEM.equals(defaultViewAction.getSystemSource())) {
            defaultViewAction.setDisplayName(displayName)
                    .setLabel(defaultViewAction.getDisplayName())
                    .setName(viewActionName)
                    .setModel(data.getModel());
            defaultViewAction.setTitle(title)
                    .setViewType(viewType)
                    .setTarget(Optional.ofNullable(target).orElse(ActionTargetEnum.ROUTER))
                    .setResModel(resModel)
                    .setResViewName(resViewName)
                    .setResModule(null)
                    .setResModuleName(null)
                    .setContextType(contextType)
                    .setActionType(ActionTypeEnum.VIEW)
                    .setBindingType(Lists.newArrayList(ViewTypeEnum.TABLE))
                    .setContext(context)
                    .setPriority(priority)
                    .setSystemSource(SystemSourceEnum.SYSTEM);
            defaultViewAction.setSign(sign);
            if (newAction) {
                defaultViewAction.construct();
                meta.getData().get(data.getModule()).addData(defaultViewAction);
            } else {
                defaultViewAction.disableMetaCompleted();
            }
        }
    }


    /**
     * 添加一对多内嵌视图创建动作
     *
     * @param model 模型
     * @return 动作
     */
    public static UIAction makeO2MCreateAction(String model) {
        UIAction uiAction = new UIAction();
        uiAction.setModel(model);
        uiAction.setName(ViewActionConstants.O2MCreate.name);
        uiAction.setLabel(I18nUtils.getMessage(ViewActionConstants.O2MCreate.title));
        uiAction.setActionType(ActionTypeEnum.VIEW);
        uiAction.setTarget(ActionTargetEnum.DIALOG);
        uiAction.setViewType(ViewTypeEnum.FORM);
        uiAction.addProp(new Prop().setName(ActionPropConstants.ATTR_TYPE)
                .setValue(ViewActionConstants.O2MCreate.type));
        uiAction.setContextType(ActionContextTypeEnum.CONTEXT_FREE);
        uiAction.setPriority(MetaDefaultConstants.PRIORITY_VALUE_INT);
        return uiAction;
    }

    /**
     * 添加一对多内嵌视图编辑动作
     *
     * @param model 模型
     * @return 动作
     */
    public static UIAction makeO2MEditAction(String model) {
        UIAction uiAction = new UIAction();
        uiAction.setModel(model);
        uiAction.setName(ViewActionConstants.O2MEdit.name);
        uiAction.setLabel(I18nUtils.getMessage(ViewActionConstants.O2MEdit.title));
        uiAction.setActionType(ActionTypeEnum.VIEW);
        uiAction.setTarget(ActionTargetEnum.DIALOG);
        uiAction.setViewType(ViewTypeEnum.FORM);
        uiAction.addProp(new Prop().setName(ActionPropConstants.ATTR_TYPE)
                .setValue(ViewActionConstants.O2MEdit.type));
        uiAction.setContextType(ActionContextTypeEnum.SINGLE);
        uiAction.setPriority(MetaDefaultConstants.PRIORITY_VALUE_INT);
        return uiAction;
    }

    /**
     * 添加多对多内嵌视图添加动作
     *
     * @param model 模型
     * @return 动作
     */
    public static UIAction makeM2MCreateAction(String model) {
        UIAction uiAction = new UIAction();
        uiAction.setModel(model);
        uiAction.setName(ViewActionConstants.M2MCreate.name);
        uiAction.setLabel(I18nUtils.getMessage(ViewActionConstants.M2MCreate.title));
        uiAction.setActionType(ActionTypeEnum.VIEW);
        uiAction.setTarget(ActionTargetEnum.DIALOG);
        uiAction.setViewType(ViewTypeEnum.TABLE);
        uiAction.addProp(new Prop().setName(ActionPropConstants.ATTR_TYPE)
                .setValue(ViewActionConstants.M2MCreate.type));
        uiAction.setContextType(ActionContextTypeEnum.CONTEXT_FREE);
        uiAction.setPriority(MetaDefaultConstants.PRIORITY_VALUE_INT);
        return uiAction;
    }

}
