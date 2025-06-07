package pro.shushi.pamirs.meta.api.dto.meta;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.meta.api.MetaDataApi;
import pro.shushi.pamirs.meta.base.AbstractModel;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.ModelData;
import pro.shushi.pamirs.meta.domain.fun.ComputeDefinition;
import pro.shushi.pamirs.meta.domain.fun.ExpressionDefinition;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.fun.InterfaceDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.ComputeSceneEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 模块元数据
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/15 2:45 下午
 */
public class MetaData {

    private static final HoldKeeper<MetaDataApi> metaDataApiHolder = new HoldKeeper<>();

    /**
     * 元模型元数据
     * <p>
     * metadata group -> metadata sign -> MetaBaseModel
     */
    private final Map<String, Map<String, MetaBaseModel>> data = new ConcurrentHashMap<>();

    /**
     * 校验表达式缓存
     * <p>
     * type#model#sign -> ExpressionDefinition
     */
    private final Map<String, List<ExpressionDefinition>> validateExpressionMap = new ConcurrentHashMap<>();

    /**
     * 校验函数缓存
     * <p>
     * type#model#sign -> FunctionDefinition
     */
    private final Map<String, List<ComputeDefinition>> validateFunMap = new ConcurrentHashMap<>();

    /**
     * 跨模块扩展来源表
     * <p>
     * modelData code -> source module
     */
    private final Map<String, String> extendMap = new HashMap<>();

    /**
     * 安装注册信息变更表
     * <p>
     * modelData code -> modelData
     */
    private final Map<String, ModelData> diffLoadMap = new HashMap<>();

    /**
     * 跨模块元数据
     * <p>
     * module name -> MetaData
     */
    private final Map<String, MetaData> crossingMetaDataMap = new ConcurrentHashMap<>();

    public ModuleDefinition getModule() {
        if (null == data.get(ModuleDefinition.MODEL_MODEL)) {
            return null;
        }
        return (ModuleDefinition) data.get(ModuleDefinition.MODEL_MODEL).values().iterator().next();
    }

    public List<ModelDefinition> getModelList() {
        return getDataList(ModelDefinition.MODEL_MODEL);
    }

    public ModelDefinition getModel(String model) {
        return (ModelDefinition) Optional.ofNullable(data.get(ModelDefinition.MODEL_MODEL))
                .map(v -> v.get(model)).orElse(null);
    }

    public MetaData addModel(ModelDefinition modelDefinition) {
        if (null == modelDefinition) {
            return this;
        }
        data.get(ModelDefinition.MODEL_MODEL).put(modelDefinition.getModel(), modelDefinition);
        return this;
    }

    public ModelField getModelField(String model, String field) {
        ModelDefinition modelDefinition = (ModelDefinition) Optional.ofNullable(data.get(ModelDefinition.MODEL_MODEL))
                .map(v -> v.get(model)).orElse(null);
        if (null == modelDefinition || null == modelDefinition.getModelFields()) {
            return null;
        }
        for (ModelField modelField : modelDefinition.getModelFields()) {
            if (modelField.getField().equals(field)) {
                return modelField;
            }
        }
        return (ModelField) Optional.ofNullable(data.get(ModelField.MODEL_MODEL))
                .map(v -> v.get(model + CharacterConstants.SEPARATOR_DOT + field)).orElse(null);
    }

    protected MetaData addModelField(ModelField modelField) {
        String model = modelField.getModel();
        return addModelField(model, modelField);
    }

    public MetaData addModelField(String model, ModelField modelField) {
        return addModelField(model, modelField, true);
    }

