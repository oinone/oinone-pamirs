package pro.shushi.pamirs.eip.api.strategy.cache;

import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;
import pro.shushi.pamirs.eip.api.model.EipLog;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.List;
import java.util.Map;

/**
 * 日志统计缓存服务
 *
 * @author yeshenyue on 2025/4/10 15:51.
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface EipLogCountCacheApi {

    /**
     * 添加日志统计
     */
    void addLogCount(EipLog eipLog);

    /**
     * 批量获取调用次数
     */
    Map<String, Long> getCallCount(InterfaceTypeEnum interfaceType, List<String> interfaceNameList);

    /**
     * 清空缓存
     */
    void clear(InterfaceTypeEnum interfaceType, String interfaceName);
}
