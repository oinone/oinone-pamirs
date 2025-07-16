package pro.shushi.pamirs.framework.connectors.data.autoconfigure.pamirs.handler;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.BooleanTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.autoconfigure.ConfigurationCustomizer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * BooleanTypeHandler
 *
 * @author Adamancy Zhang at 14:20 on 2025-07-16
 */
@Component
public class BooleanTypeHandlerRegistry implements ConfigurationCustomizer {

    @Override
    public void customize(Configuration configuration) {
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        typeHandlerRegistry.register(Boolean.class, new PamirsBooleanTypeHandler());
        typeHandlerRegistry.register(boolean.class, new PamirsBooleanTypeHandler());
        typeHandlerRegistry.register(JdbcType.BOOLEAN, new PamirsBooleanTypeHandler());
        typeHandlerRegistry.register(JdbcType.BIT, new PamirsBooleanTypeHandler());
    }

    /**
     * BooleanTypeHandler
     *
     * @author Adamancy Zhang at 14:24 on 2025-07-16
     */
    public static class PamirsBooleanTypeHandler extends BooleanTypeHandler {

        @Override
        public void setNonNullParameter(PreparedStatement ps, int i, Boolean parameter, JdbcType jdbcType)
                throws SQLException {
            if (JdbcType.NUMERIC.equals(jdbcType)) {
                if (Boolean.TRUE.equals(parameter)) {
                    ps.setInt(i, 1);
                } else {
                    ps.setInt(i, 0);
                }
                return;
            }
            ps.setBoolean(i, parameter);
        }
    }
}
