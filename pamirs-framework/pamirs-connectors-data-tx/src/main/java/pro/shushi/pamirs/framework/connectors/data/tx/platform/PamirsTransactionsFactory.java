package pro.shushi.pamirs.framework.connectors.data.tx.platform;

import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.mybatis.spring.transaction.SpringManagedTransaction;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;

import javax.sql.DataSource;

/**
 * 事务工厂扩展点
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 17:08
 */
public class PamirsTransactionsFactory extends SpringManagedTransactionFactory {

    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        return new SpringManagedTransaction(dataSource);
    }

}