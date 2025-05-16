package pro.shushi.pamirs.framework.connectors.data.sql.segments;

import pro.shushi.pamirs.framework.connectors.data.sql.ISqlSegment;
import pro.shushi.pamirs.framework.connectors.data.sql.SqlKeyword;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 普通片段
 */
public class NormalSegmentList extends AbstractISegmentList {

    private static final long serialVersionUID = -5352241554897548818L;
    /**
     * 是否处理了的上个 not
     */
    private boolean executeNot = true;

    NormalSegmentList() {
        this.flushLastValue = true;
    }

    @Override
    protected boolean transformList(List<ISqlSegment> list, ISqlSegment firstSegment) {
        if (list.size() == 1) {
            /* 只有 and() 以及 or() 以及 not() 会进入 */
            if (!MatchSegment.NOT.match(firstSegment)) {
                //不是 not
                if (isEmpty()) {
                    //sqlSegment是 and 或者 or 并且在第一位,不继续执行
                    return false;
                }
                boolean matchLastAnd = MatchSegment.AND.match(lastValue);
                boolean matchLastOr = MatchSegment.OR.match(lastValue);
                if (matchLastAnd || matchLastOr) {
                    //上次最后一个值是 and 或者 or
                    if (matchLastAnd && MatchSegment.AND.match(firstSegment)) {
                        return false;
                    } else if (matchLastOr && MatchSegment.OR.match(firstSegment)) {
                        return false;
                    } else {
                        //和上次的不一样
                        removeAndFlushLast();
                    }
                }
            } else {
                executeNot = false;
                return false;
            }
        } else {
            if (!executeNot) {
                list.add(MatchSegment.EXISTS.match(firstSegment) ? 0 : 1, SqlKeyword.NOT);
                executeNot = true;
            }
            if (!MatchSegment.AND_OR.match(lastValue) && !isEmpty()) {
                add(SqlKeyword.AND);
            }
            if (MatchSegment.APPLY.match(firstSegment)) {
                list.remove(0);
            }
        }
        return true;
    }

    @Override
    protected String childrenSqlSegment() {
        if (MatchSegment.AND_OR.match(lastValue)) {
            removeAndFlushLast();
        }
        final String str = this.stream().map(ISqlSegment::getSqlSegment).collect(Collectors.joining(CharacterConstants.SEPARATOR_BLANK));
        return (str.startsWith(CharacterConstants.LEFT_BRACKET) && str.endsWith(CharacterConstants.RIGHT_BRACKET))
                ? str : (CharacterConstants.LEFT_BRACKET + str + CharacterConstants.RIGHT_BRACKET);
    }
}
