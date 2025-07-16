package pro.shushi.pamirs.framework.connectors.data.autoconfigure.pamirs.extend;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.spi.SessionInitApi;
import pro.shushi.pamirs.meta.api.core.session.SessionClearApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestParam;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ORM Lname To Ltype Cache
 *
 * @author Adamancy Zhang at 13:30 on 2025-07-14
 */
@Component
public class OrmLnameToLtypeCache implements SessionInitApi, SessionClearApi {

    private static final TransmittableThreadLocal<Map<String, Map<String, Class<?>>>> storage = new TransmittableThreadLocal<>();

    @Override
    public void init(HttpServletRequest request, String moduleName, PamirsRequestParam requestParam) {
        Map<String, Map<String, Class<?>>> mappings = storage.get();
        if (mappings == null) {
            mappings = new ConcurrentHashMap<>();
            storage.set(mappings);
        }
    }

    public static Map<String, Class<?>> getMapping(ModelConfig modelConfig) {
        Map<String, Map<String, Class<?>>> mappings = storage.get();
        if (mappings == null) {
            // disabled thread local cache
            mappings = new HashMap<>();
        }
        return mappings.computeIfAbsent(modelConfig.getModel(), model -> {
            Map<String, Class<?>> columnToLnameMapping = new HashMap<>();
            List<ModelFieldConfig> modelFieldConfigList = modelConfig.getModelFieldConfigList();
            for (ModelFieldConfig modelFieldConfig : modelFieldConfigList) {
                String lname = modelFieldConfig.getLname();
                if (StringUtils.isBlank(lname)) {
                    continue;
                }
                Class<?> clazz = convertLtype(modelFieldConfig.getTtype(), modelFieldConfig.getMulti(), modelFieldConfig.getLtype());
                if (clazz == null) {
                    continue;
                }
                columnToLnameMapping.put(lname, clazz);
            }
            return columnToLnameMapping;
        });
    }

    @Override
    public void clear() {
        storage.remove();
    }

    private static Class<?> convertLtype(String ttype, Boolean multi, String ltype) {
        if (TtypeEnum.ENUM.value().equals(ttype) && !Boolean.TRUE.equals(multi)) {
            Class<?> ltypeClazz = TypeUtils.getClass(ltype);
            if (TypeUtils.isIEnumClass(ltypeClazz)) {
                return ltypeClazz;
            }
        }
        switch (ltype) {
            case "boolean":
                return boolean.class;
            case "java.lang.Boolean":
                return Boolean.class;
            case "int":
                return int.class;
            case "java.lang.Integer":
                return Integer.class;
            case "long":
                return long.class;
            case "java.lang.Long":
                return Long.class;
            default:
                return null;
        }
    }
}
