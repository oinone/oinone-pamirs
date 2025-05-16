package pro.shushi.pamirs.eip.jdbc.camel;

import com.alibaba.druid.pool.DruidPooledPreparedStatement;
import org.apache.camel.Exchange;
import org.apache.camel.component.sql.DefaultSqlPrepareStatementStrategy;
import org.apache.camel.util.CollectionStringBuffer;
import org.apache.camel.util.StringQuoteHelper;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.util.CompositeIterator;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL预处理策略
 *
 * @author Adamancy Zhang at 17:37 on 2024-06-05
 */
@Slf4j
public class EipSqlPrepareStatementStrategy extends DefaultSqlPrepareStatementStrategy {

    public static final String NAME = "sqlPrepareStatementStrategy";

    public static final EipSqlPrepareStatementStrategy INSTANCE = new EipSqlPrepareStatementStrategy();

    private static final Pattern REPLACE_IN_PATTERN = Pattern.compile("\\:\\?in\\:(\\w+|\\$\\{[^\\}]+\\}|\\$simple\\{[^\\}]+\\})", Pattern.MULTILINE);

    private static final Pattern REPLACE_PATTERN = Pattern.compile("\\:\\?\\w+|\\:\\?\\$\\{[^\\}]+\\}|\\:\\?\\$simple\\{[^\\}]+\\}", Pattern.MULTILINE);

    private static final Pattern NAME_PATTERN = Pattern.compile("\\:\\?((in\\:(\\w+|\\$\\{[^\\}]+\\}|\\$simple\\{[^\\}]+\\}))|(\\w+|\\$\\{[^\\}]+\\}|\\$simple\\{[^\\}]+\\}))", Pattern.MULTILINE);

    private static final String PREPARE_QUERY_REPLACEMENT = "\\?";

    public static final char DEFAULT_SEPARATOR = ',';

    public static final String DEFAULT_KEY = "DEFAULT";

    private final char separator;

    private final String defaultKey;

    public EipSqlPrepareStatementStrategy() {
        super(DEFAULT_SEPARATOR);
        separator = DEFAULT_SEPARATOR;
        defaultKey = DEFAULT_KEY;
    }

    public EipSqlPrepareStatementStrategy(char separator, String defaultKey) {
        super(separator);
        this.separator = separator;
        this.defaultKey = defaultKey;
    }

    @Override
    public String prepareQuery(String query, boolean allowNamedParameters, Exchange exchange) throws SQLException {
        String answer;
        if (allowNamedParameters && hasNamedParameters(query)) {
            if (exchange != null) {
                // replace all :?in:word with a number of placeholders for how many values are expected in the IN values
                Matcher matcher = REPLACE_IN_PATTERN.matcher(query);
                while (matcher.find()) {
                    String found = matcher.group(1);
                    Object parameter = lookupParameter(found, exchange, exchange.getIn().getBody());
                    if (parameter != null) {
                        Iterator<?> it = createInParameterIterator(parameter);
                        CollectionStringBuffer csb = new CollectionStringBuffer(",");
                        while (it.hasNext()) {
                            it.next();
                            csb.append("\\?");
                        }
                        String replace = csb.toString();
                        String foundEscaped = found.replace("$", "\\$").replace("{", "\\{").replace("}", "\\}");
                        Matcher paramMatcher = Pattern.compile("\\:\\?in\\:" + foundEscaped, Pattern.MULTILINE).matcher(query);
                        query = paramMatcher.replaceAll(replace);
                    }
                }
            }
            // replace all :?word and :?${foo} with just ?
            answer = replaceParams(query, exchange);
        } else {
            answer = query;
        }
        log.info("prepare sql: {}", answer);
        return answer;
    }

