package pro.shushi.pamirs.middleware.schedule.core.verification.api;


import pro.shushi.pamirs.middleware.schedule.core.verification.enumeration.ParamVerificationExceptionHandlerType;

import java.util.List;

/**
 * param verification strategy definition
 *
 * @author Adamancy Zhang
 * @date 2020-10-20 13:36
 */
public interface IParamVerificationStrategyDefinition<T> {

    /**
     * param verification strategy definition priority.(ascending order)
     *
     * @return priority value
     */
    int priority();

    /**
     * param verification exception handler types.
     *
     * @return {@link ParamVerificationExceptionHandlerType}
     */
    List<ParamVerificationExceptionHandlerType> getHandlerTypes();

    /**
     * param verification exception handler types value.
     *
     * @return {@link ParamVerificationExceptionHandlerType#intValue()}
     */
    int getHandlerTypesValue();

    /**
     * get strategy tip.
     *
     * @return tip
     */
    String getStrategyTip();

    /**
     * call verify method.
     *
     * @param key   param key
     * @param value verified value
     * @return verification result (while not throw exception)
     */
    boolean verify(String key, T value);
}
