package pro.shushi.pamirs.framework.gateways.constant;

/**
 * @author Adamancy Zhang
 * @date 2021-01-11 18:48
 */
public class RsqlConstant {

    public static final String JSON_EXTRACT_FUNCTION = "JSON_EXTRACT";

    public static final String JSON_EXTRACT_FUNCTION_FORMAT = "JSON_EXTRACT(%s, '%s')";

    public static final String JSON_CONTAINS_FUNCTION = "JSON_CONTAINS";

    public static final String JSON_CONTAINS_FUNCTION_FORMAT = "JSON_CONTAINS(%s, '%s', '$')";

    public static final String JSON_SEARCH_FUNCTION = "JSON_SEARCH";

    public static final String JSON_SEARCH_FUNCTION_FORMAT = "JSON_SEARCH(%s, '%s', 'one', NULL, '$')";

    public static final String JSON_OBJECT_PREFIX = "$.";

    public static final String JSON_ARRAY_PREFIX = "$[*].";

    public static final int SUPPORTED_RELATION_PROPERTY_COUNT = 2;

    public static String getJsonArrayIndex(int index) {
        return "$[" + index + "].";
    }
}
