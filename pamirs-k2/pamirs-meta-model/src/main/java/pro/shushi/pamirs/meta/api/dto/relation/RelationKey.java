package pro.shushi.pamirs.meta.api.dto.relation;

import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.systems.ModelComputeApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.util.ListUtils;

/**
 * 关系查询key
 * <p>
 * 2021/3/29 4:48 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class RelationKey {

    private static final String RELATION_KEY_SEPARATOR = "#&#";

    private String key;

    private String modelAndField;

    private transient ModelFieldConfig modelFieldConfig;

    private Object source;

    public static String key(ModelFieldConfig modelFieldConfig, Object data) {
        String model = modelFieldConfig.getModel();
        String[] relationFields = ListUtils.toArray(modelFieldConfig.getRelationFields());
        String values = Models.compute().generateValidUniqueFieldValue(model, relationFields, data);
        if (ModelComputeApi.VALUES_SEPARATOR.equals(values)) {
            return null;
        }
        return model + CharacterConstants.SEPARATOR_OCTOTHORPE + modelFieldConfig.getField()
                + RELATION_KEY_SEPARATOR + values;
    }

    public static RelationKey init(ModelFieldConfig modelFieldConfig, Object data) {
        String key = key(modelFieldConfig, data);
        String modelAndField = modelFieldConfig.getModel() + CharacterConstants.SEPARATOR_OCTOTHORPE + modelFieldConfig.getField();
        return new RelationKey().setModelAndField(modelAndField).setKey(key).setSource(data).setModelFieldConfig(modelFieldConfig);
    }

}
