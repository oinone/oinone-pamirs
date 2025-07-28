package pro.shushi.pamirs.boot.web.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.base.constants.ClientActionConstants;
import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.boot.base.enmu.QueryModeEnum;
import pro.shushi.pamirs.boot.base.model.*;
import pro.shushi.pamirs.boot.base.ux.model.UIView;
import pro.shushi.pamirs.boot.base.ux.model.UIWidget;
import pro.shushi.pamirs.boot.base.ux.model.view.UIAction;
import pro.shushi.pamirs.boot.web.enmu.BootUxdExpEnumerate;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.Prop;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 视图动作工具类
 * 2020/11/26 3:53 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
public class UiActionUtils {

    public static UIAction refreshActionMetaData(MetaData metaData, UIView uiView, UIAction uiAction, SystemSourceEnum systemSource) {
        internalClientActionProcess(uiAction);
        if (null == uiAction.getRefs() || uiAction.getRefs()) {
            return uiAction;
        }
        String actionSign = Action.sign(uiAction.getModel(), uiAction.getName());
        Action action = metaData.getDataItem(Action.MODEL_MODEL, actionSign);
        ActionTypeEnum actionType = uiAction.getActionType();
        if (null != action && null == actionType) {
            actionType = action.getActionType();
        }
        if (ActionTypeEnum.SERVER.equals(actionType)) {
            return uiAction;
        }
        if (null == actionType) {
            throw PamirsException.construct(BootUxdExpEnumerate.BASE_ACTION_AUTO_CREATE_TYPE_IS_NULL_ERROR)
                    .appendMsg(MessageFormat.format("viewModel:{0},viewName:{1},actionName:{2}",
                            uiView.getModel(), uiView.getName(), uiAction.getName())).errThrow();
        }

        String actionTypeModel;
        switch (actionType) {
            case VIEW:
                actionTypeModel = ViewAction.MODEL_MODEL;
                ViewAction viewAction = (ViewAction) createAction(metaData, actionTypeModel, actionSign, uiAction, createAction(ViewAction::new, systemSource));
                UiActionUtils.configViewAction(uiAction, viewAction);
                viewAction.setSystemSource(systemSource);
                break;
            case URL:
                actionTypeModel = UrlAction.MODEL_MODEL;
                UrlAction urlAction = (UrlAction) createAction(metaData, actionTypeModel, actionSign, uiAction, createAction(UrlAction::new, systemSource));
                UiActionUtils.configUrlAction(uiAction, urlAction);
                urlAction.setSystemSource(systemSource);
                break;
            case CLIENT:
                actionTypeModel = ClientAction.MODEL_MODEL;
                ClientAction clientAction = (ClientAction) createAction(metaData, actionTypeModel, actionSign, uiAction, createAction(ClientAction::new, systemSource));
                UiActionUtils.configClientAction(uiAction, clientAction);
                clientAction.setSystemSource(systemSource);
                break;
            default:
        }
        return clearAction(uiAction);
    }

    private static <T extends Action> Supplier<T> createAction(Supplier<T> supplier, SystemSourceEnum systemSource) {
        return () -> {
            T action = supplier.get();
            action.setSystemSource(systemSource);
            return action;
        };
    }

