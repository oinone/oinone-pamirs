package pro.shushi.pamirs.meta.api.dto.meta;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.base.AbstractModel;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 *
 * 模块元数据
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/15 2:45 下午
 *
 */
public class MetaData {

    private Map<String/*meta model class*/, Map<String/*model sign*/, AbstractModel>> data = new ConcurrentHashMap<>();

    public ModuleDefinition getModule(){
        if(null == data.get(ModuleDefinition.class.getName())){
            return null;
        }
        return (ModuleDefinition) data.get(ModuleDefinition.class.getName()).values().iterator().next();
    }

    public List<ModelDefinition> getModelList(){
        return getDataList(ModelDefinition.class.getName());
    }

    public ModelDefinition getModel(String model){
        return (ModelDefinition)data.get(ModelDefinition.class.getName()).get(model);
    }

    protected MetaData addModel(ModelDefinition modelDefinition){
        if (null == modelDefinition) {
            return this;
        }
        data.get(ModelDefinition.class.getName()).put(modelDefinition.getModel(), modelDefinition);
        return this;
    }

    public ModelField getModelField(String model, String field){
        ModelDefinition modelDefinition = Optional.of(data.get(ModelDefinition.class.getName()))
                .map(v->v.get(model)).map(v->(ModelDefinition)v).orElse(null);
        if(null == modelDefinition || null == modelDefinition.getModelFields()){
            return null;
        }
        for(ModelField modelField : modelDefinition.getModelFields()){
            if(modelField.getField().equals(field)){
                return modelField;
            }
        }
        return null;
    }

    protected MetaData addModelField(ModelField modelField){
        String model = modelField.getModel();
        return addModelField(model, modelField);
    }

    public MetaData addModelField(String model, ModelField modelField){
        if (null == modelField) {
            return this;
        }
        ModelDefinition modelDefinition = Optional.of(data.get(ModelDefinition.class.getName()))
                .map(v->v.get(model)).map(v->(ModelDefinition)v).orElse(null);
        if (null != modelDefinition) {
            boolean isExist = null != getModelField(model, modelField.getField());
            if(!isExist){
                if (null == modelDefinition.getModelFields()) {
                    modelDefinition.setModelFields(new ArrayList<>());
                }
                modelDefinition.getModelFields().add(modelField);
            }
        }
        return this;
    }

    public List<ModelField> getModelFieldList(){
        List<ModelField> fieldList = new ArrayList<>();
        List<ModelDefinition> modelDefinitionList = getModelList();
        if(!CollectionUtils.isEmpty(modelDefinitionList)){
            for(ModelDefinition modelDefinition : modelDefinitionList){
                if(!CollectionUtils.isEmpty(modelDefinition.getModelFields())){
                    fieldList.addAll(modelDefinition.getModelFields());
                }
            }
        }
        return fieldList;
    }

    public FunctionDefinition getFunction(String namespace, String fun){
        ModelDefinition modelDefinition = Optional.of(data.get(ModelDefinition.class.getName()))
                .map(v->v.get(namespace)).map(v->(ModelDefinition)v).orElse(null);
        if(null == modelDefinition){
            return (FunctionDefinition) Optional.ofNullable(data.get(FunctionDefinition.class.getName()))
                    .map(v->v.get(namespace + CharacterConstants.SEPARATOR_DOT + fun)).orElse(null);
        }
        if(null == modelDefinition.getFunctions()){
            return null;
        }
        for(FunctionDefinition functionDefinition : modelDefinition.getFunctions()){
            if(functionDefinition.getFun().equals(fun)){
                return functionDefinition;
            }
        }
        return null;
    }

    protected MetaData addFunction(FunctionDefinition functionDefinition){
        addFunction(functionDefinition.getNamespace(), functionDefinition);
        return this;
    }

