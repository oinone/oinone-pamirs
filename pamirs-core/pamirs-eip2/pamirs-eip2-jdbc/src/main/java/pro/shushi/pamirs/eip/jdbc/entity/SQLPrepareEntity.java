package pro.shushi.pamirs.eip.jdbc.entity;

import java.util.Map;

/**
 * SQL预处理实体
 *
 * @author Adamancy Zhang at 10:05 on 2024-06-06
 */
public class SQLPrepareEntity {

    private final String originSql;

    private final String prepareSql;

    private final Map<Integer, String> prepareParameters;

    public SQLPrepareEntity(String originSql,
                            String prepareSql,
                            Map<Integer, String> prepareParameters) {
        this.originSql = originSql;
        this.prepareSql = prepareSql;
        this.prepareParameters = prepareParameters;
    }

    public String getOriginSql() {
        return originSql;
    }

    public String getPrepareSql() {
        return prepareSql;
    }

    public Map<Integer, String> getPrepareParameters() {
        return prepareParameters;
    }
}