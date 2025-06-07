package pro.shushi.pamirs.framework.connectors.event.api;

import org.springframework.messaging.Message;
import pro.shushi.pamirs.framework.connectors.event.enumeration.NotifyTransactionState;

import java.io.Serializable;

/**
 * @author Adamancy Zhang on 2021-03-17 21:38
 */
@FunctionalInterface
public interface NotifyTransactionListener {

    /**
     * 检查本地事务
     *
     * @param event 通知事件
     * @return 事务状态
     */
    NotifyTransactionState checkLocalTransaction(Message<? extends Serializable> event);

    /**
     * 执行本地事务
     *
     * @param event 通知事件
     * @return 事务状态
     */
    default NotifyTransactionState executeLocalTransaction(Message<? extends Serializable> event, Object extArg) {
        return NotifyTransactionState.UNKNOWN;
    }
}