    protected boolean removeModelField(String model, String field) {
        if (null != data.get(ModelField.MODEL_MODEL) &&
                null != data.get(ModelField.MODEL_MODEL).remove(model + CharacterConstants.SEPARATOR_DOT + field)) {
            return true;
        }
        ModelDefinition modelDefinition = (ModelDefinition) Optional.of(data.get(ModelDefinition.MODEL_MODEL))
                .map(v -> v.get(model)).orElse(null);
        if (null != modelDefinition) {
            if (null == modelDefinition.getModelFields()) {
                return true;
            }
            int index = 0;
            for (ModelField modelField : modelDefinition.getModelFields()) {
                if (modelField.getField().equals(field)) {
                    return null != modelDefinition.getModelFields().remove(index);
                }
                index++;
            }
        }
        return true;
    }

    public MetaData placeModelField(String model, ModelField modelField) {
        if (null == modelField) {
            return this;
        }
        ModelDefinition modelDefinition = (ModelDefinition) Optional.of(data.get(ModelDefinition.MODEL_MODEL))
                .map(v -> v.get(model)).orElse(null);
        if (null != modelDefinition) {
            if (null == modelDefinition.getModelFields()) {
                modelDefinition.setModelFields(new ArrayList<>());
            }
            ModelField existModelField = getModelField(model, modelField.getField());
            if (null != existModelField) {
                existModelField.get_d().putAll(modelField.get_d());
            } else {
                modelDefinition.getModelFields().add(modelField);
            }
        }
        return this;
    }

    public MetaData topModelField(String model, ModelField modelField) {
        return addModelField(model, modelField, false);
    }

    @SuppressWarnings("UnusedReturnValue")
    public MetaData addCrossingModelField(ModelField modelField) {
        if (null == modelField) {
            return this;
        }
        data.computeIfAbsent(ModelField.MODEL_MODEL, k -> new HashMap<>());
        data.get(ModelField.MODEL_MODEL).put(modelField.getSign(), modelField);
        return this;
    }

    private MetaData addModelField(String model, ModelField modelField, boolean append) {
        if (null == modelField) {
            return this;
        }
        ModelDefinition modelDefinition = (ModelDefinition) Optional.ofNullable(data.get(ModelDefinition.MODEL_MODEL))
                .map(v -> v.get(model)).orElse(null);
        if (null != modelDefinition) {
            ModelField existModelField = getModelField(model, modelField.getField());
            boolean isExist = null != existModelField;
            if (!isExist) {
                if (null == modelDefinition.getModelFields()) {
                    modelDefinition.setModelFields(new ArrayList<>());
                }
                if (append) {
                    modelDefinition.getModelFields().add(modelField);
                } else {
                    modelDefinition.getModelFields().add(0, modelField);
                }
            }
        } else {
            data.computeIfAbsent(ModelField.MODEL_MODEL, k -> new HashMap<>());
            data.get(ModelField.MODEL_MODEL).put(modelField.getSign(), modelField);
        }
        return this;
    }

    public List<ModelField> getModelFieldList() {
        List<ModelField> fieldList = new ArrayList<>();
        List<ModelDefinition> modelDefinitionList = getModelList();
        if (!CollectionUtils.isEmpty(modelDefinitionList)) {
            for (ModelDefinition modelDefinition : modelDefinitionList) {
                if (!CollectionUtils.isEmpty(modelDefinition.getModelFields())) {
                    fieldList.addAll(modelDefinition.getModelFields());
                }
            }
        }
        return fieldList;
    }

    public List<ModelField> getStandAloneFieldList() {
        List<MetaBaseModel> list = Optional.ofNullable(data.get(ModelField.MODEL_MODEL)).map(Map::values).map(ArrayList::new).orElse(null);
        if (null == list) {
            return null;
        }
        //noinspection unchecked,rawtypes
        return new ArrayList(list);
    }

    public FunctionDefinition getFunction(String namespace, String fun) {
        ModelDefinition modelDefinition = (ModelDefinition) Optional.ofNullable(data.get(ModelDefinition.MODEL_MODEL))
                .map(v -> v.get(namespace)).orElse(null);
        if (null == modelDefinition) {
            return (FunctionDefinition) Optional.ofNullable(data.get(FunctionDefinition.MODEL_MODEL))
                    .map(v -> v.get(namespace + CharacterConstants.SEPARATOR_DOT + fun)).orElse(null);
        }
        if (null == modelDefinition.getFunctions()) {
            return null;
        }
        for (FunctionDefinition functionDefinition : modelDefinition.getFunctions()) {
            if (functionDefinition.getFun().equals(fun)) {
                return functionDefinition;
            }
        }
        return null;
    }

