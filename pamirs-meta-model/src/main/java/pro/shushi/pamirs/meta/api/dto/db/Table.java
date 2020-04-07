package pro.shushi.pamirs.meta.api.dto.db;

import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 表结构
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:51 上午
 *
 */
@Data
public class Table {

    private String name;

    private String model;

    private String database;

    private String comment;

    private List<Column> columns = new ArrayList<>();

    private List<Index> indexes = new ArrayList<>();

}
