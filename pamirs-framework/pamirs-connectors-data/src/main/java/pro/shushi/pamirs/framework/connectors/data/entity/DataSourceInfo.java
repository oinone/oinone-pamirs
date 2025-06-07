package pro.shushi.pamirs.framework.connectors.data.entity;

import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 数据源URL信息
 *
 * @author Adamancy Zhang at 10:33 on 2024-10-16
 */
@Data
public class DataSourceInfo implements Serializable {

    private static final long serialVersionUID = 2268803723465020814L;

    private String url;

    private String schema;

    private String database;

    private String protocol;

    private String host;

    private int port;

    private Map<String, List<String>> parameters;
}
