package pro.shushi.pamirs.framework.connectors.data.mapper.template;

/**
 * 脚本 模板
 * <p>
 * 2020/6/29 12:16 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface ScriptTemplate {

    String SCRIPT = "<script>%s</script>";

    String BRACKET = "(%s)";

    String IN_CONDITION = "AND %s IN (%s)";

    String UPDATED_VERSION_VAL_KEY = "#updatedVersionVal#";

}
