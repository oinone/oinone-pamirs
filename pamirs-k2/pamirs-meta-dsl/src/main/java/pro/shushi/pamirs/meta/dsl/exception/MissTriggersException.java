package pro.shushi.pamirs.meta.dsl.exception;

public class MissTriggersException extends MachineException {
    /**
     *
     */
    private static final long serialVersionUID = -5341864139732571507L;

    public MissTriggersException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissTriggersException(String message) {
        super(message);
    }
}
