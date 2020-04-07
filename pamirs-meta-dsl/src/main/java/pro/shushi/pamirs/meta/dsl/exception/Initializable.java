/**
 * 
 */
package pro.shushi.pamirs.meta.dsl.exception;

public interface Initializable {
	String	PHASE_NAME	= "initialise";

	void initialise() throws InitialException;
}
