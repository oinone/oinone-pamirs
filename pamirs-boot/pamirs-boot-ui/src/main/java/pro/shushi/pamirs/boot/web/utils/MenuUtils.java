package pro.shushi.pamirs.boot.web.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import pro.shushi.pamirs.boot.base.constants.MenuActionConstants;
import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.boot.base.model.*;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxClient;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxLink;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxServer;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxMenu;
import pro.shushi.pamirs.boot.web.enmu.BootUxdExpEnumerate;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.spi.AnnotationFetcher;
import pro.shushi.pamirs.meta.util.PropUtils;

import java.text.MessageFormat;
import java.util.Optional;

/**
 * 菜单工具类
 * 2020/12/2 6:16 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class MenuUtils {

    public static String fetchMenuName(Class<?> clazz) {
        return StringUtils.substringAfterLast(clazz.getName(), CharacterConstants.SEPARATOR_DOT)
                .replace(CharacterConstants.SEPARATOR_DOLLAR, CharacterConstants.SEPARATOR_UNDERLINE);
    }

    public static String fetchMenuNameByAnnotation(Class<?> clazz) {
        UxMenu uxMenu = AnnotationFetcher.get().findAnnotation(clazz, UxMenu.class);
        if (null == uxMenu) {
            return null;
        }
        String menuName = uxMenu.name();
        if (StringUtils.isBlank(menuName)) {
            menuName = fetchMenuName(clazz);
        }
        return menuName;
    }

    public static String fetchMenuModel(String module, String appendix) {
        return module + CharacterConstants.SEPARATOR_DOT + appendix;
    }

    public static UxMenu fetchUxMenuAnnotation(Class<?> clazz) {
        return AnnotationUtils.findAnnotation(clazz, UxMenu.class);
    }

    public static <T extends Action> T configAction(String module, T action, Class<?> menuClazz) {
        UxMenu uxMenu = fetchUxMenuAnnotation(menuClazz);
        String name = uxMenu.name();
        if (StringUtils.isBlank(name)) {
            name = MenuUtils.fetchMenuName(menuClazz);
        }
        String displayName = I18nUtils.translateMenu(module, name, "displayName", Optional.of(uxMenu.label()).filter(StringUtils::isNotBlank).orElse(menuClazz.getSimpleName()));
        action.setDisplayName(displayName);
        action.setLabel(displayName);
        return action;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Action> T fetchMenuAction(Class<?> clazz, String module) {
        String menuName = fetchMenuNameByAnnotation(clazz);
        // 处理动作
        UrlAction urlAction = fetchUrlAction(fetchUrlActionAnnotation(clazz), module, menuName);
        if (null != urlAction) {
            return (T) urlAction;
        }
        ViewAction viewAction = fetchViewAction(fetchViewActionAnnotation(clazz), menuName);
        if (null != viewAction) {
            return (T) viewAction;
        }
        ServerAction serverAction = fetchServerAction(fetchServerActionAnnotation(clazz));
        if (null != serverAction) {
            return (T) serverAction;
        }
        return (T) fetchClientAction(fetchClientActionAnnotation(clazz), module, menuName);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Action> T fetchMenuAction(String model, Class<?> clazz, String module) {
        String menuName = fetchMenuNameByAnnotation(clazz);
        // 处理动作
        if (UrlAction.MODEL_MODEL.equals(model)) {
            return (T) fetchUrlAction(fetchUrlActionAnnotation(clazz), module, menuName);
        } else if (ViewAction.MODEL_MODEL.equals(model)) {
            return (T) fetchViewAction(fetchViewActionAnnotation(clazz), menuName);
        } else if (ClientAction.MODEL_MODEL.equals(model)) {
            return (T) fetchClientAction(fetchClientActionAnnotation(clazz), module, menuName);
        } else if (ServerAction.MODEL_MODEL.equals(model)) {
            return (T) fetchServerAction(fetchServerActionAnnotation(clazz));
        }
        return null;
    }

    public static UxServer fetchServerActionAnnotation(Class<?> clazz) {
        return AnnotationUtils.findAnnotation(clazz, UxServer.class);
    }

    public static ServerAction fetchServerAction(UxServer serverAnnotation) {
        if (null != serverAnnotation) {
            return (ServerAction) new ServerAction().setActionType(ActionTypeEnum.SERVER)
                    .setModel(serverAnnotation.model()).setName(serverAnnotation.name())
                    .setMapping(PropUtils.convertPropMapFromAnnotation(serverAnnotation.mapping()))
                    .setContext(PropUtils.convertPropMapFromAnnotation(serverAnnotation.context()));
        }
        return null;
    }

    public static UxClient fetchClientActionAnnotation(Class<?> clazz) {
        return AnnotationUtils.findAnnotation(clazz, UxClient.class);
    }

    public static ClientAction fetchClientAction(UxClient clientAnnotation, String module, String menuName) {
        if (null != clientAnnotation) {
            ClientAction clientAction = new ClientAction();
            String model = fetchMenuModel(module, MenuActionConstants.defaultMenuModelAppendix);
            clientAction.setActionType(ActionTypeEnum.CLIENT).setModel(model).setName(menuName);
            return clientAction;
        }
        return null;
    }

    public static UxRoute fetchViewActionAnnotation(Class<?> clazz) {
        return AnnotationUtils.findAnnotation(clazz, UxRoute.class);
    }

    public static ViewAction fetchViewAction(UxRoute routeAnnotation, String menuName) {
        if (null != routeAnnotation) {
            ViewAction viewAction = new ViewAction();
            String resModel = Optional.of(routeAnnotation.model()).filter(StringUtils::isNotBlank).orElse(null);
            if (StringUtils.isBlank(resModel)) {
                throw PamirsException.construct(BootUxdExpEnumerate.BASE_MENU_MODEL_IS_NULL_ERROR)
                        .appendMsg(MessageFormat.format("menu:{0}", menuName)).errThrow();
            }
            viewAction.setActionType(ActionTypeEnum.VIEW).setModel(resModel).setName(menuName);
            return viewAction;
        }
        return null;
    }

    public static UxLink fetchUrlActionAnnotation(Class<?> clazz) {
        return AnnotationUtils.findAnnotation(clazz, UxLink.class);
    }

    public static UrlAction fetchUrlAction(UxLink linkAnnotation, String module, String menuName) {
        if (null != linkAnnotation) {
            UrlAction urlAction = new UrlAction();
            String model = fetchMenuModel(module, MenuActionConstants.defaultMenuModelAppendix);
            urlAction.setActionType(ActionTypeEnum.URL).setModel(model).setName(menuName);
            return urlAction;
        }
        return null;
    }

}
