package pro.shushi.pamirs.framework.orm.client.converter;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.orm.api.RecursionOrmApi;
import pro.shushi.pamirs.framework.orm.client.checker.ClientModelChecker;
import pro.shushi.pamirs.framework.orm.client.converter.processor.*;
import pro.shushi.pamirs.framework.orm.named.LnameToNameProcessor;
import pro.shushi.pamirs.framework.orm.named.NameToLnameProcessor;
import pro.shushi.pamirs.framework.orm.processor.OrmMappingProcessor;
import pro.shushi.pamirs.framework.orm.processor.OrmModelingProcessor;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.convert.ClientDataConverter;
import pro.shushi.pamirs.meta.api.core.orm.template.ClientDataComputeTemplate;
import pro.shushi.pamirs.meta.api.core.orm.template.context.ModelComputeContext;
import pro.shushi.pamirs.meta.api.core.orm.template.function.PersistenceFieldComputeApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.util.ClassUtils;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.TypeUtils;

import javax.annotation.Resource;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * RPC端数据转换服务
 * <p>
 * 递归遍历
 *
 * @author cpc@shushi.pro
 * @version 1.0.0
 * date 2024/2/27
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Component
public class RemoteClientDataConverter implements ClientDataConverter {

    @Resource
    private ClientModelChecker clientModelChecker;

    @Resource
    private ClientSerializeProcessor clientSerializeProcessor;

    @Resource
    private RemoteClientPageProcessor remoteClientPageProcessor;

    @Resource
    private ClientExtendProcessor clientExtendProcessor;

    @Resource
    private ClientComputeProcessor clientComputeProcessor;

    @Resource
    private RemoteClientTypeProcessor remoteClientTypeProcessor;

    @Resource
    private ClientArrayProcessor clientArrayProcessor;

    @Resource
    private OrmModelingProcessor ormModelingProcessor;

    @Resource
    private OrmMappingProcessor ormMappingProcessor;

    @Resource
    private NameToLnameProcessor nameToLnameProcessor;

    @Resource
    private LnameToNameProcessor lnameToNameProcessor;

    // Lambda 表达式提取为 final 字段，避免重复创建
    private final PersistenceFieldComputeApi inName = (context, modelConfig, fieldConfig, dMap) -> nameToLnameProcessor.convert(fieldConfig, dMap);
    private final PersistenceFieldComputeApi inExtend = (context, modelConfig, fieldConfig, dMap) -> clientExtendProcessor.in(context, fieldConfig, dMap);
    private final PersistenceFieldComputeApi inSerialize = (context, modelConfig, fieldConfig, dMap) -> clientSerializeProcessor.in(context, fieldConfig, dMap);
    private final PersistenceFieldComputeApi inType = (context, modelConfig, fieldConfig, dMap) -> remoteClientTypeProcessor.in(context, fieldConfig, dMap);
    private final PersistenceFieldComputeApi inArray = (context, modelConfig, fieldConfig, dMap) -> clientArrayProcessor.in(context, fieldConfig, dMap);

    private final PersistenceFieldComputeApi outExtend = (context, modelConfig, fieldConfig, dMap) -> clientExtendProcessor.out(context, fieldConfig, dMap);
    private final PersistenceFieldComputeApi outType = (context, modelConfig, fieldConfig, dMap) -> remoteClientTypeProcessor.out(context, fieldConfig, dMap);
    private final PersistenceFieldComputeApi outArray = (context, modelConfig, fieldConfig, dMap) -> clientArrayProcessor.out(context, fieldConfig, dMap);
    private final PersistenceFieldComputeApi outSerialize = (context, modelConfig, fieldConfig, dMap) -> clientSerializeProcessor.out(context, fieldConfig, dMap);
    private final PersistenceFieldComputeApi outName = (context, modelConfig, fieldConfig, dMap) -> lnameToNameProcessor.convert(fieldConfig, dMap);

    @Override
    public <T> T in(ModelComputeContext totalContext, String model, Object obj) {
        if (obj == null) return null;
        int objIdTemp = System.identityHashCode(obj);
        if (D.class.isAssignableFrom(obj.getClass())) {
            objIdTemp = System.identityHashCode(FieldUtils.getDValue(obj));
        }
        int objId = objIdTemp;
        if (Models.modelDirective().isOrmReentry(obj)) {// 判断是否重入
            if (getReentryMap(objId) != null) {
                T res = (T) (getReentryMap(objId).get());
                return res;
            }
        }
        return ClientDataComputeTemplate.getInstance().compute(totalContext, model, obj,
                this::in,
                (context, oModel, oObj) -> {
                    Models.modelDirective().enableOrmReentry(oObj);// 防重入
                    Object result = null;
                    ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(oModel);
                    String lname = modelConfig.getLname();
                    if (ClassUtils.isNoClass(lname)) {
                        result = new DataMap();
                    } else {
                        result = TypeUtils.getNewInstance(lname);
                    }
                    getReentryMap().put(objId, new SoftReference<Object>(Models.modelDirective().enableOrmReentry(result)));
                    return ormModelingProcessor.before(oModel, oObj);
                },
                (context, modelConfig, oObj) -> {
                    String oModel = modelConfig.getModel();
                    oObj = RecursionOrmApi.getOrmObjectingProcessor().after(oModel, oObj);// 对象化
                    Object res = clientModelChecker.check(context, modelConfig.getModelDefinition(), oObj);// 模型约束校验
                    String lname = modelConfig.getLname();
                    if (ClassUtils.isNoClass(lname)) {
                        Map obj1 = (Map) getReentryMap().get(objId).get();
                        ((Map) res).forEach((k, v) -> {
                            obj1.put(k, v);
                        });
                    } else {
                        if (D.class.isAssignableFrom(res.getClass())) {
                            D obj1 = (D) getReentryMap().get(objId).get();
                            FieldUtils.setDValue(obj1, (Map) FieldUtils.getDValue(res));
                        } else {
                            Map obj1 = (Map) getReentryMap().get(objId).get();
                            ((Map) res).forEach((k, v) -> {
                                obj1.put(k, v);
                            });
                        }
                    }
                    return res;
                },
                inName, inExtend, inSerialize, inType, inArray
        );
    }

    @Override
    public <T> T out(String model, Object obj) {
        if (obj == null) return null;
        int objIdTemp = System.identityHashCode(obj);
        if (D.class.isAssignableFrom(obj.getClass())) {
            objIdTemp = System.identityHashCode(FieldUtils.getDValue(obj));
        }
        int objId = objIdTemp;

        if (Models.modelDirective().isOrmReentry(obj)) {// 判断是否重入
            if (getReentryMap(objId) != null) {
                T res = (T) (getReentryMap(objId).get());
                return res;
            }
        }
        return ClientDataComputeTemplate.getInstance().compute(model, obj,
                this::out,
                (oModel, oObj) -> {
                    Models.modelDirective().enableOrmReentry(oObj);
                    getReentryMap().put(objId, new SoftReference<Object>(Models.modelDirective().enableOrmReentry(new HashMap())));
                    return ormModelingProcessor.before(oModel, oObj);// 模型化
                },
                (context, modelConfig, oObj) -> {
                    String oModel = modelConfig.getModel();
                    remoteClientPageProcessor.out(oModel, oObj);// 分页数据处理
                    Map<String, Object> res = (Map<String, Object>) ormMappingProcessor.after(oModel, oObj);// map化
                    Map obj1 = (HashMap) getReentryMap().get(objId).get();
                    res.forEach((k, v) -> {
                        obj1.put(k, v);
                    });
                    return res;
                },
                outExtend, outType, outArray, outSerialize, outName
        );
    }

}

