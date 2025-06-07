package pro.shushi.pamirs.meta.api.dto.meta;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * FuseMeta
 *
 * @author yakir on 2025/01/23 13:56.
 */
public class FuseMeta implements Serializable {

    private static final long serialVersionUID = 8471103109791967091L;

    private static final Map<String/* model */, FuseMetaData> fuseModelMap = new HashMap<>();

    public static void put(String model, String module, String lname) {
        FuseMetaData data = new FuseMetaData(model, module, lname);
        fuseModelMap.putIfAbsent(model, data);
    }

    public static boolean contains(String model) {
        return fuseModelMap.containsKey(model);
    }

    public static FuseMetaData get(String model) {
        return fuseModelMap.get(model);
    }

    public static void clear() {
        fuseModelMap.clear();
    }

    public static String lname(ModelConfig modelConfig) {
        ModelDefinition modelD = modelConfig.getModelDefinition();
        String lname = Optional.ofNullable(modelD.getFname())
                .filter(StringUtils::isNotBlank)
                .orElse(null);
        if (null == lname) {
            lname = modelConfig.getLname();
        }
        return lname;
    }

    public static class FuseMetaData implements Serializable {

        private static final long serialVersionUID = -3397355944701369653L;

        private String model;
        private String module;
        private String lname;

        public FuseMetaData(String model, String module, String lname) {
            this.model = model;
            this.module = module;
            this.lname = lname;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getModule() {
            return module;
        }

        public void setModule(String module) {
            this.module = module;
        }

        public String getLname() {
            return lname;
        }

        public void setLname(String lname) {
            this.lname = lname;
        }
    }
}