    private static void internalClientActionProcess(UIAction uiAction) {
        String name = Optional.ofNullable(uiAction.getName()).orElse(uiAction.getFun());
        if (StringUtils.isBlank(name)) {
            return;
        }
        switch (name) {
            case ClientActionConstants.Import.fun:
                uiAction.setActionType(ActionTypeEnum.CLIENT);
                uiAction.setName(ClientActionConstants.Import.name);
                uiAction.setFun(ClientActionConstants.Import.fun);
                setDefaultValue(uiAction, UIAction::getLabel, UIAction::setLabel, ClientActionConstants.Import.label);
                setDefaultValue(uiAction, UIAction::getContextType, UIAction::setContextType, ActionContextTypeEnum.CONTEXT_FREE);
                setDefaultValue(uiAction, UIAction::getRefs, UIAction::setRefs, false);
                addDefaultProp(uiAction, ClientActionConstants.Import.propNameType, ClientActionConstants.Import.propValueType);
                break;
            case ClientActionConstants.Export.fun:
                uiAction.setActionType(ActionTypeEnum.CLIENT);
                uiAction.setName(ClientActionConstants.Export.name);
                uiAction.setFun(ClientActionConstants.Export.fun);
                setDefaultValue(uiAction, UIAction::getLabel, UIAction::setLabel, ClientActionConstants.Export.label);
                setDefaultValue(uiAction, UIAction::getContextType, UIAction::setContextType, ActionContextTypeEnum.CONTEXT_FREE);
                setDefaultValue(uiAction, UIAction::getRefs, UIAction::setRefs, false);
                addDefaultProp(uiAction, ClientActionConstants.Export.propNameType, ClientActionConstants.Export.propValueType);
                break;
            case ClientActionConstants.X2MDelete.fun:
                uiAction.setActionType(ActionTypeEnum.CLIENT);
                uiAction.setName(ClientActionConstants.X2MDelete.name);
                uiAction.setFun(ClientActionConstants.X2MDelete.fun);
                setDefaultValue(uiAction, UIAction::getLabel, UIAction::setLabel, ClientActionConstants.X2MDelete.label);
                setDefaultValue(uiAction, UIAction::getContextType, UIAction::setContextType, ActionContextTypeEnum.SINGLE_AND_BATCH);
                setDefaultValue(uiAction, UIAction::getRefs, UIAction::setRefs, false);
                addDefaultProp(uiAction, ClientActionConstants.X2MDelete.propNameType, ClientActionConstants.X2MDelete.propValueType);
                break;
//            case ClientActionConstants.GoBack.name:
//            case ClientActionConstants.GoBack.fun:
//                uiAction.setActionType(ActionTypeEnum.CLIENT);
//                uiAction.setName(ClientActionConstants.GoBack.name);
//                uiAction.setFun(ClientActionConstants.GoBack.fun);
//                setDefaultValue(uiAction, UIAction::getLabel, UIAction::setLabel, ClientActionConstants.GoBack.label);
//                setDefaultValue(uiAction, UIAction::getContextType, UIAction::setContextType, ActionContextTypeEnum.CONTEXT_FREE);
//                setDefaultValue(uiAction, UIAction::getRefs, UIAction::setRefs, false);
//                addDefaultProp(uiAction, ClientActionConstants.GoBack.propNameType, ClientActionConstants.GoBack.propValueType);
//                break;
        }
    }

    private static <T, V> void setDefaultValue(T data, Function<T, V> getter, BiConsumer<T, V> setter, V defaultValue) {
        V value = getter.apply(data);
        if (value == null) {
            setter.accept(data, defaultValue);
        }
    }

    private static <T extends UIWidget> void addDefaultProp(T data, String propName, String propValue) {
        Object value = data.getPropValue(propName);
        if (value == null) {
            data.addProp(new Prop().setName(propName).setValue(propValue));
        }
    }

    public static Action createAction(MetaData metaData, String actionTypeModel, String actionSign, UIAction uiAction, Supplier<Action> actionSupplier) {
        Action action = metaData.getDataItem(actionTypeModel, actionSign);
        if (null == action) {
            action = actionSupplier.get();

            // 配置动作基本属性
            UiActionUtils.configAction(uiAction, action);
            action.construct();
            metaData.addData(action);
        } else {
            action.disableMetaCompleted();
            // 配置动作基本属性
            UiActionUtils.configAction(uiAction, action);
        }
        return action;
    }

