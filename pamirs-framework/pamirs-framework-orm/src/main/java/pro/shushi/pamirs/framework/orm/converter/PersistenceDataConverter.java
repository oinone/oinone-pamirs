package pro.shushi.pamirs.framework.orm.converter;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.orm.converter.processor.PersistenceExtendProcessor;
import pro.shushi.pamirs.framework.orm.converter.processor.PersistenceSerializeProcessor;
import pro.shushi.pamirs.framework.orm.converter.processor.PersistenceTypeProcessor;
import pro.shushi.pamirs.framework.orm.converter.processor.RelatedConvertProcessor;
import pro.shushi.pamirs.framework.orm.named.ColumnToLnameProcessor;
import pro.shushi.pamirs.framework.orm.named.LnameToColumnProcessor;
import pro.shushi.pamirs.framework.orm.processor.OrmMappingProcessor;
import pro.shushi.pamirs.framework.orm.processor.OrmModelingProcessor;
import pro.shushi.pamirs.framework.orm.processor.OrmObjectingProcessor;
import pro.shushi.pamirs.meta.api.core.orm.convert.DataConverter;
import pro.shushi.pamirs.meta.api.core.orm.template.PersistenceDataComputeTemplate;

import javax.annotation.Resource;

/**
 * 持久化数据转换服务
 * <p>
 * 递归遍历
 *
 * @author d@shushi.pro
 * @author cpc@shushi.pro 去除闭包提升性能
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Component
public class PersistenceDataConverter implements DataConverter {

    @Resource
    private OrmModelingProcessor ormModelingProcessor;

    @Resource
    private OrmMappingProcessor ormMappingProcessor;

    @Resource
    private OrmObjectingProcessor ormObjectingProcessor;

    @Resource
    private LnameToColumnProcessor lnameToColumnProcessor;

    @Resource
    private ColumnToLnameProcessor columnToLnameProcessor;

    @Resource
    private RelatedConvertProcessor relatedConvertProcessor;

    @Resource
    private PersistenceTypeProcessor persistenceTypeProcessor;

    @Resource
    private PersistenceSerializeProcessor persistenceSerializeProcessor;

    @Resource
    private PersistenceExtendProcessor persistenceExtendProcessor;

    @Override
    public <T> T in(String model, Object obj) {
        return PersistenceDataComputeTemplate.getInstance().compute(model, obj,
                (oModel, oObj) -> ormModelingProcessor.before(oModel, oObj),// 模型化
                (oModel, oObj) -> ormMappingProcessor.after(oModel.getModel(), oObj),// map化
                (context, modelConfig, fieldConfig, dMap) -> persistenceExtendProcessor.in(context, fieldConfig, dMap),// 扩展处理
                (context, modelConfig, fieldConfig, dMap) -> persistenceTypeProcessor.in(modelConfig, fieldConfig, dMap),// 字段类型处理
                (context, modelConfig, fieldConfig, dMap) -> relatedConvertProcessor.in(fieldConfig, dMap),// 引用字段处理
                (context, modelConfig, fieldConfig, dMap) -> persistenceSerializeProcessor.serialize(fieldConfig, dMap),// 非String存储字段序列化
                (context, modelConfig, fieldConfig, dMap) -> lnameToColumnProcessor.convert(fieldConfig, dMap)// 列名转化
        );
    }

    @Override
    public <T> T out(String model, Object obj) {
        return PersistenceDataComputeTemplate.getInstance().compute(model, obj,
                (oModel, oObj) -> ormModelingProcessor.before(oModel, oObj),// 模型化
                (oModel, oObj) -> ormObjectingProcessor.after(oModel, oObj),// 对象化
                (context, modelConfig, fieldConfig, dMap) -> columnToLnameProcessor.convert(fieldConfig, dMap),// 列名转化
                (context, modelConfig, fieldConfig, dMap) -> persistenceExtendProcessor.out(context, fieldConfig, dMap),// 扩展处理
                (context, modelConfig, fieldConfig, dMap) -> persistenceSerializeProcessor.deserialize(fieldConfig, dMap),// 非String存储字段反序列化
                (context, modelConfig, fieldConfig, dMap) -> persistenceTypeProcessor.out(modelConfig, fieldConfig, dMap)// 字段类型处理
        );
    }

}
