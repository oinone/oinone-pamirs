package pro.shushi.pamirs.middleware.schedule.core.verification.definition.impl;

import pro.shushi.pamirs.middleware.schedule.core.verification.api.IParamVerificationStrategyDefinition;
import pro.shushi.pamirs.middleware.schedule.core.verification.api.IParamsVerificationDefinition;
import pro.shushi.pamirs.middleware.schedule.core.verification.definition.AbstractParamVerificationDefinition;
import pro.shushi.pamirs.middleware.schedule.core.verification.definition.AbstractParamVerificationStrategyDefinition;

import java.util.HashSet;

/**
 * @author Adamancy Zhang
 * @date 2020-10-20 17:59
 */
public class ObjectParamVerificationDefinition extends AbstractParamVerificationDefinition<Object> implements IParamsVerificationDefinition<Object> {

    public ObjectParamVerificationDefinition() {
        this(null);
    }

    public ObjectParamVerificationDefinition(String key) {
        super(key, Object.class, new HashSet<String>() {{
            this.add(Object.class.getName());
        }});
    }

    public static class NotNull<T> extends AbstractParamVerificationStrategyDefinition<T> implements IParamVerificationStrategyDefinition<T> {

        private static final NotNull<?> INSTANCE = new NotNull<>();

        private NotNull() {
            //reject create object
        }

        public static <T> NotNull<T> getInstance() {
            //noinspection unchecked
            return (NotNull<T>) INSTANCE;
        }

        @Override
        protected boolean verifyHandler(Object value) {
            return value != null;
        }

        @Override
        public int priority() {
            return 1;
        }

        @Override
        public String getStrategyTip() {
            return "not null";
        }
    }
}
