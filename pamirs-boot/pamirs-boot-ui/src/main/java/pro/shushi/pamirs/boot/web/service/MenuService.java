package pro.shushi.pamirs.boot.web.service;

import pro.shushi.pamirs.boot.base.model.Menu;

import java.util.List;

/**
 * 菜单服务接口
 * <p>
 * 2022/9/26 9:42 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface MenuService {

    List<Menu> loadMenus(String module);

}
