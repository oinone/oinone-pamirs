package pro.shushi.pamirs.framework.connectors.data.mapper.method.api;

/**
 * @author Adamancy Zhang at 12:18 on 2023-06-26
 */
public interface InsertMethod extends SQLMethod {

    InsertMethod setDuplicateKeyUpdate(boolean duplicateKeyUpdate);

    InsertMethod setValuePrefix(String valuePrefix);

    InsertMethod setBatch(boolean batch);

    String table();

    String sqlInsert();

    String sqlValues();

    String batchSqlValues();

    String onDuplicateKeyUpdate();
}
