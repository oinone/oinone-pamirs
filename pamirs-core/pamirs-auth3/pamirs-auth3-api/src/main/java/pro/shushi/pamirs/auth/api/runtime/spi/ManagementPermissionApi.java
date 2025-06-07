package pro.shushi.pamirs.auth.api.runtime.spi;

import pro.shushi.pamirs.auth.api.entity.AuthResult;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.Map;
import java.util.Set;

/**
 * 管理权限API
 *
 * @author Adamancy Zhang at 11:00 on 2024-01-12
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface ManagementPermissionApi {

    /**
     * 获取可管理模块集合
     *
     * @return 可管理模块
     */
    AuthResult<Set<String>> fetchManagementModules();

    /**
     * 获取可管理首页模块集合
     *
     * @return 可管理首页模块
     */
    AuthResult<Set<String>> fetchManagementHomepages();

    /**
     * 获取可管理菜单集合
     *
     * @return 可管理菜单
     */
    AuthResult<Set<String>> fetchManagementMenus(String module);

    /**
     * 获取可管理菜单集合
     *
     * @return 可管理菜单
     */
    AuthResult<Map<String, Set<String>>> fetchManagementMenus(Set<String> modules);

    /**
     * 获取可管理动作集合 - 当前会话
     *
     * @return 可管理菜单
     */
    AuthResult<Set<String>> fetchManagementActions();

    /**
     * 获取可管理动作集合 - 基于指定模型
     *
     * @param model 指定模型
     * @return 可管理菜单
     */
    AuthResult<Set<String>> fetchManagementActions(String model);

    /**
     * 判断指定模块是否可管理
     *
     * @param module 指定模块
     * @return 是否可管理
     */
    AuthResult<Boolean> isManagementModule(String module);

    /**
     * 判断指定模块首页是否可管理
     *
     * @param module 指定模块
     * @return 是否可管理
     */
    AuthResult<Boolean> isManagementHomepage(String module);

    /**
     * 判断指定模块菜单是否可管理
     *
     * @param module 指定模块
     * @param name   指定菜单名称
     * @return 是否可管理
     */
    AuthResult<Boolean> isManagementMenu(String module, String name);

    /**
     * 判断指定动作是否可管理
     *
     * @param model 模型编码
     * @param name  动作名称
     * @return 是否可管理
     */
    AuthResult<Boolean> isManagementAction(String model, String name);

    /**
     * 判断指定动作是否可管理
     *
     * @param path 动作路径
     * @return 是否可管理
     */
    AuthResult<Boolean> isManagementAction(String path);
}
