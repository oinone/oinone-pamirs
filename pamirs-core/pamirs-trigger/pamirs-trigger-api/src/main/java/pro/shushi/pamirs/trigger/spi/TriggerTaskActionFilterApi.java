package pro.shushi.pamirs.trigger.spi;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.trigger.model.TriggerTaskAction;

import java.util.List;

/**
 * 多租户场景, 管理复制了平台工作流给租户,此时需要走租户自己的工作流
 *
 * @author wx@shushi.pro
 * @version 1.0.0
 * 2025/11/19
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface TriggerTaskActionFilterApi {

    List<TriggerTaskAction> filter(List<TriggerTaskAction> triggerTaskActions);

}
