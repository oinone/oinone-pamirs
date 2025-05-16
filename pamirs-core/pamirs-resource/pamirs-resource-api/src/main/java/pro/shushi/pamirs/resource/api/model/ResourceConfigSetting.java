package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

import java.util.Map;

@Slf4j
@Model.model(ResourceConfigSetting.MODEL_MODEL)
@Model.Advanced(name = "resourceConfigSetting")
@Model(displayName = "业务配置")
public class ResourceConfigSetting extends TransientModel {

    public static final String MODEL_MODEL = "resource.ResourceConfigSetting";

    @Field.String
    @Field(store = NullableBoolEnum.FALSE, invisible = true, displayName = "实际模型名称")
    private String actualModel;

    /*  @Action
      public ResourceConfigSetting saveConfigSetting(ResourceConfigSetting configSetting) {
          if (null==configSetting||StringUtils.isBlank(configSetting.getActualModel())){
              return configSetting;
          }
          String modelModel = configSetting.getActualModel();
          ModelConfig pro.shushi.pamirs.user.core.model = PamirsSession.getContext().getModelConfig(modelModel); //fixme zl 可视化配置时，内存取数可能有bug
          pro.shushi.pamirs.user.core.model = pro.shushi.pamirs.user.core.model == null ? PamirsSession.getContext().getModelConfig(ResourceConfigSetting.class.getName()) : pro.shushi.pamirs.user.core.model;
          List<ModelFieldConfig> modelFields = pro.shushi.pamirs.user.core.model.getModelFieldConfigList();
          Map<String, ModelFieldConfig> fieldMap = new HashMap<>();
          for (ModelFieldConfig field : modelFields) {
              fieldMap.put(field.getName(), field);
          }

          Set<String> keySet = new HashSet<>();
          for (Map.Entry<String, Object> entry : configSetting.entrySet()) {
              ModelFieldConfig field = fieldMap.get(entry.getKey());
              if (field == null) {
                  continue;
              }
              keySet.add(generConfigKey(modelModel, field));
          }
      }
      */
    @Action
    public Map<String, Object> saveConfigSetting(Map<String, Object> configSetting) {
        //fixme base
        /*if (MapUtils.isEmpty(configSetting) || !configSetting.containsKey("actualModel")) {
            return configSetting;
        }
        String modelModel = (String) configSetting.get("actualModel");

        ModelConfig pro.shushi.pamirs.user.core.model = PamirsSession.getContext().getModelConfig(modelModel); //fixme zl 可视化配置时，内存取数可能有bug
        pro.shushi.pamirs.user.core.model = pro.shushi.pamirs.user.core.model == null ? PamirsSession.getContext().getModelConfig(ResourceConfigSetting.class.getName()) : pro.shushi.pamirs.user.core.model;
        List<ModelFieldConfig> modelFields = pro.shushi.pamirs.user.core.model.getModelFieldConfigList();
        Map<String, ModelFieldConfig> fieldMap = new HashMap<>();
        for (ModelFieldConfig field : modelFields) {
            fieldMap.put(field.getName(), field);
        }

        Set<String> keySet = new HashSet<>();
        for (Map.Entry<String, Object> entry : configSetting.entrySet()) {
            ModelFieldConfig field = fieldMap.get(entry.getKey());
            if (field == null) {
                continue;
            }
            keySet.add(generConfigKey(modelModel, field));
        }
        List<ResourceConfig> configsInDb = new ResourceConfig().disableCompute().findAll(new Condition().andIn("key", new ArrayList<String>(){{addAll(keySet);}}));
        Map<String, ResourceConfig> configMapInDb = new HashMap<>();
        for (ResourceConfig config : configsInDb) {
            configMapInDb.put(config.getKey(), config);
        }

        List<ResourceConfig> configUpdate = new ArrayList<>();
        for (Map.Entry<String, Object> entry : configSetting.entrySet()) {
            ModelField field = fieldMap.get(entry.getKey());
            if (field == null) {
                log.error("{}, unknown field key, setting={}", ExpEnumerate.BASE_CONFIG_SETTING_READ_ERROR, MapUtils.isEmpty(configSetting) ? "" : JSON.toJSONString(configSetting));
                continue;
            }
            String configKey = generConfigKey(modelModel, field);
            ResourceConfig config = configMapInDb.get(configKey);
            config = config == null ? new ResourceConfig() : config;
            config.setKey(configKey);
            config.setTtype(field.getTtype());
            config.setValue(valToStr(entry.getValue()));
            configUpdate.add(config);
        }
        if (CollectionUtils.isNotEmpty(configUpdate)) {
            new ResourceConfig().disableCompute().update(configUpdate);
        }*/
        return configSetting;
    }

