package pro.shushi.pamirs.boot.standard.checker.environment;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import pro.shushi.pamirs.boot.standard.checker.PlatformEnvironmentChecker;
import pro.shushi.pamirs.boot.standard.checker.constants.EnvironmentCheckConstants;
import pro.shushi.pamirs.boot.standard.checker.helper.DefaultChecker;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentCheckContext;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentCheckResult;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentKey;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentKeySet;
import pro.shushi.pamirs.boot.standard.utils.PlatformEnvironmentGenerator;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.lambda.Getter;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;
import pro.shushi.pamirs.meta.domain.PlatformEnvironment;

import java.util.*;

/**
 * 抽象平台环境检查API
 *
 * @author Adamancy Zhang at 12:56 on 2024-10-11
 */
public abstract class AbstractPlatformEnvironmentChecker implements PlatformEnvironmentChecker {

    protected static final String DEFAULT_CHECKER_NAME_SUFFIX = "EnvironmentChecker";

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Environment environment;

    protected EnvironmentKeySet propertyKeys() {
        return EnvironmentKeySet.emptySet();
    }

    protected EnvironmentKeySet errorKeys() {
        return EnvironmentKeySet.emptySet();
    }

    protected EnvironmentKeySet warningKeys() {
        return EnvironmentKeySet.emptySet();
    }

    protected EnvironmentKeySet deprecatedKeys() {
        return EnvironmentKeySet.emptySet();
    }

    @Override
    public String type() {
        String name = this.getClass().getSimpleName();
        if (name.endsWith(DEFAULT_CHECKER_NAME_SUFFIX)) {
            name = name.substring(0, name.indexOf(DEFAULT_CHECKER_NAME_SUFFIX)).toLowerCase();
        }
        return name;
    }

    @Override
    public List<PlatformEnvironment> collection() {
        Set<EnvironmentKey> keys = new LinkedHashSet<>(propertyKeys());
        keys.addAll(errorKeys());
        keys.addAll(warningKeys());
        keys.addAll(deprecatedKeys());
        List<PlatformEnvironment> environments = new ArrayList<>(keys.size());
        for (EnvironmentKey environmentKey : keys) {
            String key = environmentKey.getKey();
            PlatformEnvironment environment = generatorEnvironmentProperty(key);
            if (environment != null) {
                EnvironmentKey.Checker checker = environmentKey.getChecker();
                if (checker != null) {
                    environment = checker.convert(environment);
                }
                environments.add(environment);
            }
        }
        return environments;
    }

    @Override
    public EnvironmentCheckResult deprecated(PlatformEnvironment environment) {
        EnvironmentKeySet keys = deprecatedKeys();
        EnvironmentKey key = keys.get(environment.getKey());
        if (key != null) {
            return EnvironmentCheckResult.of(key, environment);
        }
        return null;
    }

    @Override
    public List<PlatformEnvironment> check(EnvironmentCheckContext context, List<PlatformEnvironment> currentEnvironments, List<PlatformEnvironment> historyEnvironments) {
        if (historyEnvironments == null) {
            historyEnvironments = Collections.emptyList();
        }
        Map<String, PlatformEnvironment> historyEnvironmentCache = new LinkedHashMap<>(historyEnvironments.size());
        Iterator<PlatformEnvironment> historyEnvironmentIterator = historyEnvironments.iterator();
        List<PlatformEnvironment> finalEnvironments = new ArrayList<>();
        for (PlatformEnvironment currentEnvironment : currentEnvironments) {
            String code = currentEnvironment.getCode();
            PlatformEnvironment historyEnvironment = findEnvironment(historyEnvironmentCache, historyEnvironmentIterator, code);
            appendCompareResult(context, finalEnvironments, compare(context, currentEnvironment, historyEnvironment));
        }
        fillDiffEnvironment(historyEnvironmentCache, historyEnvironmentIterator);
        for (PlatformEnvironment historyEnvironment : historyEnvironmentCache.values()) {
            appendCompareResult(context, finalEnvironments, compare(context, null, historyEnvironment));
        }
        return finalEnvironments;
    }

    protected void appendCompareResult(EnvironmentCheckContext context, List<PlatformEnvironment> finalEnvironments, PlatformEnvironment environment) {
        if (environment == null) {
            return;
        }
        EnvironmentKey environmentKey = context.getKey(environment);
        if (EnvironmentKey.Level.isAllowSaveEnvironment(environmentKey.getLevel())) {
            finalEnvironments.add(environment);
        }
    }