    public MetaData addFunction(String namespace, FunctionDefinition functionDefinition){
        if (null == functionDefinition) {
            return this;
        }
        ModelDefinition modelDefinition = Optional.of(data.get(ModelDefinition.class.getName()))
                .map(v->v.get(namespace)).map(v->(ModelDefinition)v).orElse(null);
        if (null != modelDefinition) {
            boolean isExist = null != getFunction(namespace, functionDefinition.getFun());
            if(!isExist){
                if (null == modelDefinition.getFunctions()) {
                    modelDefinition.setFunctions(new ArrayList<>());
                }
                modelDefinition.getFunctions().add(functionDefinition);
            }
        }
        return this;
    }

    public List<FunctionDefinition> getFunctionList(){
        List<FunctionDefinition> functionList = new ArrayList<>();
        List<ModelDefinition> modelDefinitionList = getModelList();
        if(!CollectionUtils.isEmpty(modelDefinitionList)){
            for(ModelDefinition modelDefinition : modelDefinitionList){
                if(!CollectionUtils.isEmpty(modelDefinition.getFunctions())){
                    functionList.addAll(modelDefinition.getFunctions());
                }
            }
        }
        return functionList;
    }

    public <T> MetaData addData(T dataItem){
        if (null == dataItem) {
            return this;
        }
        if (TypeUtils.isCollection(dataItem.getClass())) {
            for (AbstractModel item : (List<AbstractModel>) dataItem) {
                addDataItem(item);
            }
        } else {
            addDataItem((AbstractModel) dataItem);
        }
        return this;
    }

    protected <T extends AbstractModel> void addDataItem(T item){
        if (null == item) {
            return;
        }
        if (item instanceof ModelField) {
            addModelField((ModelField) item);
        } else if (item instanceof FunctionDefinition) {
            addFunction((FunctionDefinition) item);
        } else {
            if (null == data.get(item.getClass().getName())) {
                data.put(item.getClass().getName(), new HashMap<>());
            }
            data.get(item.getClass().getName()).put(item.getSign(), item);
        }
    }

    public <T extends AbstractModel> T getDataItem(String metaModelClass, String sign){
        if (ModelField.class.getName().equals(metaModelClass) || FunctionDefinition.class.getName().equals(metaModelClass)) {
            String model = StringUtils.substringBeforeLast(sign, CharacterConstants.SEPARATOR_DOT);
            ModelDefinition modelDefinition = (ModelDefinition) Optional.ofNullable(data.get(ModelDefinition.class.getName()))
                    .map(v->v.get(model)).orElse(null);
            if (null == modelDefinition){
                return null;
            }
            if(ModelField.class.getName().equals(metaModelClass)){
                if (!CollectionUtils.isEmpty(modelDefinition.getModelFields())) {
                    for(ModelField modelField : modelDefinition.getModelFields()){
                        if(modelField.getSign().equals(sign)){
                            return (T)modelField;
                        }
                    }
                }
                return null;
            }else if (FunctionDefinition.class.getName().equals(metaModelClass)){
                if (!CollectionUtils.isEmpty(modelDefinition.getFunctions())) {
                    for(FunctionDefinition functionDefinition : modelDefinition.getFunctions()){
                        if(functionDefinition.getSign().equals(sign)){
                            return (T)functionDefinition;
                        }
                    }
                }
                return null;
            }
        }
        return (T) Optional.ofNullable(data.get(metaModelClass)).map(v -> v.get(sign)).orElse(null);
    }

    public <T extends AbstractModel> List<T> getDataList(String metaModelClass){
        if(ModelField.class.getName().equals(metaModelClass)){
            return (List<T>)getModelFieldList();
        }else if (FunctionDefinition.class.getName().equals(metaModelClass)){
            return (List<T>)getFunctionList();
        }
        return (List<T>)Optional.ofNullable(data.get(metaModelClass)).map(v-> v.values()).map(t->t.stream().collect(Collectors.toList())).orElse(null);
    }

}
