package pro.shushi.pamirs.channel.core.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.orm.converter.processor.PersistenceExtendProcessor;
import pro.shushi.pamirs.framework.orm.converter.processor.PersistenceSerializeProcessor;
import pro.shushi.pamirs.framework.orm.converter.processor.PersistenceTypeProcessor;
import pro.shushi.pamirs.framework.orm.converter.processor.RelatedConvertProcessor;
import pro.shushi.pamirs.framework.orm.named.ColumnToLnameProcessor;
import pro.shushi.pamirs.framework.orm.processor.OrmMappingProcessor;
import pro.shushi.pamirs.framework.orm.processor.OrmModelingProcessor;
import pro.shushi.pamirs.framework.orm.processor.OrmObjectingProcessor;
import pro.shushi.pamirs.meta.api.core.orm.template.PersistenceDataComputeTemplate;

/**
 * ElasticDataConverter
 *
 * @author yakir on 2023/04/08 17:05.
 */
@Component
public class ElasticDataConverter {

    @Autowired
    private OrmModelingProcessor ormModelingProcessor;

    @Autowired
    private OrmMappingProcessor ormMappingProcessor;

    @Autowired
    private OrmObjectingProcessor ormObjectingProcessor;

    @Autowired
    private ColumnToLnameProcessor columnToLnameProcessor;

    @Autowired
    private RelatedConvertProcessor relatedConvertProcessor;

    @Autowired
    private PersistenceTypeProcessor persistenceTypeProcessor;

    @Autowired
    private PersistenceSerializeProcessor persistenceSerializeProcessor;

    @Autowired
    private PersistenceExtendProcessor persistenceExtendProcessor;

    public <T> T in(String model, Object obj) {
        return PersistenceDataComputeTemplate.getInstance().compute(model, obj,
                (oModel, oObj) -> ormModelingProcessor.before(oModel, oObj),// 模型化
                (oModel, oObj) -> ormMappingProcessor.after(oModel.getModel(), oObj),// map化
                (context, modelConfig, fieldConfig, dMap) -> persistenceExtendProcessor.in(context, fieldConfig, dMap),// 扩展处理
                (context, modelConfig, fieldConfig, dMap) -> relatedConvertProcessor.in(fieldConfig, dMap),// 引用字段处理
                (context, modelConfig, fieldConfig, dMap) -> persistenceTypeProcessor.in(modelConfig, fieldConfig, dMap),// 字段类型处理
                (context, modelConfig, fieldConfig, dMap) -> persistenceSerializeProcessor.serialize(fieldConfig, dMap)// 非String存储字段序列化
        );
    }

    public <T> T out(String model, Object obj) {
        return PersistenceDataComputeTemplate.getInstance().compute(model, obj,
                (oModel, oObj) -> ormModelingProcessor.before(oModel, oObj),// 模型化
                (oModel, oObj) -> ormObjectingProcessor.after(oModel, oObj),// 对象化
                (context, modelConfig, fieldConfig, dMap) -> columnToLnameProcessor.convert(fieldConfig, dMap),// 列名转化
                (context, modelConfig, fieldConfig, dMap) -> persistenceExtendProcessor.out(context, fieldConfig, dMap),// 扩展处理
                (context, modelConfig, fieldConfig, dMap) -> persistenceSerializeProcessor.deserialize(fieldConfig, dMap),// 非String存储字段反序列化
                (context, modelConfig, fieldConfig, dMap) -> persistenceTypeProcessor.out(modelConfig, fieldConfig, dMap),// 字段类型处理
                (context, modelConfig, fieldConfig, dMap) -> relatedConvertProcessor.out(fieldConfig, dMap)// 引用字段处理
        );
    }
}
