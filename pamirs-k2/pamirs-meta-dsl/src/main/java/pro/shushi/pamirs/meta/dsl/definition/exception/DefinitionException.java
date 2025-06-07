package pro.shushi.pamirs.meta.dsl.definition.exception;

public class DefinitionException extends RuntimeException {

    private static final long serialVersionUID = 6636435474408453145L;

    public DefinitionException(String message) {
        super(message);
    }

    public DefinitionException(String message, Throwable e) {
        super(message, e);
    }
}
