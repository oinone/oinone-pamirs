package pro.shushi.pamirs.middleware.schedule.core.verification.api;

import java.util.List;
import java.util.Set;

/**
 * param verification definition interface.
 *
 * @author Adamancy Zhang
 * @date 2020-10-20 13:10
 */
public interface IParamsVerificationDefinition<T> {

    /**
     * get param key
     *
     * @return param key
     */
    String getKey();

    /**
     * get value of class.
     *
     * @return value of class
     */
    Class<T> getClassType();

    /**
     * first call {@link IParamsVerificationDefinition#getClassType()}
     * if not match, use contain keys do match.
     * final call {@link IParamsVerificationDefinition#convertToValue(Object)}
     *
     * @return value class contain keys
     */
    Set<String> getContainKeys();

    /**
     * object convert to definition class type value
     *
     * @param object any object.
     * @return value
     */
    T convertToValue(Object object);

    /**
     * get strategy tip.
     *
     * @return tip
     */
    String getStrategyTip();

    /**
     * get strategy definition list.
     *
     * @return strategy definition list
     */
    List<IParamVerificationStrategyDefinition<T>> getStrategyDefinitions();

    /**
     * initialization finished call.
     */
    void afterProperties();

    /**
     * call verify method.
     *
     * @param value verified value
     * @return verification result
     */
    boolean verify(T value);
}
