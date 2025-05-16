package pro.shushi.pamirs.framework.connectors.data.api.ddl;

import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * ddl结果
 * 2020/6/23 10:23 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class DdlResult {

    private List<TableDdl> dropList = new ArrayList<>();

    private List<TableDdl> createOrAlterList = new ArrayList<>();

    public void addDropDdl(String dsKey, String module, String model, String table, String ddl) {
        dropList.add(new TableDdl().setDsKey(dsKey).setModule(module).setModel(model).setTable(table).setDdl(ddl));
    }

    public void addCreateOrAlterDdl(TableDdl tableDdl) {
        createOrAlterList.add(tableDdl);
    }

}
