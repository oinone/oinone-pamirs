package pro.shushi.pamirs.framework.connectors.data.api.domain.wrapper;

import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.util.ModelUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 模型配置封装代理类
 * <p>
 * 2020/6/24 2:33 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class ModelWrapper {

    private String module;

    private String model;

    private String table;

    private String type;

    private String summary;

    private String remark;

    private List<String> pk;

    private Boolean isRelationship;

    private Boolean isChangeTable;

    private String dsKey;

    List<String> uniques;

    List<String> indexes;

    private List<FieldWrapper> modelFields;

    public static ModelWrapper wrap(ModelDefinition modelDefinition) {
        ModelWrapper modelWrapper = new ModelWrapper()
                .setModule(modelDefinition.getModule())
                .setModel(modelDefinition.getModel())
                .setTable(modelDefinition.getTable())
                .setType(modelDefinition.getType().value())
                .setSummary(modelDefinition.getSummary())
                .setRemark(modelDefinition.getRemark())
                .setPk(modelDefinition.getPk())
                .setIsChangeTable(ModelUtils.isChangeTableInherited(modelDefinition))
                .setIsRelationship(modelDefinition.getIsRelationship())
                .setDsKey(modelDefinition.getDsKey())
                .setUniques(modelDefinition.getUniques())
                .setIndexes(modelDefinition.getIndexes());
        List<FieldWrapper> modelFieldList = new ArrayList<>();
        for (ModelField modelField : modelDefinition.getModelFields()) {
            modelFieldList.add(FieldWrapper.wrap(modelField));
        }
        modelWrapper.setModelFields(modelFieldList);
        return modelWrapper;
    }

    public static ModelWrapper wrap(String module, ModelConfig modelConfig) {
        ModelWrapper modelWrapper = new ModelWrapper()
                .setModule(module)
                .setModel(modelConfig.getModel())
                .setTable(modelConfig.getTable())
                .setType(modelConfig.getType().value())
                .setSummary(modelConfig.getSummary())
                .setRemark(modelConfig.getRemark())
                .setPk(modelConfig.getPk())
                .setIsChangeTable(ModelUtils.isChangeTableInherited(modelConfig.getModelDefinition()))
                .setIsRelationship(modelConfig.getRelationship())
                .setDsKey(modelConfig.getDsKey())
                .setUniques(modelConfig.getUniques())
                .setIndexes(modelConfig.getIndexes());
        List<FieldWrapper> modelFieldList = new ArrayList<>();
        for (ModelFieldConfig modelFieldConfig : modelConfig.getModelFieldConfigList()) {
            modelFieldList.add(FieldWrapper.wrap(modelFieldConfig));
        }
        modelWrapper.setModelFields(modelFieldList);
        return modelWrapper;
    }

}
