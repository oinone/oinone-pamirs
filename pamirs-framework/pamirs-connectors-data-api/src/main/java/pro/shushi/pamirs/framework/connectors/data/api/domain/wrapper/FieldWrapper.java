package pro.shushi.pamirs.framework.connectors.data.api.domain.wrapper;

import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.KeyGeneratorEnum;
import pro.shushi.pamirs.meta.enmu.SerializeEnum;

import java.util.Optional;

/**
 * 字段配置封装代理类
 * <p>
 * 2020/6/24 2:33 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class FieldWrapper {

    private String name;

    private String model;

    private String field;

    private String column;

    private String columnDefinition;

    private Boolean isPrimaryKey;

    private String keyGenerator;

    private String ttype;

    private String ltype;

    private Integer size;

    private Integer decimal;

    private Boolean multi;

    private Boolean bit;

    private String summary;

    private Boolean store;

    private Long priority;

    private Boolean unique;

    private Boolean index;

    private String charset;

    private String collation;

    private String source;

    public static FieldWrapper wrap(ModelField modelField) {
        return new FieldWrapper()
                .setName(modelField.getName())
                .setModel(modelField.getModel())
                .setField(modelField.getField())
                .setColumn(modelField.getColumn())
                .setColumnDefinition(modelField.getColumnDefinition())
                .setIsPrimaryKey(modelField.getPk())
                .setKeyGenerator(Optional.ofNullable(modelField.getKeyGenerator()).map(KeyGeneratorEnum::value).orElse(null))
                .setTtype(modelField.getTtype().value())
                .setLtype(modelField.getLtype())
                .setSize(modelField.getSize())
                .setDecimal(modelField.getDecimal())
                .setMulti(modelField.getMulti())
                .setBit(SerializeEnum.BIT.value().equals(modelField.getStoreSerialize()))
                .setSummary(modelField.getSummary())
                .setStore(modelField.getStore())
                .setPriority(modelField.getPriority())
                .setUnique(modelField.getUnique())
                .setIndex(modelField.getIndex())
                .setCharset(null == modelField.getCharset() ? null : modelField.getCharset().value())
                .setCollation(null == modelField.getCollation() ? null : modelField.getCollation().value())
                .setSource(null == modelField.getSystemSource() ? null : modelField.getSystemSource().value())
                ;
    }

    public static FieldWrapper wrap(ModelFieldConfig modelField) {
        return new FieldWrapper()
                .setName(modelField.getName())
                .setModel(modelField.getModel())
                .setField(modelField.getField())
                .setColumn(modelField.getColumn())
                .setColumnDefinition(modelField.getColumnDefinition())
                .setIsPrimaryKey(modelField.getPk())
                .setKeyGenerator(modelField.getKeyGenerator())
                .setTtype(modelField.getTtype())
                .setLtype(modelField.getLtype())
                .setSize(modelField.getSize())
                .setDecimal(modelField.getDecimal())
                .setMulti(modelField.getMulti())
                .setBit(SerializeEnum.BIT.value().equals(modelField.getStoreSerialize()))
                .setSummary(modelField.getSummary())
                .setStore(modelField.getStore())
                .setPriority(modelField.getPriority())
                .setUnique(modelField.getUnique())
                .setIndex(modelField.getIndex())
                .setCharset(null == modelField.getCharset() ? null : modelField.getCharset())
                .setCollation(null == modelField.getCollation() ? null : modelField.getCollation())
                .setSource(modelField.getSource())
                ;
    }

}
