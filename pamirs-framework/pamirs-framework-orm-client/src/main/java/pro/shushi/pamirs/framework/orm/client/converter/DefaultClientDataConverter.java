package pro.shushi.pamirs.framework.orm.client.converter;

import javax.annotation.Resource;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.orm.api.RecursionOrmApi;
import pro.shushi.pamirs.framework.orm.client.checker.ClientFieldChecker;
import pro.shushi.pamirs.framework.orm.client.checker.ClientModelChecker;
import pro.shushi.pamirs.framework.orm.client.converter.processor.*;
import pro.shushi.pamirs.framework.orm.named.LnameToNameProcessor;
import pro.shushi.pamirs.framework.orm.named.NameToLnameProcessor;
import pro.shushi.pamirs.framework.orm.processor.OrmMappingProcessor;
import pro.shushi.pamirs.framework.orm.processor.OrmModelingProcessor;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.convert.ClientDataConverter;
import pro.shushi.pamirs.meta.api.core.orm.template.DataComputeTemplate;
import pro.shushi.pamirs.meta.api.core.orm.template.context.ModelComputeContext;
import pro.shushi.pamirs.meta.api.core.orm.template.function.FieldComputeApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.api.dto.meta.FuseMeta;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.util.ClassUtils;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 前后端数据转换服务
 * <p>
 * 递归遍历
 *
 * @author d@shushi.pro
 * @author cpc@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@SuppressWarnings("unchecked")
@Component
public class DefaultClientDataConverter implements ClientDataConverter {

    @Resource
    private ClientModelChecker clientModelChecker;

    @Resource
    private ClientSerializeProcessor clientSerializeProcessor;

    @Resource
    private ClientPageProcessor clientPageProcessor;

    @Resource
    private ClientExtendProcessor clientExtendProcessor;

    @Resource
    private ClientComputeProcessor clientComputeProcessor;

    @Resource
    private ClientFieldChecker clientFieldChecker;

    @Resource
    private ClientTypeProcessor clientTypeProcessor;

    @Resource
    private ClientArrayProcessor clientArrayProcessor;

    @Resource
    private OrmModelingProcessor ormModelingProcessor;

    @Resource
    private OrmMappingProcessor ormMappingProcessor;

    @Resource
    private DataComputeTemplate dataComputeTemplate;

    @Resource
    private NameToLnameProcessor nameToLnameProcessor;

    @Resource
    private LnameToNameProcessor lnameToNameProcessor;

    // Lambda 表达式提取为 final 字段，避免重复创建
    private final FieldComputeApi inName = (context, fieldConfig, dMap) -> nameToLnameProcessor.convert(fieldConfig, dMap);
    private final FieldComputeApi inExtend = (context, fieldConfig, dMap) -> clientExtendProcessor.in(context, fieldConfig, dMap);
    private final FieldComputeApi inSerialize = (context, fieldConfig, dMap) -> clientSerializeProcessor.in(context, fieldConfig, dMap);
    private final FieldComputeApi inType = (context, fieldConfig, dMap) -> clientTypeProcessor.in(context, fieldConfig, dMap);
    private final FieldComputeApi inArray = (context, fieldConfig, dMap) -> clientArrayProcessor.in(context, fieldConfig, dMap);
    private final FieldComputeApi inCompute = (context, fieldConfig, dMap) -> clientComputeProcessor.in(context, fieldConfig, dMap);
    private final FieldComputeApi inChecker = (context, fieldConfig, dMap) -> clientFieldChecker.check(context, fieldConfig, dMap);

    private final FieldComputeApi outExtend = (context, fieldConfig, dMap) -> clientExtendProcessor.out(context, fieldConfig, dMap);
    private final FieldComputeApi outType = (context, fieldConfig, dMap) -> clientTypeProcessor.out(context, fieldConfig, dMap);
    private final FieldComputeApi outArray = (context, fieldConfig, dMap) -> clientArrayProcessor.out(context, fieldConfig, dMap);
    private final FieldComputeApi outSerialize = (context, fieldConfig, dMap) -> clientSerializeProcessor.out(context, fieldConfig, dMap);
    private final FieldComputeApi outName = (context, fieldConfig, dMap) -> lnameToNameProcessor.convert(fieldConfig, dMap);

    @Override
    public <T> T in(ModelComputeContext totalContext, String model, Object obj) {

        if (obj == null) return null;
        int objIdTemp = System.identityHashCode(obj);
        if (D.class.isAssignableFrom(obj.getClass())) {
            objIdTemp = System.identityHashCode(FieldUtils.getDValue(obj));
        }
        int objId = objIdTemp;
        if (Models.modelDirective().isOrmReentry(obj)) {
            if (getReentryMap(objId) != null) {
                T res = (T) (getReentryMap(objId).get());
                return res;
            }
        }
        return dataComputeTemplate.compute(totalContext, model, obj,
                this::in,
                (context, oModel, oObj) -> {
                    Models.modelDirective().enableOrmReentry(oObj);
                    Object result = null;
                    ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(oModel);
                    String lname = FuseMeta.lname(modelConfig);
                    if (ClassUtils.isNoClass(lname)) {
                        result = new DataMap();
                    } else {
                        result = TypeUtils.getNewInstance(lname);
                    }
                    getReentryMap().put(objId, new SoftReference<Object>(Models.modelDirective().enableOrmReentry(result)));
                    return ormModelingProcessor.before(oModel, oObj);
                },
                (context, oModel, oObj) -> {
                    oObj = RecursionOrmApi.getOrmObjectingProcessor().after(oModel, oObj);
                    Object res = clientModelChecker.check(context, oModel, oObj);
                    ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(oModel);
                    String lname = FuseMeta.lname(modelConfig);
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
                inName, inExtend, inSerialize, inType, inArray, inCompute, inChecker
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

        if (Models.modelDirective().isOrmReentry(obj)) {
            if (getReentryMap(objId) != null) {
                T res = (T) (getReentryMap(objId).get());
                return res;
            }
        }
        return dataComputeTemplate.compute(model, obj,
                this::out,
                (oModel, oObj) -> {
                    Models.modelDirective().enableOrmReentry(oObj);
                    getReentryMap().put(objId, new SoftReference<Object>(Models.modelDirective().enableOrmReentry(new HashMap())));
                    return ormModelingProcessor.before(oModel, oObj);
                },
                (oModel, oObj) -> {
                    clientPageProcessor.out(oModel, oObj);
                    Map<String, Object> res = (Map<String, Object>) ormMappingProcessor.after(oModel, oObj);
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

