package pro.shushi.pamirs.framework.connectors.data.autoconfigure.pamirs.extend;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.spi.SessionInitApi;
import pro.shushi.pamirs.meta.api.core.session.SessionClearApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestParam;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;

import jakarta.servlet.http.HttpServletRequest;
import java.sql.Time;
import java.util.Date;
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

    private static final Map<String, Map<String, Class<?>>> STATIC_STORAGE = new ConcurrentHashMap<>();

    private static final TransmittableThreadLocal<Map<String, Map<String, Class<?>>>> STORAGE = new TransmittableThreadLocal<>();

    @Override
    public void init(HttpServletRequest request, String moduleName, PamirsRequestParam requestParam) {
        Map<String, Map<String, Class<?>>> mappings = STORAGE.get();
        if (mappings == null) {
            mappings = new ConcurrentHashMap<>();
            STORAGE.set(mappings);
        }
    }

    public static Map<String, Class<?>> getMapping(ModelConfig modelConfig) {
        if (modelConfig.isStaticConfig() || SystemSourceEnum.isBase(modelConfig.getModelDefinition().getSystemSource())) {
            return STATIC_STORAGE.computeIfAbsent(modelConfig.getModel(), new Mapping(modelConfig)::accept);
        }
        Map<String, Map<String, Class<?>>> mappings = STORAGE.get();
        if (mappings == null) {
            // disabled thread local cache
            mappings = new ConcurrentHashMap<>();
        }
        return mappings.computeIfAbsent(modelConfig.getModel(), new Mapping(modelConfig)::accept);
    }

    @Override
    public void clear() {
        STORAGE.remove();
    }

    private static class Mapping {

        private final ModelConfig modelConfig;

        public Mapping(ModelConfig modelConfig) {
            this.modelConfig = modelConfig;
        }

        public Map<String, Class<?>> accept(String model) {
            Map<String, Class<?>> columnToLnameMapping = new HashMap<>();
            List<ModelFieldConfig> modelFieldConfigList = modelConfig.getModelFieldConfigList();
            for (ModelFieldConfig modelFieldConfig : modelFieldConfigList) {
                String lname = modelFieldConfig.getLname();
                if (StringUtils.isBlank(lname)) {
                    continue;
                }
                TtypeEnum exactTtype = modelFieldConfig.getModelField().getExactTtype();
                if (exactTtype == null) {
                    continue;
                }
                Class<?> clazz = convertLtype(exactTtype.value(), modelFieldConfig.getMulti(), modelFieldConfig.getLtype());
                if (clazz == null) {
                    continue;
                }
                columnToLnameMapping.put(lname, clazz);
            }
            return columnToLnameMapping;
        }

        private Class<?> convertLtype(String ttype, Boolean multi, String ltype) {
            if (TtypeEnum.ENUM.value().equals(ttype) && !Boolean.TRUE.equals(multi)) {
                Class<?> ltypeClazz = TypeUtils.getClass(ltype);
                if (TypeUtils.isIEnumClass(ltypeClazz)) {
                    return ltypeClazz;
                }
            } else if (TtypeEnum.TIME.value().equals(ttype) && Date.class.getName().equals(ltype)) {
                return Time.class;
            } else if (TtypeEnum.DATE.value().equals(ttype) && Date.class.getName().equals(ltype)) {
                return java.sql.Date.class;
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
}
