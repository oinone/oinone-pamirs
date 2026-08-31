package pro.shushi.pamirs.framework.gateways.rsql.connector;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;

/**
 * 抽象节点连接器
 * <ul>
 *   <li>eq<br>
 *     <sql>
 *       select * from auth_auth_role where name == 'cs''5';    -- => cs'5
 *       select * from auth_auth_role where name == 'cs%9';     -- => cs%9
 *       select * from auth_auth_role where name == 'cs_8';     -- => cs_8
 *     </sql>
 *   </li>
 *   <li>in<br>
 *     <sql>
 *       select * from auth_auth_role where name in ('cs''5');  -- => cs'5
 *       select * from auth_auth_role where name in ('cs%9');   -- => cs%9
 *       select * from auth_auth_role where name in ('cs_8');   -- => cs_8
 *     </sql>
 *   </li>
 *   <li>like<br>
 *     <sql>
 *       select * from auth_auth_role where name like '%''%';   -- => cs'5
 *       select * from auth_auth_role where name like '%cs\%%'; -- => cs%9
 *       select * from auth_auth_role where name like '%cs\_8%'; -- => cs_8
 *     </sql>
 *   </li>
 * </ul>
 *
 * @author Adamancy Zhang at 10:51 on 2024-09-27
 */
public abstract class AbstractNodeConnector {

    protected static final String[] PRECISE_SEARCH_CHARACTERS = new String[]{"_", "%"};

    protected static final String[] FUZZY_SEARCH_CHARACTERS = new String[]{};

    protected String getArgumentString(ComparisonOperator operator, List<String> arguments) {
        if (operator.isMultiValue()) {
            return CharacterConstants.LEFT_BRACKET + "'" + joinSerializableValues("','", PRECISE_SEARCH_CHARACTERS, arguments) + "'" + CharacterConstants.RIGHT_BRACKET;
        } else {
            return "'" + serializableValue(arguments.get(0), PRECISE_SEARCH_CHARACTERS) + "'";
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

    protected String joinSerializableValues(CharSequence delimiter, String[] characters, Iterable<? extends CharSequence> elements) {
        return join(delimiter, element -> {
            String value;
            if (element instanceof String) {
                value = ((String) element).trim();
            } else {
                value = String.valueOf(element).trim();
            }
            return serializableValue(value, characters);
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

    /**
     * Since the frontend has performed escaping on `_`, `%`, and `'`, the backend needs to perform special processing when generating RSQL or SQL
     */
    protected String serializableValue(String value, String[] characters) {
        for (String character : characters) {
            value = String.join(String.format("%s", character), value.split(String.format("\\\\%s", character), -1));
        }
        return value;
    }
}
