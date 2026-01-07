package pro.shushi.pamirs.trigger.spi.impl;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.trigger.model.TriggerTaskAction;
import pro.shushi.pamirs.trigger.spi.TriggerTaskActionFilterApi;

import java.util.List;

/**
 * 多租户场景, 管理复制了平台工作流给租户,此时需要走租户自己的工作流；
 * 在租户包中会根据关联关系，确定是否走平台的工作流，还是租户自己的工作流。
 *
 * @author wx@shushi.pro
 * @version 1.0.0
 * 2025/11/19
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@SPI.Service
public class DefaultTriggerTaskActionFilter implements TriggerTaskActionFilterApi {

    @Override
    public List<TriggerTaskAction> filter(List<TriggerTaskAction> triggerTaskActions) {
        return triggerTaskActions;
    }

}
