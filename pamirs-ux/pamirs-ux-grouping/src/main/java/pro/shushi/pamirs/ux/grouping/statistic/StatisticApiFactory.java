package pro.shushi.pamirs.ux.grouping.statistic;

import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.ux.grouping.enumeration.GroupStatisticMethodEnum;
import pro.shushi.pamirs.ux.grouping.statistic.defaults.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计API工厂
 *
 * @author Adamancy Zhang at 14:32 on 2025-11-20
 */
@Slf4j
public class StatisticApiFactory {

    private static final class StatisticApiFactoryHolder {

        private static final StatisticApiFactory INSTANCE = init();

        private static StatisticApiFactory init() {
            StatisticApiFactory factory = new StatisticApiFactory();
            factory.register(GroupStatisticMethodEnum.COUNT.name(), StatisticCount.class);
            factory.register(GroupStatisticMethodEnum.NULL.name(), StatisticNull.class);
            factory.register(GroupStatisticMethodEnum.NOT_NULL.name(), StatisticNotNull.class);
            factory.register(GroupStatisticMethodEnum.NULL_PERCENT.name(), StatisticNullPercent.class);
            factory.register(GroupStatisticMethodEnum.NOT_NULL_PERCENT.name(), StatisticNotNullPercent.class);
            factory.register(GroupStatisticMethodEnum.MAX.name(), StatisticMax.class);
            factory.register(GroupStatisticMethodEnum.MIN.name(), StatisticMin.class);
            factory.register(GroupStatisticMethodEnum.UNIQUE.name(), StatisticUnique.class);
            factory.register(GroupStatisticMethodEnum.UNIQUE_PERCENT.name(), StatisticUniquePercent.class);
            factory.register(GroupStatisticMethodEnum.EARLIEST_TIME.name(), StatisticEarliestTime.class);
            factory.register(GroupStatisticMethodEnum.LATEST_TIME.name(), StatisticLatestTime.class);
            factory.register(GroupStatisticMethodEnum.TIME_RANGE_DAY.name(), StatisticTimeRangeDay.class);
            factory.register(GroupStatisticMethodEnum.TIME_RANGE_MONTH.name(), StatisticTimeRangeMonth.class);
            factory.register(GroupStatisticMethodEnum.TIME_RANGE_YEAR.name(), StatisticTimeRangeYear.class);
            factory.register(GroupStatisticMethodEnum.SUM.name(), StatisticSum.class);
            factory.register(GroupStatisticMethodEnum.AVERAGE.name(), StatisticAverage.class);
            factory.register(GroupStatisticMethodEnum.MEDIAN.name(), StatisticMedian.class);
            List<StatisticApiRegister> registers = BeanDefinitionUtils.getBeansOfTypeByOrdered(StatisticApiRegister.class);
            for (StatisticApiRegister register : registers) {
                register.register(factory);
            }
            return factory;
        }
    }

    private final Map<String, Class<?>> storage;

    public StatisticApiFactory() {
        this.storage = new HashMap<>();
    }

    public <API extends AbstractStatisticApi<?>> void register(String statisticMethod, Class<API> clazz) {
        storage.put(statisticMethod, clazz);
    }

    public <API extends AbstractStatisticApi<?>> void registerByModel(String statisticMethod, String model, Class<API> clazz) {
        storage.put(statisticMethod + CharacterConstants.SEPARATOR_OCTOTHORPE + model, clazz);
    }

    public <API extends AbstractStatisticApi<?>> void registerByField(String statisticMethod, String model, String field, Class<API> clazz) {
        storage.put(statisticMethod + CharacterConstants.SEPARATOR_OCTOTHORPE + model + CharacterConstants.SEPARATOR_OCTOTHORPE + field, clazz);
    }

    public <API extends AbstractStatisticApi<?>> void registerByTtype(String statisticMethod, String ttype, Class<API> clazz) {
        storage.put(statisticMethod + CharacterConstants.SEPARATOR_UNDERLINE + ttype, clazz);
    }

    public <API extends AbstractStatisticApi<?>> void registerByTtype(String statisticMethod, String model, String ttype, Class<API> clazz) {
        storage.put(statisticMethod + CharacterConstants.SEPARATOR_UNDERLINE + model + CharacterConstants.SEPARATOR_UNDERLINE + ttype, clazz);
    }

    @SuppressWarnings("unchecked")
    private <T> StatisticApi<T> selector(String statisticMethod, StatisticField statisticField) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Class<?> clazz = selector0(statisticMethod, statisticField);
        Constructor<?> constructor = clazz.getConstructor(StatisticField.class);
        return (StatisticApi<T>) constructor.newInstance(statisticField);
    }

    private Class<?> selector0(String statisticMethod, StatisticField statisticField) {
        String model = statisticField.getModel();
        String field = statisticField.getField();
        String ttype = statisticField.getTtype();
        Class<?> clazz = storage.get(statisticMethod + CharacterConstants.SEPARATOR_OCTOTHORPE + model + CharacterConstants.SEPARATOR_OCTOTHORPE + field);
        if (clazz != null) {
            return clazz;
        }
        clazz = storage.get(statisticMethod + CharacterConstants.SEPARATOR_UNDERLINE + model + CharacterConstants.SEPARATOR_UNDERLINE + ttype);
        if (clazz != null) {
            return clazz;
        }
        clazz = storage.get(statisticMethod + CharacterConstants.SEPARATOR_OCTOTHORPE + model);
        if (clazz != null) {
            return clazz;
        }
        clazz = storage.get(statisticMethod + CharacterConstants.SEPARATOR_UNDERLINE + ttype);
        if (clazz != null) {
            return clazz;
        }
        clazz = storage.get(statisticMethod);
        if (clazz == null) {
            throw new UnsupportedOperationException("Invalid statistic method. statistic: " + statisticMethod + ", model: " + model + ", field: " + field);
        }
        return clazz;
    }

    public static <T> StatisticApi<T> getApi(String statisticMethod, StatisticField statisticField) {
        try {
            return StatisticApiFactoryHolder.INSTANCE.selector(statisticMethod, statisticField);
        } catch (Throwable e) {
            log.error("get statistic api instance error.", e);
            return null;
        }
    }
}