    @Override
    public void populateStatement(PreparedStatement ps, Iterator<?> iterator, int expectedParams) throws SQLException {
        if (expectedParams <= 0) {
            return;
        }

        final Object[] args = new Object[expectedParams];
        int i = 0;
        int argNumber = 1;

        SQLType sqlType = SQLType.UNKNOWN;
        if (ps instanceof DruidPooledPreparedStatement) {
            String sql = ((DruidPooledPreparedStatement) ps).getSql();
            sqlType = getSQLType(sql);
        }

        while (iterator != null && iterator.hasNext()) {
            Object value = iterator.next();
            if (SQLType.INSERT.equals(sqlType) && value == null) {
                continue;
            }
            // special for SQL IN where we need to set dynamic number of values
            if (value instanceof CompositeIterator) {
                Iterator<?> it = (Iterator<?>) value;
                while (it.hasNext()) {
                    Object val = it.next();
                    if (argNumber <= expectedParams) {
                        args[i] = val;
                    }
                    argNumber++;
                    i++;
                }
            } else {
                if (argNumber <= expectedParams) {
                    args[i] = value;
                }
                argNumber++;
                i++;
            }
        }

        if (argNumber - 1 != expectedParams) {
            throw new SQLException("Number of parameters mismatch. Expected: " + expectedParams + ", was: " + (argNumber - 1));
        }

        // use argument setter as it deals with various JDBC drivers setObject vs setLong/setInteger/setString etc.
        ArgumentPreparedStatementSetter setter = new ArgumentPreparedStatementSetter(args);
        setter.setValues(ps);
    }

    @Override
    public Iterator<?> createPopulateIterator(String query, String preparedQuery, int expectedParams, Exchange exchange, Object value) throws SQLException {
        if (hasNamedParameters(query)) {
            // create an iterator that returns the value in the named order
            return new PopulateIterator(query, exchange, value);
        } else {
            // if only 1 parameter and the body is a String then use body as is
            if (expectedParams == 1 && value instanceof String) {
                return Collections.singletonList(value).iterator();
            } else {
                // is the body a String
                if (value instanceof String) {
                    // if the body is a String then honor quotes etc.
                    String[] tokens = StringQuoteHelper.splitSafeQuote((String) value, separator, true);
                    List<String> list = Arrays.asList(tokens);
                    return list.iterator();
                } else {
                    // just use a regular iterator
                    return exchange.getContext().getTypeConverter().convertTo(Iterator.class, value);
                }
            }
        }
    }

    private String replaceParams(String query, Exchange exchange) {
        // nested parameters are not replaced properly just by the REPLACE_PATTERN
        // for example ":?${array[${index}]}"
        query = replaceBracketedParams(query);
        SQLType sqlType = getSQLType(query);
        String answer = query;
        Matcher matcher = REPLACE_PATTERN.matcher(query);
        if (matcher.find()) {
            StringBuffer sb = new StringBuffer();
            do {
                String parameterName = matcher.group();
                if (parameterName.startsWith(":?")) {
                    parameterName = parameterName.substring(2);
                    if (hasParameter(parameterName, exchange, exchange.getMessage().getBody())) {
                        matcher.appendReplacement(sb, PREPARE_QUERY_REPLACEMENT);
                    } else {
                        switch (sqlType) {
                            case INSERT:
                                matcher.appendReplacement(sb, this.defaultKey);
                                break;
                            default:
                                matcher.appendReplacement(sb, PREPARE_QUERY_REPLACEMENT);
                        }

                    }
                }
            } while (matcher.find());
            matcher.appendTail(sb);
            answer = sb.toString();
        }
        return answer;
    }

    private String replaceBracketedParams(String query) {
        while (query.contains(":?${")) {
            int i = query.indexOf(":?${");
            int j = findClosingBracket(query, i + 3);

            if (j == -1) {
                throw new IllegalArgumentException("String doesn't have equal opening and closing brackets: " + query);
            }

            query = query.substring(0, i) + "?" + query.substring(j + 1);
        }
        return query;
    }

