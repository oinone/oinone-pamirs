package pro.shushi.pamirs.framework.connectors.data.dialect.shardingsphere;

import org.apache.shardingsphere.sql.parser.api.visitor.type.*;
import org.apache.shardingsphere.sql.parser.mysql.visitor.format.impl.*;
import org.apache.shardingsphere.sql.parser.spi.SQLVisitorFacade;

/**
 * @author Adamancy Zhang at 15:03 on 2025-08-07
 */
public class MySQLNdsFormatSQLVisitorFacade implements SQLVisitorFacade {

    @Override
    public Class<? extends DMLSQLVisitor> getDMLVisitorClass() {
        return MySQLDMLFormatSQLVisitor.class;
    }

    @Override
    public Class<? extends DDLSQLVisitor> getDDLVisitorClass() {
        return MySQLDDLFormatSQLVisitor.class;
    }

    @Override
    public Class<? extends TCLSQLVisitor> getTCLVisitorClass() {
        return MySQLTCLFormatSQLVisitor.class;
    }

    @Override
    public Class<? extends DCLSQLVisitor> getDCLVisitorClass() {
        return MySQLDCLFormatSQLVisitor.class;
    }

    @Override
    public Class<? extends DALSQLVisitor> getDALVisitorClass() {
        return MySQLDALFormatSQLVisitor.class;
    }

    @Override
    public Class<? extends RLSQLVisitor> getRLVisitorClass() {
        return MySQLRLFormatSQLVisitor.class;
    }

    @Override
    public String getDatabaseType() {
        return MySQLNdsType.NAME;
    }

    @Override
    public String getVisitorType() {
        return "FORMAT";
    }
}
