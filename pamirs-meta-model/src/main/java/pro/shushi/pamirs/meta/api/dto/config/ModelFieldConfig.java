package pro.shushi.pamirs.meta.api.dto.config;

import pro.shushi.pamirs.meta.domain.model.DataDictionaryItem;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.domain.model.SequenceConfig;
import pro.shushi.pamirs.meta.enmu.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 字段配置
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/15 2:45 下午
 */
public class ModelFieldConfig implements Serializable {

    private static final long serialVersionUID = 7676656509984259114L;

    private ModelField modelField;

    private boolean isVirtual;

    public ModelFieldConfig() {
        this.setModelField(new ModelField());
    }

    public ModelFieldConfig(ModelField modelField) {
        this.setModelField(modelField);
    }

    public ModelField getModelField() {
        return modelField;
    }

    public void setModelField(ModelField modelField) {
        this.modelField = modelField;
    }

    public boolean isVirtual() {
        return isVirtual;
    }

    public void setIsVirtual(boolean isVirtual) {
        this.isVirtual = isVirtual;
    }

    public String getModel() {
        return getModelField().getModel();
    }

    public ModelFieldConfig setModel(String model) {
        getModelField().setModel(model);
        return this;
    }

    public String getName() {
        return getModelField().getName();
    }

    public ModelFieldConfig setName(String name) {
        getModelField().setName(name);
        return this;
    }

    public String getDisplayName() {
        return getModelField().getDisplayName();
    }

    public ModelFieldConfig setDisplayName(String displayName) {
        getModelField().setDisplayName(displayName);
        return this;
    }

    public String getField() {
        return getModelField().getField();
    }

    public ModelFieldConfig setField(String field) {
        getModelField().setField(field);
        return this;
    }

    public String getColumn() {
        return getModelField().getColumn();
    }

    public ModelFieldConfig setColumn(String column) {
        getModelField().setColumn(column);
        return this;
    }

    public String getColumnDefinition() {
        return getModelField().getColumnDefinition();
    }

    public ModelFieldConfig setColumnDefinition(String columnDefinition) {
        getModelField().setColumnDefinition(columnDefinition);
        return this;
    }

    public Boolean getOnlyColumn() {
        return getModelField().getOnlyColumn();
    }

    public ModelFieldConfig setOnlyColumn(Boolean onlyColumn) {
        getModelField().setOnlyColumn(onlyColumn);
        return this;
    }

    public String getInsertStrategy() {
        return Optional.ofNullable(getModelField().getInsertStrategy()).map(FieldStrategyEnum::value).orElse(null);
    }

    public ModelFieldConfig setInsertStrategy(String insertStrategy) {
        getModelField().setInsertStrategy(Arrays.stream(FieldStrategyEnum.values()).filter(v -> v.value().equals(insertStrategy)).findFirst().orElse(null));
        return this;
    }

    public String getBatchStrategy() {
        return Optional.ofNullable(getModelField().getBatchStrategy()).map(FieldStrategyEnum::value).orElse(null);
    }

    public ModelFieldConfig setBatchStrategy(String batchStrategy) {
        getModelField().setBatchStrategy(Arrays.stream(FieldStrategyEnum.values()).filter(v -> v.value().equals(batchStrategy)).findFirst().orElse(null));
        return this;
    }

    public String getUpdateStrategy() {
        return Optional.ofNullable(getModelField().getUpdateStrategy()).map(FieldStrategyEnum::value).orElse(null);
    }

    public ModelFieldConfig setUpdateStrategy(String updateStrategy) {
        getModelField().setUpdateStrategy(Arrays.stream(FieldStrategyEnum.values()).filter(v -> v.value().equals(updateStrategy)).findFirst().orElse(null));
        return this;
    }

    public String getWhereStrategy() {
        return Optional.ofNullable(getModelField().getWhereStrategy()).map(FieldStrategyEnum::value).orElse(null);
    }

