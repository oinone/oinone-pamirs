package pro.shushi.pamirs.framework.gateways.rsql.connector;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;

/**
 * 抽象节点连接器
 *
 * @author Adamancy Zhang at 10:51 on 2024-09-27
 */
public abstract class AbstractNodeConnector {

    protected String getArgumentString(ComparisonOperator operator, List<String> arguments) {
        if (operator.isMultiValue()) {
            return CharacterConstants.LEFT_BRACKET + "'" + join("', '", arguments) + "'" + CharacterConstants.RIGHT_BRACKET;
        } else {
            return "'" + arguments.get(0) + "'";
        }
    }

    protected <T> String join(CharSequence delimiter, Function<T, String> function, Iterable<T> elements) {
        StringJoiner joiner = new StringJoiner(delimiter);
        for (T element : elements) {
            joiner.add(function.apply(element));
        }
        return joiner.toString();
    }

    protected String join(CharSequence delimiter, Iterable<? extends CharSequence> elements) {
        return join(delimiter, element -> {
            if (element instanceof String) {
                return ((String) element).trim();
            } else {
                return String.valueOf(element);
            }
        }, elements);
    }

    protected String concat(String split, String base, String... ss) {
        StringBuilder builder = new StringBuilder(base);
        for (String s : ss) {
            if (StringUtils.isBlank(s)) {
                continue;
            }
            builder.append(split).append(s);
        }
        return builder.toString();
    }
}
