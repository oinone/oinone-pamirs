package pro.shushi.pamirs.eip.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.*;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.eip.api.enmu.EipOpenConverterTypeEnum;
import pro.shushi.pamirs.eip.api.pamirs.*;
import pro.shushi.pamirs.eip.api.processor.DefaultOpenInterfaceProcessor;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

import java.util.Optional;

/**
 * 开放接口
 *
 * @author Adamancy Zhang at 19:20 on 2021-06-09
 */
@Base
@Model.model(EipOpenInterface.MODEL_MODEL)
@Model.Advanced(unique = {"interfaceName"})
@Model(displayName = "开放接口", labelFields = "name")
public class EipOpenInterface extends AbstractSingleInterface implements IEipOpenInterface<SuperMap> {

    private static final long serialVersionUID = 7512074951371784726L;

    public static final String MODEL_MODEL = "pamirs.eip.EipOpenInterface";

    @Base
    @Field.String(size = 512)
    @Field(displayName = "接口路由", required = true, summary = "Camel内置路由规则")
    private String uri;

    @Base
    @Field.many2one
    @Field.Advanced(columnDefinition = "LONGTEXT")
    @Field.Relation(store = false)
    @Field(displayName = "请求参数处理器", store = NullableBoolEnum.TRUE, serialize = Field.serialize.JSON)
    private EipOpenParamProcessor requestParamProcessor;

