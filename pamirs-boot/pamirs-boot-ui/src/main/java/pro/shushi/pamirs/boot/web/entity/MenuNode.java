package pro.shushi.pamirs.boot.web.entity;

import pro.shushi.pamirs.boot.base.model.Menu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 菜单节点
 *
 * @author Adamancy Zhang at 16:20 on 2024-01-11
 */
public class MenuNode {

    public Menu menu;

    public Long priority;

    public List<MenuNode> children;

    public MenuNode(Menu menu) {
        this.menu = menu;
        this.priority = menu.getPriority();
        this.children = new ArrayList<>(16);
    }

    public Menu collections(Consumer<Menu> consumer) {
        consumer.accept(menu);
        menu.setChildren(children.stream().sorted(Comparator.comparing((v) -> v.priority)).map(v -> v.collections(consumer)).collect(Collectors.toList()));
        return menu;
    }
}