    /**
     * 为菜单模型补充相关元数据
     *
     * @param metaMap  元数据
     * @param module   模块
     * @param doAction 补充逻辑
     */
    public static void doSomethingForMenuModel(Map<String, Meta> metaMap, String module, BiConsumer<Meta, ModelDefinition> doAction) {
        Meta meta = metaMap.get(module);
        MetaData metaData = meta.getCurrentModuleData();
        Set<String> viewModelSet = new LinkedHashSet<>();
        List<Menu> menuList = metaData.getDataList(Menu.MODEL_MODEL);
        List<ModuleDefinition> ueModuleList = metaData.getDataList(ModuleDefinition.MODEL_MODEL);
        if (CollectionUtils.isNotEmpty(menuList)) {
            viewModelSet.addAll(menuList.stream().map(Menu::getModel)
                    .filter(StringUtils::isNotBlank).collect(Collectors.toSet()));
        }
        if (CollectionUtils.isNotEmpty(ueModuleList)) {
            viewModelSet.addAll(ueModuleList.stream().map(ModuleDefinition::getHomePageModel)
                    .filter(StringUtils::isNotBlank).collect(Collectors.toSet()));
        }
        for (String model : viewModelSet) {
            ModelDefinition modelDefinition = meta.getModel(model);
            if (null == modelDefinition) {
                continue;
            }
            // 为菜单和首页生成默认窗口动作
            doAction.accept(meta, modelDefinition);
        }
    }

    /**
     * 配置动作基本信息
     *
     * @param uiAction 动作基本配置注解
     * @param action   动作
     * @param <T>      动作类型
     */
    public static <T extends Action> void configAction(UIAction uiAction, T action) {
        action.setModel(uiAction.getModel())
                .setName(uiAction.getName())
                .setDisplayName(Optional.of(uiAction).map(UIAction::getDisplayName).filter(StringUtils::isNotBlank).orElse(action.getName()))
                .setLabel(Optional.of(uiAction).map(UIAction::getLabel).filter(StringUtils::isNotBlank).orElse(action.getDisplayName()))
                .setSummary(Optional.of(uiAction).map(UIAction::getSummary).filter(StringUtils::isNotBlank).orElse(null))

                .setActionType(uiAction.getActionType())
                .setContextType(uiAction.getContextType())
                .setInvisible(Optional.of(uiAction).map(UIAction::getInvisible).filter(StringUtils::isNotBlank).orElse(null))
                .setRule(Optional.of(uiAction).map(UIAction::getRule).filter(StringUtils::isNotBlank).orElse(null))
                .setDisable(Optional.of(uiAction).map(UIAction::getDisabled).filter(StringUtils::isNotBlank).orElse(null))
                .setPriority(Optional.of(uiAction).map(UIAction::getPriority).orElse(99));
    }


    public static void configClientAction(UIAction uiAction, ClientAction clientAction) {
        clientAction.setFun(Optional.of(uiAction).map(UIAction::getFun).filter(StringUtils::isNotBlank).orElse(null))
                .setCompute(Optional.of(uiAction).map(UIAction::getCompute).filter(StringUtils::isNotBlank).orElse(null))

                .setModel(Optional.of(uiAction).map(UIAction::getModel).filter(StringUtils::isNotBlank).orElse(null))

                .setMapping(uiAction.getMapping())
                .setContext(uiAction.getContext())

                .setContextType(uiAction.getContextType())
                .setBindingType(null)
                .setSystemSource(SystemSourceEnum.MANUAL);
    }

    public static void configUrlAction(UIAction uiAction, UrlAction urlAction) {
        urlAction.setUrl(uiAction.getUrl())
                .setTarget(uiAction.getTarget())
                .setCompute(uiAction.getCompute())

                .setModel(Optional.of(uiAction).map(UIAction::getModel).filter(StringUtils::isNotBlank).orElse(null))

                .setMapping(uiAction.getMapping())
                .setContext(uiAction.getContext())

                .setContextType(uiAction.getContextType())
                .setBindingType(null)
                .setSystemSource(SystemSourceEnum.MANUAL);
    }

