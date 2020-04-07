package pro.shushi.pamirs.meta.dsl.exception;

public class MachineException extends RuntimeException {

	private static final long serialVersionUID = -1331218166376247396L;

	public MachineException(String message) {
		super(message);
	}
	
	public MachineException(String message, Throwable cause) {
		super(message, cause);
	}
}
