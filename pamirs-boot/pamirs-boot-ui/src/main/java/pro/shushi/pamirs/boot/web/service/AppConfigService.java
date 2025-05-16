package pro.shushi.pamirs.boot.web.service;

import pro.shushi.pamirs.boot.base.enmu.AppConfigScopeEnum;
import pro.shushi.pamirs.boot.base.model.AppConfig;

import java.util.List;
import java.util.Map;

/**
 * 应用配置服务接口
 * 2022/2/22 10:43 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface AppConfigService {

    /**
     * 创建或更新应用配置
     *
     * @param appConfig 应用配置
     * @return 应用配置
     */
    AppConfig save(AppConfig appConfig);

    /**
     * 获取当前请求的应用配置
     *
     * @param product 产品编码
     * @param app     应用编码
     * @return 应用配置
     */
    AppConfig fetchRequestAppConfig(String product, String app);

    /**
     * 获取全局应用配置
     *
     * @return 全局应用配置
     */
    AppConfig fetchGlobalConfig();

    /**
     * 获取产品通用配置
     *
     * @param product 产品编码
     * @return 产品通用配置
     */
    AppConfig fetchProductConfig(String product);

    /**
     * 获取产品应用配置
     *
     * @param product 产品编码
     * @param app     应用编码
     * @return 产品应用配置
     */
    AppConfig fetchProductAppConfig(String product, String app);

    /**
     * 获取应用配置
     *
     * @param app 应用编码
     * @return 应用配置
     */
    AppConfig fetchAppConfig(String app);

    /**
     * 通过配置域获取应用配置
     * <p>
     * 配置域由scopeType和codes拼接而成，以#分隔
     *
     * @param scopeType 配置域类型，scope必填
     * @param codes     业务编码，如果scope值不为GLOBAL则必填，否则不填，支持多个业务编码
     * @return 应用配置
     * @see pro.shushi.pamirs.boot.base.enmu.AppConfigScopeEnum
     */
    AppConfig fetchAppConfig(AppConfigScopeEnum scopeType, String... codes);

    /**
     * 获取应用配置列表
     *
     * @param apps 应用编码列表
     * @return 应用配置列表
     */
    List<AppConfig> fetchAppConfigList(List<String> apps);

    /**
     * 获取应用配置map
     *
     * @param apps 应用编码列表
     * @return 应用配置map
     */
    Map<String, AppConfig> fetchAppConfigMap(List<String> apps);

    /**
     * 获取全域应用配置Map
     *
     * @param product 产品编码
     * @param app     应用编码
     * @return 应用配置Map
     */
    Map<AppConfigScopeEnum, AppConfig> fetchAppConfigMap(String product, String app);

}