    public static void configViewAction(UIAction uiAction, ViewAction viewAction) {
        ViewTypeEnum viewType = uiAction.getViewType();
        if (null == viewType) {
            throw PamirsException.construct(BootUxdExpEnumerate.BASE_ACTION_AUTO_CREATE_VIEW_TYPE_IS_NULL_ERROR)
                    .appendMsg(MessageFormat.format("model:{0},actionName:{1}",
                            uiAction.getModel(), uiAction.getName())).errThrow();
        }
        viewAction.setActionType(ActionTypeEnum.VIEW)
                .setViewType(viewType)
                .setDataType(ViewUtils.dataContainerType(viewType))
                .setTarget(ActionTargetEnum.ROUTER)
                .setResModule(uiAction.getResModule())
                .setResModuleName(uiAction.getResModuleName())
                .setResModel(Optional.ofNullable(uiAction.getResModel()).filter(StringUtils::isNotBlank).orElse(null))
                .setResModelName(uiAction.getResModelName())
                .setTarget(uiAction.getTarget())
                .setTheme(Optional.ofNullable(uiAction.getTheme()).filter(StringUtils::isNotBlank).orElse(null))
                .setMask(Optional.ofNullable(uiAction.getMask()).filter(StringUtils::isNotBlank).orElse(null))
                .setResViewName(Optional.ofNullable(uiAction.getResViewName()).filter(StringUtils::isNotBlank).orElse(null))
                .setOptionViewTypes(uiAction.getOptionViewTypes())
                .setQueryMode(Optional.ofNullable(uiAction.getQueryMode()).filter(v -> !QueryModeEnum.DOMAIN.equals(v)).orElse(null))
                .setLoad(Optional.ofNullable(uiAction.getLoad()).filter(StringUtils::isNotBlank).orElse(null))
                .setFilter(Optional.ofNullable(uiAction.getFilter()).filter(StringUtils::isNotBlank).orElse(null))
                .setDomain(Optional.ofNullable(uiAction.getDomain()).filter(StringUtils::isNotBlank).orElse(null))
                .setLimit(uiAction.getLimit())

                .setMapping(uiAction.getMapping())
                .setContext(uiAction.getContext())

                .setContextType(uiAction.getContextType())
                .setBindingType(null)
                .setSystemSource(SystemSourceEnum.MANUAL);
    }

    public static UIAction clearAction(UIAction uiAction) {
        clearUiActionForAction(uiAction);
        clearUiActionForViewAction(uiAction);
        clearUiActionForClientAction(uiAction);
        return clearUiActionForUrlAction(uiAction);
    }

    public static UIAction clearUiActionForAction(UIAction uiAction) {
        uiAction.unsetModel()
                .unsetDisplayName()
                .unsetLabel()
                .unsetSummary()
                .unsetActionType()
                .unsetContextType()
                .unsetRefs()
                .unsetRule()
                .unsetDisabled()
                .unsetInvisible();
        return uiAction;
    }

    public static UIAction clearUiActionForViewAction(UIAction uiAction) {
        uiAction.unsetActionType()
                .unsetViewType()
                .unsetDataType()
                .unsetTarget()
                .unsetResModule()
                .unsetResModuleName()
                .unsetResModel()
                .unsetResModelName()
                .unsetTarget()
                .unsetTheme()
                .unsetMask()
                .unsetResViewName()
                .unsetOptionViewTypes()
                .unsetQueryMode()
                .unsetLoad()
                .unsetFilter()
                .unsetDomain()
                .unsetLimit()
                .unsetMapping()
                .unsetContext();
        return uiAction;
    }

    public static UIAction clearUiActionForClientAction(UIAction uiAction) {
        uiAction.unsetFun()
                .unsetCompute()
                .unsetModel()
                .unsetMapping()
                .unsetContext();
        return uiAction;
    }

    public static UIAction clearUiActionForUrlAction(UIAction uiAction) {
        uiAction.unsetUrl()
                .unsetTarget()
                .unsetCompute()
                .unsetModel()
                .unsetMapping()
                .unsetContext();
        return uiAction;
    }

}
