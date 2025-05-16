package pro.shushi.pamirs.framework.connectors.event.api;

import pro.shushi.pamirs.framework.connectors.event.engine.NotifyEvent;
import pro.shushi.pamirs.framework.connectors.event.enumeration.NotifyTransactionState;

/**
 * @author Adamancy Zhang on 2021-04-01 15:07
 */
@FunctionalInterface
public interface NotifyExecuteLocalTransactionCallback {

    /**
     * 执行本地事务回调
     *
     * @param executeState 执行本地事务后的事务状态
     * @param event        通知事件
     * @return 事务状态
     */
    NotifyTransactionState callback(NotifyTransactionState executeState, NotifyEvent event);
}