    public ModelFieldConfig setWhereStrategy(String whereStrategy) {
        getModelField().setWhereStrategy(Arrays.stream(FieldStrategyEnum.values()).filter(v -> v.value().equals(whereStrategy)).findFirst().orElse(null));
        return this;
    }

    public String getWhereCondition() {
        return getModelField().getWhereCondition();
    }

    public ModelFieldConfig setWhereCondition(String whereCondition) {
        getModelField().setWhereCondition(whereCondition);
        return this;
    }

    public String getCharset() {
        return Optional.ofNullable(getModelField().getCharset()).map(CharsetEnum::value).orElse(null);
    }

    public ModelFieldConfig setCharset(String charset) {
        getModelField().setCharset(Arrays.stream(CharsetEnum.values()).filter(v -> v.value().equals(charset)).findFirst().orElse(null));
        return this;
    }

    public String getCollation() {
        return Optional.ofNullable(getModelField().getCollation()).map(CollationEnum::value).orElse(null);
    }

    public ModelFieldConfig setCollation(String collation) {
        getModelField().setCollation(Arrays.stream(CollationEnum.values()).filter(v -> v.value().equals(collation)).findFirst().orElse(null));
        return this;
    }

    public String getTtype() {
        TtypeEnum ttype = getModelField().getTtype();
        if (ttype == null) {
            return null;
        }
        return ttype.value();
    }

    public ModelFieldConfig setTtype(String ttype) {
        getModelField().setTtype(TtypeEnum.getEnumByValue(TtypeEnum.class, ttype));
        return this;
    }

    public String getRelatedTtype() {
        TtypeEnum relatedTtype = getModelField().getRelatedTtype();
        if (relatedTtype == null) {
            return null;
        }
        return relatedTtype.value();
    }

    public ModelFieldConfig setRelatedTtype(String relatedTtype) {
        getModelField().setRelatedTtype(TtypeEnum.getEnumByValue(TtypeEnum.class, relatedTtype));
        return this;
    }

    public String getLname() {
        return getModelField().getLname();
    }

    public ModelFieldConfig setLname(String lname) {
        getModelField().setLname(lname);
        return this;
    }

    public String getLtype() {
        return getModelField().getLtype();
    }

    public ModelFieldConfig setLtype(String ltype) {
        getModelField().setLtype(ltype);
        return this;
    }

    public String getLtypeT() {
        return getModelField().getLtypeT();
    }

    public ModelFieldConfig setLtypeT(String ltypeT) {
        getModelField().setLtypeT(ltypeT);
        return this;
    }

    public String getSummary() {
        return getModelField().getSummary();
    }

    public ModelFieldConfig setSummary(String summary) {
        getModelField().setSummary(summary);
        return this;
    }

    public Integer getSize() {
        return getModelField().getSize();
    }

    public ModelFieldConfig setSize(Integer size) {
        getModelField().setSize(size);
        return this;
    }

    public Integer getDecimal() {
        return getModelField().getDecimal();
    }

    public ModelFieldConfig setDecimal(Integer decimal) {
        getModelField().setDecimal(decimal);
        return this;
    }

    public Boolean getMulti() {
        return getModelField().getMulti();
    }

    public ModelFieldConfig setMulti(Boolean multi) {
        getModelField().setMulti(multi);
        return this;
    }

    public Boolean getStore() {
        return getModelField().getStore();
    }

    public ModelFieldConfig setStore(Boolean store) {
        getModelField().setStore(store);
        return this;
    }

    public String getDictionary() {
        return getModelField().getDictionary();
    }

    public ModelFieldConfig setDictionary(String dictionary) {
        getModelField().setDictionary(dictionary);
        return this;
    }

    public List<DataDictionaryItem> getOptions() {
        return getModelField().getOptions();
    }

    public ModelFieldConfig setOptions(List<DataDictionaryItem> options) {
        getModelField().setOptions(options);
        return this;
    }

    public Boolean getRelationStore() {
        return getModelField().getRelationStore();
    }

