package pro.shushi.pamirs.framework.connectors.data.api.datasource;

import pro.shushi.pamirs.meta.api.CommonApi;

/**
 * 数据源获取器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
public interface DsKeyFetcher extends CommonApi {

    /**
     * 获取逻辑数据源
     *
     * @param dsKey 初始逻辑数据源
     * @return 基础设施使用的逻辑数据源
     */
    String fetchLogicDsKey(String dsKey);

    /**
     * 为建表获取逻辑数据源
     *
     * @param dsKey 初始逻辑数据源
     * @return 基础设施使用的逻辑数据源
     */
    String fetchLogicDsKeyForDDL(String dsKey);

}
