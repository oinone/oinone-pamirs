package pro.shushi.pamirs.meta.dsl.signal;

public class SignalException extends RuntimeException{

	private static final long	serialVersionUID	= 5633739384456798707L;

	public SignalException(String message) {
		super(message);
	}
	
	public SignalException(String message, Throwable cause) {
		super(message, cause);
	}
}
