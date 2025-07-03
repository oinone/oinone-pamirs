package pro.shushi.pamirs.eip.jdbc.helper;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.eip.jdbc.check.exception.SQLCheckException;
import pro.shushi.pamirs.eip.jdbc.check.exception.SQLParseCheckException;
import pro.shushi.pamirs.eip.jdbc.entity.SQLPrepareEntity;
import pro.shushi.pamirs.eip.jdbc.service.EipSQLChecker;
import pro.shushi.pamirs.eip.jdbc.service.checker.DefaultMySqlSQLChecker;
import pro.shushi.pamirs.eip.jdbc.service.checker.DefaultOracleSQLChecker;
import pro.shushi.pamirs.eip.jdbc.service.checker.DefaultPGSQLChecker;
import pro.shushi.pamirs.eip.jdbc.service.checker.DefaultSQLServerSQLChecker;
import pro.shushi.pamirs.eip.jdbc.spring.EipJdbcSprintSupport;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * SQL检查帮助类
 *
 * @author Adamancy Zhang at 15:21 on 2024-05-17
 */
@Slf4j
public class SQLCheckHelper {

    private static final SQLUtils.FormatOption DEFAULT_FORMAT_OPTION = new SQLUtils.FormatOption(false, false);

    public static final Set<String> BLACK_LIST_TABLES = new HashSet<>();

    public static final String SQL_EOF = ";";

    static {
        BLACK_LIST_TABLES.add("base_action");
        BLACK_LIST_TABLES.add("base_app_config");
        BLACK_LIST_TABLES.add("base_apps_module_rel_user");
        BLACK_LIST_TABLES.add("base_client_action");
        BLACK_LIST_TABLES.add("base_compute_definition");
        BLACK_LIST_TABLES.add("base_data_dictionary");
        BLACK_LIST_TABLES.add("base_error_definition");
        BLACK_LIST_TABLES.add("base_errors_definition");
        BLACK_LIST_TABLES.add("base_event");
        BLACK_LIST_TABLES.add("base_expression_definition");
        BLACK_LIST_TABLES.add("base_ext_point");
        BLACK_LIST_TABLES.add("base_ext_point_implementation");
        BLACK_LIST_TABLES.add("base_field");
        BLACK_LIST_TABLES.add("base_function");
        BLACK_LIST_TABLES.add("base_hook");
        BLACK_LIST_TABLES.add("base_interfaces");
        BLACK_LIST_TABLES.add("base_layout_definition");
        BLACK_LIST_TABLES.add("base_mask_definition");
        BLACK_LIST_TABLES.add("base_menu");
        BLACK_LIST_TABLES.add("base_model");
        BLACK_LIST_TABLES.add("base_model_category");
        BLACK_LIST_TABLES.add("base_model_category_module_rel");
        BLACK_LIST_TABLES.add("base_model_data");
        BLACK_LIST_TABLES.add("base_model_index");
        BLACK_LIST_TABLES.add("base_model_inherited");
        BLACK_LIST_TABLES.add("base_model_relation");
        BLACK_LIST_TABLES.add("base_module");
        BLACK_LIST_TABLES.add("base_module_category");
        BLACK_LIST_TABLES.add("base_module_dependency");
        BLACK_LIST_TABLES.add("base_module_exclusion");
        BLACK_LIST_TABLES.add("base_module_upstream");
        BLACK_LIST_TABLES.add("base_pamirs_file");
        BLACK_LIST_TABLES.add("base_sequence_config");
        BLACK_LIST_TABLES.add("base_server_action");
        BLACK_LIST_TABLES.add("base_theme_definition");
        BLACK_LIST_TABLES.add("base_transaction");
        BLACK_LIST_TABLES.add("base_ui_tree_node");
        BLACK_LIST_TABLES.add("base_url_action");
        BLACK_LIST_TABLES.add("base_view");
        BLACK_LIST_TABLES.add("base_view_action");
        BLACK_LIST_TABLES.add("base_view_category");
        BLACK_LIST_TABLES.add("base_view_category_module_rel");
        BLACK_LIST_TABLES.add("base_widget_definition");
        BLACK_LIST_TABLES.add("base_widget_group");
        BLACK_LIST_TABLES.add("base_worker_node");
        BLACK_LIST_TABLES.add("leaf_alloc");
        BLACK_LIST_TABLES.add("pamirs_field_column");
        BLACK_LIST_TABLES.add("pamirs_model_table");
        BLACK_LIST_TABLES.add("pamirs_module_index");
    }

    private SQLCheckHelper() {
        //reject create object
    }

    /**
     * 限制表
     *
     * @param table 表名称
     * @return 是否为限制表
     */
    public static boolean isLimitTable(String table) {
        return BLACK_LIST_TABLES.contains(table);
    }

