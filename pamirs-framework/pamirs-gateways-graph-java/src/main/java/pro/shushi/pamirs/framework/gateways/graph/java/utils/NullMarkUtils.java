package pro.shushi.pamirs.framework.gateways.graph.java.utils;

import graphql.schema.DataFetchingEnvironment;
import pro.shushi.pamirs.framework.common.entry.NullValue;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.base.D;

import java.util.Map;

/**
 * Null Value Handler
 *
 * @author Adamancy Zhang at 01:10 on 2024-05-04
 */
public final class NullMarkUtils {

    private NullMarkUtils() {
        //reject create object
    }

    @SuppressWarnings("unchecked")
    public static Object handleDataFetchingEnvironmentNullMark(DataFetchingEnvironment dataFetchingEnvironment, ModelFieldConfig modelFieldConfig) {
        Object source = dataFetchingEnvironment.getSource();
        if (NullValue.INSTANCE.equals(source)) {
            return NullValue.INSTANCE;
        }
        Map<String, Object> target;
        if (source instanceof D) {
            target = ((D) source).get_d();
        } else if (source instanceof Map) {
            target = (Map<String, Object>) source;
        } else {
            return null;
        }
        String lname = modelFieldConfig.getLname();
        Object value = target.get(lname);
        if (value == null && target.containsKey(lname)) {
            return NullValue.INSTANCE;
        }
        return null;
    }
}
