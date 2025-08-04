package pro.shushi.pamirs.framework.connectors.data.xa.platform;

import com.atomikos.icatch.jta.UserTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;
import pro.shushi.pamirs.framework.connectors.data.api.datasource.TransactionApi;

import jakarta.annotation.Resource;
import jakarta.transaction.UserTransaction;

/**
 * pamirs分布式事务bean
 *
 * @author deng
 */
@Component
public class PamirsXaTransactionManager implements TransactionApi {

    @Resource
    private UserTransaction userTransaction;

    @Resource
    private UserTransactionManager userTransactionManager;

    @Override
    public PlatformTransactionManager getJtaTransactionManager() {
        return new JtaTransactionManager(userTransaction, userTransactionManager);
    }

}
