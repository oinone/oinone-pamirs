package pro.shushi.pamirs.ux.grouping.configure;

import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.util.Optional;

/**
 * 分组配置
 *
 * @author Adamancy Zhang at 11:43 on 2025-11-21
 */
public class GroupingConfigure {

    private static final int DEFAULT_FULL_QUERY_COUNT = 200;

    private static final int DEFAULT_EXPENDED_ALL_COUNT = 200;

    private static final boolean DEFAULT_RELATION_MANY_SHOW_NULL = false;

    private GroupingConfigure() {
        // reject create object
    }

    public static int getFullQueryCount(String model) {
        return Optional.ofNullable(getTableConfiguration(model))
                .map(GroupingConfiguration.Table::getFullQueryCount)
                .orElse(DEFAULT_FULL_QUERY_COUNT);
    }

    public static int getExpendedAllCount(String model) {
        return Optional.ofNullable(getTableConfiguration(model))
                .map(GroupingConfiguration.Table::getExpendedAllCount)
                .orElse(DEFAULT_EXPENDED_ALL_COUNT);
    }

    public static boolean isRelationManyShowNull(String model) {
        return Optional.ofNullable(getTableConfiguration(model))
                .map(GroupingConfiguration.Table::getRelationManyShowNull)
                .orElse(DEFAULT_RELATION_MANY_SHOW_NULL);
    }

    private static GroupingConfiguration.Table getTableConfiguration(String model) {
        GroupingConfiguration groupingConfiguration = BeanDefinitionUtils.getBean(GroupingConfiguration.class);
        if (groupingConfiguration == null) {
            return null;
        }
        return Optional.ofNullable(groupingConfiguration.getModelTable())
                .map(v -> v.get(model))
                .orElse(groupingConfiguration.getTable());
    }
}
