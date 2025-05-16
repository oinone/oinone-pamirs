package pro.shushi.pamirs.meta.api.dto.config;

import org.apache.commons.collections.CollectionUtils;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.api.dto.config.api.ModelConfigApi;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.SequenceConfig;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 模型配置
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/15 2:45 下午
 */
@Data
public class ModelConfig implements Serializable {

    private static final long serialVersionUID = -6851798158330852134L;

    private ModelDefinition modelDefinition;

    private PamirsTableInfo pamirsTableInfo;

    private boolean onlyBasicTypeField;

    public boolean isStaticConfig() {
        return getModelDefinition().isStaticConfig();
    }

    public void setStaticConfig(boolean staticConfig) {
        this.getModelDefinition().setStaticConfig(staticConfig);
    }

    public ModelConfig() {
        super();
        this.setModelDefinition(new ModelDefinition());
    }

    public ModelConfig(ModelDefinition modelDefinition) {
        super();
        this.setModelDefinition(modelDefinition);
    }

    /**
     * 模型是否拥有主键
     *
     * @return 拥有主键
     */
    public boolean havePk() {
        return CollectionUtils.isNotEmpty(this.getPk());
    }

    public List<String> getPkProperties() {
        if (havePk()) {
            return this.getModelFieldConfigList().stream()
                    .filter(v -> getPk().contains(v.getField()))
                    .map(ModelFieldConfig::getLname)
                    .collect(Collectors.toList());
        }
        return null;
    }

    public String getName() {
        return getModelDefinition().getName();
    }

    public ModelConfig setName(String name) {
        getModelDefinition().setName(name);
        return this;
    }

    public String getDisplayName() {
        return getModelDefinition().getDisplayName();
    }

    public ModelConfig setDisplayName(String displayName) {
        getModelDefinition().setDisplayName(displayName);
        return this;
    }

    public String getModel() {
        return getModelDefinition().getModel();
    }

    public ModelConfig setModel(String model) {
        getModelDefinition().setModel(model);
        return this;
    }

    public String getLname() {
        return getModelDefinition().getLname();
    }

    public ModelConfig setLname(String lname) {
        getModelDefinition().setLname(lname);
        return this;
    }

    public String getModule() {
        return getModelDefinition().getModule();
    }

    public ModelConfig setModule(String module) {
        getModelDefinition().setModule(module);
        return this;
    }

    public String getModuleAbbr() {
        return getModelDefinition().getModuleAbbr();
    }

    public ModelConfig setModuleAbbr(String abbr) {
        getModelDefinition().setModuleAbbr(abbr);
        return this;
    }

    public String getDsModule() {
        return getModelDefinition().getDsModule();
    }

    public ModelConfig setDsModule(String dsModule) {
        getModelDefinition().setDsModule(dsModule);
        return this;
    }

    public String getOriginDsKey() {
        return getModelDefinition().getDsKey();
    }

    public String getDsKey() {
        return getModelDefinition().getCompletedDsKey();
    }

    public ModelConfig setDsKey(String dsKey) {
        getModelDefinition().setDsKey(dsKey);
        return this;
    }

    public String getSummary() {
        return getModelDefinition().getSummary();
    }

    public ModelConfig setSummary(String summary) {
        getModelDefinition().setSummary(summary);
        return this;
    }

    public String getRemark() {
        return getModelDefinition().getRemark();
    }

    public ModelConfig setRemark(String remark) {
        getModelDefinition().setRemark(remark);
        return this;
    }

    public String getTable() {
        return getModelDefinition().getTable();
    }

    public ModelConfig setTable(String table) {
        getModelDefinition().setTable(table);
        return this;
    }

    public ModelTypeEnum getType() {
        return getModelDefinition().getType();
    }

    public ModelConfig setType(ModelTypeEnum type) {
        getModelDefinition().setType(type);
        return this;
    }

    public SequenceConfig getSequenceConfig() {
        return getModelDefinition().getSequenceConfig();
    }

