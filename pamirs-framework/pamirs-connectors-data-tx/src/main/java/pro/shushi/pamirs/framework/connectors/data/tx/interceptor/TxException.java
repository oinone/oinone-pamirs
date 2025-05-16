package pro.shushi.pamirs.framework.connectors.data.tx.interceptor;

/**
 * 事务切面异常
 * 2020/7/7 6:31 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class TxException extends RuntimeException {

    private static final long serialVersionUID = 2882690268664621591L;

    public TxException() {
        super();
    }

    public TxException(Throwable t) {
        super(t);
    }

}
