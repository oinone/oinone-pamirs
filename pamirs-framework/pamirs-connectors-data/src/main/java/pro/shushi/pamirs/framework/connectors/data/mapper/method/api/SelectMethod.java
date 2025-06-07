package pro.shushi.pamirs.framework.connectors.data.mapper.method.api;

/**
 * @author Adamancy Zhang at 12:16 on 2023-06-26
 */
public interface SelectMethod extends SQLMethod {

    SelectMethod setOnlyColumn(boolean onlyColumn);

    SelectMethod setQueryWrapper(boolean queryWrapper);

    SelectMethod setWithPk(boolean withPk);

    SelectMethod setKeyConflict(boolean keyConflict);

    SelectMethod setUseOptimisticLocker(boolean useOptimisticLocker);

    SelectMethod setOptimisticLockerPrefix(String optimisticLockerPrefix);

    SelectMethod setNonEmptyUniqueKey(String[] nonEmptyUniqueKey);

    SelectMethod setPkPrefix(String pkPrefix);

    String table();

    String sqlSelect();

    String sqlCount();

    String sqlSegment();

    String sqlPk();

    String sqlPks();

    String sqlUnique();

    String sqlUniques();
}
