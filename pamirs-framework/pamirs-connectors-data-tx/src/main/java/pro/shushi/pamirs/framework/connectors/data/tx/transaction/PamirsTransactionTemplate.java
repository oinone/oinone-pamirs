package pro.shushi.pamirs.framework.connectors.data.tx.transaction;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.support.CallbackPreferringPlatformTransactionManager;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;
import pro.shushi.pamirs.framework.connectors.data.api.datasource.TransactionApi;
import pro.shushi.pamirs.framework.connectors.data.tx.interceptor.PamirsTransactional;
import pro.shushi.pamirs.framework.connectors.data.tx.interceptor.TxException;
import pro.shushi.pamirs.framework.connectors.data.tx.parser.TxAttributeParser;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.dto.config.TxConfig;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.Map;
import java.util.function.Consumer;

import static pro.shushi.pamirs.framework.connectors.data.tx.enmu.TxExpEnumerate.BASE_TRANSACTION_MANAGER_ERROR;
import static pro.shushi.pamirs.framework.connectors.data.tx.transaction.Tx.determineManager;

/**
 * 事务模板
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/11 2:02 上午
 */
public class PamirsTransactionTemplate extends TransactionTemplate {

    private static final long serialVersionUID = -8627585248291015093L;

    private TransactionAttribute transactionAttribute;

    private String[] rollbackForExpCode;

    private String[] noRollbackForExpCode;

    /**
     * 请勿使用，不会自动检测事务管理器，也不能自动检测
     */
    protected PamirsTransactionTemplate() {
        super();
    }

    @SuppressWarnings("unused")
    protected PamirsTransactionTemplate(String transactionManager) {
        super();
        this.setTransactionManager(determineManager(transactionManager));
    }

    protected PamirsTransactionTemplate(PlatformTransactionManager transactionManager) {
        super();
        this.setTransactionManager(transactionManager);
    }

    protected PamirsTransactionTemplate config(TxConfig txConfig) {
        if (null == txConfig || !txConfig.isEnable()) {
            return new PamirsNonTransactionTemplate();
        }
        if (null != txConfig.getIsolation()) {
            this.setIsolationLevel(txConfig.getIsolation());
        }
        if (null != txConfig.getPropagation()) {
            this.setPropagationBehavior(txConfig.getPropagation());
        }
        if (null != txConfig.getTimeout()) {
            this.setTimeout(txConfig.getTimeout());
        }
        if (null != txConfig.getReadOnly()) {
            this.setReadOnly(txConfig.getReadOnly());
        }
        Map<String, Object> txMap = txConfig.map();
        TxAttributeParser.parseTransactionAnnotation(this, txMap);
        this.transactionAttribute = TxAttributeParser.parseAnnotationRule(txMap);
        this.setRollbackForExpCode(txConfig.getRollbackForExpCode());
        this.setNoRollbackForExpCode(txConfig.getNoRollbackForExpCode());
        return this;
    }

    protected PamirsTransactionTemplate config(PamirsTransactional pamirsTransactional) {
        TxAttributeParser.parseTransactionAnnotation(this, pamirsTransactional);
        this.transactionAttribute = TxAttributeParser.parseAnnotationRule(pamirsTransactional);
        this.setRollbackForExpCode(pamirsTransactional.rollbackForExpCode());
        this.setNoRollbackForExpCode(pamirsTransactional.noRollbackForExpCode());
        return this;
    }

    protected static PlatformTransactionManager jtaTransactionManager() {
        TransactionApi transactionApi = CommonApiFactory.getApi(TransactionApi.class);
        if (null == transactionApi) {
            return null;
        }
        return transactionApi.getJtaTransactionManager();
    }

    @Override
    public <T> T execute(@SuppressWarnings("NullableProblems") TransactionCallback<T> action) throws TransactionException {
        return execute0(action);
    }

    public void executeWithoutResult(Consumer<TransactionStatus> action) throws TransactionException {
        this.execute0((status) -> {
            action.accept(status);
            return null;
        });
    }

