package pro.shushi.pamirs.core.common.placeholder;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.connectors.data.sql.AbstractWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.ISqlSegment;
import pro.shushi.pamirs.framework.connectors.data.sql.segments.MergeSegments;
import pro.shushi.pamirs.meta.api.core.faas.hook.PlaceHolderParser;
import pro.shushi.pamirs.meta.constant.RSqlConstants;

import java.util.Optional;

public abstract class AbstractPlaceHolderParser implements PlaceHolderParser {

    static final String auth_Condition = "authCondition";

    protected static final String EMPTY_CONDITION = "''";

    @Override
    public Object[] parse(Object... args) {
        String authCondition = getAuthCondition(args);
        //authCondition = replace(authCondition);
        String where = getWhereCondition(args);
        //where = replace(where);
        return args;
    }

    public String replace(String where) {
        if (StringUtils.isNotBlank(where) && match(where)) {
            return where.replaceAll(covert2regex(), value());
        }
        return where;
    }

    protected abstract String value();

    public String getAuthCondition(Object... args) {
        return getValueByType(auth_Condition, args);
    }

    public String getWhereCondition(Object... args) {
        return getValueByType(RSqlConstants.WHERE, args);
    }

    String getValueByType(String type, Object... args) {
        if (null != args && args.length > 0) {
            int index = 0;
            while (index < args.length && null != args[index]) {
                if (args[index] instanceof AbstractWrapper) {
                    replace((AbstractWrapper<?, ?, ?>) args[index]);
                    break;
                }
                index++;
            }
        }
        return StringUtils.EMPTY;
    }

    protected void replace(AbstractWrapper<?, ?, ?> wrapper) {
        Optional.ofNullable(wrapper.getExpression())
                .map(MergeSegments::getNormal)
                .filter(v -> !v.isEmpty())
                .ifPresent(segments -> {
                    for (ISqlSegment segment : segments) {
                        if (segment instanceof AbstractWrapper) {
                            replace((AbstractWrapper<?, ?, ?>) segment);
                        }
                    }
                });
        wrapper.setRsql(replace(wrapper.getRsql()));
    }

    String covert2regex() {
        String namespace = namespace();
        String value = namespace.replaceAll("\\$\\{(\\S*)\\}$", "$1");
        return "\\$\\{" + value + "\\}";
    }

    Boolean match(String where) {
        return where.matches(".*" + covert2regex() + ".*");
    }
}