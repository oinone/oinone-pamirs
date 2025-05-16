package pro.shushi.pamirs.framework.connectors.data.tx.transaction;

import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.PlatformTransactionManager;
import pro.shushi.pamirs.framework.connectors.data.api.constants.DataBeanNameConstants;
import pro.shushi.pamirs.framework.connectors.data.tx.interceptor.PamirsTransactional;
import pro.shushi.pamirs.meta.api.dto.config.TxConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.util.Optional;

/**
 * 事务模板快捷入口
 * <p>
 * 2020/7/7 4:36 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class Tx {

    public static PamirsTransactionTemplate build(String namespace, String fun) {
        TxConfig txConfig = Optional.ofNullable(PamirsSession.getContext()).map(v -> v.getTxConfig(namespace, fun)).orElse(null);
        if (null == txConfig) {
            return PamirsNonTransactionTemplate.INSTANCE;
        }
        return build(txConfig);
    }

    public static PamirsTransactionTemplate build(TxConfig txConfig) {
        if (null == txConfig || !txConfig.isEnable()) {
            return PamirsNonTransactionTemplate.INSTANCE;
        }
        if (txConfig.isEnableXa()) {
            PlatformTransactionManager jtaTransactionManager = PamirsTransactionTemplate.jtaTransactionManager();
            if (null == jtaTransactionManager) {
                return PamirsNonTransactionTemplate.INSTANCE;
            }
            return new PamirsTransactionTemplate(jtaTransactionManager).config(txConfig);
        }
        if (!StringUtils.isBlank(txConfig.getTransactionManager())) {
            return build(txConfig.getTransactionManager()).config(txConfig);
        }
        return build(true).config(txConfig);
    }

    public static PamirsTransactionTemplate build(PamirsTransactional pamirsTransactional) {
        if (pamirsTransactional.enableXa()) {
            PlatformTransactionManager jtaTransactionManager = PamirsTransactionTemplate.jtaTransactionManager();
            if (null == jtaTransactionManager) {
                return PamirsNonTransactionTemplate.INSTANCE;
            }
            return new PamirsTransactionTemplate(jtaTransactionManager).config(pamirsTransactional);
        }
        if (!StringUtils.isBlank(pamirsTransactional.transactionManager())) {
            return build(pamirsTransactional.transactionManager()).config(pamirsTransactional);
        }
        return build(true).config(pamirsTransactional);
    }

    public static PamirsTransactionTemplate build(boolean enable) {
        if (!enable) {
            return PamirsNonTransactionTemplate.INSTANCE;
        }
        return new PamirsTransactionTemplate(determineManager());
    }

    public static PamirsTransactionTemplate build() {
        return new PamirsTransactionTemplate(determineManager());
    }

    public static PamirsTransactionTemplate build(String transactionManager) {
        return new PamirsTransactionTemplate(determineManager(transactionManager));
    }

    public static PamirsTransactionTemplate build(PlatformTransactionManager transactionManager) {
        return new PamirsTransactionTemplate(transactionManager);
    }

    public static PlatformTransactionManager determineManager() {
        return BeanDefinitionUtils.getBean(DataBeanNameConstants.TRANSACTION_MANAGER_BEAN_NAME, PlatformTransactionManager.class);
    }

    public static PlatformTransactionManager determineManager(String beanName) {
        if (!BeanDefinitionUtils.containsBean(beanName)) {
            return null;
        }
        return BeanDefinitionUtils.getBean(beanName, PlatformTransactionManager.class);
    }

}
