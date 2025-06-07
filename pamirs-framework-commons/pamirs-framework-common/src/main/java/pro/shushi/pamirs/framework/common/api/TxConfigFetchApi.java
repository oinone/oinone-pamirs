package pro.shushi.pamirs.framework.common.api;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.fun.TransactionConfig;

import java.lang.reflect.Method;

/**
 * 事务配置获取api
 * 2020/12/11 7:49 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI
public interface TxConfigFetchApi {

    TransactionConfig fetchTx(Method method, TransactionConfig transactionConfig);

}
