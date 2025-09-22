package pro.shushi.pamirs.eip.api.strategy.context;

import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;
import pro.shushi.pamirs.eip.api.strategy.config.PamirsEipLogProperties;
import pro.shushi.pamirs.eip.api.strategy.entity.EipLogStrategyEntity;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * EIP 日志策略上下文
 *
 * @author Adamancy Zhang at 17:03 on 2025-08-16
 */
public class EipLogStrategyContext {

    private static final Map<String, EipLogStrategyEntity> CACHE = new ConcurrentHashMap<>();

    public static EipLogStrategyEntity get(InterfaceTypeEnum type, String interfaceName) {
        EipLogStrategyEntity logStrategy = CACHE.get(type.value() + CharacterConstants.SEPARATOR_OCTOTHORPE + interfaceName);
        if (logStrategy == null) {
            logStrategy = defaultLogStrategy();
        }
        return logStrategy;
    }

    public static EipLogStrategyEntity getAllowNullable(InterfaceTypeEnum type, String interfaceName) {
        return CACHE.get(type.value() + CharacterConstants.SEPARATOR_OCTOTHORPE + interfaceName);
    }

    public static EipLogStrategyEntity getOrCreate(InterfaceTypeEnum type, String interfaceName) {
        EipLogStrategyEntity logStrategy = getAllowNullable(type, interfaceName);
        if (logStrategy == null) {
            logStrategy = defaultLogStrategy();
            logStrategy.setInterfaceType(type);
            logStrategy.setInterfaceName(interfaceName);
            put(type, interfaceName, logStrategy);
        }
        return logStrategy;
    }

    public static List<EipLogStrategyEntity> values() {
        return new ArrayList<>(CACHE.values());
    }

    public static void put(InterfaceTypeEnum type, String interfaceName, EipLogStrategyEntity logStrategy) {
        CACHE.put(type.value() + CharacterConstants.SEPARATOR_OCTOTHORPE + interfaceName, logStrategy);
    }

    public static void remove(InterfaceTypeEnum type, String interfaceName) {
        CACHE.remove(type.value() + CharacterConstants.SEPARATOR_OCTOTHORPE + interfaceName);
    }

    public static EipLogStrategyEntity defaultLogStrategy() {
        double frequency = PamirsEipLogProperties.DEFAULT_FREQUENCY;
        PamirsEipLogProperties logProperties = BeanDefinitionUtils.getBean(PamirsEipLogProperties.class);
        if (logProperties != null) {
            frequency = logProperties.getFrequency();
        }
        EipLogStrategyEntity logStrategy = new EipLogStrategyEntity();
        logStrategy.setEnabled(true);
        logStrategy.setFrequency(frequency);
        return logStrategy;
    }
}
