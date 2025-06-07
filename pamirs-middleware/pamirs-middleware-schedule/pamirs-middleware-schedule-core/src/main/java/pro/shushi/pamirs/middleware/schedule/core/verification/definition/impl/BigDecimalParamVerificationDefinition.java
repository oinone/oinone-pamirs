package pro.shushi.pamirs.middleware.schedule.core.verification.definition.impl;

import pro.shushi.pamirs.middleware.schedule.core.verification.api.IParamVerificationStrategyDefinition;
import pro.shushi.pamirs.middleware.schedule.core.verification.api.IParamsVerificationDefinition;
import pro.shushi.pamirs.middleware.schedule.core.verification.definition.AbstractParamVerificationDefinition;
import pro.shushi.pamirs.middleware.schedule.core.verification.definition.AbstractParamVerificationStrategyDefinition;

import java.math.BigDecimal;
import java.util.HashSet;

/**
 * @author Adamancy Zhang
 * @date 2020-10-21 10:22
 */
public class BigDecimalParamVerificationDefinition extends AbstractParamVerificationDefinition<BigDecimal> implements IParamsVerificationDefinition<BigDecimal> {

    public BigDecimalParamVerificationDefinition() {
        this(null);
    }

    public BigDecimalParamVerificationDefinition(String key) {
        super(key, BigDecimal.class, new HashSet<>());
    }

    public static class Min extends AbstractParamVerificationStrategyDefinition<BigDecimal> implements IParamVerificationStrategyDefinition<BigDecimal> {

        private final BigDecimal value;

        public Min(BigDecimal value) {
            assert value != null;
            this.value = value;
        }

        @Override
        protected boolean verifyHandler(BigDecimal value) {
            if (value == null) {
                return Boolean.FALSE;
            }
            return this.value.compareTo(value) >= 0;
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

    public static class Max extends AbstractParamVerificationStrategyDefinition<BigDecimal> implements IParamVerificationStrategyDefinition<BigDecimal> {

        private final BigDecimal value;

        public Max(BigDecimal value) {
            assert value != null;
            this.value = value;
        }

        @Override
        protected boolean verifyHandler(BigDecimal value) {
            if (value == null) {
                return Boolean.FALSE;
            }
            return this.value.compareTo(value) <= 0;
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