    protected MetaData addFunction(FunctionDefinition functionDefinition) {
        addFunction(functionDefinition.getNamespace(), functionDefinition);
        return this;
    }

    public MetaData addFunction(String namespace, FunctionDefinition functionDefinition) {
        if (null == functionDefinition) {
            return this;
        }
        ModelDefinition modelDefinition = getModel(namespace);

        boolean isExist = null != getFunction(namespace, functionDefinition.getFun());
        if (!isExist) {
            if (null != modelDefinition) {
                if (null == modelDefinition.getFunctions()) {
                    modelDefinition.setFunctions(new ArrayList<>());
                }
                modelDefinition.getFunctions().add(functionDefinition);
            } else {
                data.computeIfAbsent(FunctionDefinition.MODEL_MODEL, k -> new HashMap<>());
                data.get(FunctionDefinition.MODEL_MODEL).put(functionDefinition.getSign(), functionDefinition);
            }
        }
        return this;
    }

    protected boolean removeFunction(String namespace, String fun) {
        ModelDefinition modelDefinition = getModel(namespace);
        if (null == modelDefinition) {
            Map<String/*model sign*/, MetaBaseModel> m = data.get(FunctionDefinition.MODEL_MODEL);
            if (null == m) {
                return false;
            }
            return null != m.remove(namespace + CharacterConstants.SEPARATOR_DOT + fun);
        }
        if (null == modelDefinition.getFunctions()) {
            return false;
        }
        int index = 0;
        for (FunctionDefinition functionDefinition : modelDefinition.getFunctions()) {
            if (functionDefinition.getFun().equals(fun)) {
                return null != modelDefinition.getFunctions().remove(index);
            }
            index++;
        }
        return false;
    }

    public void placeFunction(String namespace, FunctionDefinition functionDefinition) {
        if (null == functionDefinition) {
            return;
        }
        ModelDefinition modelDefinition = (ModelDefinition) Optional.of(data.get(ModelDefinition.MODEL_MODEL))
                .map(v -> v.get(namespace)).orElse(null);
        if (null != modelDefinition) {
            if (null == modelDefinition.getFunctions()) {
                modelDefinition.setFunctions(new ArrayList<>());
            }
            FunctionDefinition existFunction = getFunction(namespace, functionDefinition.getFun());
            if (null != existFunction) {
                existFunction.get_d().putAll(functionDefinition.get_d());
            } else {
                modelDefinition.getFunctions().add(functionDefinition);
            }
        } else {
            data.computeIfAbsent(FunctionDefinition.MODEL_MODEL, k -> new HashMap<>());
            data.get(FunctionDefinition.MODEL_MODEL).put(functionDefinition.getSign(), functionDefinition);
        }
    }

    public List<FunctionDefinition> getModelFunctionList() {
        List<FunctionDefinition> functionList = new ArrayList<>();
        List<ModelDefinition> modelDefinitionList = getModelList();
        if (!CollectionUtils.isEmpty(modelDefinitionList)) {
            for (ModelDefinition modelDefinition : modelDefinitionList) {
                if (!CollectionUtils.isEmpty(modelDefinition.getFunctions())) {
                    functionList.addAll(modelDefinition.getFunctions());
                }
            }
        }
        return functionList;
    }

    public List<FunctionDefinition> getStandAloneFunctionList() {
        List<MetaBaseModel> list = Optional.ofNullable(data.get(FunctionDefinition.MODEL_MODEL)).map(Map::values).map(ArrayList::new).orElse(null);
        if (null == list) {
            return null;
        }
        //noinspection unchecked,rawtypes
        return new ArrayList(list);
    }

