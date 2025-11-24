package pro.shushi.pamirs.grouping.query;

/**
 * 表格分组查询策略
 *
 * @author Adamancy Zhang at 09:57 on 2025-11-24
 */
public class TableGroupingQueryStrategy {

    private boolean fetchAll;

    private boolean relationManyShowNull;

    public boolean isFetchAll() {
        return fetchAll;
    }

    public void setFetchAll(boolean fetchAll) {
        this.fetchAll = fetchAll;
    }

    public boolean isRelationManyShowNull() {
        return relationManyShowNull;
    }

    public void setRelationManyShowNull(boolean relationManyShowNull) {
        this.relationManyShowNull = relationManyShowNull;
    }
}