    @Base
    @Field.many2one
    @Field.Advanced(columnDefinition = "LONGTEXT")
    @Field.Relation(store = false)
    @Field(displayName = "响应参数处理器", store = NullableBoolEnum.TRUE, serialize = Field.serialize.JSON)
    private EipOpenParamProcessor responseParamProcessor;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"authenticationProcessorNamespace", "authenticationProcessorFun"}, referenceFields = {"namespace", "fun"})
    @Field(displayName = "认证处理器函数")
    private FunctionDefinition authenticationProcessorFunction;

    @Base
    @Field.String
    @Field(displayName = "认证处理器函数命名空间", required = true)
    private String authenticationProcessorNamespace;

    @Base
    @Field.String
    @Field(displayName = "认证处理器函数名称", required = true)
    private String authenticationProcessorFun;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"serializableNamespace", "serializableFun"}, referenceFields = {"namespace", "fun"})
    @Field(displayName = "序列化函数")
    private FunctionDefinition serializableFunction;

    @Base
    @Field.String
    @Field(displayName = "序列化函数命名空间", required = true)
    private String serializableNamespace;

    @Base
    @Field.String
    @Field(displayName = "序列化函数名称", required = true)
    private String serializableFun;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"deserializationNamespace", "deserializationFun"}, referenceFields = {"namespace", "fun"})
    @Field(displayName = "反序列化函数")
    private FunctionDefinition deserializationFunction;

    @Base
    @Field.String
    @Field(displayName = "反序列化函数命名空间", required = true)
    private String deserializationNamespace;

    @Base
    @Field.String
    @Field(displayName = "反序列化函数名称", required = true)
    private String deserializationFun;

    @Deprecated
    @Base
    @Field.Enum
    @Field(displayName = "处理方式", required = true)
    private EipOpenConverterTypeEnum converterType;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"converterNamespace", "converterFun"}, referenceFields = {"namespace", "fun"})
    @Field(displayName = "结果处理函数")
    private FunctionDefinition converterFunction;

    @Base
    @Field.String
    @Field(displayName = "结果处理函数命名空间", required = true)
    private String converterNamespace;

    @Base
    @Field.String
    @Field(displayName = "结果处理函数名称", required = true)
    private String converterFun;

    @Base
    @Field.String
    @Field(displayName = "最终结果键值")
    private String finalResultKey;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"inOutConverterNamespace", "inOutConverterFun"}, referenceFields = {"namespace", "fun"})
    @Field(displayName = "输入输出转换器函数")
    private FunctionDefinition inOutConverterFunction;

    @Base
    @Field.String
    @Field(displayName = "输入输出转换器函数命名空间", required = true)
    private String inOutConverterNamespace;

    @Base
    @Field.String
    @Field(displayName = "输入输出转换器函数名称", required = true)
    private String inOutConverterFun;

    @Base
    @Field.String
    @Field(displayName = "请求预处理函数命名空间")
    private String requestDecryptNamespace;

    @Base
    @Field.String
    @Field(displayName = "请求预处理函数名称")
    private String requestDecryptFun;

    @Base
    @Field.String
    @Field(displayName = "响应预处理函数命名空间")
    private String responseEncryptionNamespace;

    @Base
    @Field.String
    @Field(displayName = "响应预处理函数名称")
    private String responseEncryptionFun;

    @JSONField(serialize = false)
    @Override
    public IEipContextSupplier<SuperMap> getContextSupplier() {
        String namespace = getContextSupplierNamespace();
        String fun = getContextSupplierFun();
        if (StringUtils.isAnyBlank(namespace, fun)) {
            return EipFunctionConstant.DEFAULT_CONTEXT_SUPPLIER;
        }
        return new DefaultContextSupplierFunction<>(namespace, fun);
    }

    @JSONField(serialize = false)
    @Override
    public IEipDecryptProcessor getRequestDecryptProcessor() {
        String namespace = getRequestDecryptNamespace();
        String fun = getRequestDecryptFun();
        if (StringUtils.isAnyBlank(namespace, fun)) {
            return null;
        }
        return new DefaultDecryptFunction(namespace, fun);
    }

    @JSONField(serialize = false)
    @Override
    public IEipProcessor<IEipOpenInterface<SuperMap>> getProcessor() {
        return new DefaultOpenInterfaceProcessor(this);
    }

    @JSONField(serialize = false)
    @Override
    public IEipConverter<SuperMap> getConverter() {
        String namespace = getConverterNamespace();
        String fun = getConverterFun();
        if (StringUtils.isAnyBlank(namespace, fun)) {
            return null;
        }

        EipOpenConverterTypeEnum localConverterType = getConverterType();
        if (localConverterType == null) {
            // 兼容老代码
            if (Optional.ofNullable(PamirsSession.getContext()).map(v -> v.getModelConfig(namespace)).orElse(null) == null) {
                localConverterType = EipOpenConverterTypeEnum.EIP_FUNCTION;
            } else {
                localConverterType = EipOpenConverterTypeEnum.MODEL_FUNCTION;
            }
        }

        if (EipOpenConverterTypeEnum.EIP_FUNCTION.equals(localConverterType)) {
            return new DefaultConverterFunction<>(namespace, fun);
        } else {
            switch (localConverterType) {
                case EIP_FUNCTION_WITH_RESULT:
                    return new DefaultOpenEipFunctionConverterFunction<>(namespace, fun);
                case MODEL_FUNCTION:
                    return new DefaultOpenModelFunctionConverterFunction<>(namespace, fun);
                case FUNCTION:
                    return new DefaultOpenFunctionConverterFunction<>(namespace, fun);
                default:
                    return new DefaultConverterFunction<>(namespace, fun);
            }
        }
    }

    @JSONField(serialize = false)
    @Override
    public IEipAuthenticationProcessor<SuperMap> getAuthenticationProcessor() {
        String namespace = getAuthenticationProcessorNamespace();
        String fun = getAuthenticationProcessorFun();
        if (StringUtils.isAnyBlank(namespace, fun)) {
            return null;
        }
        return new DefaultAuthenticationProcessorFunction(namespace, fun);
    }

    @JSONField(serialize = false)
    @Override
    public IEipSerializable<SuperMap> getSerializable() {
        String namespace = getSerializableNamespace();
        String fun = getSerializableFun();
        if (StringUtils.isAnyBlank(namespace, fun)) {
            return EipFunctionConstant.DEFAULT_JSON_SERIALIZABLE;
        }
        return new DefaultSerializableFunction<>(namespace, fun);
    }

    @JSONField(serialize = false)
    @Override
    public IEipDeserialization<SuperMap> getDeserialization() {
        String namespace = getDeserializationNamespace();
        String fun = getDeserializationFun();
        if (StringUtils.isAnyBlank(namespace, fun)) {
            return EipFunctionConstant.DEFAULT_JSON_SERIALIZABLE;
        }
        return new DefaultDeserializationFunction<>(namespace, fun);
    }

    @JSONField(serialize = false)
    @Override
    public IEipInOutConverter getInOutConverter() {
        String namespace = getInOutConverterNamespace();
        String fun = getInOutConverterFun();
        if (StringUtils.isAnyBlank(namespace, fun)) {
            return EipFunctionConstant.DEFAULT_IN_OUT_CONVERTER;
        }
        return new DefaultInOutConverterFunction(namespace, fun);
    }

    @JSONField(serialize = false)
    @Override
    public IEipEncryptionProcessor getResponseEncryptionProcessor() {
        String namespace = getResponseEncryptionNamespace();
        String fun = getResponseEncryptionFun();
        if (StringUtils.isAnyBlank(namespace, fun)) {
            return null;
        }
        return new DefaultEncryptionFunction(namespace, fun);
    }

    @JSONField(serialize = false)
    @Override
    public IEipOpenInterface<SuperMap> afterProperty() {
        return this;
    }
}