    public ModelConfig setSequenceConfig(SequenceConfig sequenceConfig) {
        getModelDefinition().setSequenceConfig(sequenceConfig);
        return this;
    }

    public Boolean isDataManager() {
        return getModelDefinition().getDataManager();
    }

    public ModelConfig setDataManager(Boolean managed) {
        getModelDefinition().setDataManager(managed);
        return this;
    }

    public String getOrdering() {
        return getModelDefinition().getOrdering();
    }

    public ModelConfig setOrdering(String ordering) {
        getModelDefinition().setOrdering(ordering);
        return this;
    }

    public Boolean getRelationship() {
        return getModelDefinition().getIsRelationship();
    }

    public ModelConfig setRelationship(Boolean relationship) {
        getModelDefinition().setIsRelationship(relationship);
        return this;
    }

    public String getProxy() {
        return getModelDefinition().getProxy();
    }

    public ModelConfig setProxy(String proxyModel) {
        getModelDefinition().setProxy(proxyModel);
        return this;
    }

    public List<String> getUniques() {
        return getModelDefinition().getUniques();
    }

    public ModelConfig setUniques(List<String> uniques) {
        getModelDefinition().setUniques(uniques);
        return this;
    }

    public List<String> getIndexes() {
        return getModelDefinition().getIndexes();
    }

    public ModelConfig setIndexes(List<String> indexes) {
        getModelDefinition().setIndexes(indexes);
        return this;
    }

    public List<String> getPk() {
        return getModelDefinition().getPk();
    }

    public ModelConfig setPk(List<String> pk) {
        getModelDefinition().setPk(pk);
        return this;
    }

    public String getOptimisticLockerField() {
        return getModelDefinition().getOptimisticLockerField();
    }

    public ModelConfig setOptimisticLockerField(String optimisticLockerField) {
        getModelDefinition().setOptimisticLockerField(optimisticLockerField);
        return this;
    }

    public List<String> getUnInheritedField() {
        return getModelDefinition().getUnInheritedFields();
    }

    public ModelConfig setUnInheritedField(List<String> unInheritedField) {
        getModelDefinition().setUnInheritedFields(unInheritedField);
        return this;
    }

    public List<String> getSuperModels() {
        return getModelDefinition().getSuperModels();
    }

    public ModelConfig setSuperModels(List<String> superModels) {
        getModelDefinition().setSuperModels(superModels);
        return this;
    }

    public List<ModelFieldConfig> getModelFieldConfigList() {
        return ModelConfigApi.get().getModelFieldConfigList(this);
    }

    public List<ModelFieldConfig> getModelFieldConfigListSort() {
        return ModelConfigApi.get().getModelFieldConfigListSort(this);
    }

    public List<ModelFieldConfig> getSqlMethodModelFieldConfigList() {
        return ModelConfigApi.get().getSqlMethodModelFieldConfigList(this);
    }

    public List<String> getModels() {
        return ModelConfigApi.get().getModels(this);
    }

    public ModelConfig setModelFieldConfigList(List<ModelFieldConfig> modelFieldConfigList) {
        getModelDefinition().setModelFields(modelFieldConfigList.stream().map(ModelFieldConfig::getModelField).collect(Collectors.toList()));
        return this;
    }

    public List<Function> getFunctionList() {
        return Optional.ofNullable(getModelDefinition())
                .map(ModelDefinition::getFunctions)
                .map(List::stream)
                .orElse(Stream.empty())
                .map(Function::new)
                .collect(Collectors.toList());
    }

    public ModelConfig setFunctionList(List<Function> functionList) {
        getModelDefinition().setFunctions(functionList.stream().map(Function::getFunctionDefinition).collect(Collectors.toList()));
        return this;
    }

    public Long getBitOptions() {
        return getModelDefinition().getBitOptions();
    }

    public ModelConfig setBitOptions(Long bitOptions) {
        getModelDefinition().setBitOptions(bitOptions);
        return this;
    }

}
