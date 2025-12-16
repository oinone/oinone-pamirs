package pro.shushi.pamirs.boot.web.converter.nav;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.base.model.Menu;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxMenu;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxMenus;
import pro.shushi.pamirs.boot.web.utils.MenuUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ConverterType;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.enmu.ActiveEnum;
import pro.shushi.pamirs.meta.enmu.ClientTypeEnum;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜单前端配置注解转化器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings("rawtypes")
@Slf4j
@Component
public class MenuConverter implements ModelConverter<Map<String, Menu>, Class> {

    @Override
    public int priority() {
        return 99;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Result validate(ExecuteContext context, MetaNames names, Class source) {
        UxMenus menuAnnotation = AnnotationUtils.getAnnotation(source, UxMenus.class);
        Result result = new Result();
        if (null == menuAnnotation) {
            return result.error();
        }
        return result;
    }

    @Override
    public Map<String, Menu> convert(MetaNames names, @SuppressWarnings("rawtypes") Class source, Map<String, Menu> metaModelObject) {
        UxMenus menuAnnotation = AnnotationUtils.getAnnotation(source, UxMenus.class);
        String module = Optional.ofNullable(menuAnnotation).map(UxMenus::module).filter(StringUtils::isNotBlank).orElse(names.getModule());
        Integer basePriority = Optional.ofNullable(menuAnnotation).map(UxMenus::basePriority).orElse(0);
        Map<String, Menu> menuMap = new HashMap<>();
        Class[] declaredClasses = source.getDeclaredClasses();
        menus(module, metaModelObject, menuMap, null, declaredClasses, basePriority);
        return menuMap;
    }

    private void menus(String module, Map<String, Menu> context, Map<String, Menu> menuMap, Menu parent, Class[] declaredClasses, Integer basePriority) {
        if (ArrayUtils.isEmpty(declaredClasses)) {
            return;
        }
        long i = declaredClasses.length;
        for (Class clazz : declaredClasses) {
            if (null == clazz) {
                continue;
            }
            Menu menu = fetchMenu(module, context, menuMap, parent, clazz, basePriority + i);
            if (null == menu) {
                continue;
            }
            Class[] childDeclaredClasses = clazz.getDeclaredClasses();
            menus(module, context, menuMap, menu, childDeclaredClasses, basePriority);
            i--;
        }
    }

    private Menu fetchMenu(String module, Map<String, Menu> context, Map<String, Menu> menuMap, Menu parent, Class clazz, long priority) {
        UxMenu uxMenu = AnnotationUtils.findAnnotation(clazz, UxMenu.class);
        if (null == uxMenu) {
            return null;
        }

        String name = uxMenu.name();
        if (StringUtils.isBlank(name)) {
            name = MenuUtils.fetchMenuName(clazz);
        }
        String sign = fetchMenuSign(module, name);
        Menu menu;
        if (context.containsKey(sign)) {
            menu = context.get(sign).disableMetaCompleted();
        } else {
            menu = new Menu();
            menuMap.put(sign, menu);
        }

        List<ClientTypeEnum> clientTypeEnums = Arrays.stream(uxMenu.clientTypes()).collect(Collectors.toList());

        // 处理菜单
        menu.setName(name);
        menu.setDefaultDisplayName(Optional.of(uxMenu.label()).filter(StringUtils::isNotBlank).orElse(clazz.getSimpleName()));
        menu.setDescription(Optional.of(uxMenu.summary()).filter(StringUtils::isNotBlank).orElse(menu.getDisplayName()));
        menu.setModule(module);
        menu.setClientTypes(clientTypeEnums);
        menu.setDefaultPriority(priority);
        menu.setDefaultIcon(Optional.of(uxMenu.icon()).filter(StringUtils::isNotBlank).orElse(null));
        menu.setDefaultParentName(Optional.ofNullable(parent).map(Menu::getName).orElse(null));
        menu.setDefaultShow(ActiveEnum.ACTIVE);
        menu.setSign(sign);
        menu.setModel(null);
        menu.setActionName(null);
        menu.setActionType(null);
        menu.setMapping(null);
        menu.setContext(null);

        // 处理动作
        Action action = MenuUtils.fetchMenuAction(clazz, module);
        if (null != action) {
            menu.setModel(action.getModel());
            menu.setActionName(action.getName());
            menu.setActionType(action.getActionType());
            menu.setMapping(action.getMapping());
            menu.setContext(action.getContext());

            fixActionModel(menu.getServerAction(), action.getModel(), action.getName());
            fixActionModel(menu.getViewAction(), action.getModel(), action.getName());
            fixActionModel(menu.getUrlAction(), action.getModel(), action.getName());
            fixActionModel(menu.getClientAction(), action.getModel(), action.getName());
        }

        return menu;
    }

    private void fixActionModel(Action action, String model, String name) {
        if (null == action) {
            return;
        }
        if (null != model) {
            action.setModel(model);
        }
        if (null != name) {
            action.setName(name);
        }
    }

    @Override
    public String group() {
        return Menu.MODEL_MODEL;
    }

    @Override
    public Class<?> metaModelClazz() {
        return Menu.class;
    }

    @Override
    public ConverterType type() {
        return ConverterType.map;
    }

    @Override
    public List<String> signs(MetaNames names, Class source) {
        List<String> signs = new ArrayList<>();
        UxMenus menuAnnotation = AnnotationUtils.getAnnotation(source, UxMenus.class);
        String module = Optional.ofNullable(menuAnnotation).map(UxMenus::module).filter(StringUtils::isNotBlank).orElse(names.getModule());
        Class[] declaredClasses = source.getDeclaredClasses();
        menuNames(signs, module, declaredClasses);
        return signs;
    }

    private void menuNames(List<String> signs, String module, Class[] declaredClasses) {
        if (ArrayUtils.isEmpty(declaredClasses)) {
            return;
        }
        for (Class clazz : declaredClasses) {
            if (null == clazz) {
                continue;
            }
            String menuSign = fetchMenuSign(module, clazz);
            if (null == menuSign) {
                continue;
            }
            signs.add(menuSign);
            Class[] childDeclaredClasses = clazz.getDeclaredClasses();
            menuNames(signs, module, childDeclaredClasses);
        }
    }

    private String fetchMenuSign(String module, Class clazz) {
        UxMenu uxMenu = AnnotationUtils.findAnnotation(clazz, UxMenu.class);
        if (null == uxMenu) {
            return null;
        }
        String name = uxMenu.name();
        if (StringUtils.isBlank(name)) {
            name = MenuUtils.fetchMenuName(clazz);
        }
        return fetchMenuSign(module, name);
    }

    private String fetchMenuSign(String module, String menuName) {
        return Menu.sign(module, menuName);
    }

}
