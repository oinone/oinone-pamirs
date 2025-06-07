/**
 *
 */
package pro.shushi.pamirs.meta.dsl.exception;


public class InitialException extends Exception {
    public String resolveDescription;

    public String getResolveDescription() {
        return resolveDescription;
    }

    public void setResolveDescription(String resolveDescription) {
        this.resolveDescription = resolveDescription;
    }

    /**
     *
     */
    private static final long serialVersionUID = 8239390974971987256L;

    public InitialException() {
        super();
    }

    public InitialException(String message, Throwable cause) {
        super(message, cause);
    }

    public InitialException(String message) {
        super(message);
    }

    public InitialException(Throwable cause) {
        super(cause);
    }

}
