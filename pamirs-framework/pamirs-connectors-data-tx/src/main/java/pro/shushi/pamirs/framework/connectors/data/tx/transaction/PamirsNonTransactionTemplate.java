package pro.shushi.pamirs.framework.connectors.data.tx.transaction;

import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import java.util.function.Consumer;

/**
 * 非事务模板
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/11 2:02 上午
 */
final class PamirsNonTransactionTemplate extends PamirsTransactionTemplate {

    private static final long serialVersionUID = 5012036212120797759L;

    static final PamirsNonTransactionTemplate INSTANCE = new PamirsNonTransactionTemplate();

    @Override
    @Nullable
    public <T> T execute(TransactionCallback<T> action) throws TransactionException {
        return action.doInTransaction(new SimpleTransactionStatus(false));
    }

    public void executeWithoutResult(Consumer<TransactionStatus> action) throws TransactionException {
        action.accept(new SimpleTransactionStatus(false));
    }

}
