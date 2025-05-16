package pro.shushi.pamirs.framework.connectors.data.sql.segments;

import pro.shushi.pamirs.framework.connectors.data.sql.ISqlSegment;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.Arrays;
import java.util.List;

/**
 * 合并 SQL 片段
 */
public class MergeSegments implements ISqlSegment {

    private static final long serialVersionUID = -3667665203940628021L;

    private final NormalSegmentList normal = new NormalSegmentList();
    private final GroupBySegmentList groupBy = new GroupBySegmentList();
    private final HavingSegmentList having = new HavingSegmentList();
    private final OrderBySegmentList orderBy = new OrderBySegmentList();

    private String sqlSegment = CharacterConstants.SEPARATOR_EMPTY;

    private boolean cacheSqlSegment = true;

    public void add(ISqlSegment... iSqlSegments) {
        List<ISqlSegment> list = Arrays.asList(iSqlSegments);
        ISqlSegment firstSqlSegment = list.get(0);
        if (MatchSegment.ORDER_BY.match(firstSqlSegment)) {
            orderBy.addAll(list);
        } else if (MatchSegment.GROUP_BY.match(firstSqlSegment)) {
            groupBy.addAll(list);
        } else if (MatchSegment.HAVING.match(firstSqlSegment)) {
            having.addAll(list);
        } else {
            normal.addAll(list);
        }
        cacheSqlSegment = false;
    }

    @Override
    public String getSqlSegment() {
        if (cacheSqlSegment) {
            return sqlSegment;
        }
        cacheSqlSegment = true;
        if (normal.isEmpty()) {
            if (!groupBy.isEmpty() || !orderBy.isEmpty()) {
                sqlSegment = groupBy.getSqlSegment() + having.getSqlSegment() + orderBy.getSqlSegment();
            }
        } else {
            sqlSegment = normal.getSqlSegment() + groupBy.getSqlSegment() + having.getSqlSegment() + orderBy.getSqlSegment();
        }
        return sqlSegment;
    }

    public NormalSegmentList getNormal() {
        return normal;
    }

    public GroupBySegmentList getGroupBy() {
        return groupBy;
    }

    public HavingSegmentList getHaving() {
        return having;
    }

    public OrderBySegmentList getOrderBy() {
        return orderBy;
    }
}
