package pro.shushi.pamirs.middleware.schedule.core.verification.definition.impl;

import pro.shushi.pamirs.middleware.schedule.core.verification.api.IParamVerificationStrategyDefinition;
import pro.shushi.pamirs.middleware.schedule.core.verification.api.IParamsVerificationDefinition;
import pro.shushi.pamirs.middleware.schedule.core.verification.definition.AbstractParamVerificationDefinition;
import pro.shushi.pamirs.middleware.schedule.core.verification.definition.AbstractParamVerificationStrategyDefinition;

import java.util.HashSet;

/**
 * @author Adamancy Zhang
 * @date 2020-10-20 18:37
 */
public class IntegerParamVerificationDefinition extends AbstractParamVerificationDefinition<Integer> implements IParamsVerificationDefinition<Integer> {

    public IntegerParamVerificationDefinition() {
        this(null);
    }

    public IntegerParamVerificationDefinition(String key) {
        super(key, Integer.class, new HashSet<String>() {{
            this.add(Integer.class.getName());
            this.add("int");
        }});
    }

    public static class Min extends AbstractParamVerificationStrategyDefinition<Integer> implements IParamVerificationStrategyDefinition<Integer> {

        private final int value;

        public Min(int value) {
            this.value = value;
        }

        @Override
        protected boolean verifyHandler(Integer value) {
            if (value == null) {
                return Boolean.FALSE;
            }
            return this.value <= value;
        }

        @Override
        public int priority() {
            return 2;
        }

        @Override
        public String getStrategyTip() {
            return "min value: " + value;
        }
    }

    public static class Max extends AbstractParamVerificationStrategyDefinition<Integer> implements IParamVerificationStrategyDefinition<Integer> {

        private final int value;

        public Max(int value) {
            this.value = value;
        }

        @Override
        protected boolean verifyHandler(Integer value) {
            if (value == null) {
                return Boolean.FALSE;
            }
            return value <= this.value;
        }

        @Override
        public int priority() {
            return 2;
        }

        @Override
        public String getStrategyTip() {
            return "max value: " + value;
        }
    }
}
