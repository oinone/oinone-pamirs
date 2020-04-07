package pro.shushi.pamirs.meta.api.dto.db;

import pro.shushi.pamirs.meta.annotation.fun.Data;

/**
 * 库结构
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:51 上午
 *
 */
@Data
public class Database {

    private String database;

    private String defaultCharacterSetName;

    private String defaultCollationName;

}
