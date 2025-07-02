package pro.shushi.pamirs.boot.web.utils;

import com.google.common.collect.Lists;
import pro.shushi.pamirs.boot.base.constants.ClientActionConstants;
import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.boot.base.model.ClientAction;
import pro.shushi.pamirs.boot.base.ux.model.view.UIAction;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.constant.MetaDefaultConstants;
import pro.shushi.pamirs.meta.domain.model.Prop;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.util.Optional;

/**
 * 客户端动作工具类
 * <p>
 * 2022/5/4 11:48 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class ClientActionUtils {

    /**
     * 生成客户端动作
     *
     * @param meta     元数据
     * @param module   模块
     * @param model    模型
     * @param uiAction 动作
     */
    public static void makeDefaultClientAction(Meta meta, String module, String model, UIAction uiAction) {
        makeDefaultClientAction(meta,
                module,
                model,
                uiAction.getName(),
                Optional.ofNullable(uiAction.getDisplayName()).orElse(uiAction.getLabel()),
                uiAction.getFun(),
                uiAction.getContextType(),
                uiAction.getPriority()
        );
    }

    /**
     * 生成客户端动作
     *
     * @param meta        元数据
     * @param module      模块
     * @param model       模型
     * @param actionName  动作名称
     * @param displayName 动作显示名称
     * @param fun         客户端函数
     * @param contextType 上下文类型
     * @param priority    优先级
     */
    public static void makeDefaultClientAction(Meta meta,
                                               String module,
                                               String model,
                                               String actionName,
                                               String displayName,
                                               String fun,
                                               ActionContextTypeEnum contextType,
                                               int priority) {
        String sign = ClientAction.sign(model, actionName);
        ClientAction defaultAction = meta.getData().get(module)
                .getDataItem(ClientAction.MODEL_MODEL, sign);
        boolean newAction = false;
        if (null == defaultAction) {
            defaultAction = new ClientAction();
            newAction = true;
        }
        if (newAction || SystemSourceEnum.SYSTEM.equals(defaultAction.getSystemSource())) {
            defaultAction.setDisplayName(displayName)
                    .setLabel(defaultAction.getDisplayName())
                    .setName(actionName)
                    .setModel(model);
            defaultAction.setFun(fun)
                    .setContextType(contextType)
                    .setActionType(ActionTypeEnum.CLIENT)
                    .setBindingType(Lists.newArrayList(ViewTypeEnum.TABLE))
                    .setPriority(priority)
                    .setSystemSource(SystemSourceEnum.SYSTEM);
            defaultAction.setSign(sign);
            if (newAction) {
                defaultAction.construct();
                meta.getData().get(module).addData(defaultAction);
            } else {
                defaultAction.disableMetaCompleted();
            }
        }
    }

    /**
     * 添加返回动作
     *
     * @return 动作
     */
    public static UIAction makeGoBackAction() {
        UIAction uiAction;
        uiAction = new UIAction();
        uiAction.setName(ClientActionConstants.GoBack.name);
        uiAction.setLabel(ClientActionConstants.GoBack.label);
        uiAction.setActionType(ActionTypeEnum.CLIENT);
        uiAction.setFun(ClientActionConstants.GoBack.fun);
        uiAction.addProp(new Prop().setName(ClientActionConstants.GoBack.propNameType)
                .setValue(ClientActionConstants.GoBack.propValueType));
        uiAction.setPriority(MetaDefaultConstants.PRIORITY_VALUE_INT - 1);
        return uiAction;
    }

    /**
     * 添加内嵌视图删除动作
     *
     * @return 动作
     */
    public static UIAction makeX2MDeleteAction() {
        UIAction uiAction;
        uiAction = new UIAction();
        uiAction.setName(ClientActionConstants.X2MDelete.name);
        uiAction.setLabel(ClientActionConstants.X2MDelete.label);
        uiAction.setActionType(ActionTypeEnum.CLIENT);
        uiAction.setFun(ClientActionConstants.X2MDelete.fun);
        uiAction.addProp(new Prop().setName(ClientActionConstants.X2MDelete.propNameType)
                .setValue(ClientActionConstants.X2MDelete.propValueType));
        uiAction.setContextType(ActionContextTypeEnum.SINGLE_AND_BATCH);
        uiAction.setPriority(MetaDefaultConstants.PRIORITY_VALUE_INT);
        return uiAction;
    }
}
