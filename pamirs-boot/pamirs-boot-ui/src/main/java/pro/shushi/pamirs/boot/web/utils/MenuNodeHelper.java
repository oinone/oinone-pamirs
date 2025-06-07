package pro.shushi.pamirs.boot.web.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.base.model.Menu;
import pro.shushi.pamirs.boot.web.entity.MenuNode;
import pro.shushi.pamirs.meta.api.core.auth.AuthApi;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 菜单树帮助类
 *
 * @author Adamancy Zhang at 16:14 on 2024-01-11
 */
public class MenuNodeHelper {

    private MenuNodeHelper() {
        //reject create object
    }

    public static Collection<MenuNode> buildMenuTree(String module, List<Menu> menus) {
        return buildMenuTree(module, menus, v -> true);
    }

    public static Collection<MenuNode> buildMenuTree(String module, List<Menu> menus, Predicate<Menu> filter) {
        Map<String, MenuNode> rootMap = new HashMap<>(menus.size());
        Map<String, MenuNode> childrenMap = new HashMap<>(menus.size());
        Map<String, List<MenuNode>> lazySetParentMap = new HashMap<>(menus.size());
        for (Menu menu : menus) {
            String name = menu.getName();
            String parentName = menu.getParentName();
            if (!filter.test(menu)) {
                continue;
            }
            MenuNode menuNode = new MenuNode(menu);
            menu.setModule(module);
            if (StringUtils.isBlank(parentName)) {
                rootMap.put(name, menuNode);
            } else {
                childrenMap.put(name, menuNode);
                MenuNode parentMenu = rootMap.get(parentName);
                if (parentMenu == null) {
                    parentMenu = childrenMap.get(parentName);
                }
                if (parentMenu == null) {
                    lazySetParentMap.computeIfAbsent(parentName, v -> new ArrayList<>()).add(menuNode);
                } else {
                    parentMenu.children.add(menuNode);
                }
            }
            List<MenuNode> lazySetParentMenus = lazySetParentMap.remove(name);
            if (CollectionUtils.isNotEmpty(lazySetParentMenus)) {
                menuNode.children.addAll(lazySetParentMenus);
            }
        }
        return rootMap.values();
    }

    public static MenuNode findHighPriorityMenu(Collection<MenuNode> nodes) {
        if (CollectionUtils.isEmpty(nodes)) {
            return null;
        }
        List<MenuNode> sortedNodes = nodes.stream().sorted(Comparator.comparing((v) -> v.priority)).collect(Collectors.toList());
        for (MenuNode node : sortedNodes) {
            MenuNode lasted = findHighPriorityMenu(node.children);
            if (lasted != null) {
                return lasted;
            }
            if (AuthApi.get().canAccessMenu(node.menu.getModule(), node.menu.getName()).getSuccess()) {
                return node;
            }
        }
        return null;
    }
}
