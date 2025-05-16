package pro.shushi.pamirs.framework.connectors.data.sql.segments;

import pro.shushi.pamirs.framework.connectors.data.sql.ISqlSegment;
import pro.shushi.pamirs.framework.connectors.data.sql.SqlKeyword;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.List;

import static java.util.stream.Collectors.joining;

/**
 * Group By SQL 片段
 */
public class GroupBySegmentList extends AbstractISegmentList {

    private static final long serialVersionUID = 7002746957554159894L;

    @Override
    protected boolean transformList(List<ISqlSegment> list, ISqlSegment firstSegment) {
        list.remove(0);
        return true;
    }

    @Override
    protected String childrenSqlSegment() {
        if (isEmpty()) {
            return CharacterConstants.SEPARATOR_EMPTY;
        }
        return this.stream().map(ISqlSegment::getSqlSegment).collect(joining(CharacterConstants.SEPARATOR_COMMA,
                CharacterConstants.SEPARATOR_BLANK + SqlKeyword.GROUP_BY.getSqlSegment() + CharacterConstants.SEPARATOR_BLANK, CharacterConstants.SEPARATOR_EMPTY));
    }
}
