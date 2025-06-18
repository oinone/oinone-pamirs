package pro.shushi.pamirs.meta.constant;

/**
 * 模块函数常量
 *
 * @author cpc at 14:41 on 2025-06-12
 */
public interface ModuleFunctionConstants {

    String BEAN_MODEL_FUNCTION_PUBLISH = "modelFunctionPublish";
    String FUN_PUBLISH_SERVICE = "publishService";

    String FUN_BUILD_TABLE = "buildTable";
    String FUN_DROP_TABLE = "dropTable";

    interface DataSourceFunction {
        String BEAN_NAME = "dataSourceFetcher";
        String GET_DATABASE_FUN = "getDatabase";
    }

    interface ClassMetadataFunction {
        String BEAN_NAME = "uxClassMetadataFetcher";
        String GET_CLASS_METADATA_FUN = "getClassMetadata";
    }
}
