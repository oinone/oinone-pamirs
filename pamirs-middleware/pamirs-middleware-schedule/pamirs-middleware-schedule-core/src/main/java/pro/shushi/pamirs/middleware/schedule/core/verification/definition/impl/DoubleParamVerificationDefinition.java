package pro.shushi.pamirs.middleware.schedule.core.verification.definition.impl;

import pro.shushi.pamirs.middleware.schedule.core.verification.api.IParamVerificationStrategyDefinition;
import pro.shushi.pamirs.middleware.schedule.core.verification.api.IParamsVerificationDefinition;
import pro.shushi.pamirs.middleware.schedule.core.verification.definition.AbstractParamVerificationDefinition;
import pro.shushi.pamirs.middleware.schedule.core.verification.definition.AbstractParamVerificationStrategyDefinition;

import java.util.HashSet;

/**
 * @author Adamancy Zhang
 * @date 2020-10-20 22:30
 */
public class DoubleParamVerificationDefinition extends AbstractParamVerificationDefinition<Double> implements IParamsVerificationDefinition<Double> {

    public DoubleParamVerificationDefinition() {
        this(null);
    }

    public DoubleParamVerificationDefinition(String key) {
        super(key, Double.class, new HashSet<String>() {{
            this.add("double");
        }});
    }

    public static class Min extends AbstractParamVerificationStrategyDefinition<Double> implements IParamVerificationStrategyDefinition<Double> {

        private final double value;

        public Min(double value) {
            this.value = value;
        }

        @Override
        protected boolean verifyHandler(Double value) {
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

    public static class Max extends AbstractParamVerificationStrategyDefinition<Double> implements IParamVerificationStrategyDefinition<Double> {

        private final double value;

        public Max(double value) {
            this.value = value;
        }

        @Override
        protected boolean verifyHandler(Double value) {
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
