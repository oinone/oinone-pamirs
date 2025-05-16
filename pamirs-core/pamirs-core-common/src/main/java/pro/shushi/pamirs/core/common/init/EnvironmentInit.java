package pro.shushi.pamirs.core.common.init;

import pro.shushi.pamirs.core.common.InitializationUtil;
import pro.shushi.pamirs.core.common.enmu.ModuleLifecycleEnum;
import pro.shushi.pamirs.meta.api.core.compute.Prioritized;

/**
 * @author Adamancy Zhang on 2021-02-02 13:26
 */
public interface EnvironmentInit extends Prioritized {

    /**
     * 启动模块
     *
     * @return 模块编码
     */
    String getBootModule();

    /**
     * 启动模块
     *
     * @return 模块名称
     */
    String getBootModuleName();

    /**
     * 优先级
     *
     * @return 从小到大排序的优先级
     */
    @Override
    default int priority() {
        return 0;
    }

    /**
     * 开发环境配置后缀数组
     *
     * @return 配置后缀数组
     */
    default String[] getDevActiveSuffix() {
        return new String[]{"dev"};
    }

    /**
     * 开发环境元数据初始化
     *
     * @param util 元数据初始化工具
     */
    default void devMetadataInit(InitializationUtil util) {
    }

    /**
     * 开发环境初始化
     *
     * @param lifecycle    生命周期枚举
     * @param version      版本号
     * @param existVersion 升级前已存在的版本号，当且仅当升级时存在该值
     */
    default void devInit(ModuleLifecycleEnum lifecycle, String version, String existVersion) {
    }

    /**
     * 生产环境配置后缀数组
     *
     * @return 配置后缀数组
     */
    default String[] getProdActiveSuffix() {
        return new String[]{"prod"};
    }

    /**
     * 生产环境元数据初始化
     *
     * @param util 元数据初始化工具
     */
    default void prodMetadataInit(InitializationUtil util) {
    }

    /**
     * 生产环境初始化
     *
     * @param lifecycle    生命周期枚举
     * @param version      版本号
     * @param existVersion 升级前已存在的版本号，当且仅当升级时存在该值
     */
    default void prodInit(ModuleLifecycleEnum lifecycle, String version, String existVersion) {
    }

    /**
     * 测试环境配置后缀数组
     *
     * @return 配置后缀数组
     */
    default String[] getTestActiveSuffix() {
        return new String[]{"test"};
    }

    /**
     * 测试环境元数据初始化
     *
     * @param util 元数据初始化工具
     */
    default void testMetadataInit(InitializationUtil util) {
    }

    /**
     * 测试环境初始化
     *
     * @param lifecycle    生命周期枚举
     * @param version      版本号
     * @param existVersion 升级前已存在的版本号，当且仅当升级时存在该值
     */
    default void testInit(ModuleLifecycleEnum lifecycle, String version, String existVersion) {
    }
}
