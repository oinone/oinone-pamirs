package pro.shushi.pamirs.eip.api.constant;

import pro.shushi.pamirs.eip.api.IEipContext;

/**
 * Eip 上下文常量
 *
 * @author Adamancy Zhang on 2021-02-05 11:36
 */
public class EipContextConstant {

    public static final String CONTEXT_KEY = "context";

    public static final String INTERFACE_KEY = "interface";

    public static final String EMPTY_KEY = "empty";

    public static final String RESULT_KEY = "result";

    public static final String LIST_KEY = "list";

    public static final String DATA_KEY = "data";

    public static final String FINAL_BODY_KEY = "__final__body__";

    public static final String INTERFACE_BASE_PATH_KEY = "basePath";

    public static final String INTERFACE_BASE_PATH_PARAMETER = generatorExchangePropertyParameterKey(INTERFACE_BASE_PATH_KEY);

    //region 执行上下文固定键值

    public static final String DEFAULT_LIST_PARAMETER_KEY = LIST_KEY + IEipContext.DEFAULT_LIST_FLAG_KEY;

    public static final String CONFIG_KEY = "config";

    public static final String CONFIGURATION_KEY = CONFIG_KEY + ".config";

    public static final String CONFIGURATION_ENVIRONMENT_KEY = CONFIGURATION_KEY + ".environment";

    public static final String CONFIGURATION_TAGS_KEY = CONFIGURATION_KEY + ".tags";

    public static final String INCREMENTAL_TAGS_KEY = CONFIG_KEY + ".incremental.tags";

    public static final String OPEN_API_ROUTE_PREFIX = "openapi/";

    //endregion

    public static String generatorExchangePropertyParameterKey(String key) {
        return "language:simple:$simple{exchangeProperty." + key + "}+";
    }
}
