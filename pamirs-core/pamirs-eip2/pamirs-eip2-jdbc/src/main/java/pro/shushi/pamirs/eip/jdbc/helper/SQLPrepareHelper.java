package pro.shushi.pamirs.eip.jdbc.helper;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;
import pro.shushi.pamirs.eip.jdbc.entity.SQLPrepareEntity;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL预处理帮助类
 *
 * @author Adamancy Zhang at 22:23 on 2024-06-05
 */
public class SQLPrepareHelper {

    public static final Pattern SQL_PARAMETER_PATTERN = Pattern.compile("\\{([^}]*)\\}");

    public static final String SQL_PARAMETER_REPLACEMENT = ":#$1";

    public static final String SQL_PREPARE_PARAMETER_REPLACEMENT = "\\?";

    public static final Pattern SQL_PREPARE_PATTERN = Pattern.compile("\\n");

    public static final String SQL_PREPARE_PARAMETER_PREFIX = "#{__";

    public static final String SQL_PREPARE_PARAMETER_SUFFIX = "__}";

    public static final Pattern SQL_PREPARE_PARAMETER_PATTERN = Pattern.compile("#\\{__([\\d]*)__}");

    public static String prepareSQL(String sql) {
        return SQL_PREPARE_PATTERN.matcher(sql).replaceAll(CharacterConstants.SEPARATOR_BLANK);
    }

    public static String toUriSQL(String sql) {
        return SQLPrepareHelper.SQL_PARAMETER_PATTERN.matcher(sql).replaceAll(SQLPrepareHelper.SQL_PARAMETER_REPLACEMENT);
    }

    public static SQLPrepareEntity prepareParameters(String sql) {
        return prepareParameters(sql, SQL_PREPARE_PARAMETER_PREFIX, SQL_PREPARE_PARAMETER_SUFFIX);
    }

    public static SQLPrepareEntity prepareParameters(String sql, String prepareL, String prepareR) {
        Matcher matcher = SQL_PARAMETER_PATTERN.matcher(sql);
        if (matcher.find()) {
            Map<Integer, String> prepareParameters = new HashMap<>();
            int index = 1;
            StringBuffer sb = new StringBuffer();
            do {
                String matched = matcher.group();
                prepareParameters.put(index, matched);
                matcher.appendReplacement(sb, prepareL + index++ + prepareR);
            } while (matcher.find());
            matcher.appendTail(sb);
            return new SQLPrepareEntity(sql, sb.toString(), Collections.unmodifiableMap(prepareParameters));
        }
        return new SQLPrepareEntity(sql, sql, Collections.emptyMap());
    }

    public static Integer parsePrepareParameterKey(String name) {
        return parsePrepareParameterKey(name, SQL_PREPARE_PARAMETER_PREFIX, SQL_PREPARE_PARAMETER_SUFFIX);
    }

    public static Integer parsePrepareParameterKey(String name, String prepareL, String prepareR) {
        if (name.startsWith(prepareL) && name.endsWith(prepareR)) {
            try {
                return Integer.parseInt(name.substring(prepareL.length(), name.length() - prepareR.length()));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public static String replacePrepareParameters(Map<Integer, String> prepareParameters, String text) {
        Matcher matcher = SQL_PREPARE_PARAMETER_PATTERN.matcher(text);
        StringBuilder builder = new StringBuilder();
        int lasted = 0;
        while (matcher.find()) {
            String replacement = matcher.group();
            Integer key = SQLPrepareHelper.parsePrepareParameterKey(replacement);
            if (key != null) {
                String targetName = prepareParameters.get(key);
                if (targetName != null) {
                    replacement = targetName;
                }
            }
            builder.append(text, lasted, matcher.start()).append(replacement);

            lasted = matcher.end();
        }
        if (lasted == 0) {
            return text;
        }
        if (text.length() > lasted) {
            builder.append(text.substring(lasted));
        }
        return builder.toString();
    }

    public static List<SQLStatement> parseStatements(String sql, String dbType) {
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType, false);
        List<SQLStatement> stmtList = parser.parseStatementList();
        if (parser.getLexer().token() != Token.EOF) {
            throw new ParserException("syntax error : " + sql);
        }
        return stmtList;
    }
}
