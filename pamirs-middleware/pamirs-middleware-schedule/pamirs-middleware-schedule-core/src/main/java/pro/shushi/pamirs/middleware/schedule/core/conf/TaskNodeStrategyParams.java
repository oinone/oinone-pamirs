package pro.shushi.pamirs.middleware.schedule.core.conf;

import pro.shushi.pamirs.middleware.schedule.core.verification.api.IParamsVerificationDefinition;
import pro.shushi.pamirs.middleware.schedule.core.verification.api.IParamsVerificationEnumeration;
import pro.shushi.pamirs.middleware.schedule.core.verification.definition.impl.CollectionParamVerificationDefinition;
import pro.shushi.pamirs.middleware.schedule.core.verification.definition.impl.IntegerParamVerificationDefinition;
import pro.shushi.pamirs.middleware.schedule.core.verification.definition.impl.ObjectParamVerificationDefinition;
import pro.shushi.pamirs.middleware.schedule.core.verification.definition.impl.StringParamVerificationDefinition;

/**
 * @author Adamancy Zhang
 * @date 2020-10-20 13:10
 */
public enum TaskNodeStrategyParams implements IParamsVerificationEnumeration {

    /**
     * IPList param definition
     */
    IP_LIST(new CollectionParamVerificationDefinition<String>("IPList") {{
        this.addStrategyDefinition(CollectionParamVerificationDefinition.NotEmpty.getInstance());
        this.addChildParamVerificationDefinition(new StringParamVerificationDefinition() {{
            this.addStrategyDefinition(StringParamVerificationDefinition.NotBlank.getInstance());
        }});
        this.afterProperties();
    }}),

    /**
     * numOfSingleServer param definition
     */
    NUM_OF_SINGLE_SERVER(new IntegerParamVerificationDefinition("numOfSingleServer") {{
        this.addStrategyDefinition(new IntegerParamVerificationDefinition.Min(0));
        this.afterProperties();
    }}),

    /**
     * assignNum param definition
     */
    ASSIGN_NUM(new IntegerParamVerificationDefinition("assignNum") {{
        this.addStrategyDefinition(new IntegerParamVerificationDefinition.Min(1));
        this.afterProperties();
    }}),

    /**
     * kind param definition
     */
    KIND(new StringParamVerificationDefinition("kind") {{
        this.addStrategyDefinition(StringParamVerificationDefinition.NotBlank.getInstance());
        this.afterProperties();
    }}),

    /**
     * taskParameter param definition
     */
    TASK_PARAMETER(new StringParamVerificationDefinition("taskParameter") {{
        this.addStrategyDefinition(ObjectParamVerificationDefinition.NotNull.getInstance());
        this.afterProperties();
    }}),

    /**
     * sts param definition
     */
    STS(new StringParamVerificationDefinition("sts") {{
        this.addStrategyDefinition(StringParamVerificationDefinition.NotBlank.getInstance());
        this.afterProperties();
    }});

    private final IParamsVerificationDefinition<?> verificationDefinition;

    TaskNodeStrategyParams(IParamsVerificationDefinition<?> verificationDefinition) {
        this.verificationDefinition = verificationDefinition;
    }

    @Override
    public IParamsVerificationDefinition<?> getVerificationDefinition() {
        return verificationDefinition;
    }
}
