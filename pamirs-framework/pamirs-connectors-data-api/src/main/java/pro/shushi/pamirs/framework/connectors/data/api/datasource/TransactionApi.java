package pro.shushi.pamirs.framework.connectors.data.api.datasource;

import org.springframework.transaction.PlatformTransactionManager;

/**
 * 事务接口
 * <p>
 * 2020/7/8 12:33 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface TransactionApi {

    PlatformTransactionManager getJtaTransactionManager();

}
