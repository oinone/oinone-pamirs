package pro.shushi.pamirs.middleware.schedule.core.verification.definition;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.middleware.schedule.core.verification.api.IParamVerificationStrategyDefinition;
import pro.shushi.pamirs.middleware.schedule.core.verification.api.IParamsVerificationDefinition;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * @author Adamancy Zhang
 * @date 2020-10-20 15:57
 */
public abstract class AbstractParamVerificationDefinition<T> extends AbstractTipPrint implements IParamsVerificationDefinition<T> {

    private final String key;

    private final Class<?> classType;

    private final Set<String> containKeys;

    private String strategyTip;

    private final List<IParamVerificationStrategyDefinition<T>> strategyDefinitions;

    protected AbstractParamVerificationDefinition(String key, Class<?> classType, Set<String> containKeys) {
        this.key = key;
        this.classType = classType;
        this.containKeys = containKeys;
        this.strategyDefinitions = new ArrayList<>();
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public Class<T> getClassType() {
        //noinspection unchecked
        return (Class<T>) classType;
    }

    @Override
    public Set<String> getContainKeys() {
        return containKeys;
    }

    @Override
    public String getStrategyTip() {
        return strategyTip;
    }

    @Override
    public T convertToValue(Object object) {
        //noinspection unchecked
        return (T) object;
    }

    @Override
    public List<IParamVerificationStrategyDefinition<T>> getStrategyDefinitions() {
        return strategyDefinitions;
    }

    public void addStrategyDefinition(IParamVerificationStrategyDefinition<T> strategyDefinition) {
        this.strategyDefinitions.add(strategyDefinition);
    }

    @Override
    public void afterProperties() {
        this.strategyTip = generatorStrategyTip();
        if (key != null && StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("key is blank.");
        }
        if (classType == null) {
            throw new IllegalArgumentException("class type is null. key = " + key);
        }
        if (containKeys == null) {
            throw new IllegalArgumentException("contain keys is empty. key = " + key);
        }
        if (strategyDefinitions.isEmpty()) {
            throw new IllegalArgumentException("strategy definitions is empty. key = " + key);
        }
        strategyDefinitions.sort(Comparator.comparingInt(IParamVerificationStrategyDefinition::priority));
    }

    @Override
    public boolean verify(T value) {
        Class<T> cls = getClassType();
        if (value != null) {
            Class<?> clz = value.getClass();
            boolean isVerifiable = Boolean.FALSE;
            if (cls.isAssignableFrom(clz)) {
                isVerifiable = Boolean.TRUE;
            } else {
                //Processing for compatibility with value types
                if (containKeys.contains(clz.getName())) {
                    value = convertToValue(value);
                    isVerifiable = Boolean.TRUE;
                }
            }
            if (!isVerifiable) {
                throw new IllegalArgumentException("Invalid param type. key = " + key);
            }
        }
        for (IParamVerificationStrategyDefinition<T> strategyDefinition : strategyDefinitions) {
            if (!strategyDefinition.verify(key, value)) {
                print();
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    @Override
    protected void print(String text) {
        logger.error(text);
    }

    protected String generatorStrategyTip() {
        int number = 1;
        for (IParamVerificationStrategyDefinition<T> strategyDefinition : strategyDefinitions) {
            appendLine(generatorStrategyTip(number, strategyDefinition.getStrategyTip()));
            number++;
        }
        return getTipText();
    }

    protected String generatorStrategyTip(int number, String tip) {
        return number + ". " + tip;
    }
}
