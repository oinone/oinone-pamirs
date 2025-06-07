package pro.shushi.pamirs.framework.connectors.data.api.ddl;

import pro.shushi.pamirs.meta.annotation.fun.Data;

/**
 * schema table映射
 * <p>
 * 2020/8/13 4:59 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class SchemaTableKey {

    private String dsKey;

    private String tableSchema;

    private String tableName;

    private Object tableShardingNode;

    public static String fetchDsKey(SchemaTableKey schemaTableKey) {
        if (null == schemaTableKey) {
            return null;
        }
        return schemaTableKey.getDsKey();
    }

    public static String fetchTableSchema(SchemaTableKey schemaTableKey) {
        if (null == schemaTableKey) {
            return null;
        }
        return schemaTableKey.getTableSchema();
    }

    public static String fetchTableName(SchemaTableKey schemaTableKey) {
        if (null == schemaTableKey) {
            return null;
        }
        return schemaTableKey.getTableName();
    }

    public static Object fetchTableShardingNode(SchemaTableKey schemaTableKey) {
        if (null == schemaTableKey) {
            return null;
        }
        return schemaTableKey.getTableShardingNode();
    }

}