    public void initLifecycleValidation() {
        Map<String, ExpressionDefinition> validateExpressionMapPerModule = getDataMap(ExpressionDefinition.MODEL_MODEL);
        if (!MapUtils.isEmpty(validateExpressionMapPerModule)) {
            for (ExpressionDefinition expressionDefinition : validateExpressionMapPerModule.values()) {
                if (expressionDefinition.isMetaCompleted()) {
                    continue;
                }
                String validationSign = ExpressionDefinition.key(expressionDefinition.getType(),
                        expressionDefinition.getModel(), expressionDefinition.getLocation());
                pro.shushi.pamirs.meta.common.util.MapUtils
                        .concurrentComputeIfAbsent(validateExpressionMap, validationSign, k -> new ArrayList<>()).add(expressionDefinition);
            }
        }
        Map<String, ComputeDefinition> validateFunMapPerModule = getDataMap(ComputeDefinition.MODEL_MODEL);
        if (!MapUtils.isEmpty(validateFunMapPerModule)) {
            for (ComputeDefinition computeDefinition : validateFunMapPerModule.values()) {
                if (computeDefinition.isMetaCompleted()) {
                    continue;
                }
                String validationSign = ExpressionDefinition.key(computeDefinition.getType(),
                        computeDefinition.getModel(), computeDefinition.getLocation());
                pro.shushi.pamirs.meta.common.util.MapUtils
                        .concurrentComputeIfAbsent(validateFunMap, validationSign, k -> new ArrayList<>()).add(computeDefinition);
            }
        }
    }

    public List<ExpressionDefinition> getExpressionDefinitionList(ComputeSceneEnum computeScene, String model, String sign) {
        return validateExpressionMap.get(ExpressionDefinition.key(computeScene, model, sign));
    }

    public List<ComputeDefinition> getComputeDefinitionList(ComputeSceneEnum computeScene, String model, String sign) {
        return validateFunMap.get(ExpressionDefinition.key(computeScene, model, sign));
    }

    public Map<String, List<ExpressionDefinition>> getValidateExpressionMap() {
        return validateExpressionMap;
    }

    public Map<String, List<ComputeDefinition>> getValidateFunMap() {
        return validateFunMap;
    }

    public <T> MetaData addData(T dataItem) {
        if (null == dataItem) {
            return this;
        }
        if (TypeUtils.isCollection(dataItem.getClass())) {
            //noinspection unchecked
            for (MetaBaseModel item : (List<MetaBaseModel>) dataItem) {
                addDataItem(item);
            }
        } else if (TypeUtils.isMap(dataItem.getClass())) {
            //noinspection unchecked
            for (MetaBaseModel item : ((Map<String, MetaBaseModel>) dataItem).values()) {
                addDataItem(item);
            }
        } else {
            addDataItem((MetaBaseModel) dataItem);
        }
        return this;
    }

    protected <T extends MetaBaseModel> void addDataItem(T item) {
        if (null == item) {
            return;
        }
        MetaDataApi metaDataApi = fetchMetaDataApi();
        if (item instanceof ModelField) {
            ModelField modelField = (ModelField) item;
            metaDataApi.whenAddModelField(data, modelField);
            addModelField(modelField);
        } else if (item instanceof FunctionDefinition && !(item instanceof InterfaceDefinition)) {
            FunctionDefinition functionDefinition = (FunctionDefinition) item;
            metaDataApi.whenAddFunction(data, functionDefinition);
            addFunction(functionDefinition);
        } else {
            String group = Models.api().getModel(item);
            String sign = item.getSign();
            metaDataApi.whenAddDataItem(data, group, sign, item);
            pro.shushi.pamirs.meta.common.util.MapUtils
                    .concurrentComputeIfAbsent(data, group, k -> new HashMap<>()).put(sign, item);
        }
    }

