package pro.shushi.pamirs.framework.connectors.data.datasource;

import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.sql.Connection;

/**
 * 连接封装类
 * <p>
 * 2020/7/7 3:19 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class ConnectionWrapper {

    private Connection connection;

    private int timeout;

    private boolean auth;

    private String username;

    private String password;

}
