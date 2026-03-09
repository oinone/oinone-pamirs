package pro.shushi.pamirs.ux.draft.metadata;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.config.api.ModelConfigExtendApi;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.FieldStrategyEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.ux.draft.constant.DraftConstants;

import java.util.List;

/**
 * 草稿字段元数据动态补充
 *
 * @author Adamancy Zhang at 15:15 on 2025-10-29
 */
@Order(10)
@Component
public class DraftCodeMetadataExtend implements ModelConfigExtendApi {

    @Override
    public void getModelFieldConfigList(ModelConfig modelConfig, List<ModelFieldConfig> modelFieldConfigList) {
        modelFieldConfigList.add(generatorDraftCodeField(modelConfig.getModel()));
    }

    @Override
    public void getModelFieldConfigListSort(ModelConfig modelConfig, List<ModelFieldConfig> modelFieldConfigList) {
        modelFieldConfigList.add(generatorDraftCodeField(modelConfig.getModel()));
    }

    private ModelFieldConfig generatorDraftCodeField(String model) {
        ModelField modelField = new ModelField();
        modelField.setModel(model);
        modelField.setField(DraftConstants.DRAFT_CODE_FILED);
        modelField.setName(DraftConstants.DRAFT_CODE_FILED);
        modelField.setLname(DraftConstants.DRAFT_CODE_FILED);
        modelField.setTtype(TtypeEnum.STRING);
        modelField.setMulti(false);
        modelField.setLtype(String.class.getName());
        modelField.setDisplayName("草稿编码");
        modelField.setInvisible(true);
        modelField.setStore(false);
        modelField.setRelationStore(false);
        modelField.setTranslate(false);
        modelField.setOnlyColumn(true);
        modelField.setInsertStrategy(FieldStrategyEnum.NEVER);
        modelField.setUpdateStrategy(FieldStrategyEnum.NEVER);
        modelField.setBatchStrategy(FieldStrategyEnum.NEVER);
        modelField.setWhereStrategy(FieldStrategyEnum.NEVER);
        ModelFieldConfig modelFieldConfig = new ModelFieldConfig(modelField);
        modelFieldConfig.setIsVirtual(true);
        return modelFieldConfig;
    }
}
