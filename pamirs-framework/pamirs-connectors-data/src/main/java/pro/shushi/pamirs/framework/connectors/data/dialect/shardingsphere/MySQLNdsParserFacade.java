package pro.shushi.pamirs.framework.connectors.data.dialect.shardingsphere;

import org.apache.shardingsphere.sql.parser.api.parser.SQLLexer;
import org.apache.shardingsphere.sql.parser.api.parser.SQLParser;
import org.apache.shardingsphere.sql.parser.mysql.parser.MySQLLexer;
import org.apache.shardingsphere.sql.parser.mysql.parser.MySQLParser;
import org.apache.shardingsphere.sql.parser.spi.SQLParserFacade;

/**
 * @author Adamancy Zhang at 15:01 on 2025-08-07
 */
public final class MySQLNdsParserFacade implements SQLParserFacade {

    @Override
    public String getDatabaseType() {
        return MySQLNdsType.NAME;
    }

    @Override
    public Class<? extends SQLLexer> getLexerClass() {
        return MySQLLexer.class;
    }

    @Override
    public Class<? extends SQLParser> getParserClass() {
        return MySQLParser.class;
    }
}
