package pro.shushi.pamirs.framework.connectors.event.spi;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * Notify Topic 编辑API
 *
 * @author Adamancy Zhang on 2021-05-12 16:23
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface NotifyTopicEditorApi {

    /**
     * 生成topic
     *
     * @param topic 旧topic
     * @return 新topic
     */
    String handlerTopic(String topic);
}