    /**
     * 单行SQL检查
     *
     * @param sql    SQL
     * @param dbType {@link JdbcUtils}
     * @return 预处理后SQL
     * @throws SQLCheckException SQL检查异常
     */
    public static String checkSingle(String sql, String dbType) throws SQLCheckException {
        return check(sql, dbType, (prepareEntity, statements, visitor) -> {
            if (statements.size() >= 2) {
                throw SQLParseCheckException.createSingleSQLException();
            }
            SQLStatement statement = statements.get(0);
            try {
                statement.accept(visitor);
            } catch (SQLCheckException e) {
                throw e;
            } catch (Throwable e) {
                throw SQLParseCheckException.createSQLVisitError(e);
            }
            String newSql = SQLUtils.toSQLString(statement, dbType, DEFAULT_FORMAT_OPTION);
            newSql = newSql.trim();
            if (newSql.endsWith(SQL_EOF)) {
                newSql = newSql.substring(0, newSql.length() - 1);
            }
            return newSql;
        });
    }

    /**
     * 多行SQL检查
     *
     * @param sql    SQL
     * @param dbType {@link JdbcUtils}
     * @return 预处理后SQL
     * @throws SQLCheckException SQL检查异常
     */
    public static String checkMulti(String sql, String dbType) throws SQLCheckException {
        return check(sql, dbType, (prepareEntity, statements, visitor) -> {
            try {
                for (SQLStatement statement : statements) {
                    statement.accept(visitor);
                }
            } catch (SQLCheckException e) {
                throw e;
            } catch (Throwable e) {
                throw SQLParseCheckException.createSQLVisitError(e);
            }
            return SQLUtils.toSQLString(statements, dbType, DEFAULT_FORMAT_OPTION);
        });
    }

    private static String check(String sql, String dbType, SQLChecker checkConsumer) {
        List<SQLStatement> statements = null;
        SQLASTVisitor visitor = null;
        List<EipSQLChecker> checkers = fetchSQLCheckers();
        ParserException parserException = null;
        EipSQLChecker checker = fetchSQLChecker(checkers, dbType);
        if (checker == null) {
            throw SQLCheckException.createCommonException();
        }
        SQLPrepareEntity prepareEntity = checker.prepare(sql);
        sql = prepareEntity.getPrepareSql();
        try {
            statements = checker.parser(sql);
            visitor = checker.visitor(prepareEntity);
        } catch (ParserException e) {
            List<String> secondaryDbTypes = checker.secondaryDbTypes();
            if (CollectionUtils.isEmpty(secondaryDbTypes)) {
                throw SQLParseCheckException.createSQLError(e);
            }
            parserException = e;
            if (log.isDebugEnabled()) {
                log.debug("{} parser error.", dbType, e);
            }
            for (String secondaryDbType : secondaryDbTypes) {
                EipSQLChecker secondaryChecker = fetchSQLChecker(checkers, secondaryDbType);
                if (secondaryChecker != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("Try to use secondary dbType: {}", secondaryDbType);
                    }
                    try {
                        statements = secondaryChecker.parser(sql);
                        visitor = secondaryChecker.visitor(prepareEntity);
                        parserException = null;
                        break;
                    } catch (ParserException ee) {
                        if (log.isDebugEnabled()) {
                            log.debug("{} parser error.", secondaryDbType, ee);
                        }
                    }
                }
            }
        }
        if (parserException != null) {
            throw SQLParseCheckException.createSQLError(parserException);
        }
        if (CollectionUtils.isEmpty(statements)) {
            throw SQLCheckException.createCommonException();
        }
        return checkConsumer.check(prepareEntity, statements, visitor);
    }

    public static String clearQuote(String name, String quote) {
        return clearQuote(name, quote, quote);
    }

    public static String clearQuote(String name, String quoteL, String quoteR) {
        if (name.startsWith(quoteL) && name.endsWith(quoteR)) {
            return name.substring(1, name.length() - 1);
        }
        return name;
    }

    private static List<EipSQLChecker> fetchSQLCheckers() {
        List<EipSQLChecker> checkers = EipJdbcSprintSupport.getSQLCheckers();
        if (checkers == null) {
            checkers = new ArrayList<>();
            checkers.add(new DefaultMySqlSQLChecker());
            checkers.add(new DefaultOracleSQLChecker());
            checkers.add(new DefaultPGSQLChecker());
            checkers.add(new DefaultSQLServerSQLChecker());
        }
        return FetchUtil.cast(checkers);
    }

    private static EipSQLChecker fetchSQLChecker(List<EipSQLChecker> checkers, String dbType) {
        for (EipSQLChecker checker : checkers) {
            if (StringUtils.equalsIgnoreCase(checker.dbType(), dbType)) {
                return checker;
            }
        }
        return null;
    }

    @FunctionalInterface
    private interface SQLChecker {

        String check(SQLPrepareEntity prepareEntity, List<SQLStatement> statements, SQLASTVisitor visitor);
    }
}
