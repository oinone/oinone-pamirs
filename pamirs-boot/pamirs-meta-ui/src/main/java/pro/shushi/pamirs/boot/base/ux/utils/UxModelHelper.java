package pro.shushi.pamirs.boot.base.ux.utils;

import pro.shushi.pamirs.boot.base.ux.entity.RegisterSearchWidget;
import pro.shushi.pamirs.boot.base.ux.entity.UxModelEntity;
import pro.shushi.pamirs.boot.base.ux.entity.UxModelFieldEntity;
import pro.shushi.pamirs.boot.base.ux.entity.annotation.UxWidgetWrapper;
import pro.shushi.pamirs.boot.base.ux.service.UxClassMetadataFetcher;
import pro.shushi.pamirs.boot.base.ux.spi.ViewTemplateStrategyApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;

/**
 * UxModel 帮助类
 *
 * @author Adamancy Zhang at 15:17 on 2025-06-17
 */
public class UxModelHelper {


    private UxModelHelper() {
        // reject create object
    }

    public static UxModelEntity getModel(String model) {
        ModelConfig modelConfig = PamirsSession.getContext().getSimpleModelConfig(model);
        if (modelConfig == null) {
            return null;
        }
        UxModelEntity uxModel = UxClassMetadataFetcher.getApi().getClassMetadataByModel(modelConfig);
        if (uxModel == null) {
            return null;
        }
        uxModel.setModelConfig(modelConfig);
        return uxModel;
    }

    public static UxModelFieldEntity getModelField(UxModelEntity uxModel, ModelFieldConfig modelFieldConfig) {
        String field = modelFieldConfig.getField();
        UxModelFieldEntity modelFieldEntity = uxModel.getUxModelFieldEntity(field);
        if (modelFieldEntity == null) {
            modelFieldEntity = new UxModelFieldEntity();
            modelFieldEntity.setField(field);
        }
        modelFieldEntity.setModelFieldConfig(modelFieldConfig);
        return modelFieldEntity;
    }

    public static UxWidgetWrapper getRegisterSearchWidget(ModelDefinition modelDefinition, ModelField modelField) {
        RegisterSearchWidget registerSearchWidget = ViewTemplateStrategyApi.getApi().computeSearchWidget(modelDefinition, modelField);
        if (registerSearchWidget == null) {
            return null;
        }
        return UxWidgetWrapper.wrap(registerSearchWidget);
    }
}
