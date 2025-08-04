package pro.shushi.pamirs.framework.connectors.data.optimize;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import org.apache.ibatis.executor.statement.CallableStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.ISqlParser;
import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.List;

/**
 * AbstractSqlParserHandler
 *
 * @author yakir on 2025/07/31 15:35.
 */
@Data
public abstract class AbstractSqlParserHandler {

    private List<ISqlParser> sqlParserList;

    /**
     * 拦截 SQL 解析执行
     */
    protected void sqlParser(MetaObject metaObject) {
        if (null != metaObject) {
            Object originalObject = metaObject.getOriginalObject();
            StatementHandler statementHandler = PluginUtils.realTarget(originalObject);
            metaObject = SystemMetaObject.forObject(statementHandler);

            // SQL 解析
            if (CollectionUtils.isNotEmpty(this.sqlParserList)) {
                // 好像不用判断也行,为了保险起见,还是加上吧.
                statementHandler = metaObject.hasGetter("delegate") ? (StatementHandler) metaObject.getValue("delegate") : statementHandler;
                if (!(statementHandler instanceof CallableStatementHandler)) {
                    // 标记是否修改过 SQL
                    boolean sqlChangedFlag = false;
                    String originalSql = (String) metaObject.getValue(PluginUtils.DELEGATE_BOUNDSQL_SQL);
                    for (ISqlParser sqlParser : this.sqlParserList) {
                        if (sqlParser.doFilter(metaObject, originalSql)) {
                            String sql = sqlParser.parser(metaObject, originalSql);
                            if (null != sql) {
                                originalSql = sql;
                                sqlChangedFlag = true;
                            }
                        }
                    }
                    if (sqlChangedFlag) {
                        metaObject.setValue(PluginUtils.DELEGATE_BOUNDSQL_SQL, originalSql);
                    }
                }
            }
        }
    }
}