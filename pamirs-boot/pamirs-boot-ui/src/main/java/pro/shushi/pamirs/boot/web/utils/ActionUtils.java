package pro.shushi.pamirs.boot.web.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.boot.base.enmu.QueryModeEnum;
import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.base.model.ClientAction;
import pro.shushi.pamirs.boot.base.model.UrlAction;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxAction;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxClient;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxLink;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.util.ArrayUtils;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;
import pro.shushi.pamirs.meta.util.PropUtils;

import java.util.*;

/**
 * 动作工具类
 * 2020/11/26 3:53 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class ActionUtils {

    public static Map<String, List<Action>> makeActionMap(MetaData metaData) {
        List<Action> actionList = metaData.getDataList(Action.MODEL_MODEL);
        Map<String, List<Action>> actionMap = null;
        if (CollectionUtils.isNotEmpty(actionList)) {
            actionMap = new HashMap<>();
            for (Action action : actionList) {
                actionMap.putIfAbsent(action.getModel(), new ArrayList<>());
                actionMap.get(action.getModel()).add(action);
            }
        }
        return actionMap;
    }

    public static <T extends Action> Action toAction(Action action, T subAction) {
        if (null == action) {
            action = new Action();
        } else {
            action.disableMetaCompleted();
        }
        action.setModel(subAction.getModel());
        action.setName(subAction.getName());
        action.setActionType(subAction.getActionType());
        action.setLabel(subAction.getLabel());
        action.setDisplayName(subAction.getDisplayName());
        action.setSummary(subAction.getSummary());
        action.setDescription(subAction.getDescription());
        action.setInvisible(subAction.getInvisible());
        action.setDisable(subAction.getDisable());
        action.setContextType(subAction.getContextType());
        action.setBindingType(subAction.getBindingType());
        action.setBindingViewName(subAction.getBindingViewName());
        action.setContext(subAction.getContext());
        action.setMapping(subAction.getMapping());
        action.setPriority(subAction.getPriority());
        action.setSign(subAction.getSign());
        action.setBitOptions(subAction.getBitOptions());
        action.setAttributes(subAction.getAttributes());
        action.setSys(subAction.getSys());
        action.setSystemSource(subAction.getSystemSource());
        return action;
    }

    /**
     * 配置动作基本信息
     *
     * @param uxAction 动作基本配置注解
     * @param action   动作
     * @param <T>      动作类型
     */
    public static <T extends Action> void configAction(UxAction uxAction, T action) {
        action.setDisplayName(Optional.of(uxAction).map(UxAction::displayName).filter(StringUtils::isNotBlank).orElse(action.getName()))
                .setLabel(Optional.of(uxAction).map(UxAction::label).filter(StringUtils::isNotBlank).orElse(action.getDisplayName()))
                .setSummary(Optional.of(uxAction).map(UxAction::summary).filter(StringUtils::isNotBlank).orElse(null))

                .setContextType(uxAction.contextType())
                .setBindingType(ArrayUtils.toList(uxAction.bindingType()))
                .setInvisible(Optional.of(uxAction).map(UxAction::invisible).filter(StringUtils::isNotBlank).orElse(null))
                .setRule(Optional.of(uxAction).map(UxAction::rule).filter(StringUtils::isNotBlank).orElse(null))
                .setDisable(Optional.of(uxAction).map(UxAction::disable).filter(StringUtils::isNotBlank).orElse(null))
                .setBindingViewName(Optional.of(uxAction).map(UxAction::bindingView).filter(StringUtils::isNotBlank).orElse(null))
                .setPriority(Optional.of(uxAction).map(UxAction::priority).orElse(99));
    }


    public static void configClientAction(ClientAction clientAction, UxClient clientAnnotation) {
        clientAction.setActionType(ActionTypeEnum.CLIENT)
                .setFun(Optional.of(clientAnnotation).map(UxClient::fun).filter(StringUtils::isNotBlank).orElse(null))
                .setCompute(Optional.of(clientAnnotation).map(UxClient::compute).filter(StringUtils::isNotBlank).orElse(null))

                .setModel(Optional.ofNullable(clientAction.getModel())
                        .orElse(Optional.of(clientAnnotation).map(UxClient::model).filter(StringUtils::isNotBlank).orElse(null)))

                .setMapping(PropUtils.convertPropMapFromAnnotation(clientAnnotation.mapping()))
                .setContext(PropUtils.convertPropMapFromAnnotation(clientAnnotation.context()))

                .setSystemSource(SystemSourceEnum.MANUAL);
    }

    public static void configUrlAction(UrlAction urlAction, UxLink linkAnnotation) {
        urlAction.setActionType(ActionTypeEnum.URL)
                .setUrl(linkAnnotation.value())
                .setTarget(linkAnnotation.openType())
                .setCompute(linkAnnotation.compute())

                .setModel(Optional.ofNullable(urlAction.getModel())
                        .orElse(Optional.of(linkAnnotation).map(UxLink::model).filter(StringUtils::isNotBlank).orElse(null)))

                .setMapping(PropUtils.convertPropMapFromAnnotation(linkAnnotation.mapping()))
                .setContext(PropUtils.convertPropMapFromAnnotation(linkAnnotation.context()))

                .setSystemSource(SystemSourceEnum.MANUAL);
    }

    public static void configViewAction(ViewAction viewAction, UxRoute routeAnnotation) {
        ViewTypeEnum viewType = routeAnnotation.viewType();
        viewAction.setActionType(ActionTypeEnum.VIEW)
                .setViewType(viewType)
                .setDataType(ViewUtils.dataContainerType(viewType))
                .setResModule(routeAnnotation.module())
                .setResModel(Optional.of(routeAnnotation.model()).filter(StringUtils::isNotBlank).orElse(null))
                .setTarget(routeAnnotation.openType())
                .setTitle(Optional.of(routeAnnotation.title()).filter(StringUtils::isNotBlank).orElse(null))
                .setTheme(Optional.of(routeAnnotation.theme()).filter(StringUtils::isNotBlank).orElse(null))
                .setMask(Optional.of(routeAnnotation.mask()).filter(StringUtils::isNotBlank).orElse(null))
                .setResViewName(Optional.of(routeAnnotation.viewName()).filter(StringUtils::isNotBlank).orElse(null))
                .setOptionViewTypes(ArrayUtils.toList(routeAnnotation.views()))
                .setQueryMode(Optional.of(routeAnnotation.queryMode()).orElse(QueryModeEnum.DOMAIN))
                .setLoad(Optional.of(routeAnnotation.load()).filter(StringUtils::isNotBlank).orElse(null))
                .setFilter(Optional.of(routeAnnotation.filter()).filter(StringUtils::isNotBlank).orElse(null))
                .setDomain(Optional.of(routeAnnotation.domain()).filter(StringUtils::isNotBlank).orElse(null))
                .setLimit(routeAnnotation.limit())

                .setMapping(PropUtils.convertPropMapFromAnnotation(routeAnnotation.mapping()))
                .setContext(PropUtils.convertPropMapFromAnnotation(routeAnnotation.context()))

                .setSystemSource(SystemSourceEnum.MANUAL);

        String model = Optional.ofNullable(viewAction.getModel()).orElse(viewAction.getResModel());
        // 补充模块名称
        if (StringUtils.isBlank(viewAction.getModule())) {
            if (StringUtils.isNotBlank(model) && PamirsSession.getContext().getModelConfig(model) != null) {
                ModelDefinition modelDefinition = PamirsSession.getContext().getModelConfig(model).getModelDefinition();
                if (null != modelDefinition) {
                    viewAction.setModule(modelDefinition.getModule());
                    viewAction.setModuleName(modelDefinition.getModuleName());
                }
            }
        }

        Optional.ofNullable(viewAction.getModelDefinition()).ifPresent(v -> v.setModel(viewAction.getModel()));
        Optional.ofNullable(viewAction.getResModelDefinition()).ifPresent(v -> v.setModel(viewAction.getResModel()));
        Optional.ofNullable(viewAction.getResModuleDefinition()).ifPresent(v -> v.setModule(viewAction.getResModule()));
        Optional.ofNullable(viewAction.getMaskDefinition()).ifPresent(v -> v.setName(viewAction.getMask()));
        Optional.ofNullable(viewAction.getThemeDefinition()).ifPresent(v -> v.setName(viewAction.getTheme()));
        Optional.ofNullable(viewAction.getLoadFunction()).ifPresent(v -> v.setNamespace(viewAction.getModel()));
        Optional.ofNullable(viewAction.getLoadFunction()).ifPresent(v -> v.setFun(viewAction.getLoad()));
        Optional.ofNullable(viewAction.getResView()).ifPresent(v -> v.setName(viewAction.getResViewName()));
    }

}
