package pro.shushi.pamirs.meta.api.dto.db;

import pro.shushi.pamirs.meta.annotation.fun.Data;

/**
 * 列定义
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:51 上午
 *
 */
@Data
public class Column {

    private String name;

    private String field;

    private String type;

    private boolean nullable;

    private String defaultValue;

    private String extra;

    private Long ordinalPosition;

    private String comment;

}