    //    @Action
    public Map<String, Object> construct(Map<String, Object> configSetting) {
        //fixme base
/*
        if (MapUtils.isEmpty(configSetting) || !configSetting.containsKey("actualModel")) {
            return configSetting;
        }
        String modelModel = (String) configSetting.get("actualModel");
        pro.shushi.pamirs.base.pro.shushi.pamirs.user.pro.shushi.pamirs.user.core.base.model.meta.Model pro.shushi.pamirs.user.pro.shushi.pamirs.user.core.base.model = PamirsCacheRead.getModel(modelModel); //fixme zl 可视化配置时，内存取数可能有bug
        pro.shushi.pamirs.user.pro.shushi.pamirs.user.core.base.model = pro.shushi.pamirs.user.pro.shushi.pamirs.user.core.base.model == null ? PamirsCacheRead.getModel(ResourceConfigSetting.class.getName()) : pro.shushi.pamirs.user.pro.shushi.pamirs.user.core.base.model;

        ResourceConfig configDao = new ResourceConfig().disableCompute();
        List<ModelField> modelFields = pro.shushi.pamirs.user.pro.shushi.pamirs.user.core.base.model.getModelFields();
        Map<String, Object> ret = new HashMap<>();
        if (CollectionUtils.isNotEmpty(modelFields)) {
            try {
                for (ModelField field : modelFields) {
                    ResourceConfig config = configDao.fetchConfigValue(generConfigKey(modelModel, field)); //fixme zl 先不管性能如何
                    ret.put(field.getName(), strToVal(config.getValue(), config.getTtype()));
                }
            } catch (Exception e) {
                throw log.error(ExpEnumerate.BASE_CONFIG_SETTING_READ_ERROR, e, "settings={}", MapUtils.isEmpty(configSetting) ? "" : JSON.toJSONString(configSetting)).errThrow();
            }
        }*/
        return configSetting;
    }

  /*  //actualModel先不用，用了default_, group_ 这几个机制不好支持
    private String generConfigKey(String actualModel, ModelFieldConfig field) {
        String config = "ModuleConfig-";
        config = StringUtils.isNotBlank(field.get()) ? config + pro.shushi.pamirs.base.pro.shushi.pamirs.user.pro.shushi.pamirs.user.core.base.util.StringUtils.substringAfterLastIfExists(field.getResModel(), ".") + "-" : config;
        config = config + field.getName();
        return config;
    }

    private String valToStr(Object obj) {
        if (ParamUtils.isComplexObject(obj)) {
            return JSON.toJSONString(obj);
        } else {
            return obj.toString();
        }
    }

    private Object strToVal(String str, String ttype) {
        if (StringUtils.isBlank(ttype) || ttype.equalsIgnoreCase(ModelTtypeEnum.STRING.getValue())) {
            return str;
        }
        if (StringUtils.isBlank(str)) {
            return null;
        }

        if (ttype.equalsIgnoreCase(ModelTtypeEnum.INT.getValue())) {
            return Integer.parseInt(str);
        } else if (ttype.equalsIgnoreCase(ModelTtypeEnum.LONG.getValue())) {
            return Long.parseLong(str);
        } else if (ttype.equalsIgnoreCase(ModelTtypeEnum.FLOAT.getValue())) {
            return Float.parseFloat(str);
        } else if (ttype.equalsIgnoreCase(ModelTtypeEnum.BOOLEAN.getValue())) {
            return Boolean.parseBoolean(str);
        } else if (ttype.equalsIgnoreCase(ModelTtypeEnum.DATE.getValue())) {
            return DateUtils.formatDate(str, DateUtils.yyyyMMddHHmmss);
        } else if (ttype.equalsIgnoreCase(ModelTtypeEnum.many2one.getValue())) {
            Map<String, Object> obj = JSON.parseObject(str, new TypeReference<Map<String, Object>>(){});
            return obj;
        } else {
            return str;
        }
    }*/

}
