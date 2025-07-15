package pro.shushi.pamirs.middleware.schedule.core.dialect.oracle11g;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleOutputVisitor;
import com.alibaba.druid.util.JdbcConstants;

/**
 * Oracle 11g toString
 *
 * @author Adamancy Zhang at 21:26 on 2025-07-14
 */
public class Oracle11gToStringVisitor extends OracleOutputVisitor {

    public Oracle11gToStringVisitor(Appendable appender) {
        super(appender);
    }

    public Oracle11gToStringVisitor(Appendable appender, boolean printPostSemi) {
        super(appender, printPostSemi);
    }

    protected void printFetchFirst(SQLSelectQueryBlock x) {
        SQLLimit limit = x.getLimit();
        if (limit == null) {
            return;
        }

        SQLExpr offset = limit.getOffset();
        SQLExpr first = limit.getRowCount();

        if (JdbcConstants.INFORMIX.equals(dbType)) {
            if (offset != null) {
                print0(ucase ? "SKIP " : "skip ");
                offset.accept(this);
            }

            print0(ucase ? " FIRST " : " first ");
            first.accept(this);
            print(' ');
        } else if (JdbcConstants.DB2.equals(dbType)
                || JdbcConstants.ORACLE.equals(dbType)
                || JdbcConstants.SQL_SERVER.equals(dbType)) {
            //order by 语句必须在FETCH FIRST ROWS ONLY之前
            SQLObject parent = x.getParent();
            if (parent instanceof SQLSelect) {
                SQLOrderBy orderBy = ((SQLSelect) parent).getOrderBy();
                if (orderBy != null && orderBy.getItems().size() > 0) {
                    println();
                    print0(ucase ? "ORDER BY " : "order by ");
                    printAndAccept(orderBy.getItems(), ", ");
                }
            }

            println();

            if (offset != null) {
                print0(ucase ? "OFFSET " : "offset ");
                offset.accept(this);
                print0(ucase ? " ROWS" : " rows");
            }

            if (first != null) {
                if (offset != null) {
                    print(' ');
                }
                if (JdbcConstants.SQL_SERVER.equals(dbType) && offset != null) {
                    print0(ucase ? "FETCH NEXT " : "fetch next ");
                } else {
                    print0(ucase ? "FETCH FIRST " : "fetch first ");
                }
                first.accept(this);
                print0(ucase ? " ROWS ONLY" : " rows only");
            }
        } else {
            println();
            limit.accept(this);
        }
    }
}
