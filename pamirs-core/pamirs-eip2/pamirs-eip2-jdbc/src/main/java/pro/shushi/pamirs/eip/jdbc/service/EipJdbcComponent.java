package pro.shushi.pamirs.eip.jdbc.service;

import pro.shushi.pamirs.eip.api.model.connector.EipConnector;

/**
 * EipJdbcComponent
 *
 * @author yakir on 2025/06/16 19:08.
 */
public interface EipJdbcComponent {

    String dbType();

    String jdbcUrl(EipConnector connector);

}