    protected PlatformEnvironment compare(EnvironmentCheckContext context, PlatformEnvironment currentEnvironment, PlatformEnvironment historyEnvironment) {
        if (historyEnvironment == null) {
            if (createNewEnvironment(context, currentEnvironment)) {
                return currentEnvironment;
            }
            return null;
        }
        if (currentEnvironment == null) {
            if (deleteHistoryEnvironment(context, historyEnvironment)) {
                return null;
            }
            return historyEnvironment;
        }
        String oldValue = historyEnvironment.getValue();
        String newValue = currentEnvironment.getValue();
        if (StringUtils.isAllBlank(oldValue, newValue)) {
            return null;
        }
        EnvironmentKey key = context.getKey(currentEnvironment);
        EnvironmentKey.Checker checker = key.getChecker();
        if (checker != null) {
            return checker.check(context, currentEnvironment, historyEnvironment);
        }
        return DefaultChecker.INSTANCE.check(context, currentEnvironment, historyEnvironment);
    }

    protected boolean createNewEnvironment(EnvironmentCheckContext context, PlatformEnvironment currentEnvironment) {
        EnvironmentKey key = context.getKey(currentEnvironment);
        EnvironmentKey.Checker checker = key.getChecker();
        if (checker != null) {
            currentEnvironment = checker.checkNewEnvironment(context, currentEnvironment);
        }
        if (currentEnvironment == null) {
            return false;
        }
        if (EnvironmentKey.Level.isAllowSaveEnvironment(key.getLevel())) {
            addCreate(context, currentEnvironment);
            return true;
        }
        return false;
    }

    protected boolean deleteHistoryEnvironment(EnvironmentCheckContext context, PlatformEnvironment historyEnvironment) {
        EnvironmentKey key = context.getKey(historyEnvironment);
        EnvironmentKey.Checker checker = key.getChecker();
        if (checker != null) {
            historyEnvironment = checker.checkDeleteEnvironment(context, historyEnvironment);
        }
        if (historyEnvironment == null) {
            return false;
        }
        EnvironmentKey.Level level = key.getLevel();
        if (EnvironmentKey.Level.NONE.equals(level)) {
            return false;
        }
        if (EnvironmentKey.Level.isNotAllowDeleteEnvironment(level)) {
            addError(context, historyEnvironment, getErrorMessage(key.getMessage(), EnvironmentCheckConstants.IMMUTABLE_TIP + historyEnvironment.getValue()));
        } else {
            addDelete(context, historyEnvironment);
        }
        return true;
    }

    protected String getErrorMessage(String message, String defaultMessage) {
        if (StringUtils.isBlank(message)) {
            return defaultMessage;
        }
        return message;
    }

    protected PlatformEnvironment generatorEnvironmentProperty(String key) {
        String value;
        try {
            value = getProperty(key);
        } catch (Throwable e) {
            log.error("get property value error. key: {}", key, e);
            return null;
        }
        if (StringUtils.isBlank(value)) {
            value = getEnvironmentDefaultValue(key);
        }
        return generatorEnvironmentProperty(key, value);
    }

    protected PlatformEnvironment generatorEnvironmentProperty(String key, String value) {
        return PlatformEnvironmentGenerator.newInstance(type(), key, value);
    }

    protected String getEnvironmentDefaultValue(String key) {
        return EnvironmentKey.of(key).getDefaultValue();
    }

    protected PlatformEnvironment generatorEnvironmentProperty(String key, Object value) {
        return generatorEnvironmentProperty(key, convertPropertyStringValue(value));
    }

    protected PlatformEnvironment generatorImmutableEnvironmentProperty(String key, Object value) {
        PlatformEnvironment environment = generatorEnvironmentProperty(key, convertPropertyStringValue(value));
        EnvironmentKey.immutable(environment.getKey());
        return environment;
    }

    protected <T> PlatformEnvironment generatorEnvironmentProperty(String keyPrefix, T object, Getter<T, ?> getter) {
        return generatorEnvironmentProperty(keyPrefix + camelCase2Hyphen(LambdaUtil.fetchFieldName(getter)), getter.apply(object));
    }

