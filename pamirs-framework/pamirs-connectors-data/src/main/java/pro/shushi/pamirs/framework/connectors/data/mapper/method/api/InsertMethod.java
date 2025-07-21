package pro.shushi.pamirs.framework.connectors.data.mapper.method.api;

import pro.shushi.pamirs.framework.connectors.data.mapper.template.ScriptTemplate;
import pro.shushi.pamirs.framework.connectors.data.mapper.template.SqlTemplate;

/**
 * @author Adamancy Zhang at 12:18 on 2023-06-26
 */
public interface InsertMethod extends SQLMethod {

    InsertMethod setDuplicateKeyUpdate(boolean duplicateKeyUpdate);

    InsertMethod setValuePrefix(String valuePrefix);

    InsertMethod setBatch(boolean batch);

    default String insert() {
        return String.format(ScriptTemplate.SCRIPT, String.format(SqlTemplate.INSERT, table(), sqlInsert(), sqlValues(), onDuplicateKeyUpdate()));
    }

    default String insertBatch() {
        return String.format(ScriptTemplate.SCRIPT, String.format(SqlTemplate.INSERT, table(), sqlInsert(), batchSqlValues(), onDuplicateKeyUpdate()));
    }

    String table();

    String sqlInsert();

    String sqlValues();

    String batchSqlValues();

    String onDuplicateKeyUpdate();
}
