package pro.shushi.pamirs.framework.connectors.event.spi;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 系统机制消费者组编辑API
 *
 * @author Adamancy Zhang at 11:45 on 2021-08-26
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface SystemConsumerGroupEditorApi {

    /**
     * 生成系统消费者组
     *
     * @param group 通过{@link ConsumerGroupEditorApi#handlerConsumerGroup(String)}编辑后的消费者组
     * @return 系统消费者组
     */
    String handlerConsumerGroup(String group);
}
