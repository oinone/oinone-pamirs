package pro.shushi.pamirs.core.common.exception;

/**
 * <h>不可比较异常</h>
 * <p>
 * 一般用于实现了{@link Comparable}接口的可比较对象中使用
 * </p>
 *
 * @author Adamancy Zhang on 2021-06-07 14:10
 */
public class IncomparableException extends RuntimeException {

    private static final long serialVersionUID = 6740629303431941231L;

    public IncomparableException(String message) {
        super(message);
    }
}
