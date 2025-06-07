package pro.shushi.pamirs.framework.connectors.data.tx.service;

import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import pro.shushi.pamirs.framework.common.api.TxConfigFetchApi;
import pro.shushi.pamirs.framework.connectors.data.tx.interceptor.PamirsTransactional;
import pro.shushi.pamirs.framework.connectors.data.tx.parser.TxAttributeParser;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.util.ListUtils;
import pro.shushi.pamirs.meta.domain.fun.TransactionConfig;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 事务配置获取api
 * 2020/12/11 7:49 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI.Service
public class TxConfigFetchApiImpl implements TxConfigFetchApi {

    public TransactionConfig fetchTx(Method method, TransactionConfig transactionConfig) {
        PamirsTransactional pamirsTransactional = AnnotationUtils.getAnnotation(method, PamirsTransactional.class);
        if (null == pamirsTransactional) {
            return null;
        }
        if (null == transactionConfig) {
            transactionConfig = new TransactionConfig();
        }
        parseTransactionAnnotation(transactionConfig, pamirsTransactional);
        return transactionConfig;
    }

    public static void parseTransactionAnnotation(TransactionConfig transactionConfig, Annotation annotation) {
        AnnotationAttributes attributes = AnnotationUtils.getAnnotationAttributes(annotation, false, false);
        parseTransactionAnnotation(transactionConfig, attributes);
    }

    @SuppressWarnings("unchecked")
    public static void parseTransactionAnnotation(TransactionConfig transactionConfig, AnnotationAttributes attributes) {
        transactionConfig.setActive(true);
        transactionConfig.setEnableXa(attributes.getBoolean("enableXa"));
        transactionConfig.setTransactionManager(attributes.getString("transactionManager"));

        Object propagationValue = attributes.get("propagation");
        Propagation propagation;
        if (propagationValue instanceof Number) {
            propagation = TxAttributeParser.propagation(attributes.getNumber("propagation"));
        } else {
            propagation = (Propagation) propagationValue;
        }
        transactionConfig.setPropagation(propagation.value());
        Object isolationValue = attributes.get("isolation");
        Isolation isolation;
        if (isolationValue instanceof Number) {
            isolation = TxAttributeParser.isolation(attributes.getNumber("isolation"));
        } else {
            isolation = (Isolation) isolationValue;
        }
        transactionConfig.setIsolation(isolation.value());
        transactionConfig.setTimeout(attributes.getNumber("timeout").intValue());
        transactionConfig.setReadOnly(attributes.getBoolean("readOnly"));

        transactionConfig.setNoRollbackFor((Class<Throwable>[]) attributes.getClassArray("noRollbackFor"));
        transactionConfig.setNoRollbackForClassName(attributes.getStringArray("noRollbackForClassName"));
        transactionConfig.setRollbackFor((Class<Throwable>[]) attributes.getClassArray("rollbackFor"));
        transactionConfig.setRollbackForClassName(attributes.getStringArray("rollbackForClassName"));

        transactionConfig.setNoRollbackForExpCode(ListUtils.toList(attributes.getStringArray("noRollbackForExpCode")));
        transactionConfig.setRollbackForExpCode(ListUtils.toList(attributes.getStringArray("rollbackForExpCode")));
    }

}
