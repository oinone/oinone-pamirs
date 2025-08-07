package pro.shushi.pamirs.framework.connectors.data.dialect.shardingsphere;

import org.apache.shardingsphere.infra.metadata.model.physical.jdbc.handler.DatabaseMetaDataDialectHandler;
import org.apache.shardingsphere.sql.parser.sql.common.constant.QuoteCharacter;

import java.util.Properties;

/**
 * @author Adamancy Zhang at 10:51 on 2025-08-07
 */
public final class MySQLNdsDatabaseMetaDataDialectHandler implements DatabaseMetaDataDialectHandler {

    private Properties props;

    @Override
    public QuoteCharacter getQuoteCharacter() {
        return QuoteCharacter.BACK_QUOTE;
    }

    @Override
    public String getType() {
        return "MySQL-Nds";
    }

    @Override
    public Properties getProps() {
        return props;
    }

    @Override
    public void setProps(Properties props) {
        this.props = props;
    }
}
