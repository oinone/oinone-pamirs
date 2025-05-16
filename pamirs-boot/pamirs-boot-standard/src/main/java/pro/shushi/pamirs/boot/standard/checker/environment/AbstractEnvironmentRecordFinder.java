package pro.shushi.pamirs.boot.standard.checker.environment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import pro.shushi.pamirs.boot.standard.checker.EnvironmentRecordFinder;
import pro.shushi.pamirs.boot.standard.entity.StartupEnvironmentInfo;
import pro.shushi.pamirs.boot.standard.utils.PlatformEnvironmentGenerator;
import pro.shushi.pamirs.meta.domain.PlatformEnvironment;
import pro.shushi.pamirs.meta.domain.PlatformEnvironmentHistoryRecord;
import pro.shushi.pamirs.meta.enmu.PlatformEnvironmentTypeEnum;

import javax.annotation.Resource;

/**
 * 环境
 * @author Gesi at 11:16 on 2024/11/29
 */
public abstract class AbstractEnvironmentRecordFinder implements EnvironmentRecordFinder {

    protected static final String DEFAULT_FINDER_NAME_SUFFIX = "EnvironmentRecordFinder";

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Environment environment;

    @Resource
    private ApplicationArguments arguments;

    protected String type() {
        String name = this.getClass().getSimpleName();
        if (name.endsWith(DEFAULT_FINDER_NAME_SUFFIX)) {
            name = name.substring(0, name.indexOf(DEFAULT_FINDER_NAME_SUFFIX)).toLowerCase();
        }
        return name;
    }

    protected Environment getEnvironment() {
        return environment;
    }

    protected PlatformEnvironment generatorEnvironmentProperty(String key, String value) {
        return PlatformEnvironmentGenerator.newInstance(type(), key, value);
    }

    protected PlatformEnvironmentHistoryRecord generatorEnvironmentHistory(PlatformEnvironment environment, String currentValue, String historyValue, PlatformEnvironmentTypeEnum alterType) {
        PlatformEnvironmentHistoryRecord platformEnvironmentHistoryRecord = new PlatformEnvironmentHistoryRecord();
        platformEnvironmentHistoryRecord.setStartupCode(StartupEnvironmentInfo.getCurrentStartupCode(this.environment, this.arguments));
        platformEnvironmentHistoryRecord.setHistoryValue(historyValue);
        platformEnvironmentHistoryRecord.setAlterType(alterType);
        platformEnvironmentHistoryRecord.setEnvironmentType(environment.getType());
        platformEnvironmentHistoryRecord.setEnvironmentCode(environment.getCode());
        platformEnvironmentHistoryRecord.setEnvironmentKey(environment.getKey());
        platformEnvironmentHistoryRecord.setCurrentValue(currentValue);
        return platformEnvironmentHistoryRecord;
    }

    protected PlatformEnvironmentHistoryRecord generatorEnvironmentHistory(String type, String code, String key, String currentValue, String historyValue, PlatformEnvironmentTypeEnum alterType) {
        PlatformEnvironmentHistoryRecord platformEnvironmentHistoryRecord = new PlatformEnvironmentHistoryRecord();
        platformEnvironmentHistoryRecord.setStartupCode(StartupEnvironmentInfo.getCurrentStartupCode(this.environment, this.arguments));
        platformEnvironmentHistoryRecord.setHistoryValue(historyValue);
        platformEnvironmentHistoryRecord.setAlterType(alterType);
        platformEnvironmentHistoryRecord.setEnvironmentType(type);
        platformEnvironmentHistoryRecord.setEnvironmentCode(code);
        platformEnvironmentHistoryRecord.setEnvironmentKey(key);
        platformEnvironmentHistoryRecord.setCurrentValue(currentValue);
        return platformEnvironmentHistoryRecord;
    }

    protected String getProperty(String key) {
        return getProperty(key, null);
    }

    protected String getProperty(String key, String defaultValue) {
        if (environment instanceof ConfigurableEnvironment) {
            ConfigurableEnvironment configurableEnvironment = (ConfigurableEnvironment) environment;
            ConfigurableConversionService configurableConversionService = configurableEnvironment.getConversionService();
            MutablePropertySources propertySources = configurableEnvironment.getPropertySources();
            for (PropertySource<?> propertySource : propertySources) {
                Object value = propertySource.getProperty(key);
                if (value != null) {
                    if (value instanceof String) {
                        return (String) value;
                    }
                    return convertValueIfNecessary(configurableConversionService, value);
                }
            }
            return defaultValue;
        }
        return environment.getProperty(key, String.class, defaultValue);
    }

    protected String convertValueIfNecessary(ConfigurableConversionService configurableConversionService, Object value) {
        return configurableConversionService.convert(value, String.class);
    }

}
