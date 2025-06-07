package pro.shushi.pamirs.middleware.schedule.core.verification.definition.impl;

import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.middleware.schedule.core.verification.api.IParamVerificationStrategyDefinition;
import pro.shushi.pamirs.middleware.schedule.core.verification.api.IParamsVerificationDefinition;
import pro.shushi.pamirs.middleware.schedule.core.verification.definition.AbstractParamVerificationDefinition;
import pro.shushi.pamirs.middleware.schedule.core.verification.definition.AbstractParamVerificationStrategyDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * @author Adamancy Zhang
 * @date 2020-10-20 19:24
 */
@SuppressWarnings("rawtypes")
public class CollectionParamVerificationDefinition<T> extends AbstractParamVerificationDefinition<Collection<T>> implements IParamsVerificationDefinition<Collection<T>> {

    private final List<IParamsVerificationDefinition<T>> childrenParamVerificationDefinition;

    public CollectionParamVerificationDefinition() {
        this(null);
    }

    public CollectionParamVerificationDefinition(String key) {
        super(key, Collection.class, new HashSet<String>() {{
            this.add(Collection.class.getName());
        }});
        childrenParamVerificationDefinition = new ArrayList<>();
    }

    public void addChildParamVerificationDefinition(IParamsVerificationDefinition<T> childParamVerificationDefinition) {
        childrenParamVerificationDefinition.add(childParamVerificationDefinition);
    }

    @Override
    public boolean verify(Collection<T> value) {
        if (super.verify(value)) {
            if (!childrenParamVerificationDefinition.isEmpty()) {
                for (T item : value) {
                    for (IParamsVerificationDefinition<T> verificationDefinition : childrenParamVerificationDefinition) {
                        verificationDefinition.verify(item);
                    }
                }
            }
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public void afterProperties() {
        super.afterProperties();
        for (IParamsVerificationDefinition<T> verificationDefinition : childrenParamVerificationDefinition) {
            verificationDefinition.afterProperties();
        }
    }

    public static class NotEmpty<T> extends AbstractParamVerificationStrategyDefinition<Collection<T>> implements IParamVerificationStrategyDefinition<Collection<T>> {

        private static final NotEmpty<?> INSTANCE = new NotEmpty<>();

        private NotEmpty() {
            //reject create object
        }

        public static <T> NotEmpty<T> getInstance() {
            //noinspection unchecked
            return (NotEmpty<T>) INSTANCE;
        }

        @Override
        protected boolean verifyHandler(Collection value) {
            return CollectionUtils.isNotEmpty(value);
        }

        @Override
        public int priority() {
            return 1;
        }

        @Override
        public String getStrategyTip() {
            return "not empty";
        }
    }
}
