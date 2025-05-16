package pro.shushi.pamirs.middleware.schedule.core.verification.api;

/**
 * @author Adamancy Zhang
 * @date 2020-10-20 21:23
 */
public interface IParamsVerificationEnumeration {

    /**
     * get verification definition
     *
     * @return verification definition
     */
    IParamsVerificationDefinition<?> getVerificationDefinition();
}
