package pro.shushi.pamirs.middleware.schedule.core.verification.definition.impl;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.middleware.schedule.core.verification.api.IParamVerificationStrategyDefinition;
import pro.shushi.pamirs.middleware.schedule.core.verification.api.IParamsVerificationDefinition;
import pro.shushi.pamirs.middleware.schedule.core.verification.definition.AbstractParamVerificationDefinition;
import pro.shushi.pamirs.middleware.schedule.core.verification.definition.AbstractParamVerificationStrategyDefinition;

import java.util.HashSet;

/**
 * @author Adamancy Zhang
 * @date 2020-10-20 14:06
 */
public class StringParamVerificationDefinition extends AbstractParamVerificationDefinition<String> implements IParamsVerificationDefinition<String> {

    public StringParamVerificationDefinition() {
        this(null);
    }

    public StringParamVerificationDefinition(String key) {
        super(key, String.class, new HashSet<String>() {{
            this.add(String.class.getName());
        }});
    }

    public static class NotBlank extends AbstractParamVerificationStrategyDefinition<String> implements IParamVerificationStrategyDefinition<String> {

        private static final NotBlank INSTANCE = new NotBlank();

        private NotBlank() {
            //reject create object
        }

        public static NotBlank getInstance() {
            return INSTANCE;
        }

        @Override
        protected boolean verifyHandler(String value) {
            return StringUtils.isNotBlank(value);
        }

        @Override
        public int priority() {
            return 1;
        }

        @Override
        public String getStrategyTip() {
            return "not blank";
        }
    }

    public static class Size extends AbstractParamVerificationStrategyDefinition<String> implements IParamVerificationStrategyDefinition<String> {

        private final int size;

        public Size(int size) {
            this.size = size;
        }

        @Override
        protected boolean verifyHandler(String value) {
            if (value == null) {
                return Boolean.TRUE;
            }
            return value.length() <= size;
        }

        @Override
        public int priority() {
            return 3;
        }

        @Override
        public String getStrategyTip() {
            return "max size: " + size;
        }
    }
}
