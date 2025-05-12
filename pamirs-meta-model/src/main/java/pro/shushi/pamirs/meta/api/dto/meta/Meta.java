package pro.shushi.pamirs.meta.api.dto.meta;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.base.BaseModel;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.domain.fun.ExtPointImplementation;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 模块元数据，含依赖元数据
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/15 2:45 下午
 */
@Data
public class Meta {

    private String module;

    private Set<String> bootModuleSet;

    private Map<String/*module name*/, MetaData> data = new LinkedHashMap<>();

    public ModuleDefinition getCurrentModule() {
        return data.get(module).getModule();
    }

    public String whichModule(String model) {
        for (String module : data.keySet()) {
            MetaData metaData = data.get(module);
            ModelDefinition modelDefinition = metaData.getModel(model);
            if (null != modelDefinition) {
                return module;
            }
        }
        return this.module;
    }

    public MetaData getCurrentModuleData() {
        return data.get(this.module);
    }

    public boolean isInBootModules(String module) {
        return null != module && null != bootModuleSet && bootModuleSet.contains(module);
    }

    public ModelDefinition getModel(String model) {
        for (MetaData metaData : data.values()) {
            ModelDefinition modelDefinition = metaData.getModel(model);
            if (null != modelDefinition) {
                return modelDefinition;
            }
        }
        return null;
    }

    public ModelField getModelField(String model, String field) {
        for (MetaData metaData : data.values()) {
            ModelField modelField = metaData.getModelField(model, field);
            if (null != modelField) {
                return modelField;
            }
        }
        return null;
    }

    public void addModelField(String model, ModelField modelField) {
        String module = whichModule(model);
        if (null != module) {
            data.get(module).addModelField(model, modelField);
        }
    }

    public void placeModelField(String model, ModelField modelField) {
        String module = whichModule(model);
        if (null != module) {
            data.get(module).placeModelField(model, modelField);
        }
    }

    public void addCrossingModelField(String sourceModule, ModelField modelField) {
        String model = modelField.getModel();
        String module = whichModule(model);
        if (null != module) {
            if (!module.equals(sourceModule)) {
                modelField.enableMetaCrossing();
                data.get(sourceModule).addCrossingModelField(modelField);
                data.get(module).addCrossingExtendData(ModelField.MODEL_MODEL, modelField.getSign(), sourceModule);
            }
        }
    }

    public String getFieldCrossingModule(ModelField modelField) {
        String crossingModule = null;
        String module = whichModule(modelField.getModel());
        if (null != module) {
            crossingModule = data.get(module).getCrossingModule(ModelField.MODEL_MODEL, modelField.getSign());
        }
        if (null == crossingModule) {
            return module;
        }
        return crossingModule;
    }

    public FunctionDefinition findFunction(String namespace, String fun) {
        FunctionDefinition function = getFunction(namespace, fun);
        if (null == function) {
            function = getFunction(BaseModel.MODEL_MODEL, fun);
        }
        if (null == function) {
            function = getFunction(NamespaceConstants.pamirs, fun);
        }
        return function;
    }

    public FunctionDefinition getFunction(String namespace, String fun) {
        for (MetaData metaData : data.values()) {
            FunctionDefinition functionDefinition = metaData.getFunction(namespace, fun);
            if (null != functionDefinition) {
                return functionDefinition;
            }
        }
        return null;
    }

    public FunctionDefinition getValidFunction(String namespace, String fun) {
        for (MetaData metaData : data.values()) {
            FunctionDefinition functionDefinition = metaData.getFunction(namespace, fun);
            if (null != functionDefinition && !functionDefinition.isMetaCompleted()) {
                return functionDefinition;
            }
        }
        return null;
    }

    public void addFunction(String namespace, FunctionDefinition functionDefinition) {
        String module = whichModule(namespace);
        if (null != module) {
            data.get(module).addFunction(namespace, functionDefinition);
        }
    }

    public void placeFunction(String namespace, FunctionDefinition functionDefinition) {
        String module = whichModule(namespace);
        MetaData metaData = data.get(module);
        metaData.placeFunction(namespace, functionDefinition);
        String sourceModule = functionDefinition.getModule();
        if (!sourceModule.equals(module)) {
            MetaData sourceMetaData = data.get(functionDefinition.getModule());
            if (sourceMetaData == null) {
                sourceMetaData = metaData.getCrossingMetaData(sourceModule);
            }
            if (sourceMetaData != null) {
                sourceMetaData.placeFunction(namespace, functionDefinition);
            }
        }
    }

    public void placeCrossingMetadata(String sourceModule, MetaBaseModel metaBaseModel) {
        MetaData sourceMetaData = data.get(sourceModule);
        if (sourceMetaData == null) {
            sourceMetaData = getCurrentModuleData().getCrossingMetaData(sourceModule);
        }
        if (sourceMetaData != null) {
            sourceMetaData.addDataItem(metaBaseModel);
        }
    }

    public List<ExtPointImplementation> getExtPointImplementationList(String namespace) {
        List<ExtPointImplementation> result = new ArrayList<>();
        for (MetaData metaData : data.values()) {
            List<ExtPointImplementation> extPointImplementations = metaData.getDataList(ExtPointImplementation.MODEL_MODEL);
            if (!CollectionUtils.isEmpty(extPointImplementations)) {
                result.addAll(extPointImplementations.stream().filter(v -> namespace.equals(v.getNamespace())).collect(Collectors.toList()));
            }
        }
        return result;
    }

    public <T extends MetaBaseModel> void addData(String module, T dataItem) {
        data.get(module).addData(dataItem);
    }

    @SuppressWarnings("unchecked")
    public <T> T getDataItem(String group, String sign) {
        for (MetaData metaData : data.values()) {
            T dataItem = (T) metaData.getDataItem(group, sign);
            if (null != dataItem) {
                return dataItem;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> Pair<String, T> getDataItemWithModule(String group, String sign) {
        for (String module : data.keySet()) {
            MetaData metaData = data.get(module);
            T dataItem = (T) metaData.getDataItem(group, sign);
            if (null != dataItem) {
                return new ImmutablePair<>(module, dataItem);
            }
        }
        return new ImmutablePair<>(null, null);
    }

    @SuppressWarnings("unchecked")
    public <T> String getDataModule(String group, String sign) {
        for (String module : data.keySet()) {
            MetaData metaData = data.get(module);
            T dataItem = (T) metaData.getDataItem(group, sign);
            if (null != dataItem) {
                return module;
            }
        }
        return null;
    }

    public String getCrossingModule(String group, String sign) {
        String module = getDataModule(group, sign);
        MetaData metaData = getData().get(module);
        return metaData.getCrossingModule(group, sign);
    }

}