    public ModelFieldConfig setRelationStore(Boolean relationStore) {
        getModelField().setRelationStore(relationStore);
        return this;
    }

    public String getFormat() {
        return Optional.ofNullable(getModelField().getFormat()).map(DateFormatEnum::value).orElse(null);
    }

    public ModelFieldConfig setFormat(String format) {
        getModelField().setFormat(Arrays.stream(DateFormatEnum.values()).filter(v -> v.value().equals(format)).findFirst().orElse(null));
        return this;
    }

    public Boolean getRequired() {
        return getModelField().getRequired();
    }

    public ModelFieldConfig setRequired(Boolean required) {
        getModelField().setRequired(required);
        return this;
    }

    public String getDefaultValue() {
        return getModelField().getDefaultValue();
    }

    public ModelFieldConfig setDefaultValue(String defaultValue) {
        getModelField().setDefaultValue(defaultValue);
        return this;
    }

    public Boolean getImmutable() {
        return getModelField().getImmutable();
    }

    public ModelFieldConfig setImmutable(Boolean immutable) {
        getModelField().setImmutable(immutable);
        return this;
    }

    public Long getPriority() {
        return getModelField().getPriority();
    }

    public ModelFieldConfig setPriority(Long priority) {
        getModelField().setPriority(priority);
        return this;
    }

    public Boolean getPk() {
        return getModelField().getPk();
    }

    public ModelFieldConfig setPk(Boolean pk) {
        getModelField().setPk(pk);
        return this;
    }

    public Integer getPkIndex() {
        return getModelField().getPkIndex();
    }

    public ModelFieldConfig setPkIndex(Integer pkIndex) {
        getModelField().setPkIndex(pkIndex);
        return this;
    }

    public String getKeyGenerator() {
        KeyGeneratorEnum keyGeneratorEnum = getModelField().getKeyGenerator();
        if (null == keyGeneratorEnum) {
            return null;
        }
        return keyGeneratorEnum.value();
    }

    public ModelFieldConfig setKeyGenerator(String keyGenerator) {
        getModelField().setKeyGenerator(Arrays.stream(KeyGeneratorEnum.values()).filter(v -> v.value().equals(keyGenerator)).findFirst().orElse(null));
        return this;
    }

    public Boolean getOptimisticLocker() {
        return getModelField().getOptimisticLocker();
    }

    public ModelFieldConfig setOptimisticLocker(Boolean optimisticLocker) {
        getModelField().setOptimisticLocker(optimisticLocker);
        return this;
    }

    public Boolean getUnique() {
        return getModelField().getUnique();
    }

    public ModelFieldConfig setUnique(Boolean unique) {
        getModelField().setUnique(unique);
        return this;
    }

    public Boolean getIndex() {
        return getModelField().getIndex();
    }

    public ModelFieldConfig setIndex(Boolean index) {
        getModelField().setIndex(index);
        return this;
    }

    public String getCompute() {
        return getModelField().getCompute();
    }

    public ModelFieldConfig setCompute(String compute) {
        getModelField().setCompute(compute);
        return this;
    }

    public Boolean getInverse() {
        return getModelField().getInverse();
    }

    public ModelFieldConfig setInverse(Boolean inverse) {
        getModelField().setInverse(inverse);
        return this;
    }

    public String getDomain() {
        return getModelField().getDomain();
    }

    public ModelFieldConfig setDomain(String domain) {
        getModelField().setDomain(domain);
        return this;
    }

    public String getSearch() {
        return getModelField().getSearch();
    }

    public ModelFieldConfig setSearch(String search) {
        getModelField().setSearch(search);
        return this;
    }

    public String getRequestSerialize() {
        return getModelField().getRequestSerialize();
    }

    public ModelFieldConfig setRequestSerialize(String serialize) {
        getModelField().setRequestSerialize(serialize);
        return this;
    }

    public String getStoreSerialize() {
        return getModelField().getStoreSerialize();
    }

