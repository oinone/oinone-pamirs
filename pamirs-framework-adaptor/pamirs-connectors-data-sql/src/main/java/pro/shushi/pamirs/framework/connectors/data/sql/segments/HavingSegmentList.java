package pro.shushi.pamirs.framework.connectors.data.sql.segments;

import pro.shushi.pamirs.framework.connectors.data.sql.ISqlSegment;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.List;

import static java.util.stream.Collectors.joining;
import static pro.shushi.pamirs.framework.connectors.data.sql.SqlKeyword.AND;
import static pro.shushi.pamirs.framework.connectors.data.sql.SqlKeyword.HAVING;

/**
 * Having SQL 片段
 */
public class HavingSegmentList extends AbstractISegmentList {

    private static final long serialVersionUID = -869834337301835300L;

    @Override
    protected boolean transformList(List<ISqlSegment> list, ISqlSegment firstSegment) {
        if (!isEmpty()) {
            this.add(AND);
        }
        list.remove(0);
        return true;
    }

    @Override
    protected String childrenSqlSegment() {
        if (isEmpty()) {
            return CharacterConstants.SEPARATOR_EMPTY;
        }
        return this.stream().map(ISqlSegment::getSqlSegment).collect(joining(CharacterConstants.SEPARATOR_BLANK,
                CharacterConstants.SEPARATOR_BLANK + HAVING.getSqlSegment() + CharacterConstants.SEPARATOR_BLANK, CharacterConstants.SEPARATOR_EMPTY));
    }
}
