package pro.shushi.pamirs.framework.connectors.data.dialect.shardingsphere;

import org.apache.shardingsphere.sql.parser.api.visitor.type.*;
import org.apache.shardingsphere.sql.parser.mysql.visitor.statement.impl.*;
import org.apache.shardingsphere.sql.parser.spi.SQLVisitorFacade;

/**
 * @author Adamancy Zhang at 15:02 on 2025-08-07
 */
public class MySQLNdsStatementSQLVisitorFacade implements SQLVisitorFacade {

    @Override
    public Class<? extends DMLSQLVisitor> getDMLVisitorClass() {
        return MySQLDMLStatementSQLVisitor.class;
    }

    @Override
    public Class<? extends DDLSQLVisitor> getDDLVisitorClass() {
        return MySQLDDLStatementSQLVisitor.class;
    }

    @Override
    public Class<? extends TCLSQLVisitor> getTCLVisitorClass() {
        return MySQLTCLStatementSQLVisitor.class;
    }

    @Override
    public Class<? extends DCLSQLVisitor> getDCLVisitorClass() {
        return MySQLDCLStatementSQLVisitor.class;
    }

    @Override
    public Class<? extends DALSQLVisitor> getDALVisitorClass() {
        return MySQLDALStatementSQLVisitor.class;
    }

    @Override
    public Class<? extends RLSQLVisitor> getRLVisitorClass() {
        return MySQLRLStatementSQLVisitor.class;
    }

    @Override
    public String getDatabaseType() {
        return MySQLNdsType.NAME;
    }

    @Override
    public String getVisitorType() {
        return "STATEMENT";
    }
}
