package pro.shushi.pamirs.framework.connectors.data.api.ddl;

import pro.shushi.pamirs.meta.annotation.fun.Data;

/**
 * 含分库分表通配符的ddl
 * <p>
 * 2020/7/9 2:38 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class TableDdl {

    private String dsKey;

    private String module;

    private String model;

    private String table;

    private String ddl;

}