    private static int findClosingBracket(String text, int openPosition) {
        if (text.charAt(openPosition) != '{') {
            throw new IllegalArgumentException("Character at specified position is not an open bracket");
        }

        int remainingClosingBrackets = 0;

        for (int i = openPosition; i < text.length(); i++) {
            if (text.charAt(i) == '{') {
                remainingClosingBrackets++;
            } else if (text.charAt(i) == '}') {
                remainingClosingBrackets--;
            }
            if (remainingClosingBrackets == 0) {
                return i;
            }
        }
        return -1;
    }

    private static final class PopulateIterator implements Iterator<Object> {
        private static final String MISSING_PARAMETER_EXCEPTION =
                "Cannot find key [%s] in message body or headers to use when setting named parameter in query [%s]";
        private final String query;
        private final NamedQueryParser parser;
        private final Exchange exchange;
        private final Object body;
        private String nextParam;

        private PopulateIterator(String query, Exchange exchange, Object body) {
            this.query = query;
            this.exchange = exchange;
            this.body = body;
            this.parser = new NamedQueryParser(query);
            this.nextParam = parser.next();
        }

        @Override
        public boolean hasNext() {
            return nextParam != null;
        }

        @Override
        public Object next() {
            if (nextParam == null) {
                throw new NoSuchElementException();
            }

            // is it a SQL in parameter
            boolean in = false;
            if (nextParam.startsWith("in:")) {
                in = true;
                nextParam = nextParam.substring(3);
            }

            Object next = null;
            try {
                boolean hasNext = hasParameter(nextParam, exchange, body);
                if (hasNext) {
                    next = lookupParameter(nextParam, exchange, body);
                    if (in && next != null) {
                        // if SQL IN we need to return an iterator that can iterate the parameter values
                        next = createInParameterIterator(next);
                    }
                } else {
                    return null;
//                    throw new RuntimeExchangeException(String.format(MISSING_PARAMETER_EXCEPTION, nextParam, query), exchange);
                }
            } finally {
                nextParam = parser.next();
            }

            return next;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

    private static final class NamedQueryParser {

        private final String query;
        private final Matcher matcher;

        private NamedQueryParser(String query) {
            this.query = query;
            this.matcher = NAME_PATTERN.matcher(query);
        }

        public String next() {
            if (matcher.find()) {
                String param = matcher.group(1);

                int openingBrackets = 0;
                int closingBrackets = 0;
                for (int i = 0; i < param.length(); i++) {
                    if (param.charAt(i) == '{') {
                        openingBrackets++;
                    }
                    if (param.charAt(i) == '}') {
                        closingBrackets++;
                    }
                }
                if (openingBrackets != closingBrackets) {
                    // nested parameters are not found properly by the NAME_PATTERN
                    // for example param ":?${array[?${index}]}"
                    // is detected as "${array[?${index}"
                    // we have to find correct closing bracket manually
                    String querySubstring = query.substring(matcher.start());
                    int i = querySubstring.indexOf('{');
                    int j = findClosingBracket(querySubstring, i);
                    param = "$" + querySubstring.substring(i, j + 1);
                }

                return param;
            }

            return null;
        }
    }

    protected SQLType getSQLType(String query) {
        int firstBlank = query.indexOf(CharacterConstants.SEPARATOR_BLANK);
        if (firstBlank == -1) {
            return SQLType.UNKNOWN;
        }
        return SQLType.of(query.substring(0, firstBlank));
    }

    private enum SQLType {
        SELECT("select"),
        INSERT("insert"),
        UPDATE("update"),
        DELETE("delete"),
        UNKNOWN("unknown");

        private final String value;

        SQLType(String value) {
            this.value = value;
        }

        private static final List<SQLType> SUPPORTED_SQL_TYPES = Arrays.asList(SELECT, INSERT, UPDATE, DELETE);

        public static SQLType of(String value) {
            value = value.toLowerCase();
            for (SQLType sqlType : SUPPORTED_SQL_TYPES) {
                if (sqlType.value.equals(value)) {
                    return sqlType;
                }
            }
            return UNKNOWN;
        }
    }
}