    public <T extends MetaBaseModel> boolean removeDataItem(T item) {
        if (null == item) {
            return false;
        }
        MetaDataApi metaDataApi = fetchMetaDataApi();
        if (item instanceof ModelField) {
            ModelField modelField = (ModelField) item;
            if (!metaDataApi.whenRemoveModelField(data, modelField.getModel(), modelField.getField())) {
                return false;
            }
            return removeModelField(modelField.getModel(), modelField.getField());
        } else if (item instanceof FunctionDefinition && !(item instanceof InterfaceDefinition)) {
            FunctionDefinition functionDefinition = (FunctionDefinition) item;
            if (!metaDataApi.whenRemoveFunction(data, functionDefinition.getNamespace(), functionDefinition.getFun())) {
                return false;
            }
            return removeFunction(functionDefinition.getNamespace(), functionDefinition.getFun());
        } else {
            String group = Models.api().getModel(item);
            if (null == data.get(group)) {
                return false;
            }
            String sign = item.getSign();
            if (!metaDataApi.whenRemoveDataItem(data, group, sign)) {
                return false;
            }
            return null != data.get(group).remove(sign);
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean removeDataItem(String group, String sign) {
        if (null == group || null == sign) {
            return false;
        }
        MetaDataApi metaDataApi = fetchMetaDataApi();
        if (ModelField.MODEL_MODEL.equals(group)) {
            String model = StringUtils.substringBeforeLast(sign, CharacterConstants.SEPARATOR_DOT);
            String field = StringUtils.substringAfterLast(sign, CharacterConstants.SEPARATOR_DOT);
            if (!metaDataApi.whenRemoveModelField(data, model, field)) {
                return false;
            }
            return removeModelField(model, field);
        } else if (FunctionDefinition.MODEL_MODEL.equals(group)) {
            String namespace = StringUtils.substringBeforeLast(sign, CharacterConstants.SEPARATOR_DOT);
            String fun = StringUtils.substringAfterLast(sign, CharacterConstants.SEPARATOR_DOT);
            if (!metaDataApi.whenRemoveFunction(data, namespace, fun)) {
                return false;
            }
            return removeFunction(namespace, fun);
        } else {
            if (null == data.get(group)) {
                return false;
            }
            if (!metaDataApi.whenRemoveDataItem(data, group, sign)) {
                return false;
            }
            return null != data.get(group).remove(sign);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends AbstractModel> T getDataItem(String group, String sign) {
        if (ModelField.MODEL_MODEL.equals(group) || FunctionDefinition.MODEL_MODEL.equals(group)) {
            String model = StringUtils.substringBeforeLast(sign, CharacterConstants.SEPARATOR_DOT);
            ModelDefinition modelDefinition = (ModelDefinition) Optional.ofNullable(data.get(ModelDefinition.MODEL_MODEL))
                    .map(v -> v.get(model)).orElse(null);
            if (null != modelDefinition) {
                if (ModelField.MODEL_MODEL.equals(group)) {
                    if (!CollectionUtils.isEmpty(modelDefinition.getModelFields())) {
                        for (ModelField modelField : modelDefinition.getModelFields()) {
                            if (modelField.getSign().equals(sign)) {
                                return (T) modelField;
                            }
                        }
                    }
                } else {
                    if (!CollectionUtils.isEmpty(modelDefinition.getFunctions())) {
                        for (FunctionDefinition functionDefinition : modelDefinition.getFunctions()) {
                            if (functionDefinition.getSign().equals(sign)) {
                                return (T) functionDefinition;
                            }
                        }
                    }
                }
            }
        }
        return (T) Optional.ofNullable(data.get(group)).map(v -> v.get(sign)).orElse(null);
    }

    @SuppressWarnings("unchecked")
    public <T extends AbstractModel> List<T> getDataList(String group) {
        List<T> dataList = (List<T>) Optional.ofNullable(data.get(group)).map(Map::values).map(ArrayList::new).orElse(null);
        if (ModelField.MODEL_MODEL.equals(group)) {
            List<T> modelFieldList = (List<T>) getModelFieldList();
            if (null != dataList) {
                modelFieldList.addAll(dataList);
            }
            return modelFieldList;
        } else if (FunctionDefinition.MODEL_MODEL.equals(group)) {
            List<T> functionList = (List<T>) getModelFunctionList();
            if (null != dataList) {
                functionList.addAll(dataList);
            }
            return functionList;
        }
        if (null == dataList) {
            return new ArrayList<>();
        }
        return dataList;
    }

    @SuppressWarnings("unchecked")
    public <T extends MetaBaseModel> Map<String/*sign*/, T> getDataMap(String group) {
        Map<String, T> dataMap = Optional.ofNullable((Map<String/*sign*/, T>) data.get(group)).orElse(null);
        if (ModelField.MODEL_MODEL.equals(group)) {
            List<T> modelFieldList = (List<T>) getModelFieldList();
            Map<String, T> modelFieldMap = modelFieldList.stream().collect(Collectors.toMap(T::getSign, v -> v));
            if (null != dataMap) {
                modelFieldMap.putAll(dataMap);
            }
            return modelFieldMap;
        } else if (FunctionDefinition.MODEL_MODEL.equals(group)) {
            List<T> functionList = (List<T>) getModelFunctionList();
            Map<String, T> functionMap = functionList.stream().collect(Collectors.toMap(T::getSign, v -> v));
            if (null != dataMap) {
                functionMap.putAll(dataMap);
            }
            return functionMap;
        }
        if (null == dataMap) {
            return new HashMap<>();
        }
        return dataMap;
    }

    public Map<String/*meta model group*/, Map<String/*model sign*/, MetaBaseModel>> getData() {
        return data;
    }

    public MetaData fill(MetaData metaData) {
        for (String metaModelGroup : metaData.getData().keySet()) {
            Map<String/*model sign*/, MetaBaseModel> modelMap = metaData.getData().get(metaModelGroup);
            pro.shushi.pamirs.meta.common.util.MapUtils
                    .computeIfAbsent(getData(), metaModelGroup, k -> new HashMap<>()).putAll(modelMap);
        }
        return this;
    }

    public void addCrossingExtendData(String group, String sign, String sourceModule) {
        ModuleDefinition currentModule = getModule();
        if (null != currentModule && currentModule.getModule().equals(sourceModule)) {
            return;
        }
        String code = ModelData.generateCode(group, sign);
        extendMap.put(code, sourceModule);
    }

    public String removeCrossingExtendData(String group, String sign) {
        String code = ModelData.generateCode(group, sign);
        return extendMap.remove(code);
    }

    public boolean isCrossingExtendData(String group, String sign) {
        String code = ModelData.generateCode(group, sign);
        return extendMap.containsKey(code);
    }

    public String getCrossingModule(String group, String sign) {
        String code = ModelData.generateCode(group, sign);
        return extendMap.get(code);
    }

    public Map<String, String> getExtendMap() {
        return extendMap;
    }

    public void addDiffModelData(String group, String sign, ModelData modelData) {
        String code = ModelData.generateCode(group, sign);
        diffLoadMap.put(code, modelData);
    }

    @SuppressWarnings("unused")
    public ModelData removeDiffModelData(String group, String sign) {
        String code = ModelData.generateCode(group, sign);
        return diffLoadMap.remove(code);
    }

    public Map<String, ModelData> getDiffLoadMap() {
        return diffLoadMap;
    }

    public Map<String, MetaData> getCrossingMetaDataMap() {
        return crossingMetaDataMap;
    }

    public void addCrossingMetaData(String module, MetaData metaData) {
        crossingMetaDataMap.putIfAbsent(module, metaData);
    }

    public MetaData getCrossingMetaData(String module) {
        return crossingMetaDataMap.get(module);
    }

    public MetaDataApi fetchMetaDataApi() {
        return metaDataApiHolder.supply(() -> Spider.getDefaultExtension(MetaDataApi.class));
    }

}
