package pro.shushi.pamirs.framework.compute.exception;

/**
 * 元数据计算异常
 *
 * @author Adamancy Zhang at 19:10 on 2025-02-21
 */
public class MetaDataComputeException extends RuntimeException {

    private static final long serialVersionUID = 6779544264836757431L;

    public MetaDataComputeException() {
    }

    public MetaDataComputeException(String message) {
        super(message);
    }

    public MetaDataComputeException(Throwable cause) {
        super(cause);
    }
}