    public ModelFieldConfig setStoreSerialize(String serialize) {
        getModelField().setStoreSerialize(serialize);
        return this;
    }

    public SequenceConfig getSequenceConfig() {
        return getModelField().getSequenceConfig();
    }

    public ModelFieldConfig setSequenceConfig(SequenceConfig sequenceConfig) {
        getModelField().setSequenceConfig(sequenceConfig);
        return this;
    }

    public Boolean getInvisible() {
        return getModelField().getInvisible();
    }

    public ModelFieldConfig setInvisible(Boolean invisible) {
        getModelField().setInvisible(invisible);
        return this;
    }

    public List<String> getRelated() {
        return getModelField().getRelated();
    }

    public ModelFieldConfig setRelated(List<String> related) {
        getModelField().setRelated(related);
        return this;
    }

    public List<String> getRelationFields() {
        return getModelField().getRelationFields();
    }

    public ModelFieldConfig setRelationFields(List<String> relationFields) {
        getModelField().setRelationFields(relationFields);
        return this;
    }

    public List<String> getThroughRelationFields() {
        return getModelField().getThroughRelationFields();
    }

    public ModelFieldConfig setThroughRelationFields(List<String> throughRelationFields) {
        getModelField().setThroughRelationFields(throughRelationFields);
        return this;
    }

    public String getReferences() {
        return getModelField().getReferences();
    }

    public ModelFieldConfig setReferences(String references) {
        getModelField().setReferences(references);
        return this;
    }

    public String getThrough() {
        return getModelField().getThrough();
    }

    public ModelFieldConfig setThrough(String through) {
        getModelField().setThrough(through);
        return this;
    }

    public List<String> getThroughReferenceFields() {
        return getModelField().getThroughReferenceFields();
    }

    public ModelFieldConfig setThroughReferenceFields(List<String> throughReferenceFields) {
        getModelField().setThroughReferenceFields(throughReferenceFields);
        return this;
    }

    public List<String> getReferenceFields() {
        return getModelField().getReferenceFields();
    }

    public ModelFieldConfig setReferenceFields(List<String> referenceFields) {
        getModelField().setReferenceFields(referenceFields);
        return this;
    }

    public Long getPageSize() {
        return getModelField().getPageSize();
    }

    public ModelFieldConfig setPageSize(Long pageSize) {
        getModelField().setPageSize(pageSize);
        return this;
    }

    public String getOrdering() {
        return getModelField().getOrdering();
    }

    public ModelFieldConfig setOrdering(String ordering) {
        getModelField().setOrdering(ordering);
        return this;
    }

    public String getOnUpdate() {
        return Optional.ofNullable(getModelField().getOnUpdate()).map(OnCascadeEnum::value).orElse(null);
    }

    public ModelFieldConfig setOnUpdate(String onUpdate) {
        getModelField().setOnUpdate(Arrays.stream(OnCascadeEnum.values()).filter(v -> v.value().equals(onUpdate)).findFirst().orElse(null));
        return this;
    }

    public String getOnDelete() {
        return Optional.ofNullable(getModelField().getOnDelete()).map(OnCascadeEnum::value).orElse(null);
    }

    public ModelFieldConfig setOnDelete(String onDelete) {
        getModelField().setOnDelete(Arrays.stream(OnCascadeEnum.values()).filter(v -> v.value().equals(onDelete)).findFirst().orElse(null));
        return this;
    }

    public Long getBitOptions() {
        return getModelField().getBitOptions();
    }

    public ModelFieldConfig setBitOptions(Long bitOptions) {
        getModelField().setBitOptions(bitOptions);
        return this;
    }

    public String getSource() {
        return Optional.ofNullable(getModelField().getSystemSource()).map(SystemSourceEnum::value).orElse(null);
    }

    public ModelFieldConfig setSource(String source) {
        getModelField().setSystemSource(Arrays.stream(SystemSourceEnum.values()).filter(v -> v.value().equals(source)).findFirst().orElse(null));
        return this;
    }

}
