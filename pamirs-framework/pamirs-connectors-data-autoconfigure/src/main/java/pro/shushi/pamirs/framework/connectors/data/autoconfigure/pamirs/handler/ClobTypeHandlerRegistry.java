package pro.shushi.pamirs.framework.connectors.data.autoconfigure.pamirs.handler;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.ClobTypeHandler;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.autoconfigure.ConfigurationCustomizer;

import java.sql.Clob;

/**
 * ClobTypeHandler
 *
 * @author Adamancy Zhang at 19:00 on 2023-06-27
 */
@Component
public class ClobTypeHandlerRegistry implements ConfigurationCustomizer {

    @Override
    public void customize(Configuration configuration) {
        configuration.getTypeHandlerRegistry().register(Clob.class, ClobTypeHandler.class);
    }
}
