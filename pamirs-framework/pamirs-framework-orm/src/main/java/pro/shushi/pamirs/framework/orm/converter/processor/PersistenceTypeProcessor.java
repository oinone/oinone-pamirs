package pro.shushi.pamirs.framework.orm.converter.processor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.orm.converter.entity.relation.ManyToOneRelationConverter;
import pro.shushi.pamirs.framework.orm.converter.entity.type.*;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import jakarta.annotation.Resource;
import java.util.Map;

/**
 * 字段类型转换服务
 *
 * @author d@shushi.pro
 * @author cpc@shushi.pro 去除闭包提升性能
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Slf4j
@Component
public class PersistenceTypeProcessor {

    @Resource
    private PersistenceStringConverter persistenceStringConverter;

    @Resource
    private PersistenceIntegerConverter persistenceIntegerConverter;

    @Resource
    private PersistenceFloatConverter persistenceFloatConverter;

    @Resource
    private PersistenceByteConverter persistenceByteArrayConverter;

    @Resource
    private PersistenceDateConverter persistenceDateConverter;

    @Resource
    private PersistenceEnumConverter persistenceEnumConverter;

    @Resource
    private PersistenceBooleanConverter persistenceBooleanConverter;

    @Resource
    private ManyToOneRelationConverter manyToOneRelationConverter;

    public void in(ModelConfig modelConfig, ModelFieldConfig fieldConfig, Map<String, Object> origin) {
        // 处理引用字段类型
        String ttype = fieldConfig.getTtype();
        if (TtypeEnum.isRelatedType(fieldConfig.getTtype())) {
            ttype = fieldConfig.getRelatedTtype();
        }
        if (StringUtils.isBlank(ttype)) {
            return;
        }
        // 类型转换
        switch (ttype) {
            case "binary":
                persistenceByteArrayConverter.in(fieldConfig, origin);
                break;
            case "integer":
                persistenceIntegerConverter.in(fieldConfig, origin);
                break;
            case "float":
                persistenceFloatConverter.in(fieldConfig, origin);
                break;
            case "datetime":
            case "date":
            case "time":
            case "year":
                persistenceDateConverter.in(fieldConfig, origin);
                break;
            case "enum":
                persistenceEnumConverter.in(modelConfig, fieldConfig, origin);
                break;
            case "m2o":
            case "o2o":
                manyToOneRelationConverter.in(fieldConfig, origin);
                break;
        }
    }

    public void out(ModelConfig modelConfig, ModelFieldConfig fieldConfig, Map<String, Object> origin) {
        // 处理引用字段类型
        String ttype = fieldConfig.getTtype();
        if (TtypeEnum.isRelatedType(fieldConfig.getTtype())) {
            ttype = fieldConfig.getRelatedTtype();
        }
        if (StringUtils.isBlank(ttype)) {
            return;
        }
        // 类型转换
        switch (ttype) {
            case "binary":
                persistenceByteArrayConverter.out(fieldConfig, origin);
                break;
            case "string":
            case "text":
            case "html":
                persistenceStringConverter.out(fieldConfig, origin);
                break;
            case "integer":
                persistenceIntegerConverter.out(fieldConfig, origin);
                break;
            case "float":
                persistenceFloatConverter.out(fieldConfig, origin);
                break;
            case "datetime":
            case "date":
            case "time":
            case "year":
                persistenceDateConverter.out(fieldConfig, origin);
                break;
            case "enum":
                persistenceEnumConverter.out(modelConfig, fieldConfig, origin);
                break;
            case "bool":
                persistenceBooleanConverter.out(fieldConfig, origin);
                break;
            case "m2o":
            case "o2o":
                manyToOneRelationConverter.out(fieldConfig, origin);
                break;
        }
    }

}