    protected <T> PlatformEnvironment generatorImmutableEnvironmentProperty(String keyPrefix, T object, Getter<T, ?> getter) {
        PlatformEnvironment environment = generatorEnvironmentProperty(keyPrefix, object, getter);
        EnvironmentKey.immutable(environment.getKey());
        return environment;
    }

    private String convertPropertyStringValue(Object value) {
        String stringValue;
        if (value == null) {
            stringValue = null;
        } else if (value instanceof String) {
            stringValue = (String) value;
        } else {
            stringValue = JSON.toJSONString(value);
        }
        return stringValue;
    }

    private String camelCase2Hyphen(String fieldName) {
        int len = fieldName.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = fieldName.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                sb.append(CharacterConstants.SEPARATOR_HYPHEN);
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }

    protected String getProperty(String key) {
        return getProperty(key, null);
    }

    protected String getProperty(String key, String defaultValue) {
        String value = null;
        try {
            value = environment.getProperty(key, String.class, defaultValue);
        } catch (IllegalArgumentException e) {
            if (environment instanceof ConfigurableEnvironment) {
                ConfigurableEnvironment configurableEnvironment = (ConfigurableEnvironment) environment;
                ConfigurableConversionService configurableConversionService = configurableEnvironment.getConversionService();
                MutablePropertySources propertySources = configurableEnvironment.getPropertySources();
                for (PropertySource<?> propertySource : propertySources) {
                    Object objectValue = propertySource.getProperty(key);
                    if (objectValue != null) {
                        if (objectValue instanceof String) {
                            return (String) objectValue;
                        }
                        return convertValueIfNecessary(configurableConversionService, objectValue);
                    }
                }
                return defaultValue;
            }
        }
        return value;
    }

    protected String getPropertyWithDefaultValue(String key) {
        String value = getProperty(key);
        if (value == null) {
            value = getEnvironmentDefaultValue(key);
        }
        return value;
    }

    protected String convertValueIfNecessary(ConfigurableConversionService configurableConversionService, Object value) {
        return configurableConversionService.convert(value, String.class);
    }

    protected PlatformEnvironment findEnvironment(Map<String, PlatformEnvironment> cache, Iterator<PlatformEnvironment> iterator, String code) {
        PlatformEnvironment result = cache.remove(code);
        if (result == null) {
            while (iterator.hasNext()) {
                PlatformEnvironment target = iterator.next();
                if (code.equals(target.getCode())) {
                    result = target;
                    break;
                }
                cache.put(target.getCode(), target);
            }
        }
        return result;
    }

    protected PlatformEnvironment findEnvironmentByKey(List<PlatformEnvironment> environments, String key) {
        for (PlatformEnvironment environment : environments) {
            if (key.equals(environment.getKey())) {
                return environment;
            }
        }
        return null;
    }

    protected void fillDiffEnvironment(Map<String, PlatformEnvironment> cache, Iterator<PlatformEnvironment> iterator) {
        while (iterator.hasNext()) {
            PlatformEnvironment target = iterator.next();
            cache.put(target.getCode(), target);
        }
    }

    protected final EnvironmentKeySet newEnvironmentKeySet(EnvironmentKey.Level defaultLevel, Object... elements) {
        EnvironmentKeySet set = new EnvironmentKeySet(elements.length);
        for (Object element : elements) {
            if (element instanceof String) {
                switch (defaultLevel) {
                    case IMMUTABLE:
                        set.add(EnvironmentKey.immutable((String) element));
                        break;
                    case ADD:
                        set.add(EnvironmentKey.add((String) element));
                        break;
                    case ADD_OR_DELETE:
                        set.add(EnvironmentKey.addOrDelete((String) element));
                        break;
                    case ERROR:
                        set.add(EnvironmentKey.error((String) element));
                        break;
                    case WARNING:
                        set.add(EnvironmentKey.warning((String) element));
                        break;
                    case DEPRECATED:
                        set.add(EnvironmentKey.deprecated((String) element));
                        break;
                }
            } else if (element instanceof EnvironmentKey) {
                set.add((EnvironmentKey) element);
            } else if (element instanceof EnvironmentKeySet) {
                set.addAll((EnvironmentKeySet) element);
            } else {
                throw new IllegalArgumentException("Invalid environment key. The type must be String, EnvironmentKey or EnvironmentKeySet. class: " + element.getClass());
            }
        }
        return set;
    }
}
