package pro.shushi.pamirs.framework.connectors.data.elastic.common.util;

import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.api.prefix.KeyPrefixManager;

import static pro.shushi.pamirs.meta.common.constants.CharacterConstants.SEPARATOR_UNDERLINE;

/**
 * IndexNaming
 *
 * <pre>
 *     Lowercase only
 *     Cannot include \, /, *, ?, ", <, >, |, ` ` (space character), ,, #,:
 *     Cannot start with -, _, +
 *     Cannot be . or ..
 *     Cannot be longer than 255 bytes (note it is bytes, so multi-byte characters will count towards the 255 limit faster)
 * </pre>
 *
 * @author yakir on 2020/04/18 13:43.
 */
public class IndexNaming {

    public static String alias(String model) {
        String[] modelNameArr = model.split("\\.");
        String[] modelNameArr1 = new String[modelNameArr.length];
        for (int i = 0; i < modelNameArr.length; i++) {
            String modelName0 = modelNameArr[i];
            String one = modelName0.substring(0, 1).toLowerCase();
            String two = modelName0.substring(1);
            modelNameArr1[i] = one + two;
        }
        String result = String.join("-", modelNameArr1);

        return CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE).convert(result);
    }

    public static String alias(String tenant, String naming) {
        String cTenant = CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE).convert(tenant);
        return (null == cTenant ? "" : cTenant.concat(SEPARATOR_UNDERLINE).concat(SEPARATOR_UNDERLINE)).concat(naming);
    }

    public static String aliasByModel(String model) {
        String naming = alias(model);
        String tenant = KeyPrefixManager.generate("");
        String cTenant = CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE).convert(tenant);
        return (StringUtils.isBlank(cTenant) ? "" : cTenant.concat(SEPARATOR_UNDERLINE).concat(SEPARATOR_UNDERLINE)).concat(naming);
    }

    public static String aliasByModel(String tenant, String model) {
        String naming = alias(model);
        String cTenant = CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE).convert(tenant);
        return (StringUtils.isBlank(cTenant) ? "" : cTenant.concat(SEPARATOR_UNDERLINE).concat(SEPARATOR_UNDERLINE)).concat(naming);
    }

    public static String row(String column) {
        return CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE).convert(column);
    }

    /**
     * index
     *
     * <pre>
     *     pamirs  yakir.Foo
     *     pamirs  yakir.FooSearch   ->  pamirs__yakir-foo_search
     * </pre>
     *
     * @param naming String
     * @param pos    int
     * @return String
     */
    public static String index(String naming, int pos) {
        return naming.concat("+").concat(String.valueOf(pos));
    }

}