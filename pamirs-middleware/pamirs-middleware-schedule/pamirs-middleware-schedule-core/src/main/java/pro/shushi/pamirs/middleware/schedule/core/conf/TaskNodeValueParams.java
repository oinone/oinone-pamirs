package pro.shushi.pamirs.middleware.schedule.core.conf;

import pro.shushi.pamirs.middleware.schedule.core.verification.api.IParamsVerificationDefinition;
import pro.shushi.pamirs.middleware.schedule.core.verification.api.IParamsVerificationEnumeration;
import pro.shushi.pamirs.middleware.schedule.core.verification.definition.impl.*;

import java.math.BigDecimal;

/**
 * @author Adamancy Zhang
 * @date 2020-10-20 13:10
 */
public enum TaskNodeValueParams implements IParamsVerificationEnumeration {

    /**
     * heartBeatRate param definition
     */
    HEART_BEAT_RATE(new IntegerParamVerificationDefinition("heartBeatRate") {{
        this.addStrategyDefinition(ObjectParamVerificationDefinition.NotNull.getInstance());
        this.afterProperties();
    }}),

    /**
     * judgeDeadInterval param definition
     */
    JUDGE_DEAD_INTERVAL(new IntegerParamVerificationDefinition("judgeDeadInterval") {{
        this.addStrategyDefinition(ObjectParamVerificationDefinition.NotNull.getInstance());
        this.afterProperties();
    }}),

    /**
     * sleepTimeNoData param definition
     */
    SLEEP_TIME_NO_DATA(new IntegerParamVerificationDefinition("sleepTimeNoData") {{
        this.addStrategyDefinition(ObjectParamVerificationDefinition.NotNull.getInstance());
        this.afterProperties();
    }}),

    /**
     * sleepTimeInterval param definition
     */
    SLEEP_TIME_INTERVAL(new IntegerParamVerificationDefinition("sleepTimeInterval") {{
        this.addStrategyDefinition(ObjectParamVerificationDefinition.NotNull.getInstance());
        this.afterProperties();
    }}),

    /**
     * fetchDataNumber param definition
     */
    FETCH_DATA_NUMBER(new IntegerParamVerificationDefinition("fetchDataNumber") {{
        this.addStrategyDefinition(ObjectParamVerificationDefinition.NotNull.getInstance());
        this.afterProperties();
    }}),

    /**
     * executeNumber param definition
     */
    EXECUTE_NUMBER(new IntegerParamVerificationDefinition("executeNumber") {{
        this.addStrategyDefinition(new IntegerParamVerificationDefinition.Min(1));
        this.afterProperties();
    }}),

    /**
     * threadNumber param definition
     */
    THREAD_NUMBER(new IntegerParamVerificationDefinition("threadNumber") {{
        this.addStrategyDefinition(new IntegerParamVerificationDefinition.Min(1));
        this.afterProperties();
    }}),

    /**
     * processorType param definition
     */
    PROCESSOR_TYPE(new StringParamVerificationDefinition("processorType") {{
        this.addStrategyDefinition(StringParamVerificationDefinition.NotBlank.getInstance());
        this.afterProperties();
    }}),

    /**
     * expireOwnSignInterval param definition
     */
    EXPIRE_OWN_SIGN_INTERVAL(new BigDecimalParamVerificationDefinition("expireOwnSignInterval") {{
        this.addStrategyDefinition(new BigDecimalParamVerificationDefinition.Min(new BigDecimal(1)));
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
     * taskKind param definition
     */
    TASK_KIND(new StringParamVerificationDefinition("taskKind") {{
        this.addStrategyDefinition(StringParamVerificationDefinition.NotBlank.getInstance());
        this.afterProperties();
    }}),

    /**
     * taskItems param definition
     */
    TASK_ITEMS(new CollectionParamVerificationDefinition<Integer>("taskItems") {{
        this.addStrategyDefinition(CollectionParamVerificationDefinition.NotEmpty.getInstance());
        this.addChildParamVerificationDefinition(new IntegerParamVerificationDefinition() {{
            this.addStrategyDefinition(ObjectParamVerificationDefinition.NotNull.getInstance());
        }});
        this.afterProperties();
    }}),

    /**
     * maxTaskItemsOfOneThreadGroup param definition
     */
    MAX_TASK_ITEMS_OF_ONE_THREAD_GROUP(new IntegerParamVerificationDefinition("maxTaskItemsOfOneThreadGroup") {{
        this.addStrategyDefinition(new IntegerParamVerificationDefinition.Min(0));
        this.afterProperties();
    }}),

    /**
     * version param definition
     */
    version(new IntegerParamVerificationDefinition("version") {{
        this.addStrategyDefinition(new IntegerParamVerificationDefinition.Min(0));
        this.afterProperties();
    }}),

    /**
     * sts param definition
     */
    sts(new StringParamVerificationDefinition("sts") {{
        this.addStrategyDefinition(StringParamVerificationDefinition.NotBlank.getInstance());
        this.afterProperties();
    }});

    private final IParamsVerificationDefinition<?> verificationDefinition;

    TaskNodeValueParams(IParamsVerificationDefinition<?> verificationDefinition) {
        this.verificationDefinition = verificationDefinition;
    }

    @Override
    public IParamsVerificationDefinition<?> getVerificationDefinition() {
        return verificationDefinition;
    }
}
