package pro.shushi.pamirs.middleware.canal.service;

import pro.shushi.pamirs.middleware.canal.domain.Destination;
import pro.shushi.pamirs.middleware.canal.entity.RefreshFilterEntity;
import pro.shushi.pamirs.middleware.canal.entity.SimpleResult;

/**
 * @author Adamancy Zhang
 * @date 2020-12-24 13:48
 */
public interface CanalService {

    /**
     * 获取当前过滤表达式
     *
     * @param destination 订阅名称
     * @return 过滤表达式
     */
    String getCurrentFilter(String destination);

    /**
     * 追加过滤表达式
     *
     * @param data 追加过滤表达式数据
     * @return 追加结果
     */
    SimpleResult<RefreshFilterEntity> appendFilter(RefreshFilterEntity data);

    /**
     * 移除指定过滤表达式
     *
     * @param data 移除指定过滤表达式
     * @return 移除结果
     */
    SimpleResult<RefreshFilterEntity> removeFilter(RefreshFilterEntity data);

    /**
     * 刷新过滤表达式
     *
     * @param data 刷新过滤表达式数据
     * @return 追加结果
     */
    SimpleResult<RefreshFilterEntity> refreshFilter(RefreshFilterEntity data);

    /**
     * 追加订阅Destination
     *
     * @param data
     * @return 追加结果
     */
    SimpleResult<Void> addDestination(Destination data);

    /**
     * 移除指定的订阅Destination
     *
     * @param data
     * @return 移除结果
     */
    SimpleResult<Void> removeDestination(Destination data);
}
