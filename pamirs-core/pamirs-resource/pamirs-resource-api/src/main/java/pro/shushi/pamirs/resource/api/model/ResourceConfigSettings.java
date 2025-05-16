package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.List;
import java.util.Map;

@Model.model(ResourceConfigSettings.MODEL_MODEL)
@Model(displayName = "应用配置")
public class ResourceConfigSettings extends TransientModel {

    public static final String MODEL_MODEL = "resource.ResourceConfigSettings";

    @Field.one2many
    @Field(displayName = "业务配置模型列表")
    private List<ModelDefinition> models;

    @Field.one2many
    @Field(displayName = "业务应用")
    private List<ModuleDefinition> modules;

    @Action
    public Map<String, Object> loadModels(Map<String, Object> settings) {

       /* if (settings == null) {
            settings = new HashMap<>();
        }

        List<Model> models = new ModelDefinition().disableCompute().disableRelation().findAll(new Condition().andEqual("name", "ResourceConfigSetting"));
        if (CollectionUtils.isNotEmpty(models)) {
            Map<Long, Model> modelIdMap = new HashMap<>();
            models.stream().forEach(_v->{
                modelIdMap.put(_v.getId(), _v);
            });

            List<ModelData> modelDataList = new ModelData().disableCompute().disableRelation().findAll(new Condition().andEqual("pro.shushi.pamirs.user.pro.shushi.pamirs.user.core.base.model", Model.class.getName()).andIn("resId", new ArrayList<>(modelIdMap.keySet())));
            Map<String, Model> moduleModelMap = new HashMap<>();
            Set<String> moduleSet = new HashSet<>();
            modelDataList.stream().forEach(_v -> {
                moduleSet.add(_v.getModule());
                Model pro.shushi.pamirs.user.pro.shushi.pamirs.user.core.base.model = modelIdMap.get(_v.getResId());
                moduleModelMap.put(_v.getModule(), pro.shushi.pamirs.user.pro.shushi.pamirs.user.core.base.model);
            });
            List<Module> modules = new Module().disableCompute().disableRelation().andRead("icon").findAll(new Condition().andIn("name", new ArrayList<>(moduleSet)));

            List<Model> sequenceModels = new ArrayList<>();
            List<Module> sequenceModules = new ArrayList<>();
            modules.stream().forEach(_v -> {
                if (_v.getApplication() != null && _v.getApplication()) {
                    sequenceModules.add(_v);
                    Model m = moduleModelMap.get(_v.getName());
                    sequenceModels.add(m);
                }
            });

            settings.put("models", ObjectMapUtils.modelToMap(sequenceModels, Model.class.getName()));
            settings.put("modules", ObjectMapUtils.modelToMap(sequenceModules, Module.class.getName()));
        }*/

        return settings;
    }

}
