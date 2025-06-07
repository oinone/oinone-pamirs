package pro.shushi.pamirs.auth.api.spi;

import pro.shushi.pamirs.auth.api.entity.AuthResult;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import java.util.List;
import java.util.Map;

/**
 * <h3>权限过滤服务</h3>
 * <p>
 * 多个权限过滤服务只要有一个服务允许访问，即可正常访问
 * </p>
 *
 * @author Adamancy Zhang at 23:15 on 2024-04-15
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface AuthFilterService {

    /**
     * 是否可访问模块
     *
     * @param module 指定模块
     * @return 是否可访问
     */
    default Boolean isAccessModule(String module) {
        return null;
    }

    /**
     * 是否可访问模块首页
     *
     * @param module 指定模块
     * @return 是否可访问
     */
    default Boolean isAccessHomepage(String module) {
        return null;
    }

    /**
     * 是否可访问菜单
     *
     * @param module 菜单模块
     * @param name   菜单名称
     * @return 是否可访问
     */
    default Boolean isAccessMenu(String module, String name) {
        return null;
    }

    /**
     * 当前函数是否可访问
     *
     * @param namespace 函数命名空间
     * @param fun       函数编码
     * @return 是否可访问
     */
    default Boolean isAccessFunction(String namespace, String fun) {
        return null;
    }

    /**
     * 当前动作是否可访问
     *
     * @param model 动作模型编码
     * @param name  动作名称
     * @return 是否可访问
     */
    default Boolean isAccessAction(String model, String name) {
        return null;
    }

    /**
     * 当前动作是否可访问
     *
     * @param actionPath 动作资源路径
     * @return 是否可访问
     */
    default Boolean isAccessAction(String actionPath) {
        return null;
    }

    /**
     * 当前模型是否可访问
     *
     * @param model         模型编码
     * @param functionTypes 函数类型
     * @return 是否可访问
     */
    default Boolean isAccessModel(String model, List<FunctionTypeEnum> functionTypes) {
        return null;
    }

    /**
     * 获取字段权限集合
     *
     * @param model 模型编码
     * @return 字段权限集合
     */
    default AuthResult<Map<String, Long>> fetchFieldPermissions(String model) {
        return null;
    }

    /**
     * 获取读权限过滤表达式
     *
     * @param model 模型编码
     * @return 读权限过滤表达式
     */
    default AuthResult<String> fetchModelFilterForRead(String model) {
        return null;
    }

    /**
     * 获取写权限过滤表达式
     *
     * @param model 模型编码
     * @return 写权限过滤表达式
     */
    default AuthResult<String> fetchModelFilterForWrite(String model) {
        return null;
    }

    /**
     * 获取删除权限过滤表达式
     *
     * @param model 模型编码
     * @return 删除权限过滤表达式
     */
    default AuthResult<String> fetchModelFilterForDelete(String model) {
        return null;
    }
}
