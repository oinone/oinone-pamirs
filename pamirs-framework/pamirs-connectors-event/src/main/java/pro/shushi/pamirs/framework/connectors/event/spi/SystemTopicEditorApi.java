package pro.shushi.pamirs.framework.connectors.event.spi;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 系统机制Topic编辑API
 *
 * @author Adamancy Zhang at 12:05 on 2021-08-18
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface SystemTopicEditorApi {

    /**
     * 生成系统topic
     *
     * @param topic 通过{@link NotifyTopicEditorApi#handlerTopic(String)}编辑后的topic
     * @return 系统topic
     */
    String handlerTopic(String topic);
}
