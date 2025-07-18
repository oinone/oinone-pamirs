package pro.shushi.pamirs.framework.connectors.data.autoconfigure.pamirs.extend;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.spi.SessionInitApi;
import pro.shushi.pamirs.meta.api.core.session.SessionClearApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ORM Column To Lname Cache
 *
 * @author Adamancy Zhang at 13:30 on 2025-07-14
 */
@Component
public class OrmColumnToLnameCache implements SessionInitApi, SessionClearApi {

    private static final TransmittableThreadLocal<Map<String, Map<String, String>>> storage = new TransmittableThreadLocal<>();

    @Override
    public void init(HttpServletRequest request, String moduleName, PamirsRequestParam requestParam) {
        Map<String, Map<String, String>> mappings = storage.get();
        if (mappings == null) {
            mappings = new ConcurrentHashMap<>();
            storage.set(mappings);
        }
    }

    public static Map<String, String> getMapping(ModelConfig modelConfig) {
        Map<String, Map<String, String>> mappings = storage.get();
        if (mappings == null) {
            // disabled thread local cache
            mappings = new ConcurrentHashMap<>();
        }
        return mappings.computeIfAbsent(modelConfig.getModel(), model -> {
            Map<String, String> columnToLnameMapping = new HashMap<>();
            List<ModelFieldConfig> modelFieldConfigList = modelConfig.getModelFieldConfigList();
            for (ModelFieldConfig modelFieldConfig : modelFieldConfigList) {
                String column = modelFieldConfig.getColumn();
                if (StringUtils.isBlank(column)) {
                    continue;
                }
                columnToLnameMapping.put(column, modelFieldConfig.getLname());
            }
            return columnToLnameMapping;
        });
    }

    @Override
    public void clear() {
        storage.remove();
    }
}
