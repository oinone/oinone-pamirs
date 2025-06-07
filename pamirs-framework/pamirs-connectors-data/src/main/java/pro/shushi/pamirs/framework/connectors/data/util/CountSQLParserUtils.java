package pro.shushi.pamirs.framework.connectors.data.util;

import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.extension.plugins.pagination.optimize.JsqlParserCountOptimize;
import org.apache.ibatis.reflection.MetaObject;

/**
 * Count SQL 解析工具类
 *
 * @author Adamancy Zhang at 12:29 on 2024-11-05
 */
public class CountSQLParserUtils {

    private static final ISqlParser COUNT_SQL_PARSER = new JsqlParserCountOptimize();

    public static final CountSQLParserUtils INSTANCE = CountSQLParserUtils.newInstance();

    private ISqlParser parser;

    private String countSQLFormat;

    public static CountSQLParserUtils newInstance() {
        return newInstance("SELECT COUNT(*) FROM (%s) TOTAL");
    }

    public static CountSQLParserUtils newInstance(String countSQLFormat) {
        CountSQLParserUtils utils = new CountSQLParserUtils();
        utils.parser = COUNT_SQL_PARSER;
        utils.countSQLFormat = countSQLFormat;
        return utils;
    }

    public CountSQLParserUtils setParser(ISqlParser parser) {
        this.parser = parser;
        return this;
    }

    public String getOptimizeCountSql(boolean optimizeCountSql, MetaObject metaObject, String originalSql) {
        if (optimizeCountSql) {
            return parser.parser(metaObject, originalSql).getSql();
        }
        return getOriginalCountSql(originalSql);
    }

    public String getOriginalCountSql(String originalSql) {
        return String.format(countSQLFormat, originalSql);
    }
}
