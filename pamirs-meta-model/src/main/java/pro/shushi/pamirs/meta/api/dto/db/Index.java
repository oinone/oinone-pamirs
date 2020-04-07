package pro.shushi.pamirs.meta.api.dto.db;

import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.List;

/**
 * 索引
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:51 上午
 *
 */
@Data
public class Index {

    private String table;

    private String name;

    private List<String> column;

    private boolean unique;

}
