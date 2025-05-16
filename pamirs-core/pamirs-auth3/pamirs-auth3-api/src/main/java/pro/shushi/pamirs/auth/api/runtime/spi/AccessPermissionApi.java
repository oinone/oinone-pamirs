package pro.shushi.pamirs.auth.api.runtime.spi;

import pro.shushi.pamirs.auth.api.entity.AuthResult;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.Map;
import java.util.Set;

/**
 * 访问权限API
 *
 * @author Adamancy Zhang at 16:20 on 2024-01-06
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface AccessPermissionApi {

    /**
     * 获取可访问模块集合
     *
     * @return 可访问模块
     */
    AuthResult<Set<String>> fetchAccessModules();

    /**
     * 获取可访问首页模块集合
     *
     * @return 可访问首页模块
     */
    AuthResult<Set<String>> fetchAccessHomepages();

    /**
     * 获取可访问菜单集合
     *
     * @return 可访问菜单
     */
    AuthResult<Set<String>> fetchAccessMenus(String module);

    /**
     * 获取可访问菜单集合
     *
     * @return 可访问菜单
     */
    AuthResult<Map<String, Set<String>>> fetchAccessMenus(Set<String> modules);

    /**
     * 获取可访问动作集合 - 当前会话
     *
     * @return 可访问动作
     */
    AuthResult<Set<String>> fetchAccessActions();

    /**
     * 获取可访问动作集合 - 基于指定模型
     *
     * @param model 指定模型
     * @return 可访问动作
     */
    AuthResult<Set<String>> fetchAccessActions(String model);

    /**
     * 判断指定模块是否可访问
     *
     * @param module 指定模块
     * @return 是否可访问
     */
    AuthResult<Boolean> isAccessModule(String module);

    /**
     * 判断指定模块首页是否可访问
     *
     * @param module 指定模块
     * @return 是否可访问
     */
    AuthResult<Boolean> isAccessHomepage(String module);

    /**
     * 判断指定模块菜单是否可访问
     *
     * @param module 指定模块
     * @param name   指定菜单
     * @return 是否可访问
     */
    AuthResult<Boolean> isAccessMenu(String module, String name);

    /**
     * 判断指定函数是否被过滤
     *
     * @param namespace 函数命名空间
     * @param fun       函数编码
     * @return 是否可访问
     */
    Boolean isFilterFunction(String namespace, String fun);

    /**
     * 判断指定函数是否仅判断登录
     *
     * @param namespace 函数命名空间
     * @param fun       函数编码
     * @return 是否可访问
     */
    Boolean isFilterFunctionOnlyLogin(String namespace, String fun);

    /**
     * 判断指定函数是否可访问
     *
     * @param namespace 函数命名空间
     * @param fun       函数编码
     * @return 是否可访问
     */
    AuthResult<Boolean> isAccessFunction(String namespace, String fun);

    /**
     * 判断指定动作是否可访问
     *
     * @param model 模型编码
     * @param name  动作名称
     * @return 是否可访问
     */
    AuthResult<Boolean> isAccessAction(String model, String name);

    /**
     * 判断指定动作是否可访问
     *
     * @param path 动作路径
     * @return 是否可访问
     */
    AuthResult<Boolean> isAccessAction(String path);
}