    private <T> T execute0(TransactionCallback<T> action) throws TransactionException {
        PlatformTransactionManager transactionManager = this.getTransactionManager();
        Assert.state(transactionManager != null, "No PlatformTransactionManager set");

        if (transactionManager instanceof CallbackPreferringPlatformTransactionManager) {
            return ((CallbackPreferringPlatformTransactionManager) transactionManager).execute(this, action);
        } else {
            Throwable throwable = null;
            TransactionStatus status = transactionManager.getTransaction(this);
            T result = null;
            try {
                result = action.doInTransaction(status);
            } catch (Throwable ex) {
                if (ex instanceof TxException) {
                    ex = ex.getCause();
                }
                if (ex instanceof PamirsException) {
                    if (rollbackOnExpCode((PamirsException) ex)) {
                        // Transactional code threw application exception -> rollback
                        rollbackOnException(status, ex);
                        throw (PamirsException) ex;
                    } else {
                        throwable = ex;
                    }
                } else if (ex instanceof RuntimeException) {
                    if (rollbackOn(ex)) {
                        // Transactional code threw application exception -> rollback
                        rollbackOnException(status, ex);
                        throw (RuntimeException) ex;
                    } else {
                        throwable = ex;
                    }
                } else if (ex instanceof Error) {
                    if (rollbackOn(ex)) {
                        // Transactional code threw application exception -> rollback
                        rollbackOnException(status, ex);
                        throw (Error) ex;
                    } else {
                        throwable = ex;
                    }
                } else {
                    if (rollbackOn(ex)) {
                        // Transactional code threw unexpected exception -> rollback
                        rollbackOnException(status, ex);
                        throw new UndeclaredThrowableException(ex, "TransactionCallback threw undeclared checked exception");
                    } else {
                        throwable = ex;
                    }
                }
            }
            transactionManager.commit(status);
            if (null != throwable) {
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                throw PamirsException.construct(BASE_TRANSACTION_MANAGER_ERROR, throwable).errThrow();
            }
            return result;
        }
    }

    private boolean rollbackOn(Throwable ex) {
        if (null == transactionAttribute) {
            return true;
        }
        return transactionAttribute.rollbackOn(ex);
    }

    /**
     * Perform a rollback, handling rollback exceptions properly.
     *
     * @param status object representing the transaction
     * @param ex     the thrown application exception or error
     * @throws TransactionException in case of a rollback error
     */
    private void rollbackOnException(TransactionStatus status, Throwable ex) throws TransactionException {
        PlatformTransactionManager transactionManager = this.getTransactionManager();
        Assert.state(transactionManager != null, "No PlatformTransactionManager set");

        logger.debug("Initiating transaction rollback on application exception", ex);
        try {
            transactionManager.rollback(status);
        } catch (TransactionSystemException ex2) {
            logger.error("Application exception overridden by rollback exception", ex);
            ex2.initApplicationException(ex);
            throw ex2;
        } catch (RuntimeException | Error ex2) {
            logger.error("Application exception overridden by rollback exception", ex);
            throw ex2;
        }
    }

    private boolean rollbackOnExpCode(PamirsException ex) {
        if (ArrayUtils.contains(this.getNoRollbackForExpCode(), ex.getCode())) {
            return false;
        }
        return ArrayUtils.isEmpty(this.getRollbackForExpCode())
                || ArrayUtils.contains(this.getRollbackForExpCode(), ex.getCode());
    }

    public String[] getRollbackForExpCode() {
        return rollbackForExpCode;
    }

    public void setRollbackForExpCode(String[] rollbackForExpCode) {
        this.rollbackForExpCode = rollbackForExpCode;
    }

    public String[] getNoRollbackForExpCode() {
        return noRollbackForExpCode;
    }

    public void setNoRollbackForExpCode(String[] noRollbackForExpCode) {
        this.noRollbackForExpCode = noRollbackForExpCode;
    }

}
