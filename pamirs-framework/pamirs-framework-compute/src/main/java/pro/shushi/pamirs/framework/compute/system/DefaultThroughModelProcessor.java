package pro.shushi.pamirs.framework.compute.system;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.configure.MetaConfiguration;
import pro.shushi.pamirs.framework.configure.MetaRelationConfigModel;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.api.core.compute.ModelDefinitionComputer;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.compute.systems.relation.ThroughModelProcessor;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.BaseRelation;
import pro.shushi.pamirs.meta.base.IdRelation;
import pro.shushi.pamirs.meta.common.constants.MetaValueConstants;
import pro.shushi.pamirs.meta.common.util.ListUtils;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.meta.constant.FieldAttributeConstants;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;
import pro.shushi.pamirs.meta.util.AttributesUtils;
import pro.shushi.pamirs.meta.util.ModelUtils;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 中间模型处理器实现
 * <p>
 * 2020/10/30 12:16 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Component
public class DefaultThroughModelProcessor implements ThroughModelProcessor {

    @Resource
    private MetaConfiguration metaConfiguration;

    @Resource
    private ModelDefinitionComputer defaultModelDefinitionComputer;

    @Override
    public ModelDefinition generate(ComputeContext context, Meta meta, ModelDefinition sourceModel, ModelField relation) {
        // 构造多对多模型
        ModelDefinition throughModel = new ModelDefinition();
        throughModel.setSystemSource(SystemSourceEnum.RELATION);
        throughModel.enableMetaCompleted();
        return update(context, meta, sourceModel, relation, throughModel);
    }

    @Override
    public ModelDefinition update(ComputeContext context, Meta meta, ModelDefinition sourceModel, ModelField relation,
                                  ModelDefinition throughModel) {
        boolean generateFromField = null != throughModel.getSystemSource()
                && SystemSourceEnum.RELATION.equals(throughModel.getSystemSource());
        if (generateFromField && throughModel.isMetaCompleted()) {
            String through = relation.getThrough();
            String name = PStringUtils.camelCaseFromModel(through);
            String displayName = name;
            ModelDefinition modelReferences = meta.getModel(relation.getReferences());
            String module = sourceModel.getModule();
            String moduleAbbr = sourceModel.getModuleAbbr();
            String dsKey = sourceModel.getDsKey();
            String configDisplayName = AttributesUtils.get(relation, ModelField::getAttributes, FieldAttributeConstants.THROUGH_DISPLAY_NAME);
            if (null == modelReferences) {
                displayName = null != configDisplayName ? configDisplayName : I18nUtils.getMessage("pamirs-framework-compute.DefaultThroughModelProcessor.relation_display_name", sourceModel.getDisplayName());
            } else if (StringUtils.isNotBlank(sourceModel.getDisplayName()) && StringUtils.isNotBlank(modelReferences.getDisplayName())) {
                Map<String, String> nameMap = new HashMap<>(2);
                nameMap.put(sourceModel.getModel(), sourceModel.getDisplayName());
                nameMap.put(modelReferences.getModel(), modelReferences.getDisplayName());
                List<String> models = ModelUtils.sortNames(sourceModel.getModel(), modelReferences.getModel());
                displayName = null != configDisplayName
                        ? configDisplayName : I18nUtils.getMessage("pamirs-framework-compute.DefaultThroughModelProcessor.relation_between_display_name", nameMap.get(models.get(0)), nameMap.get(models.get(1)));
                ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(sourceModel.getModel());
                module = modelConfig.getModule();
                moduleAbbr = modelConfig.getModuleAbbr();
                dsKey = modelConfig.getOriginDsKey();
            }
            boolean isPkId = Optional.ofNullable(metaConfiguration.getRelation()).map(MetaRelationConfigModel::isPkId).orElse(false);
            throughModel.setModel(through)
                    .setDisplayName(displayName)
                    .setName(name)
                    .setSummary(displayName)
                    .setRemark(displayName)
                    .setLname(HashMap.class.getName())
                    .setModule(module)
                    .setModuleAbbr(moduleAbbr)
                    .setDsModule(module)
                    .setDataManager(Boolean.TRUE)
                    .setDsKey(dsKey)
                    .setTable(null)
                    .setTable(ModelDefinition.generateTable(throughModel))
                    .setType(relation.getRelationStore() ? ModelTypeEnum.STORE : ModelTypeEnum.TRANSIENT)
                    .setIsRelationship(Boolean.TRUE)
                    .setSuperModels(Lists.newArrayList(isPkId ? IdRelation.MODEL_MODEL : BaseRelation.MODEL_MODEL))
                    .setPriority(MetaValueConstants.priority)
                    .setUniques(null)
                    .setSign(through)
            ;
            throughModel.disableMetaCompleted();
            defaultModelDefinitionComputer.computeModel(context, meta, ListUtils.toList(throughModel));
        }
        return throughModel;
    }

}
